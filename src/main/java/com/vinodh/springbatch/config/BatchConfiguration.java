package com.vinodh.springbatch.config;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;

import com.vinodh.springbatch.model.Person;
import com.vinodh.springbatch.transform.CleanupTasklet;
import com.vinodh.springbatch.transform.PersonItemProcessor;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Import({DbConfig.class,SpringBatchScheduler.class})
@EnableBatchProcessing
@Slf4j
public class BatchConfiguration {

	@Autowired
	JobBuilderFactory jobBuilderFactory;

	@Autowired
	StepBuilderFactory stepBuilderFactory;

	@Autowired
	DbConfig dbConfig;

	@Autowired
	PersonItemProcessor itemProcessor;
	
	@Autowired
	CleanupTasklet cleanupTasklet;
	
	@Autowired
	JobCompletionNotificationListener listener;
	
	@Autowired
	SimpleJobLauncher jobLauncher;
	
	
	@Scheduled(cron = "0/10 * * * * ?")
	public void perform() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {

		log.info("Job Started at :" + new Date());

		JobParameters param = new JobParametersBuilder().addString("JobID", String.valueOf(System.currentTimeMillis()))
				.toJobParameters();

		JobExecution execution = jobLauncher.run(processJob(), param);

		log.info("Job finished with status :" + execution.getStatus());
	}

	// Item Readers :: Read from the Flat File (sample-data.csv File)
	@Bean
	public FlatFileItemReader<Person> fileItemReader() {

		Resource resource = new ClassPathResource("sample-data.csv");

		LineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.tokenize(","); // Comma is Default one

		DelimitedLineTokenizer delimitedLine = new DelimitedLineTokenizer(",");
		delimitedLine.setNames(new String[] { "firstName", "lastName" });

		BeanWrapperFieldSetMapper<Person> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Person.class);

		DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
		lineMapper.setFieldSetMapper(fieldSetMapper);
		lineMapper.setLineTokenizer(delimitedLine);

		FlatFileItemReader<Person> fileItemReader = new FlatFileItemReader<>();
		fileItemReader.setResource(resource);
		fileItemReader.setLineMapper(lineMapper);
		fileItemReader.open(new ExecutionContext());

		return fileItemReader;
	}

	// Item Writers
	
	@Bean
	public JdbcBatchItemWriter<Person> batchItemWriter(){
		String insertSql = "INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)";
		JdbcBatchItemWriter<Person> batchItemWriter = new JdbcBatchItemWriter<>();
		batchItemWriter.setDataSource(dbConfig.dataSource_memory());
		batchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		batchItemWriter.setSql(insertSql);
		
		return batchItemWriter;
	}

	// Steps
	@Bean
	public Step step1() {
		return stepBuilderFactory
						.get("step1")
						.<Person,Person> chunk(10)
						.reader(fileItemReader())
						.processor(itemProcessor)
						.writer(batchItemWriter())
						.build();
	}
	
	@Bean
	public Step step2() {
		return stepBuilderFactory
						.get("step2")
						.tasklet(cleanupTasklet)
						.build();
	}
	
	// Jobs
	@Bean
    public Job processJob() {
        return jobBuilderFactory.get("processJob")
                .incrementer(new RunIdIncrementer())
                .start(step2())
                .next(step1())
                .listener(listener)
                .build();
    }
	
}
