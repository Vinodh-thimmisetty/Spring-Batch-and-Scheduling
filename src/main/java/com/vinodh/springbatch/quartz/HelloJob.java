package com.vinodh.springbatch.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
 
@Slf4j
@DisallowConcurrentExecution
public class HelloJob implements Job {

	@Autowired
	SampleService sampleService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("............... Inside Hello Job ..............");
		sampleService.hello(context.getJobDetail().getKey().getName(), context.getTrigger().getKey().getName());
	}

}