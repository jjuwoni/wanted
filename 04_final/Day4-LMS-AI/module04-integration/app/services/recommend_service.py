# =====================================================================
# services/recommend_service.py — 수강 이력 기반 강의 추천 (2단계 파이프라인)
# ---------------------------------------------------------------------
# [Spring의 Service에 해당 — 비즈니스 규칙의 집]
#
# ★ 실무 추천 시스템의 표준 골격: 후보 생성 → 재정렬 ★
#   ① 수강 이력 조회 : clients.spring_client (온라인 경로 → MySQL)
#   ② 후보 생성     : repositories.vector_store (이력 강좌별 벡터 검색)
#   ③ 점수화·재정렬  : ★ 이 파일 — 비즈니스 규칙이 사는 곳 ★
#   ④ 추천 이유 생성 : LCEL 체인 (Day 2에서 배운 그 문법!)
#
# [점수식 — README 5장과 1:1, 손계산 가능하게 단순 유지]
#   후보 점수 = Σ ( 유사도 × 이력 가중치 ) + 레벨업 보너스
#     이력 가중치   = 1.0 (수료) / 진도율÷100 (진행 중)
#                     → 끝까지 들은 강좌가 취향을 더 강하게 대변한다
#     레벨업 보너스 = +0.15, 시드와 같은 카테고리 + 한 단계 위 레벨
#                     → "다음 강의"의 구현: 입문 수료자에게 중급을 민다
#     하드 규칙     = 이미 수강한 강좌는 무조건 제외
#
# [왜 벡터 검색(②)에 규칙을 안 넣는가?]
#   벡터는 "비슷한 것"만 안다. "다음 단계"라는 개념은 모른다.
#   그걸 아는 건 비즈니스 규칙(③)이다 — 그래서 재정렬 계층이 따로 있다.
#
# [함수를 둘로 나눈 이유]
#   recommend_candidates(): ①~③만. LLM 호출 없음(빠르고 무료)
#     → 챗봇의 '도구'가 쓴다. 설명은 챗봇 모델이 대화 맥락에서 직접.
#   recommend_for_student(): ①~④ 완제품 → POST /courses/recommend 가 쓴다.
#   도구 안에서 또 LLM을 부르면(중첩 호출) 비용과 지연이 2배가 된다.
# =====================================================================
import logging

from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import ChatPromptTemplate

from app.clients import spring_client
from app.clients.llm import build_llm
from app.core.config import settings
from app.core.exceptions import AppException, UpstreamException
from app.repositories.vector_store import vector_store

logger = logging.getLogger(__name__)


def _history_weight(enrollment: dict) -> float:
    """이력 가중치: 수료 1.0, 진행 중이면 진도율÷100."""
    if enrollment["status"] == "COMPLETED":
        return settings.completed_weight
    return enrollment["progress_rate"] / 100


def _is_level_up(seed: dict, candidate: dict) -> bool:
    """레벨업 판정: 같은 카테고리 + 정확히 한 단계 위 레벨."""
    ranks = settings.level_ranks
    return (candidate["category"] == seed["category"]
            and ranks.get(candidate["level"], 0) == ranks.get(seed["level"], 0) + 1)


