package com.vinodh.springbatch;

import java.util.Arrays;
import java.util.Date;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.vinodh.springbatch.quartz.HelloJob;

import lombok.extern.slf4j.Slf4j;


//@SpringBootApplication
@Slf4j
public class QuartzSchdulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuartzSchdulerApplication.class, args);
	}
	
	@Bean
	CommandLineRunner commandLineRunner(ApplicationContext applicationContext) {
		return a -> {
			Arrays
				.stream(applicationContext.getBeanDefinitionNames())
				.filter( beanName -> beanName.equalsIgnoreCase("schedulerFactoryBean"))
				.forEach( xx -> { 
					log.info(">>>>>>>>>>>>>>>>>>>>> {}",xx);
				});;
		};
	}

	/**
	 * 
	 * Comment this out to Try JDBC Store Quartz scheduling Approach
	 * 
	 * @return
	 */
	@Bean
	CommandLineRunner commandLineRunner() {
		//@formatter:off
		return args -> {
			
			// Load Sample Data which can be shared across the Jobs/Triggers
			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("First", "Sample Val");
			jobDataMap.put("Second", true);
			jobDataMap.put("Third", 100);
			
			
			
			// Instantiate the Scheduler
			Scheduler scheduler = new StdSchedulerFactory().getScheduler(); 

			// Instantiate the Job
			JobKey jobKey = new JobKey("hello", "group-1");			
			JobDetail jobDetail = JobBuilder
									.newJob(HelloJob.class)
									.storeDurably()
									.withIdentity(jobKey)
									.withDescription("Sample Hello World Job") 
									.setJobData(jobDataMap)
									.usingJobData("YoYo", "Vinodh Kumar")
									.build();
			scheduler.addJob(jobDetail, true);
			// Instantiate the Triggers
			Date startDate = DateBuilder.todayAt(3, 2, 0);
			TriggerKey triggerKey = new TriggerKey("hello-trigger-with-priority-5", "group-1");
			SimpleTrigger simpleTrigger = TriggerBuilder
												.newTrigger()
												.startAt(startDate)
												.usingJobData(jobDataMap)
												.withDescription("Simple Hello World Trigger")
												.withPriority(5)
												.withIdentity(triggerKey)
												.withSchedule(SimpleScheduleBuilder
																	.simpleSchedule()
																	.withIntervalInMinutes(1)
																	.withRepeatCount(1)
																	.withMisfireHandlingInstructionIgnoreMisfires())
												.forJob(jobDetail)
												.build();
			TriggerKey triggerKey2 = new TriggerKey("hello-trigger-with-priority-10", "group-1");
			SimpleTrigger simpleTrigger2 = TriggerBuilder
					.newTrigger()
					.startAt(startDate)
					.usingJobData(jobDataMap)
					.withDescription("Simple Hello World Trigger")
					.withPriority(10)
					.withIdentity(triggerKey2)
					.withSchedule(SimpleScheduleBuilder
										.simpleSchedule()
										.withIntervalInMinutes(1)
										.withRepeatCount(1)
										.withMisfireHandlingInstructionIgnoreMisfires())
					.forJob(jobDetail)
					.build();
			
			TriggerKey cronTriggerKey = new TriggerKey("hello-cron-trigger", "group-1");
			CronExpression cronExpression = new CronExpression("0/10 * * * * ? *");
			CronTrigger cronTrigger = TriggerBuilder
												.newTrigger()
												.usingJobData(jobDataMap)
												.withDescription("Sample CRON Builder")
												.withPriority(2)
												.withIdentity(cronTriggerKey)
												.withSchedule(CronScheduleBuilder
																		.cronSchedule(cronExpression)
																		.withMisfireHandlingInstructionDoNothing())
												.forJob(jobDetail)
												.build();
			
			// Schedule the Job using Trigger
			scheduler.scheduleJob(simpleTrigger);
			scheduler.scheduleJob(simpleTrigger2);  // IF Two triggers are executed @Same time, based on Higher Priority, scheduler will be triggered
			scheduler.scheduleJob(cronTrigger);
			
			// Kick off the Scheduler
			scheduler.start();
			
			// Log the Summary of the Scheduler
			log.info(" {} ", scheduler.getMetaData().getSummary());
			
			scheduler.shutdown();
			
		  //@formatter:on	
		};
	}


}
