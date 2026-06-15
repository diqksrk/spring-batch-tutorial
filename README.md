# spring-batch-tutorial

Spring Batch 입문 강의 **Ch3 실습용** 예제 — `Hello Job`.

1~10 을 읽어 → 각 값을 2배로 변환 → 콘솔에 출력하는, 가장 단순한 청크(chunk) 기반 배치 잡입니다.
실행하면 H2 인메모리 DB 에 `BATCH_*` 메타 테이블이 자동 생성됩니다 (Ch4 주제).

## 요구사항

- JDK 21
- Gradle Wrapper 포함 (`./gradlew` — 별도 설치 불필요)

## 실행

기본 잡은 `helloJob` 입니다.

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

## 실패 → 재시작 데모 (`failRestartJob`) — Ch4

메타 테이블 덕분에 **실패한 잡을 재시작하면 성공한 step 은 건너뛰고 실패 지점부터 다시** 돈다는 것을 보여주는 예제입니다.
H2 를 파일 기반(`build/h2db/`)으로 두어 실행 사이에 메타 테이블이 보존됩니다.

```bash
# 1차 — step1 성공, step2 실패 → Job FAILED
./gradlew bootRun --args='--job.name=failRestartJob'

# 2차 — 원인을 "고쳤다"고 가정하고 같은 잡 재시작
#        step1 은 COMPLETED 라 건너뛰고, step2 부터 다시 실행 → Job COMPLETED
./gradlew bootRun --args='--job.name=failRestartJob --fix=true'
```

2차 실행 로그에서 핵심 한 줄:

```
Step already complete or not restartable, so no action to execute: ... name=step1, status=COMPLETED
Executing step: [step2]
>>> step2 성공 — 재시작이 step1 을 건너뛰고 여기부터 다시 돌았습니다.
... status: [COMPLETED]
```

> 같은 `JobInstance` 로 재시작되어야 하므로 `--fix` 는 **잡 파라미터가 아닌 일반 프로퍼티**로 넘깁니다(식별 파라미터가 바뀌면 새 인스턴스가 되어 step1 부터 다시 돕니다).
> 처음부터 새로 보려면 `./gradlew clean` 으로 `build/` 의 H2 DB 를 비우세요.

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
