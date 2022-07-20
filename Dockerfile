FROM gradle:jdk17-alpine as build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM openjdk:17-alpine
EXPOSE 9090
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/api-0.0.1-SNAPSHOT.jar /app/app.jar
ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]
