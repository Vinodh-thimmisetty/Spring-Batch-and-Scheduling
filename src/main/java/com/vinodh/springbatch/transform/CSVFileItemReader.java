package com.vinodh.springbatch.transform;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

@Component
public class CSVFileItemReader<T> implements ItemReader<T> {

	@Override
	public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		return null;
	}

}
