# k6 Result - 00-smoke-check

## Summary

| Metric | Value |
| --- | ---: |
| http_reqs | 9 |
| iterations | 3 |
| checks success rate | 100.00% |
| http_req_failed | 0.00% |
| data_received bytes | 3258 |
| data_sent bytes | 1005 |

## Duration Metrics

| Metric | avg(ms) | min(ms) | med(ms) | p90(ms) | p95(ms) | p99(ms) | max(ms) |
| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| http_req_duration | 176.60 | 2.73 | 3.51 | 525.74 | 525.76 | 525.77 | 525.77 |
| http_req_waiting | 176.03 | 1.73 | 3.51 | 525.07 | 525.41 | 525.67 | 525.74 |
| http_req_blocked | 1.12 | 0 | 0 | 2.01 | 6.04 | 9.26 | 10.07 |
| http_req_connecting | 0.06 | 0 | 0 | 0.10 | 0.30 | 0.47 | 0.51 |

## Metric Meaning

| Value | Meaning |
| --- | --- |
| avg | 전체 요청 시간의 산술 평균입니다. outlier의 영향을 받을 수 있습니다. |
| min | 가장 빠른 요청 시간입니다. 정상 동작의 하한선을 볼 때 사용합니다. |
| med | 중앙값입니다. 요청의 절반은 이 값보다 빠르고 절반은 느립니다. |
| p90 | 90% 요청이 이 값 이하로 완료됩니다. |
| p95 | 95% 요청이 이 값 이하로 완료됩니다. 수업의 주요 합격 기준입니다. |
| p99 | 99% 요청이 이 값 이하로 완료됩니다. tail latency 관찰에 사용합니다. |
| max | 가장 느린 요청 시간입니다. 단일 outlier 여부를 확인할 때 사용합니다. |

## Thresholds

| Threshold | Result |
| --- | --- |
| product detail: status is 200 | 3 pass / 0 fail |
| popular: status is 200 | 3 pass / 0 fail |
| popular: response is array | 3 pass / 0 fail |
| order: status is 201 or business failure 400 | 3 pass / 0 fail |

## How To Compare

| Compare Point | What To Look For |
| --- | --- |
| p95 | 사용자 대부분이 체감하는 지연 시간 악화 여부 |
| http_req_failed | 4xx/5xx 또는 check 실패 증가 여부 |
| http_req_waiting | 서버 처리나 DB 처리 지연 가능성 |
| Prometheus | 서버 내부 HTTP/custom metric 추세 |
| Loki | 느린 요청의 traceId와 event 로그 |

