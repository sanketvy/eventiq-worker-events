package com.eventiq.event.worker.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@PrimaryKeyClass
@Data
@Builder
public class EventsPrimaryKey implements Serializable {

    @PrimaryKeyColumn(name = "project_id", type = PrimaryKeyType.PARTITIONED)
    private String projectId;

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED)
    private UUID id;
}