def recommend_candidates(student_id: int, top_k: int = 3) -> dict:
    """추천 파이프라인 ①~③. (LLM 호출 없음 — 챗봇 도구와 공유)

    반환: {"based_on": 이력 요약, "recommendations": [{..., score, matched_from}]}
    실패: AppException → 전역 핸들러가 계약 형식 {"detail","request_id"}로 응답
    """
    # --- ① 수강 이력 조회 (실시간 데이터는 반드시 Spring API 경유!) ---
    result = spring_client.get_my_courses(student_id)
    if "error" in result:
        # 도구용 '에러 데이터'를 여기서는 '예외'로 승격한다.
        # 추천은 데이터 API라 호출자(Spring)가 성공/실패를 명확히 알아야 한다.
        raise UpstreamException(result["error"])

    enrollments = result.get("courses", [])
    if not enrollments:
        raise AppException(status_code=404,
                           detail=f"수강 이력이 없어 추천할 수 없습니다 (student_id={student_id})")

    enrolled_codes = {e["code"] for e in enrollments}

    # --- ② 후보 생성 + ③ 점수화 (이력 강좌 = 시드, 하나씩 순회) ---
    # scores[code] = {"score": 누적 점수, "matched_from": 기여한 시드들, "info": 강좌 정보}
    scores: dict[str, dict] = {}
    bonus_given: set[str] = set()   # 레벨업 보너스는 후보당 최대 1번만

    for e in enrollments:
        weight = _history_weight(e)

        # 시드 문서/메타데이터는 Vector DB에서 (level 정보가 여기에 있다)
        seed = vector_store.get_course(e["code"])
        if seed is None:
            # MySQL엔 있는데 Chroma에 없다 = 색인 누락(정합성 문제).
            # 죽는 대신 이 시드만 건너뛰고, 로그로 운영자에게 알린다.
            logger.warning("Vector DB에 시드 강좌 없음: %s (색인 배치 누락?)", e["code"])
            continue

        # 순수 벡터 검색 — 자기 자신이 포함되므로 +1개 조회
        hits = vector_store.similar_courses(seed["document"],
                                            n=settings.per_seed_candidates + 1)
        for cand in hits:
            code = cand["code"]
            if code in enrolled_codes:        # 하드 규칙: 수강한 강좌 제외
                continue                       # (자기 자신도 여기서 걸러진다)

            entry = scores.setdefault(code, {"score": 0.0, "matched_from": [],
                                             "info": cand})
            # 핵심 한 줄: 유사도 × 이력 가중치를 '누적' —
            # 여러 시드에서 반복 등장하는 후보일수록 점수가 쌓인다
            entry["score"] += cand["similarity"] * weight
            entry["matched_from"].append(seed["title"])

            # 레벨업 보너스 (후보당 1번만): "다음 강의"를 위로 밀어준다
            if code not in bonus_given and _is_level_up(seed, cand):
                entry["score"] += settings.level_up_bonus
                bonus_given.add(code)

    if not scores:
        raise AppException(status_code=404,
                           detail="추천 후보를 만들 수 없습니다. 색인 배치(ingestion) 실행 여부를 확인하세요.")

    # --- ③ 재정렬: 점수 내림차순 top_k ---
    ranked = sorted(scores.values(), key=lambda x: x["score"], reverse=True)[:top_k]
    recommendations = [
        {**r["info"], "score": round(r["score"], 4),
         "matched_from": sorted(set(r["matched_from"]))}
        for r in ranked
    ]
    for r in recommendations:
        r.pop("similarity", None)   # 개별 유사도는 점수에 녹았으므로 응답에서 제거

    # 이력 요약: "Java 프로그래밍 기초(수료) 외 2건" 형태
    first = enrollments[0]
    based_on = f"{first['title']}({'수료' if first['status']=='COMPLETED' else str(first['progress_rate'])+'%'})"
    if len(enrollments) > 1:
        based_on += f" 외 {len(enrollments)-1}건"

    logger.info("추천 완료 (student_id=%d): %s", student_id,
                [(r["code"], r["score"]) for r in recommendations])
    return {"based_on": based_on, "recommendations": recommendations}


# ----------------------------------------------------------------------
# ④ 추천 이유 생성 — Day 2에서 배운 LCEL 체인(prompt | llm | parser)!
#    체인은 모듈 로드 시 1번만 조립하고, 요청마다 invoke만 한다.
# ----------------------------------------------------------------------
_explain_prompt = ChatPromptTemplate.from_messages([
    ("system",
     "당신은 LMS의 학습 경로 상담사입니다. 수강생의 학습 이력과 추천 강좌 "
     "목록이 주어집니다. 각 추천 강좌가 '다음 단계'로 왜 적합한지, 이력과의 "
     "연결고리(matched_from)를 들어 강좌마다 한 문장씩 친근한 존댓말로 "
     "설명하세요. 목록에 없는 강좌를 지어내지 마세요."),
    ("human", "수강 이력: {based_on}\n\n추천 강좌 목록:\n{candidates}"),
])
_explain_chain = _explain_prompt | build_llm(temperature=0.4) | StrOutputParser()


def recommend_for_student(student_id: int, top_k: int = 3) -> dict:
    """①~④ 완제품 (POST /courses/recommend). 설명 실패해도 목록은 준다."""
    result = recommend_candidates(student_id, top_k)

    candidates_text = "\n".join(
        f"- {r['title']} ({r['category']}/{r['level']}, 점수 {r['score']}) "
        f"— 근거 이력: {', '.join(r['matched_from'])}"
        for r in result["recommendations"]
    )
    try:
        explanation = _explain_chain.invoke({
            "based_on": result["based_on"], "candidates": candidates_text})
    except Exception as e:
        # 부분 성공 정책: LLM 장애가 추천 전체를 죽이면 안 된다
        logger.error("추천 이유 생성 실패: %s", e, exc_info=True)
        explanation = "추천 이유 생성에 일시적인 문제가 있어 목록만 제공합니다."

    return {**result, "explanation": explanation}
