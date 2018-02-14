package com.vinodh.springbatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.vinodh.springbatch.model.Person;

//@Configuration
//@EnableBatchProcessing
@Import(DataSourceAutoConfiguration.class)
public class SampleBatchConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Step step1(ItemReader<Person> itemReader, ItemProcessor<Person, Person> itemProcessor,
			ItemWriter<Person> itemWriter) {

		return stepBuilderFactory.get("step1").<Person, Person>chunk(10).reader(itemReader).processor(itemProcessor)
				.writer(itemWriter).build();
	}

	@Bean
	public Step step2(Tasklet tasklet) {
		return stepBuilderFactory.get("step2").tasklet(tasklet).build();
	}

	@Bean
	public Job job(@Qualifier("step1") Step step1, @Qualifier("step2") Step step2) {

		return jobBuilderFactory.get("myJob").start(step1).next(step2).build();
	}

}
