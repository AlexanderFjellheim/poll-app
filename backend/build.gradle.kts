plugins {
	java
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.dat250"
version = "0.0.1-SNAPSHOT"
description = "Poll app"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// If you are using Redis
	//implementation("redis.clients:jedis:6.2.0")
	// If you are using Valkey
	implementation("io.valkey:valkey-java:5.4.0")

	implementation("org.springframework.boot:spring-boot-starter-data-redis")


	implementation("org.hibernate.orm:hibernate-core:7.1.1.Final")
	implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
	implementation("com.h2database:h2:2.3.232")

	implementation("org.springframework.boot:spring-boot-starter-amqp")

	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.8.8")

}

//tasks.test { // Dumps generated DDLs for testing
//	systemProperty("jakarta.persistence.schema-generation.scripts.action", "drop-and-create")
//	systemProperty("jakarta.persistence.schema-generation.scripts.create-target", "${project.buildDir}/schema-create.sql")
//	systemProperty("jakarta.persistence.schema-generation.scripts.drop-target", "${project.buildDir}/schema-drop.sql")
//}



tasks.withType<Test> {
	useJUnitPlatform()
	jvmArgs("-XX:+EnableDynamicAgentLoading")
}
