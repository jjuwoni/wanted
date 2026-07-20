# =====================================================================
# routers/chat.py — RAG 챗봇 엔드포인트 (Spring의 @RestController 대응)
# ---------------------------------------------------------------------
# 라우터의 규칙: '얇게' 유지하라.
#   - 하는 일: HTTP ↔ 스키마 변환, 서비스 호출. 끝.
#   - 하지 않는 일: 비즈니스 로직, try/except (전역 핸들러가 담당),
#     외부 시스템 호출. — 컨트롤러에 로직을 넣지 말라던 Spring 수업의
#     그 원칙이 여기서도 그대로다.
# =====================================================================
from fastapi import APIRouter

from app.schemas.chat import ChatRequest, ChatResponse
from app.services import chat_service

router = APIRouter(tags=["chat"])


@router.post("/chat", response_model=ChatResponse)
def chat(req: ChatRequest) -> ChatResponse:
    answer, used_tools = chat_service.answer(req.question, req.student_id, req.history)
    return ChatResponse(answer=answer, used_tools=used_tools)
