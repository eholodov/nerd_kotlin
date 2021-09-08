import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.4"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.21"
	kotlin("plugin.spring") version "1.5.21"
}

group = "com.dunice"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	// https://mvnrepository.com/artifact/org.springframework.data/spring-data-mongodb
	implementation("org.springframework.data:spring-data-mongodb:3.2.4")

	// https://mvnrepository.com/artifact/com.slack.api/slack-api-client
	implementation("com.slack.api:slack-api-client:1.12.0")

	// https://mvnrepository.com/artifact/com.google.apis/google-api-services-sheets
	implementation("com.google.apis:google-api-services-sheets:v4-rev612-1.25.0")

	// https://mvnrepository.com/artifact/com.google.api-client/google-api-client
	implementation("com.google.api-client:google-api-client:1.32.1")


}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
