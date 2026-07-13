import torch

# 기능별 Hugging Face Hub 모델 ID입니다.
# Zero-shot 분류는 별도의 재학습 없이 사용자가 제시한 후보 중 하나를 고릅니다.
ZERO_SHOT_MODEL_ID = "MoritzLaurer/mDeBERTa-v3-base-mnli-xnli"
# NER(Named Entity Recognition)은 사람, 기관, 장소 등의 개체명을 찾습니다.
NER_MODEL_ID = "Leo97/KoELECTRA-small-v3-modu-ner"
# 긴 한국어 문장을 짧은 문장으로 생성하는 요약 모델입니다.
SUMMARIZATION_MODEL_ID = "eenzeenee/t5-small-korean-summarization"

def get_pipeline_device() -> int:
    """Transformers Pipeline 장치를 반환합니다.

    Returns:
        첫 번째 CUDA GPU는 0, CPU는 -1입니다.
    """
    return 0 if torch.cuda.is_available() else -1


def get_device_name() -> str:
    """현재 추론 장치의 이름을 반환합니다."""
    # 상태 확인 API처럼 사람이 읽을 문자열이 필요할 때 사용합니다.
    return "cuda" if torch.cuda.is_available() else "cpu"