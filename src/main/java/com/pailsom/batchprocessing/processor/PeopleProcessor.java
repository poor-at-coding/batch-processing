package com.pailsom.batchprocessing.processor;

import com.pailsom.batchprocessing.model.Person;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class PeopleProcessor implements ItemProcessor<Person,Person> {
    @Override
    public Person process(Person person) throws Exception {
        if(!person.getLastName().equalsIgnoreCase("Doe"))
            return null;
        return  person;
    }
}
