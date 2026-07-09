from ai_service import AIService
from prompt_service import PromptService


# import 후 인스턴스 생성
prompt_service = PromptService()
ai_service = AIService()

# spring 쪽에서 전달한 사용자의 질문
question = "RAG 가 도대체 무엇인가요???"

# 사용할 프롬프트를 가져오기
prompt = prompt_service.create_tutor_prompt(question)

# 프롬프트를 바탕으로 응답할 준비
response = ai_service.answer(prompt)

print("=" * 50)
print("Prompt")
print("=" * 50)
print(prompt)

print("\n" + "=" * 50)
print("AI Response")
print("=" * 50)
print(response)
