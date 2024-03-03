package com.hayden.tracing.repository;

import com.hayden.tracing.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository
public interface EventRepository extends CrudRepository<Event, Long>  {

//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    public int save(Event event) {
//        return jdbcTemplate.update(
//                "INSERT INTO event (id, data) VALUES (?, ?)",
//                new Object[] {0, event.getData()}, new int[] {Types.BIGINT, Types.OTHER}
//        );
//    }

}
