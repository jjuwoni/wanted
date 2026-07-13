from fastapi import FastAPI, File, Form, HTTPException, UploadFile

from app.models import get_device_name
from app.schemas import (
    ClassificationResponse,
    HealthResponse,
    NerResponse,
    SummarizationResponse
)

from contextlib import asynccontextmanager
from app.document_loader import extract_text_from_pdf
from app.services import DocumentAnalysisService

# 서버 전체에서 하나의 서비스 객체를 공유한다.
# 서버 시작 전과 종료 후에는 None 상태가 된다.
document_service : DocumentAnalysisService | None = None

# Spring 과 대조해서 알면 좋다.
# @PostConstruct
# 서버 실행 시 바로 읽어들어야 하는 config 파일 환경 설정 등에 대한 것들을
# 진행하는 어노테이션
@asynccontextmanager
async def lifespan(app : FastAPI):
    # 서버 생명 주기 동안 서비스 객체를 관리할 수 있다.
    # global : 전역
    global document_service

    # 서버 시작 시 바로 서비스 객체 생성
    # 서비스 내부의 AI 모델 로딩은 요청 시 로딩
    document_service = DocumentAnalysisService()

    # yield 키워드 위쪽은 서버 시작 시 동작
    yield # Spring AOP @Around 와 비슷하게 동작한다.
    # yield 키워드 아래쪽은 서버 종료 시 동작
    # 서버 종료 시 명시적으로 서비스 객체 None 설정
    document_service = None

app = FastAPI(
    title = "문서 분석 API",
    description = (
        "PDF 파일을 업로드하여 문서 분류, 추출, 요약을 수행한다."
    ),
    # 위쪽에 작성한 lifespan 함수 등록
    lifespan=lifespan
)

def get_document_service() -> DocumentAnalysisService:
    """초기화된 문서 분석 서비스를 반환합니다."""
    # 아직 lifespan 초기화가 끝나지 않은 요청에는 503을 반환합니다.
    if document_service is None:
        raise HTTPException(
            status_code=503,
            detail="문서 분석 서비스가 아직 준비되지 않았습니다.",
        )

    return document_service


def parse_categories(categories: str) -> list[str]:
    """쉼표로 구분된 카테고리 문자열을 목록으로 변환합니다."""
    # split(",")으로 나눈 뒤 공백과 빈 항목을 제거합니다.
    parsed = [
        category.strip()
        for category in categories.split(",")
        if category.strip()
    ]

    if len(parsed) < 2:
        raise HTTPException(
            status_code=422,
            detail="카테고리를 쉼표로 구분하여 두 개 이상 입력해주세요.",
        )

    if len(parsed) > 10:
        raise HTTPException(
            status_code=422,
            detail="카테고리는 최대 10개까지 입력할 수 있습니다.",
        )

    # set은 중복 값을 제거하므로 길이가 다르면 중복이 있다는 뜻입니다.
    if len(parsed) != len(set(parsed)):
        raise HTTPException(
            status_code=422,
            detail="중복되지 않은 카테고리를 입력해주세요.",
        )

    return parsed


# 데코레이터는 이 함수를 GET /health 요청과 연결합니다.
# response_model은 반환 데이터를 검증하고 Swagger 응답 예시를 만듭니다.
@app.get("/health", response_model=HealthResponse)
def health_check() -> HealthResponse:
    """서버와 모델 로딩 상태를 확인합니다."""
    service = get_document_service()

    return HealthResponse(
        status="ok",
        device=get_device_name(),
        loaded_models=service.loaded_models(),
    )


@app.post(
    "/documents/classify",
    response_model=ClassificationResponse,
    summary="PDF 문서 분류",
)
async def classify_document(
    # File과 Form을 함께 사용하면 multipart/form-data 요청을 받습니다.
    file: UploadFile = File(
        ...,
        description="분류할 텍스트 PDF 파일",
    ),
    categories: str = Form(
        ...,
        description=(
            "쉼표로 구분한 후보 카테고리. "
            "예: 인공지능 교육,여행 안내,결제 문의,스포츠 뉴스"
        ),
    ),
) -> ClassificationResponse:
    # 요청 처리 순서: 입력 정리 → PDF 텍스트 추출 → 모델 분석 → 응답 생성
    filename = file.filename or "unknown.pdf"
    category_list = parse_categories(categories)
    text = await extract_text_from_pdf(file)

    try:
        result = get_document_service().classify(
            text=text,
            categories=category_list,
        )

        # Pydantic 응답 모델로 반환 자료형을 검증합니다.
        return ClassificationResponse(
            filename=filename,
            extracted_text_length=len(text),
            predictions=result["predictions"],
        )
    # 이미 상태 코드가 지정된 HTTPException은 500으로 바꾸지 않습니다.
    except HTTPException:
        raise
    except Exception as error:
        raise HTTPException(
            status_code=500,
            detail="문서 분류 중 오류가 발생했습니다.",
        ) from error


@app.post(
    "/documents/entities",
    response_model=NerResponse,
    summary="PDF 개체명 추출",
)
async def extract_entities(
    file: UploadFile = File(
        ...,
        description="개체명을 추출할 텍스트 PDF 파일",
    ),
) -> NerResponse:
    # UploadFile은 큰 파일을 효율적으로 다룰 수 있는 FastAPI 업로드 객체입니다.
    filename = file.filename or "unknown.pdf"
    text = await extract_text_from_pdf(file)

    try:
        result = get_document_service().extract_entities(text)

        return NerResponse(
            filename=filename,
            extracted_text_length=len(text),
            entities=result["entities"],
        )
    except HTTPException:
        raise
    except Exception as error:
        raise HTTPException(
            status_code=500,
            detail="개체명 추출 중 오류가 발생했습니다.",
        ) from error


@app.post(
    "/documents/summarize",
    response_model=SummarizationResponse,
    summary="PDF 문서 요약",
)
async def summarize_document(
    file: UploadFile = File(
        ...,
        description="요약할 텍스트 PDF 파일",
    ),
    max_length: int = Form(
        default=100,
        ge=30,
        le=300,
        description="요약문의 최대 토큰 길이",
    ),
    min_length: int = Form(
        default=20,
        ge=5,
        le=150,
        description="요약문의 최소 토큰 길이",
    ),
) -> SummarizationResponse:
    # Pydantic의 ge/le는 각 값의 범위를 검사하고, 이 조건은 두 값의 관계를 검사합니다.
    if min_length >= max_length:
        raise HTTPException(
            status_code=422,
            detail="min_length는 max_length보다 작아야 합니다.",
        )

    filename = file.filename or "unknown.pdf"
    text = await extract_text_from_pdf(file)

    try:
        result = get_document_service().summarize(
            text=text,
            max_length=max_length,
            min_length=min_length,
        )

        return SummarizationResponse(
            filename=filename,
            # **는 result 딕셔너리의 키와 값을 함수의 키워드 인자로 펼칩니다.
            **result,
        )
    except HTTPException:
        raise
    except Exception as error:
        raise HTTPException(
            status_code=500,
            detail="문서 요약 중 오류가 발생했습니다.",
        ) from error