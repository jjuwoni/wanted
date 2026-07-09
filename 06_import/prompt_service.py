class PromptService:
    # 해당 클래스는 질문 유형에 따라
    # 미리 Prompt 템플릿을 세팅해두고 사용하기 위한 클래스

    # 해당 함수는 AI 튜터 기능 관련 프롬프트를 반환하는 함수이다.
    def create_tutor_prompt(self, question : str) -> str:
        return f""" 당신은 친절한 AI 튜터입니다.
        
        아래 질문에 우리 프로젝트를 처음 활용하는 초보자들도 이해할 수 있도록 답변하세요.
        
        질문 :
        {question}
        """