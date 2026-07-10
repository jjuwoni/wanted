from app.schemas.chat_schema import ChatResponse


class AIService:
    def chat(self, question : str) -> ChatResponse :
        # 클래스 내부의 Helper 함수 호출
        # self 는 AIService 객체를 의미한다 == Java this
        answer = self.create_answer(question)

        return ChatResponse(
            question=question,
            answer=answer,
            model="gemini-flash",
            used_token=10000
        )


    def create_answer(self, question : str) -> str :
        return f"{question} 에 대한 AI 답변 생성 호출!!"