"""
RAG 를 02번 파일에서 순수한 코드로 작성했다.
다만 순수한 코드의 단점은 LLM 이 Gemini -> GPT 로 바뀐다면
기존 검색함수, 증강함수, 생성함수 등을 모두 변경해야 하는 일이 생기게 된다.

LangChain 을 도입하게 되면 '표준 인터페이스' 로 규격화 하여
언제든지 갈아끼울 수 있는 상태로 바꿀 수 있게 된다.

ex) JDBC --> JPA        -> RDBMS
         --> MyBatis

"""

from dotenv import load_dotenv
from langchain_chroma import Chroma
from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.runnables import RunnablePassthrough
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_huggingface import HuggingFaceEmbeddings

load_dotenv()

MODEL_NAME = "jhgan/ko-sroberta-multitask"
CHROMA_PATH = "./chroma_db"
DOC_COLLECTION = "lms_regulation"

#======================================================
# RAG 1. 검색

embeddings = HuggingFaceEmbeddings(model_name = MODEL_NAME)
vectorstore = Chroma(
    collection_name=DOC_COLLECTION,
    persist_directory=CHROMA_PATH,
    embedding_function=embeddings
)

# as_retriever() : "질문 문자열 -> 관련 된  Document 리스트" 변환
# search_kwargs : "k" == top_k 와 동일 == 가장 유사도가 높은 3개 반환
retriever = vectorstore.as_retriever(search_kwargs={"k" : 3})

def format_docs(docs) -> str :
    return "\n\n".join(
        f"[자료 {i+1}] (학사규정 {d.metadata['page']}페이지)\n{d.page_content}"
        for i, d in enumerate(docs)
    )
#======================================================

#======================================================
# RAG 2. 증강
# f-string 대신 템플릿 객체를 활용한다.
prompt = ChatPromptTemplate.from_messages([
    # 해당 리스트의 각 요소는 '(역할, 내용) 형태의 2개짜리 튜플이다.'
    # system : 공통 규칙 / human : 사용자가 보낼 질문
    ("system",
     "당신은 LMS 학습 도우미입니다.\n"
     "아래 '참고 자료'에 있는 내용만을 근거로 질문에 답하세요.\n"
     "규칙:\n"
     "- 참고 자료에 없는 내용은 절대 지어내지 말 것\n"
     "- 참고 자료로 답할 수 없으면 \"규정에서 관련 내용을 찾지 못했습니다\"라고 답할 것\n"
     "- 답변 끝에 (출처: 학사규정 p.N) 형식으로 근거 페이지를 표기할 것\n\n"
     "[참고 자료]\n{context}"),
    ("human", "{question}")
])

#======================================================

#======================================================
# RAG 3. 생성
# 어떤 LLM을 사용할지 정의, 어떻게 출력할지 정의
llm = ChatGoogleGenerativeAI(model="gemini-3.5-flash", temperature=0.2)
parser =  StrOutputParser() # 응답 객체에서 .text 부분만 출력해준다.
#======================================================

#======================================================
# 마지막 단계 . 조립
rag_chain = (
    # Python 에서 | (or) 는 연산자이지만, LangChain 객체는 이 연산자를
    # 앞 단계의 출력을 다음 단계로 전달하는 의미로 정의해서 사용한다.
    # RunnablePassthrough() : 입력 받은 값을 변경하지 않고, 그대로 다음 단계로 넘기는
    # LangChain 의 부품
    # format_docs 는 함수인데 () 를 붙이지 않았다. 이는 체인이 나중에 호출할 수 있도록
    # 함수의 실행이 아닌 참조만 전달힌다 라고 보면 된다.
    {"context" : retriever | format_docs , "question" : RunnablePassthrough()} 
    | prompt | llm | parser
)
#======================================================
#======================================================
# Main 흐름

if __name__ == "__main__":
    question = "동영상 강의 출석 인정 기준이 뭐야?"
    print(f"질문: {question}")
    print("-" * 60)
    # invoke() : 체인 전체를 한 번 실행!!
    # stream() : 스트리밍 형태로 실행
    print(rag_chain.invoke(question))

#======================================================