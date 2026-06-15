package com.example.hellobatch;

import java.util.stream.IntStream;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

/**
 * Hello Job — 1~10 을 읽어 2배로 만든 뒤 콘솔에 출력하는 가장 단순한 청크 기반 잡.
 * 강의 Ch3 "실습 — Hello Job 만들기" 와 동일한 코드.
 */
@Configuration
@RequiredArgsConstructor
public class HelloJobConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	@Bean
	public Job helloJob(Step helloStep) {
		return new JobBuilder("helloJob", jobRepository)
				.start(helloStep)
				.build();
	}

	@Bean
	public Step helloStep() {
		return new StepBuilder("helloStep", jobRepository)
				.<Integer, Integer>chunk(10, transactionManager)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.build();
	}

	@Bean
	public ItemReader<Integer> reader() {
		return new IteratorItemReader<>(
				IntStream.rangeClosed(1, 10).boxed().toList());
	}

	@Bean
	public ItemProcessor<Integer, Integer> processor() {
		return n -> n * 2;
	}

	@Bean
	public ItemWriter<Integer> writer() {
		return chunk -> chunk.getItems().forEach(System.out::println);
	}
}
