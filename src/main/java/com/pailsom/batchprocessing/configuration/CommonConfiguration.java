package com.pailsom.batchprocessing.configuration;

import com.google.cloud.storage.StorageOptions;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.support.ExitCodeMapper;
import org.springframework.batch.core.launch.support.SimpleJvmExitCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.HashMap;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;


@Configuration
public class CommonConfiguration {

    @Bean
    public ExitCodeMapper exitCodeMapper() {
        final SimpleJvmExitCodeMapper exitCodeMapper = new SimpleJvmExitCodeMapper();
        HashMap<String,Integer> map = new HashMap<>();
        map.put("NONE_PROCESSED", 3);
        exitCodeMapper.setMapping(map);
        return exitCodeMapper;
    }

    @Autowired
    JobRegistry jobRegistry;

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        return postProcessor;
    }

    @Bean
    public static Storage storage() throws Exception {
        InputStream keyStream = Configuration.class.getClassLoader()
                .getResourceAsStream("melodic-bolt-334412-0b3b17de57df.json");

        // Define the Google cloud storage
        assert keyStream != null;

        return StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(keyStream))
                .build()
                .getService();
    }
}
