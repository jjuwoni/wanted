from fastapi import APIRouter
from app.schemas.chat_schema import ChatRequest, ChatResponse
# Controller 에서 Service 의존성 주입 준비!!!!
from app.service.ai_service import AIService

# @RequestMapping("/api/v1/chat")
# class ~~
# @GetMapping("/{id}") "/api/v1/chat/{id}"
router = APIRouter(
    prefix="/api/v1/chat",
    tags=["Chat-API"]
)

# 서비스 객체 생성
ai_service = AIService()

# /api/v1/chat POST 요청을 처리하는 함수
@router.post("" , response_model=ChatResponse)
def chat(request : ChatRequest) :
    return ai_service.chat(request.question)