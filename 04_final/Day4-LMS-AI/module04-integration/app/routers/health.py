# =====================================================================
# routers/health.py — 상태 확인 (Spring이 AI 서버 생존 확인용으로 호출)
# =====================================================================
from fastapi import APIRouter

from app.repositories.vector_store import vector_store

router = APIRouter(tags=["ops"])


@router.get("/health")
def health():
    return {"status": "ok", **vector_store.counts()}
