package com.vinodh.springbatch.transform;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class CustomItemProcessor<I, O> implements ItemProcessor<I, O> {

	@Override
	public O process(I item) throws Exception {
		return null;
	}

}
