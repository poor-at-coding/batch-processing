package com.pailsom.batchprocessing.batch;

import com.pailsom.batchprocessing.model.Person;
import com.pailsom.batchprocessing.repository.PersonRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;

@Configuration
@EnableBatchProcessing
public class ImportUserJobFromGSConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;


    @Bean
    public FlatFileItemReader<Person> readerGC() throws MalformedURLException {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new UrlResource("https://storage.cloud.google.com/absolute-vertex-328512-bucket/sample-data.csv"))
                .delimited()
                .names("firstName", "lastName")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

    @Bean
    public PersonItemProcessor processorGC() {
        return new PersonItemProcessor();
    }

    @Bean
    public RepositoryItemWriter<Person> writerGc(PersonRepository repository) {
        return new RepositoryItemWriterBuilder<Person>()
                .methodName("save")
                .repository(repository)
                .build();
    }

    @Bean
    public Job importUserGCJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJobGC")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step stepGC1() throws MalformedURLException {
        return stepBuilderFactory.get("step1")
                .<Person, Person> chunk(10)
                .reader(readerGC())
                .processor(processorGC())
                .writer(writerGc(null))
                .build();
    }
}
