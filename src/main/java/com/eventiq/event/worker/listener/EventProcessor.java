package com.eventiq.event.worker.listener;

import com.eventiq.shared.dto.Event;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Consumer;

@Configuration
public class EventProcessor {

    @Bean
    public Consumer<List<Event>> processEvent(){
        return events -> {
            System.out.println("Batch : " + events.size());
            events.forEach(System.out::println);
        };
    }
}
