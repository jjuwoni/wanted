# .env 를 로컬파일에서 읽기 위한 os import
import os

# .env 환경 변수 읽어들이기
from dotenv import load_dotenv
# google 생태계 라이브러리 중 gemini 활용
from google import genai
# types : gemini 호출 시
from google.genai import types

# 현재 폴더의 .env 를 읽을 준비
load_dotenv()

# api_key
# api 없거나, 만료 되었을 때 Exception 처리가 필수적이다.
api_key = os.getenv("GOOGLE_API_KEY")

# google ai 객체 생성
# 해당 객체를 통해서 모든 요청을 보내게 된다.
client = genai.Client(api_key=api_key)

# flash : 빠르고 저렴하다.
# pro : 더 똑똑하지만 flash 보다 느리고 호출 비용이 더 비싸다.
MODEL = "gemini-3.5-flash"

# 기본 호출
# print("=======기본 호출=======")
# # generate_content() : 해당 함수는 응답이 완성될 때까지
# # 기다린 뒤 하나의 응답 객체를 돌려주게 된다.
# response = client.models.generate_content(
#     model=MODEL,
#     contents="RAG(검색 증강 생성)을 백엔드 개발자들에게 두 문장으로 설명해줘."
# )

# # print(response)
# print(response.text)
# print("=======기본 호출=======")

# print("=======시스템 프롬프트 호출=======")

# response = client.models.generate_content(
#     model=MODEL,
#     contents="출석 인정 기준이 궁금해!!!!!",
#     # 응답 시 추가 설정
#     config=types.GenerateContentConfig(
#         # 시스템 프롬프트 : 응답 시 value 를 참고해서
#         # 대답할 수 있도록 한다.
#         system_instruction=(
#             "너는 '원티드 AI 백엔드 개발자 과정 학습 도우미야.'"
#             "정중한 존댓말을 써주어야 하며, 답변은 3문장 이내로 간결히 해줘."
#         ),
#         # 출력의 무작위성(0 과 가까우면 일관적이며 높을 수록 창의적이고 다양하다.)
#         temperature=0.2
#     )
# )

# print(response.text)

# print("=======시스템 프롬프트 호출=======")


print("=======스트리밍 호출=======")

stream = client.models.generate_content_stream(
    model=MODEL,
    contents="아침에 안 힘들게 일어나는 방법 3가지를 알려줘!"
)

for chunk in stream:
    # end="", flush=True : 줄바꿈 없이 즉시 이어서 출력하는
    # print() 함수 기능
    print(chunk.text or "" , end="", flush=True)
print()

print("=======스트리밍 호출=======")