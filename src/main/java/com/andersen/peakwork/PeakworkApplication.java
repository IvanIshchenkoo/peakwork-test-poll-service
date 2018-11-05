package com.andersen.peakwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.zankowski.iextrading4j.client.IEXTradingClient;

@SpringBootApplication
@EnableScheduling
public class PeakworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(PeakworkApplication.class, args);
    }

    @Bean
    public IEXTradingClient client() {
        return IEXTradingClient.create();
    }
}
