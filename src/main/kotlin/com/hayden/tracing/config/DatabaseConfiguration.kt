package com.hayden.tracing.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.hayden.tracing.entity.Event
import com.hayden.tracing.repository.EventRepository
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.data.jdbc.core.convert.JdbcConverter
import org.springframework.data.jdbc.core.convert.MappingJdbcConverterImpl
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.data.relational.core.mapping.RelationalMappingContext
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import javax.sql.DataSource

@AutoConfiguration
@EntityScan(basePackageClasses = [Event::class])
@ImportAutoConfiguration(value = [
//        JdbcRepositoriesAutoConfiguration::class,
        JdbcTemplateAutoConfiguration::class,
        DataSourceAutoConfiguration::class
])
@EnableConfigurationProperties
@EnableJdbcRepositories(basePackageClasses = [EventRepository::class])
open class DatabaseConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    open fun dataSource(): DataSource {
        return DataSourceBuilder
            .create()
            .driverClassName("org.postgresql.Driver")
            .url("jdbc:postgresql://localhost:5432/postgres")
            .username("postgres")
            .password("postgres")
            .build()
    }

    @Bean
    open fun jdbcTemplate(dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource);
    }

    @Bean
    open fun namedParameterJdbcOperations(jdbcTemplate: JdbcTemplate): NamedParameterJdbcOperations {
        return NamedParameterJdbcTemplate(jdbcTemplate)
    }


    @Bean
    open fun jdbcMappingContext(): JdbcMappingContext? {
        return JdbcMappingContext()
    }

    @Bean
    open fun jdbcConverter(relationalMappingContext: RelationalMappingContext): JdbcConverter {
        return MappingJdbcConverterImpl(relationalMappingContext)
    }

    @Bean
    open fun om(): ObjectMapper {
        var om = ObjectMapper()
        om.registerModules(JavaTimeModule())
        return om
    }

}