package com.pailsom.batchprocessing.batch;

import com.google.cloud.storage.Storage;
import com.pailsom.batchprocessing.model.Person;
import com.pailsom.batchprocessing.repository.PersonRepository;
import com.pailsom.batchprocessing.writer.GSFileWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.HashMap;

@Configuration
public class GenerateReportConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;



    @Bean
    public Job job(){
        return jobBuilderFactory.get("generate-report")
                .start(generate(null))
                .build();
    }


    @Bean
    public Step generate(PersonItemProcessor personItemProcessor){
        return stepBuilderFactory.get("generate")
                .<Person,Person>chunk(100)
                .reader(loadPeople(null))
                .processor(personItemProcessor)
                .writer(gsWriter(null))
                .build();
    }

    @Bean
    public ItemWriter<Person> xmlWriter() {

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Person.class);

        return new StaxEventItemWriterBuilder<Person>()
                .name("contactItemWriter")
                .version("1.0")
                .rootTagName("ContactList")
                .resource(new FileSystemResource("people.xml"))
                .marshaller(marshaller)
                .build();
    }

    @Bean
    public ItemWriter<Person> gsWriter(Storage storage){
        GSFileWriter<Person> writer = new GSFileWriter<>();
        writer.setStorage(storage);
        writer.setBucketName("absolute-vertex-328512-bucket");
        writer.setFileName("people.csv");
        writer.setLineAggregator(new DelimitedLineAggregator<>());
        return writer;
    }


    @Bean
    public RepositoryItemReader<Person> loadPeople(PersonRepository repository) {
        final RepositoryItemReaderBuilder<Person> itemReaderBuilder =
                new RepositoryItemReaderBuilder<>();
        itemReaderBuilder.repository(repository);
        itemReaderBuilder.methodName("findAll");
        HashMap<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("firstName", Sort.Direction.DESC);
        itemReaderBuilder.sorts(sorts);
        itemReaderBuilder.saveState(true);
        return itemReaderBuilder.name("loadPerson").build();
    }
}
