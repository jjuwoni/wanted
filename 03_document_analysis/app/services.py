# Hugging Face 모델을 활용한 PDF 문서 분석 Service
# Service 계층에서 Model 을 활용한 비즈니스 로직이 수행된다.

from transformers import Pipeline, pipeline
from app.models import (
    NER_MODEL_ID,
    SUMMARIZATION_MODEL_ID,
    ZERO_SHOT_MODEL_ID,
    get_pipeline_device
)
# 여러 요청이 동시에 들어올 때 같은 모델을 중복 로딩하지 않게
# 잠글 수 있는 Lock
from threading import Lock
from typing import Any

# 문서 분석 관련 Service 클래스
class DocumentAnalysisService:
    """
    문서 분류, 개체 추출, 요약 기능을 제공하는 클래스.
    모델 3개를 서버 시작 시 동시에 적재하는 것이 아닌
    각 기능이 처음 호출 되는 시점에 1번만 로딩한다.
    """
    def __init__(self) -> None:
        # None은 아직 모델이 메모리에 로딩되지 않았다는 뜻입니다.
        self._classifier: Pipeline | None = None
        self._ner: Pipeline | None = None
        self._summarizer: Pipeline | None = None

        # 여러 요청이 동시에 들어와 같은 모델을 중복 로딩하지 않도록 잠금을 둡니다.
        self._ner_lock = Lock()
        self._classifier_lock = Lock()
        self._summarizer_lock = Lock()

    def _get_classifier(self) -> Pipeline:
        """분류 모델을 처음 사용할 때만 생성하고 이후에는 재사용합니다."""
        # 바깥쪽 검사는 이미 로딩된 경우 Lock 비용을 피하기 위한 것입니다.
        if self._classifier is None:
            with self._classifier_lock:
                # Lock을 기다리는 동안 다른 요청이 로딩했을 수 있어 다시 검사합니다.
                if self._classifier is None:
                    self._classifier = pipeline(
                        task="zero-shot-classification",
                        model=ZERO_SHOT_MODEL_ID,
                        device=get_pipeline_device(),
                    )
        return self._classifier

    def _get_ner(self) -> Pipeline:
        """개체명 추출 모델을 지연 로딩하여 반환합니다."""
        if self._ner is None:
            with self._ner_lock:
                if self._ner is None:
                    self._ner = pipeline(
                        task="ner",
                        model=NER_MODEL_ID,
                        # 나뉜 서브워드 토큰을 하나의 개체명으로 다시 묶습니다.
                        aggregation_strategy="simple",
                        device=get_pipeline_device(),
                    )
        return self._ner

    def _get_summarizer(self) -> Pipeline:
        """요약 모델을 지연 로딩하여 반환합니다."""
        if self._summarizer is None:
            with self._summarizer_lock:
                if self._summarizer is None:
                    self._summarizer = pipeline(
                        task="summarization",
                        model=SUMMARIZATION_MODEL_ID,
                        device=get_pipeline_device(),
                    )
        return self._summarizer

    def classify(
        self,
        text: str,
        categories: list[str],
    ) -> dict[str, Any]:
        """PDF 텍스트를 후보 카테고리 중 하나로 분류합니다."""
        # Pipeline 객체는 함수처럼 호출할 수 있습니다.
        result = self._get_classifier()(
            text,
            candidate_labels=categories,
            # 단일 분류이므로 전체 후보 점수의 합이 1에 가깝게 계산됩니다.
            multi_label=False,
            # 모델의 최대 입력 길이를 넘는 뒷부분은 잘라냅니다.
            truncation=True,
        )

        # labels와 scores의 같은 위치 값을 zip()으로 묶어 응답 형태로 바꿉니다.
        predictions = [
            {
                "category": str(label),
                "score": round(float(score), 4),
            }
            for label, score in zip(
                result["labels"],
                result["scores"],
            )
        ]

        return {"predictions": predictions}

    def extract_entities(self, text: str) -> dict[str, Any]:
        """PDF 텍스트에서 개체명을 추출합니다."""
        result = self._get_ner()(text)

        # NumPy 자료형 등이 포함된 모델 결과를 JSON 직렬화 가능한 기본형으로 바꿉니다.
        entities = [
            {
                "text": str(item["word"]).replace(" ", ""),
                "entity": str(item["entity_group"]),
                "score": round(float(item["score"]), 4),
                "start": int(item["start"]),
                "end": int(item["end"]),
            }
            for item in result
        ]

        return {"entities": entities}

    def summarize(
        self,
        text: str,
        max_length: int,
        min_length: int,
    ) -> dict[str, Any]:
        """PDF 텍스트를 짧게 요약합니다."""
        result = self._get_summarizer()(
            text,
            max_length=max_length,
            min_length=min_length,
            # 샘플링을 끄면 같은 입력에서 더 일관된 요약을 얻을 수 있습니다.
            do_sample=False,
            truncation=True,
        )

        # Pipeline은 입력 하나에도 리스트를 반환하므로 [0]으로 첫 결과를 꺼냅니다.
        summary = str(result[0]["summary_text"]).strip()

        return {
            "original_length": len(text),
            "summary_length": len(summary),
            "summary": summary,
        }

    def loaded_models(self) -> dict[str, bool]:
        """현재 메모리에 적재된 모델 상태를 반환합니다."""
        return {
            "classification": self._classifier is not None,
            "ner": self._ner is not None,
            "summarization": self._summarizer is not None,
        }
