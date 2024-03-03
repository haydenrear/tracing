import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("com.hayden.no-main-class")
	id("com.hayden.kotlin")
	id("com.hayden.observable-app")
}

tasks.register("prepareKotlinBuildScriptModel")

dependencies {
	implementation("org.springframework.boot:spring-boot-docker-compose")
	implementation("io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0:2.1.0-alpha")
	implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:2.1.0")
	implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter:1.22.1-alpha")
	implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.1.0")
	implementation("io.opentelemetry.javaagent:opentelemetry-javaagent:2.0.0")
	implementation("io.opentelemetry.instrumentation:opentelemetry-jdbc:2.1.0-alpha")
	implementation("io.micrometer:context-propagation:1.1.1")

	implementation("io.micrometer:micrometer-tracing-bridge-otel")

	runtimeOnly("io.micrometer:micrometer-registry-prometheus")
	runtimeOnly("io.micrometer:micrometer-core")
	runtimeOnly("io.micrometer:micrometer-registry-otlp")

	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.liquibase:liquibase-core")

	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	annotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-test")

	implementation(project(":utilitymodule"))
	implementation(project(":tracing_agent"))

	annotationProcessor(project(":tracing_apt")) {
		exclude("org.junit")
	}
	testAnnotationProcessor(project(":tracing_apt")) {
		exclude("org.junit")
	}
	api(project(":tracing_apt")) {
		exclude("org.junit")
	}
}


tasks.withType<KotlinCompile> {
	dependsOn("copyAgent")
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

tasks.withType<JavaExec> {
	dependsOn("copyAgent")
	dependsOn("dynamicTracingAgent")
	jvmArgs(
		"-javaagent:build/agent/opentelemetry-javaagent.jar",
		"-Dotel.javaagent.configuration-file=src/main/resources/otel/otel.properties",
		"-javaagent:build/dynamic_agent/tracing_agent.jar"
	)
}

tasks.withType<Test> {
	dependsOn("copyAgent")
	dependsOn("dynamicTracingAgent")
	jvmArgs(
		"-javaagent:build/agent/opentelemetry-javaagent.jar",
		"-Dotel.javaagent.configuration-file=src/main/resources/otel/otel.properties",
		"-javaagent:build/dynamic_agent/tracing_agent.jar"
	)
}

