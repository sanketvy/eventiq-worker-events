package com.eventiq.event.worker.listener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Consumer;

@Configuration
public class EventProcessor {

    @Bean
    public Consumer<Map<String, Object>> processEvent(){
        return stringObjectMap -> {
            System.out.println(stringObjectMap);
        };
    }
}
