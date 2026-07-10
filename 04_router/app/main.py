from fastapi import FastAPI
from app.api.chat_router import router as chat_router
# as : 별칭을 정한다. router 변수가 겹치기 때문에 각각의 파일에서
# 사용하는 router 에 대한 변수명을 바꿔준다.
# from app.api.rag_router import router as rag_router

# swagger 설정을 할 수 있다.
app = FastAPI(
    title="FastAPI Router Server",
    descrioption="Spring Backend 와 소통하는 AI Server",
    version="1.0.0"
)
   


@app.get("/")
def health_check():
    return { 
        "status" : "ok",
        "code" : 200, 
        "message" : "Very Good!"
    }

app.include_router(chat_router)

