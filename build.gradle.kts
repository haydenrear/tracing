import com.hayden.haydenbomplugin.BuildSrcVersionCatalogCollector
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("com.hayden.no-main-class")
	id("com.hayden.kotlin")
	id("com.hayden.observable-app")
	id("com.hayden.docker-compose")
	id("com.hayden.jdbc-persistence")
	id("com.hayden.aop")
	id("com.hayden.templating")
	id("com.hayden.spring-app")
	id("com.hayden.web-app")
	id("net.bytebuddy.byte-buddy-gradle-plugin") version "1.14.17"
}



tasks.register("prepareKotlinBuildScriptModel")

val vC = project.extensions.getByType(BuildSrcVersionCatalogCollector::class.java)


dependencies {

	vC.bundles.opentelemetryBundle.inBundle()
		.map { implementation(it) }


	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.1")

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

// TODO: ??? works when running tests individually, fails when gradle build...
tasks.withType<Test> {
//	dependsOn("copyAgent")
//	dependsOn("dynamicTracingAgent")
//	jvmArgs(
//		"-javaagent:build/agent/opentelemetry-javaagent.jar",
//		"-Dotel.javaagent.configuration-file=src/main/resources/otel/otel.properties",
//		"-javaagent:build/dynamic_agent/tracing_agent.jar"
//	)
	filter {
		includeTestsMatching("*OneTest*")
	}
}

