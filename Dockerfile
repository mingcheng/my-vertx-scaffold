# Multi-stage build setup (https://docs.docker.com/develop/develop-images/multistage-build/)

# Stage 1 (to create a "build" image, ~140MB)
FROM gradle:3.4.1-jdk8 AS builder
LABEL maintainer "hzmingcheng@corp.netease.comm"

USER root
RUN gradle -v
COPY . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle clean jar

# Stage 2 (to create a downsized "container executable", ~87MB)
FROM java:8-jre-alpine
WORKDIR /home/gradle/project
COPY --from=builder /home/gradle/project/build/libs/*.jar .

# EXPOSE 80
ENTRYPOINT ["java", "-jar", "my-vertx-scaffold-1.0.0-SNAPSHOT.jar"]
