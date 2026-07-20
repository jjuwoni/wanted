# =====================================================================
# routers/recommend.py — 강의 추천 엔드포인트
# 예외(404 수강이력 없음, 502 Spring 장애)는 서비스가 던지고
# 전역 핸들러가 계약 형식 {"detail", "request_id"}로 변환한다.
# =====================================================================
from fastapi import APIRouter

from app.schemas.recommend import RecommendRequest, RecommendResponse
from app.services import recommend_service

router = APIRouter(tags=["recommend"])


@router.post("/courses/recommend", response_model=RecommendResponse)
def recommend(req: RecommendRequest) -> RecommendResponse:
    result = recommend_service.recommend_for_student(req.student_id, req.top_k)
    return RecommendResponse(**result)
