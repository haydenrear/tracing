package com.hayden.tracing.config

import com.hayden.tracing.entity.Event
import com.hayden.tracing.repository.EventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.core.convert.JdbcConverter
import org.springframework.data.jdbc.core.convert.MappingJdbcConverterImpl
import org.springframework.data.jdbc.core.convert.RelationResolver
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.data.relational.core.mapping.RelationalMappingContext

@Configuration
@EnableJdbcRepositories(basePackageClasses = [EventRepository::class])
@EntityScan(basePackageClasses = [Event::class])
open class DatabaseConfiguration {

    @Bean
    open fun jdbcConverter(relationalMappingContext: RelationalMappingContext): JdbcConverter {
        return MappingJdbcConverterImpl(relationalMappingContext);
    }

}