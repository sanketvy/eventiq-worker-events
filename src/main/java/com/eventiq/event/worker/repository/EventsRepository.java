package com.eventiq.event.worker.repository;

import com.eventiq.event.worker.model.Events;
import com.eventiq.event.worker.model.EventsPrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface EventsRepository extends CassandraRepository<Events, EventsPrimaryKey> {
}
