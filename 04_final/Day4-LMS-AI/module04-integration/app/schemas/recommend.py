# =====================================================================
# schemas/recommend.py — 강의 추천 API의 요청/응답 DTO (계약서 v1.2)
# Spring의 AiDtos.RecommendApiResponse와 필드가 1:1로 맞아야 한다!
# =====================================================================
from pydantic import BaseModel, Field


class RecommendRequest(BaseModel):
    student_id: int = Field(..., description="추천 대상 학생 ID (Spring이 채움)")
    top_k: int = Field(3, ge=1, le=5)


class RecommendedCourse(BaseModel):
    code: str
    title: str
    category: str
    level: str
    score: float                 # 2단계 파이프라인의 최종 점수 (README 5장 점수식)
    matched_from: list[str]      # 어떤 수강 이력에서 이 후보가 나왔는지 (설명 가능성!)


class RecommendResponse(BaseModel):
    based_on: str                          # 추천의 근거가 된 수강 이력 요약
    recommendations: list[RecommendedCourse]
    explanation: str                       # LCEL 체인이 생성한 추천 이유
