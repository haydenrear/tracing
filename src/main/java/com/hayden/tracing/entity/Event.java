package com.hayden.tracing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.convert.ValueConverter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("event")
@NoArgsConstructor
@Data
public class Event {

    public Event(String data) {
        this.data = data;
    }

    @Id
    Long id;

    @Column("data")
    String data;

}
