# FastAPI 에서 req, res 를 명확하게 정의한다.
# Pydantic Schema 를 활용해서 Spring 에서의 DTO + Valid 를 적용

from fastapi import FastAPI
# BaseModel 은 DTO 역할을 하는 객체라고 생각하면 된다.
from pydantic import BaseModel, Field

app = FastAPI()

# /chat 요청 시에 Request Body 에 {"question" : "질문"} 담겨서 온다.
# {"question" : "질문"} 하나의 클래스 객체로 만들 것이다.
# Pydantic 객체로 만들 때 클래스 생성 시 BaseModel 을 넣어준다.

# Pydantic 은 타입 검증을 자동으로 수행하게 된다.
# 만약 Type-Hint 를 int 로 두었는데 정수로 변환될 수 없는 값 (ex = 안녕)
# 이 넘어오게 된다면 자동으로 검증 오류를 발생시킨다.
# 추가적으로 Field 를 활용하게 되면 더 구체적인 조건을 지정할 수 있다.
class ChatRequest(BaseModel):
    # ge : greater equals 크거나 같은 (~~ 이상), le : less equals 작거나 같은 (~~이하)
    # el 표기법
    question : str = Field(min_length=1, max_length=10, description="사용자의 질문")

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