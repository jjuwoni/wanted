# =====================================================================
# schemas/chat.py — /chat 의 요청/응답 DTO (Spring의 dto 패키지 대응)
# API 계약서 3장과 1:1. 계약이 바뀌면 '이 파일과 문서'를 함께 고친다.
# =====================================================================
from pydantic import BaseModel, Field


class ChatMessage(BaseModel):
    role: str = Field(..., pattern="^(user|assistant)$")
    content: str


class ChatRequest(BaseModel):
    question: str = Field(..., min_length=1)
    student_id: int = Field(1, description="로그인 학생 ID (Spring이 채움)")
    history: list[ChatMessage] = Field(default_factory=list)


class ChatResponse(BaseModel):
    answer: str
    used_tools: list[str]
