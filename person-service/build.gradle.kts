import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    java
    id("org.springframework.boot") version "4.0.0-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.uncledrema"
version = "0.0.1-SNAPSHOT"
description = "person-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")
    implementation("org.springdoc:springdoc-openapi-starter-common:2.8.13")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.37")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<BootBuildImage>("bootBuildImage") {
    docker {
        publishRegistry {
            username.set(System.getenv("DOCKERHUB_USERNAME"))
            password.set(System.getenv("DOCKERHUB_PASSWORD"))
            url.set("https://index.docker.io/v1/")
        }
    }

}