package com.vinodh.springbatch.config;

import javax.sql.DataSource;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration
@PropertySource("classpath:application.properties")
public class DbConfig {

	@Autowired
	Environment environment;
	
//	public DataSource dataSource_memory() {
//		return new EmbeddedDatabaseBuilder()
//							.generateUniqueName(true)
//							.setName("generic_apps")
//							.setType(EmbeddedDatabaseType.HSQL)
//							.ignoreFailedDrops(true)
//							.addScripts("classpath: quartz_hsqldb.sql")
//							.build();
//	}
	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(environment.getRequiredProperty("org.quartz.dataSource.quartzDataSource.driver"));
		dataSource.setUrl(environment.getRequiredProperty("org.quartz.dataSource.quartzDataSource.URL"));
		dataSource.setUsername(environment.getRequiredProperty("org.quartz.dataSource.quartzDataSource.user"));
		dataSource.setPassword(environment.getRequiredProperty("org.quartz.dataSource.quartzDataSource.password")); 
		return dataSource;
	}
}
