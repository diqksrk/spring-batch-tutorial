package com.example.hellobatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

/**
 * 실패 → 재시작 데모 잡.
 *
 * step1 은 항상 성공, step2 는 기본적으로 실패합니다. 같은 잡을 재시작하면 step1 은
 * 메타 테이블에 COMPLETED 로 남아 있으므로 건너뛰고, 실패했던 step2 부터 다시 실행됩니다.
 *
 *   1) ./gradlew bootRun --args='--job.name=failRestartJob'              → step2 실패 (FAILED)
 *   2) ./gradlew bootRun --args='--job.name=failRestartJob --fix=true'   → step1 건너뜀, step2 성공 (COMPLETED)
 */
@Configuration
@RequiredArgsConstructor
public class FailRestartJobConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	@Bean
	public Job failRestartJob(Step step1, Step step2) {
		return new JobBuilder("failRestartJob", jobRepository)
				.start(step1)
				.next(step2)
				.build();
	}

	@Bean
	public Step step1() {
		return new StepBuilder("step1", jobRepository)
				.tasklet((contribution, context) -> {
					System.out.println(">>> step1 성공 — 여기까지는 메타 테이블에 COMPLETED 로 기록됩니다.");
					return RepeatStatus.FINISHED;
				}, transactionManager)
				.build();
	}

	@Bean
	public Step step2(@Value("${fix:false}") boolean fix) {
		return new StepBuilder("step2", jobRepository)
				.tasklet((contribution, context) -> {
					if (!fix) {
						throw new IllegalStateException(
								"step2 실패! — 원인을 고친 뒤 --fix=true 로 같은 잡을 재시작하세요.");
					}
					System.out.println(">>> step2 성공 — 재시작이 step1 을 건너뛰고 여기부터 다시 돌았습니다.");
					return RepeatStatus.FINISHED;
				}, transactionManager)
				.build();
	}
}
