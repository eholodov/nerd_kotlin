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

	// https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt
	implementation("io.jsonwebtoken:jjwt:0.9.1")


	// https://mvnrepository.com/artifact/org.springframework.security/spring-security-crypto
	implementation("org.springframework.security:spring-security-crypto:5.5.2")

	// https://mvnrepository.com/artifact/org.springframework.security/spring-security-config
	implementation("org.springframework.security:spring-security-config:5.5.2")

	// https://mvnrepository.com/artifact/org.springframework.security/spring-security-core
	implementation("org.springframework.security:spring-security-core:5.5.2")

	// https://mvnrepository.com/artifact/jakarta.xml.bind/jakarta.xml.bind-api
	implementation("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")

	// https://mvnrepository.com/artifact/org.glassfish.jaxb/jaxb-runtime
	implementation("org.glassfish.jaxb:jaxb-runtime:3.0.1")

	// https://mvnrepository.com/artifact/javax.activation/activation
	implementation("javax.activation:activation:1.1.1")


	// https://mvnrepository.com/artifact/com.sun.xml.bind/jaxb-core
	implementation("com.sun.xml.bind:jaxb-core:3.0.1")


	// https://mvnrepository.com/artifact/com.sun.xml.bind/jaxb-impl
	implementation("com.sun.xml.bind:jaxb-impl:3.0.1")


	// https://mvnrepository.com/artifact/javax.xml.bind/jaxb-api
	implementation("javax.xml.bind:jaxb-api:2.3.1")

	// https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security
	implementation("org.springframework.boot:spring-boot-starter-security:2.5.4")

	implementation("com.slack.api:slack-api-model-kotlin-extension:1.12.1")
	implementation("com.slack.api:slack-api-client-kotlin-extension:1.12.1")

	implementation(files("libs/logback-webhook-appender-1.0.3.jar"))
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

tasks.withType<Jar> {
	duplicatesStrategy = DuplicatesStrategy.INCLUDE
}