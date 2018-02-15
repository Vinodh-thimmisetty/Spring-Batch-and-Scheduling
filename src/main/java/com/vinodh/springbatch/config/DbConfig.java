package com.vinodh.springbatch.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration 
public class DbConfig {

	@Autowired
	Environment environment;
	
	@Bean
	@Primary
	public DataSource dataSource_memory() {
		return new EmbeddedDatabaseBuilder()
							.generateUniqueName(true)
							.setName("generic_apps")
							.setType(EmbeddedDatabaseType.HSQL)
							.ignoreFailedDrops(true)
							.addScripts("classpath:/quartz_hsqldb.sql","classpath:/schema-all.sql")
							.build();
	}
	
	 
}
