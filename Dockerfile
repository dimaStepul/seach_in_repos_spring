#gradle build
FROM gradle:jdk17 as builder
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle build.gradle.kts settings.gradle.kts ./
COPY --chown=gradle:gradle src ./src
RUN gradle build
COPY . .
RUN gradle clean build
WORKDIR /app

#application build
FROM eclipse-temurin:17-jre-jammy
ENV ARTIFACT_NAME=spring_task-0.0.1-SNAPSHOT.jar
RUN addgroup stepikgroup; adduser --ingroup stepikgroup --disabled-password stepik
USER stepik
WORKDIR /app
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/
EXPOSE 8080
ENTRYPOINT  java -jar ${ARTIFACT_NAME}
