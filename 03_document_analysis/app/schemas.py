"""PDF 문서 분석 API의 응답 스키마입니다."""

from pydantic import BaseModel


# BaseModel을 상속하면 타입 검증, JSON 변환, Swagger 문서 생성이 자동 처리됩니다.
class CategoryPrediction(BaseModel):
    """후보 카테고리 하나에 대한 모델의 예측 결과입니다."""

    category: str
    score: float


class ClassificationResponse(BaseModel):
    """문서 분류 API가 클라이언트에 반환하는 데이터 구조입니다."""

    filename: str
    extracted_text_length: int
    predictions: list[CategoryPrediction]


class EntityResponse(BaseModel):
    """문서에서 발견한 개체명 하나의 위치와 예측값입니다."""

    text: str
    entity: str
    score: float
    # start는 시작 문자 위치, end는 끝 다음 문자 위치입니다.
    start: int
    end: int


class NerResponse(BaseModel):
    """개체명 추출 API의 전체 응답 구조입니다."""

    filename: str
    extracted_text_length: int
    entities: list[EntityResponse]


class SummarizationResponse(BaseModel):
    """원문과 요약문의 길이를 포함한 문서 요약 응답입니다."""

    filename: str
    original_length: int
    summary_length: int
    summary: str


class HealthResponse(BaseModel):
    """서버 장치와 기능별 모델 로딩 상태를 나타냅니다."""

    status: str
    device: str
    loaded_models: dict[str, bool]