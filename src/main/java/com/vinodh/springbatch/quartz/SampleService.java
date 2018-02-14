package com.vinodh.springbatch.quartz;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SampleService {

	public void hello(String a, String b) {
		log.info("....{}.......................... {} ..................{}..................................", a, b,
				LocalDateTime.now());
	}
}