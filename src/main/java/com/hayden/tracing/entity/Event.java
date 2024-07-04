package com.hayden.tracing.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.convert.Json;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

@Table("event")
@NoArgsConstructor
@Data
public class Event {

    public Event(String data, String trace) {
        this.data = data;
        this.trace = trace;
    }

    @Id
    Long id;

    @Column("data")
    @Json
    String data;
    @Column("trace")
    @Json
    String trace;

}
