# spring-batch-tutorial

Spring Batch 입문 강의 **Ch3 실습용** 예제 — `Hello Job`.

1~10 을 읽어 → 각 값을 2배로 변환 → 콘솔에 출력하는, 가장 단순한 청크(chunk) 기반 배치 잡입니다.
실행하면 H2 인메모리 DB 에 `BATCH_*` 메타 테이블이 자동 생성됩니다 (Ch4 주제).

## 요구사항

- JDK 21
- Gradle Wrapper 포함 (`./gradlew` — 별도 설치 불필요)

## 실행

```bash
./gradlew bootRun
```

콘솔에 다음이 출력됩니다:

```
2
4
6
8
10
12
14
16
18
20
```

## 구조

| 파일 | 역할 |
|---|---|
| `HelloBatchApplication` | 진입점 — `@SpringBootApplication` 하나면 끝 (Batch 5 부터 `@EnableBatchProcessing` 불필요) |
| `HelloJobConfig` | `Job` + `Step` + `Reader / Processor / Writer` 등록 |
| `application.yml` | H2 인메모리 + 메타 테이블 자동 초기화 |

### HelloJobConfig 의 R-P-W

- **Reader** — `IteratorItemReader` 로 1~10
- **Processor** — `n -> n * 2`
- **Writer** — `System.out.println`
- **chunk(10)** — 10건 단위로 트랜잭션 커밋

## 메타 테이블 확인

실행 후 `http://localhost:8080/h2-console` 접속 (JDBC URL: `jdbc:h2:mem:hellobatch`) →
`BATCH_JOB_INSTANCE`, `BATCH_JOB_EXECUTION`, `BATCH_STEP_EXECUTION` 등을 직접 조회해 보세요.
