"""
[목표]
1. 문장이 숫자 벡터로 바뀌는 과정과 Vector  라는 공간에 대한 이해
2. Vector 공간에 저장 된 데이터 간의 유사도인 "코사인 유사도" 이해
3. 기존 SQL 구문에서는 LIKE 로 단어 기반의 검색을 했다면 유사도에 따른 의미 기반의 검색 이해
"""

# numpy 는 여러 개의 숫자를 배열로 다루고, 해당 숫자들을 빠르게 계산할 수 있는
# 파이썬 라이브러리 이다.
# 임베딩의 결과는 수백 개의 숫자로 이루어진 배열 형태이다.
# 따라서 numpy 를 활용해서 임베딩 결과를 저장하고 계산하는 것에 사용을 할 것이다.
import numpy as np
# HuggingFace 에 있는 임베딩 Model 을 활용한다.
from sentence_transformers import SentenceTransformer


# 한국어 특화 문장 임베딩 모델
# 영어 위주의 모델은 Java == 자바 의 관계를 잘 이해하지 못한다.
MODEL_NAME = "jhgan/ko-sroberta-multitask"

print(f"임베딩 모델 로딩 중... ({MODEL_NAME})")
model = SentenceTransformer(MODEL_NAME)

# Step 2. 문장 -> 벡터로 치환
sentence = "스프링 부트로 REST API 서버 만들기"
# encode() 메서드를 호출하면 문장이 벡터가 된다.
vector = model.encode(sentence)

print("\n=====문장을 벡터로 변환=====")
print(f"입력한 문장 : {sentence}")
print(f"벡터 차원 : {vector.shape}")

# Step 3. 코사인 유사도 -> 의미가 비슷한 정도를 숫자로
# ex) 스프링 == Spring -> 0.99094   1과 가까우면 가까울 수록 의미가 비슷하다.

def cosine_similarity(a: np.ndarray, b: np.ndarray) -> float:
    """두 벡터 사이의 코사인 유사도를 계산한다.

    a: np.ndarray와 b: np.ndarray는 "두 매개변수에 NumPy 배열을 받는다"는
    타입 힌트다. 실행 결과를 바꾸지는 않지만, 어떤 값을 넣어야 하는지 알려준다.

    - 두 벡터가 이루는 '각도'를 본다. (거리가 아니라 방향!)
    - 1에 가까울수록 의미가 비슷, 0에 가까울수록 무관.
    - 공식: (A · B) / (|A| × |B|)
      → 내적을 각 벡터의 길이로 나눠서 '길이 영향'을 제거한 것.
    """
    # np.dot(a, b): 두 벡터의 같은 위치에 있는 숫자끼리 곱한 뒤 모두 더한다(내적).
    # np.linalg.norm(a): 벡터 a의 길이를 구한다. b도 같은 방식으로 길이를 구한다.
    # 따라서 아래 코드는 위 코사인 유사도 공식을 NumPy 연산으로 그대로 옮긴 것이다.
    # NumPy를 사용하면 768개 숫자를 직접 반복문으로 계산할 필요가 없다.
    return float(np.dot(a, b) / (np.linalg.norm(a) * np.linalg.norm(b)))


# 실험 대상: LIKE '%자바%' 검색이라면 어떤 것을 찾고 어떤 것을 놓칠까?
target = "자바 프로그래밍 배우고 싶어요"

candidates = [
    "Java 기초 문법 강의",          # '자바'라는 글자가 없다 → LIKE는 놓친다
    "스프링 부트 백엔드 입문",       # 글자는 다르지만 의미상 관련이 있다
    "자바스크립트 웹 개발",          # '자바'가 들어있다 → LIKE는 잘못 찾는다!
    "맛있는 김치찌개 끓이는 법",      # 완전히 무관한 문장
]

# 여러 문장을 한 번에 인코딩하는 게 하나씩 하는 것보다 훨씬 빠르다.
target_vec = model.encode(target)
# candidates의 문장마다 하나의 벡터가 만들어지므로, candidate_vecs는
# 여러 벡터가 행(row)으로 모인 2차원 NumPy 배열이 된다.
candidate_vecs = model.encode(candidates)

print(f"\n=== 기준 문장: \"{target}\" ===")
results = []
for text, vec in zip(candidates, candidate_vecs):
    score = cosine_similarity(target_vec, vec)
    results.append((score, text))

# 유사도가 높은 순으로 정렬해서 출력 → 이것이 곧 '의미 기반 검색'이다.
for score, text in sorted(results, reverse=True):
    bar = "█" * int(score * 30)          # 유사도를 막대로 시각화
    print(f"  {score:.4f} {bar} {text}")