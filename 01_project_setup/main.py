# pip install 로 설치한 FastAPI Import
from fastapi import FastAPI

# app 변수에 FastAPI 객체 할당
app = FastAPI()

# GET("/")
@app.get("/")
def health_check():
    return {
        "status" : "OK",
        "code" : 200,
        "message" : "FastAPI Server HI!"
    }