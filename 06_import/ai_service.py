class AIService:

    def __init__(self):
        self.model_name = "tts-ai-model"

    def answer(self, question : str) -> dict :

        # Map<String, Object> 역할을 하는 것이 dictionary
        return {
            "model" : self.model_name,
            "question" : question,
            "answer" : f"{question}에 대한 AI 응답입니다!"
        }