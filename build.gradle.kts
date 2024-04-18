plugins {
    java
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.2.4")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.4")
    implementation("org.springframework.retry:spring-retry:2.0.3")
    implementation("org.springframework.boot:spring-boot-starter-aop:3.2.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.4")
    testImplementation("io.projectreactor:reactor-test:3.6.5")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
