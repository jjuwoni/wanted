from fastapi import FastAPI

app = FastAPI()

# 여러 자료형을 반환하는 API
# QueryStringParameter : /hello?num1=2&num2
# PathVariable : /hello/1
# RequestBody

# Step 01 . Query Parameter 전달 받기
# url 뒤에 ?key=value 형태로 전달되는 값을 의미한다.
# /hello-query?name=donkey
@app.get("/hello-query")
def hello_query(name : str):
    return {
        "message" : f"Hello~~~~~~~~ {name} 님!"
    }

# Step 02 . Path Variable 전달 받기
# /course/1
# /course/{courseId}
@app.get("/courses/{course_id}")
def find_by_course_id(course_id : int):
    return {
        "course_id" : course_id,
        "title" : f"{course_id} 번 강의 제목!"
    }

# Step 03 . 여러 값을 전달할 때는 POST , RequestBody 를 활용한다.
@app.post("/chat")
def chatbot(request : dict):
    # dictionary 에 있는 특정 값을 꺼내는 방법?!
    # request["key"]
    question = request["question"]

    return {
        "question" : f"당신의 질문 : {question}",
        "answer" : f"{question}에 대한 답변"
    }

# 지금 방식의 요청/응답은 requestDTO, responseDTO 가 없기 때문에
# 유지보수성이 좋지 않다.
# Pydantic Schema
# Java 에서의 req, res 객체라고 생각하면 된다.
# 자동 Valid 기능이 추가되어 있다.