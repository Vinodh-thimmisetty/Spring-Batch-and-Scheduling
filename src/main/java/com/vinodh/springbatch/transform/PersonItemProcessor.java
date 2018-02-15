package com.vinodh.springbatch.transform;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.vinodh.springbatch.model.Person;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 * Converting all Input names to Title case i.e, convert vinodh,kumar to
 * Vinodh,Kumar ( First letter of each word has to be capital one)
 * 
 * @author jdeveloper
 *
 */
@Component
@Slf4j
public class PersonItemProcessor implements ItemProcessor<Person, Person> {

	@Override
	public Person process(final Person inputValues) throws Exception {

		Person transformedPerson = new Person(StringUtils.capitalize(inputValues.getFirstName()),
				StringUtils.capitalize(inputValues.getLastName()));

		log.info("Converting (" + inputValues + ") into (" + transformedPerson + ")");

		return transformedPerson;
	}

}
