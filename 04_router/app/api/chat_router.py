from fastapi import APIRouter
from app.schemas.chat_schema import ChatRequest, ChatResponse

# @RequestMapping("/api/v1/chat")
# class ~~

# @GetMapping("/{id}") "/api/v1/chat/{id}"
router = APIRouter(
    prefix="/api/v1/chat",
    tags=["Chat-API"]
)

# /api/v1/chat POST 요청을 처리하는 함수
@router.post("" , response_model=ChatResponse)
def chat(request : ChatRequest) :
    return ChatResponse(
        question=request.question,
        answer=f"{request.question} 에 대한 예시 답변!!",
        model="gemini-3.5-flash",
        used_token=10000
    )