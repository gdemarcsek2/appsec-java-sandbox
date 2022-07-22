# syntax=docker/dockerfile:1

FROM azul/zulu-openjdk-alpine:17

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

RUN ./mvnw dependency:go-offline
RUN ./mvnw package

FROM azul/zulu-openjdk-alpine:17

WORKDIR /app
COPY config.yml ./
COPY --from=0 /app/target/ormauditlogging-1.0-SNAPSHOT.jar ./

CMD ["java", "-jar", "ormauditlogging-1.0-SNAPSHOT.jar", "config.yml"]
