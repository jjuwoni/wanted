# FastAPI 에서 req, res 를 명확하게 정의한다.
# Pydantic Schema 를 활용해서 Spring 에서의 DTO + Valid 를 적용

from fastapi import FastAPI
# BaseModel 은 DTO 역할을 하는 객체라고 생각하면 된다.
from pydantic import BaseModel, Field

app = FastAPI()

# /chat 요청 시에 Request Body 에 {"question" : "질문"} 담겨서 온다.
# {"question" : "질문"} 하나의 클래스 객체로 만들 것이다.
# Pydantic 객체로 만들 때 클래스 생성 시 BaseModel 을 넣어준다.
class ChatRequest(BaseModel):
    question : str

class ChatResponse(BaseModel):
    question : str
    answer : str
    model : str
    used_token : int

# 응답 시 활용할 클래스는 endPoint 두 번째 인자에 작성한다.
@app.post("/chat", response_model=ChatResponse)
def chatbot(request : ChatRequest):

    return ChatResponse(
        question=request.question,
        answer=f"{request.question} 에 대한 답변!!!",
        model="gemini-flash",
        used_token=100000
    )