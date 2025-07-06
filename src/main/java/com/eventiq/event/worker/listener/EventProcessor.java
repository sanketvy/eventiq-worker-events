package com.eventiq.event.worker.listener;

import com.eventiq.event.worker.model.Events;
import com.eventiq.event.worker.model.EventsPrimaryKey;
import com.eventiq.event.worker.repository.EventsRepository;
import com.eventiq.shared.dto.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Configuration
@Slf4j
public class EventProcessor {

    EventsRepository eventsRepository;

    ObjectMapper objectMapper;

    public EventProcessor(EventsRepository eventsRepository, ObjectMapper objectMapper){
        this.eventsRepository = eventsRepository;
        this.objectMapper = objectMapper;
    }

    @Bean
    public Consumer<List<Event>> processEvent(){
        return events -> {
            System.out.println("Batch : " + events.size());

            events.forEach(event -> {
                try {
                    EventsPrimaryKey primaryKey = EventsPrimaryKey.builder().id(UUID.randomUUID()).projectId(event.getProjectId()).build();
                    Events tempEvent = Events.builder().projectId(primaryKey).type(event.getType()).createdAt(LocalDateTime.now()).metaData(objectMapper.writeValueAsString(event.getMetaData())).build();
                    eventsRepository.save(tempEvent);
                } catch (Exception e) {
                    log.error("Error processing event :{}", e.getMessage());
                    throw new RuntimeException(e.getMessage());
                }
            });
        };
    }
}
