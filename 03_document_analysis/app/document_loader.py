"""업로드된 PDF 파일의 유효성을 검사하고 텍스트를 추출합니다."""

from io import BytesIO

import fitz
from fastapi import HTTPException, UploadFile


# 1KB = 1024byte이므로 아래 값은 10MB입니다.
MAX_FILE_SIZE = 10 * 1024 * 1024
# 추출된 글자가 너무 적으면 분석할 수 없는 PDF로 판단합니다.
MIN_TEXT_LENGTH = 10

# sync 동기 / 순서대로 움직임
# a == not 비동기 / 별도의 Stack 에서 동작함.
async def extract_text_from_pdf(file: UploadFile) -> str:
    """업로드된 텍스트 PDF에서 전체 페이지 텍스트를 추출합니다."""
    # 파일명이 None일 수도 있으므로 빈 문자열을 기본값으로 사용합니다.
    filename = file.filename or ""

    # MIME 타입과 확장자를 모두 검사해 PDF가 아닌 파일을 일찍 차단합니다.
    if file.content_type != "application/pdf":
        raise HTTPException(
            status_code=400,
            detail="PDF 파일만 업로드할 수 있습니다.",
        )

    if filename and not filename.lower().endswith(".pdf"):
        raise HTTPException(
            status_code=400,
            detail="확장자가 .pdf인 파일만 업로드할 수 있습니다.",
        )

    # UploadFile.read()는 비동기 함수이므로 await로 파일 내용을 기다립니다.
    # 이후 검증과 PyMuPDF 처리를 위해 전체 파일을 bytes로 읽습니다.
    file_bytes = await file.read()

    try:
        if not file_bytes:
            raise HTTPException(
                status_code=400,
                detail="업로드된 파일이 비어 있습니다.",
            )

        if len(file_bytes) > MAX_FILE_SIZE:
            raise HTTPException(
                status_code=413,
                detail="PDF 파일은 최대 10MB까지 업로드할 수 있습니다.",
            )

        try:
            # BytesIO는 메모리의 bytes를 파일처럼 읽을 수 있게 감쌉니다.
            # fitz.open()에는 디스크 경로 대신 이 메모리 스트림을 전달합니다.
            with fitz.open(
                stream=BytesIO(file_bytes),
                filetype="pdf",
            ) as document:
                if document.page_count == 0:
                    raise HTTPException(
                        status_code=422,
                        detail="페이지가 없는 PDF입니다.",
                    )

                # 각 페이지에서 일반 텍스트를 추출하여 리스트로 만듭니다.
                page_texts = [
                    page.get_text("text").strip()
                    for page in document
                ]
        # 위에서 의도적으로 발생시킨 HTTP 오류는 그대로 클라이언트에 전달합니다.
        except HTTPException:
            raise
        except Exception as error:
            raise HTTPException(
                status_code=400,
                detail="손상되었거나 올바르지 않은 PDF 파일입니다.",
            ) from error

        # 내용이 있는 페이지만 줄바꿈으로 연결해 하나의 문서 문자열을 만듭니다.
        text = "\n".join(
            page_text
            for page_text in page_texts
            if page_text
        ).strip()

        if len(text) < MIN_TEXT_LENGTH:
            raise HTTPException(
                status_code=422,
                detail=(
                    "PDF에서 분석 가능한 텍스트를 찾지 못했습니다. "
                    "이미지로 구성된 스캔 PDF는 OCR 처리가 필요합니다."
                ),
            )

        return text
    finally:
        # 성공하거나 예외가 발생해도 업로드 파일 자원은 항상 닫습니다.
        await file.close()