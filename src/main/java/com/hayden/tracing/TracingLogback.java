package com.hayden.tracing;

import com.hayden.tracing.config.TracingResourceConfiguration;
import com.hayden.utilitymodule.telemetry.log.LoggingConfig;
import com.hayden.utilitymodule.telemetry.prelog.PreTelemetryAttributes;
import lombok.SneakyThrows;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.boot.logging.logback.LogbackLoggingSystem;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;

/**
 * use by setting -Dorg.springframework.boot.logging.LoggingSystem=com.hayden.tracing.TracingLogback
 */
public class TracingLogback extends LogbackLoggingSystem {
    public TracingLogback(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    public void initialize(LoggingInitializationContext initializationContext, String configLocation, LogFile logFile) {
        super.initialize(initializationContext, configLocation, logFile);
    }

    /**
     * Provide some contextual information to the Sender from the context so that even the first log contains
     * all resources.
     * @param initializationContext the logging initialization context
     * @param location the location of the configuration to load (never {@code null})
     * @param logFile the file to load or {@code null} if no log file is to be written
     */
    @SneakyThrows
    @Override
    protected void loadConfiguration(LoggingInitializationContext initializationContext, String location, LogFile logFile) {
        super.loadConfiguration(initializationContext, location, logFile);
        var a = new AnnotationConfigApplicationContext();
        var propertySource = new YamlPropertySourceLoader().load("application.yml", new ClassPathResource("application.yml"));

        StandardEnvironment environment = new StandardEnvironment();
        a.setEnvironment(environment);
        var propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setLocation(new ClassPathResource("application.yml"));

        propertySourcesPlaceholderConfigurer.setEnvironment(environment);
        a.addBeanFactoryPostProcessor(propertySourcesPlaceholderConfigurer);

        propertySource.forEach(a.getEnvironment().getPropertySources()::addFirst);
        a.register(TracingResourceConfiguration.class);
        a.register(PreTelemetryAttributes.class);
        a.register(LoggingConfig.class);
        a.refresh();
    }
}
