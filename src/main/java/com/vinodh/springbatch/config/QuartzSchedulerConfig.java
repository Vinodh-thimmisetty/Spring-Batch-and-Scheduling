package com.vinodh.springbatch.config;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.quartz.DateBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import com.vinodh.springbatch.quartz.HelloJob;

@Configuration
@PropertySource("classpath:quartz.properties")
@Import(DbConfig.class)
@ConditionalOnProperty(name = "quartz.enabled")
public class QuartzSchedulerConfig {

	@Autowired
	ApplicationContext context;

	@Autowired
	DbConfig dbConfig;

	@Autowired
	Environment env;

	JobKey jobKey = new JobKey("jdbc-hello-job", "jdbc-group");

	@Bean
	public SpringBeanJobFactory springBeanJobFactory() {
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(context);
		return jobFactory;
	}

	@Bean
	public Scheduler schedulerFactoryBean() throws Exception {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setOverwriteExistingJobs(true);
		factory.setDataSource(dbConfig.dataSource());
		// factory.setJobFactory(springBeanJobFactory());
		factory.setQuartzProperties(quartzProperties());
		// factory.setTriggers(simpleTrigger());

		factory.afterPropertiesSet();

		Scheduler scheduler = factory.getScheduler();
		scheduler.setJobFactory(springBeanJobFactory());
		if (scheduler.checkExists(jobKey)) {
			scheduler.deleteJob(jobKey);
		}
		scheduler.scheduleJob(jobDetail(), simpleTrigger());
		scheduler.start();

		return scheduler;
	}

	@Bean
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("quartz.properties"));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}

	@Bean
	public JobDetail jobDetail() {
		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
		factoryBean.setJobClass(HelloJob.class);
		factoryBean.setDurability(true);
		factoryBean.setName(jobKey.getName());
		factoryBean.setGroup(jobKey.getGroup());
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}

	@Bean
	public SimpleTrigger simpleTrigger() {
		Date startDate = DateBuilder.todayAt(4, 30, 0);
		SimpleTriggerFactoryBean simpleTrigger = new SimpleTriggerFactoryBean();
		// simpleTrigger.setJobDetail(jobDetail());
		simpleTrigger.setName("jdbc-simple-trigger");
		simpleTrigger.setGroup("jdbc-group");
		simpleTrigger.setStartTime(new Date());
		simpleTrigger.setRepeatCount(1);
		simpleTrigger.setRepeatInterval(5000);
		simpleTrigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
		simpleTrigger.afterPropertiesSet();
		return simpleTrigger.getObject();
	}
}
