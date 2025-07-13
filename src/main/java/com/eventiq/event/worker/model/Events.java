package com.eventiq.event.worker.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;


@Table("events")
@Data
@Builder
public class Events {

    @PrimaryKey
    private EventsPrimaryKey projectId;

    private String idAddress;

    private String country;

    private String city;

    private String userAgent;

    private String agentType;

    private String metaData;
}
