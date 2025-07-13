package com.eventiq.event.worker.listener;

import com.eventiq.event.worker.model.Events;
import com.eventiq.event.worker.model.EventsPrimaryKey;
import com.eventiq.event.worker.repository.EventsRepository;
import com.eventiq.event.worker.service.ClickhouseService;
import com.eventiq.event.worker.utils.Constants;
import com.eventiq.shared.dto.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Configuration
@Slf4j
public class EventProcessor {

    EventsRepository eventsRepository;

    ObjectMapper objectMapper;

    ExecutorService executorService;

    StreamBridge streamBridge;

    ClickhouseService clickhouseService;

    public EventProcessor(EventsRepository eventsRepository, ObjectMapper objectMapper, ExecutorService executorService, StreamBridge streamBridge, ClickhouseService clickhouseService) {
        this.eventsRepository = eventsRepository;
        this.objectMapper = objectMapper;
        this.executorService = executorService;
        this.streamBridge = streamBridge;
        this.clickhouseService = clickhouseService;
    }

    @Bean
    public Consumer<List<Event>> processEvent() {
        return events -> {
            log.info("Batch size under process: {}", events.size());
            CountDownLatch countDownLatch = new CountDownLatch(events.size());

            events.forEach(event -> executorService.submit(() -> {
                try {
                    log.info(event.toString());
                    LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));

                    EventsPrimaryKey primaryKey = EventsPrimaryKey.builder().sessionId(event.getSessionId()).projectId(event.getProjectId()).eventTimestamp(now.toEpochSecond(ZoneOffset.UTC)).eventType(event.getType()).randomId(UUID.randomUUID().toString()).build();

                    Events tempEvent = Events.builder().projectId(primaryKey).idAddress(event.getMetaData().get("ip").toString()).country(event.getMetaData().get("country").toString()).city(event.getMetaData().get("location").toString()).userAgent(event.getMetaData().get("browser").toString()).metaData(objectMapper.writeValueAsString(event.getMetaData())).build();

                    streamBridge.send(Constants.ANALYTICS_QUEUE, MessageBuilder.withPayload(event).build());

                    eventsRepository.save(tempEvent);

                    // clickhouse save
                    if(event.getType().equals(Constants.SESSION_START)){
                        clickhouseService.insertSession(event.getMetaData(), event);
                    }
                } catch (Exception e) {
                    log.error("Error processing event : {} , error: {}", event, e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            }));

            try {
                countDownLatch.await(2, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
