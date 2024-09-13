plugins {
	id("org.springframework.boot") version "3.3.2"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
}

group = "com.itau.insurance-api"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter:3.3.2")
	implementation("org.springframework.boot:spring-boot-starter-actuator:3.3.2")
	implementation("org.springframework.boot:spring-boot-starter-web:3.3.2")
	implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")

	// WebClient
	implementation("org.springframework.boot:spring-boot-starter-webflux:3.3.2")

	// Persistence and database
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.2")
	implementation("com.h2database:h2:2.2.220")

	// Spring-doc
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

	// Jackson
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")

	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.24")

	// Log4j2
	implementation("org.apache.logging.log4j:log4j-api:2.20.0")
	implementation("org.apache.logging.log4j:log4j-core:2.20.0")

	implementation("org.springframework.boot:spring-boot-starter-amqp")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.2")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.24")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.8.2")
	testImplementation("io.mockk:mockk:1.13.5")
	testImplementation("org.mockito:mockito-core:5.2.0")
	testImplementation("org.mockito:mockito-inline:5.2.0")
	testImplementation("org.springframework.amqp:spring-rabbit-test")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("spring.profiles.active", "test")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
	mainClass.set("com.itau.insurance.InsuranceApplicationKt")
}

extra["springCloudVersion"] = "Hoxton.SR6"

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

