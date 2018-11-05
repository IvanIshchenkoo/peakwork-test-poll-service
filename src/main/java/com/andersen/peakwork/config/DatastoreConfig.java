package com.andersen.peakwork.config;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.KeyFactory;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Setter
@Configuration
@ConfigurationProperties(prefix = "datastore")
public class DatastoreConfig {
    private String kind;

    @Bean
    public Datastore datastore() {
        return DatastoreOptions.getDefaultInstance().getService(); // Authorized Datastore service;
    }

    @Bean
    public KeyFactory keyFactory() {
        return datastore().newKeyFactory().setKind(kind);
    }
}
