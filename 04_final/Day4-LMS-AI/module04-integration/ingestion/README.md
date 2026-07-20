# ingestion 폴더

이 폴더는 웹 요청을 처리하는 FastAPI 앱과 분리된 **오프라인 색인 배치**입니다.

- `mysql_reader.py`: 읽기 전용 계정으로 강좌 데이터 조회
- `pdf_loader.py`: PDF 로드와 청킹
- `run_ingest.py`: 전체·증분 색인 실행과 상태 저장

실행:

```bash
python -m ingestion.run_ingest
python -m ingestion.run_ingest --incremental
```

첫 실행은 임베딩 모델 다운로드 때문에 오래 걸릴 수 있습니다. 완료 후 `/health`에서 컬렉션 건수를 확인하세요.
