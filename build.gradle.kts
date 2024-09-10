plugins {
	id("org.springframework.boot") version "3.3.2"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
}

group = "com.itau.insurance"
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
	// Spring-boot
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// WebClient
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	// Persistence and database
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.h2database:h2")

	// Spring-doc
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

	// Jackson
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")

	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// Log4j2
	implementation("org.apache.logging.log4j:log4j-api:2.20.0")
	implementation("org.apache.logging.log4j:log4j-core:2.20.0")

	// AWS SQS
	implementation("software.amazon.awssdk:sqs:2.20.39")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("io.mockk:mockk:1.13.5")
	testImplementation("org.mockito:mockito-core:5.2.0")
	testImplementation("org.mockito:mockito-inline:5.2.0")
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

