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
	implementation("org.springframework.boot:spring-boot-starter:2.5.4")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.4")


	// https://mvnrepository.com/artifact/org.springframework.data/spring-data-mongodb
	implementation("org.springframework.data:spring-data-mongodb:3.2.4")

	// https://mvnrepository.com/artifact/com.slack.api/slack-api-client
	implementation("com.slack.api:slack-api-client:1.12.0")

	// https://mvnrepository.com/artifact/com.google.apis/google-api-services-sheets
	implementation("com.google.apis:google-api-services-sheets:v4-rev20210629-1.32.1")

	// https://mvnrepository.com/artifact/com.google.api-client/google-api-client
	implementation("com.google.api-client:google-api-client:1.32.1")

	// https://mvnrepository.com/artifact/com.google.oauth-client/google-oauth-client-jetty
	implementation("com.google.oauth-client:google-oauth-client-jetty:1.32.1")

	// https://mvnrepository.com/artifact/com.google.auth/google-auth-library-oauth2-http
	implementation("com.google.auth:google-auth-library-oauth2-http:1.1.0")

	// https://mvnrepository.com/artifact/org.mongodb/mongodb-driver-sync
	implementation("org.mongodb:mongodb-driver-sync")

	// https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web
	implementation("org.springframework.boot:spring-boot-starter-web:2.5.4")

	// https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-validation
	implementation("org.springframework.boot:spring-boot-starter-validation:2.5.4")

	// https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-validation
	implementation("org.springframework.boot:spring-boot-starter-validation:2.5.4")

	// https://mvnrepository.com/artifact/com.slack.api/slack-api-model
	implementation("com.slack.api:slack-api-model:1.12.1")

	implementation("com.slack.api:slack-api-model-kotlin-extension:1.12.1")
	implementation("com.slack.api:slack-api-client-kotlin-extension:1.12.1")

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
