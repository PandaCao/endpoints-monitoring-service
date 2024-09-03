FROM maven:3.9.9-amazoncorretto-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM amazoncorretto:21-al2023

WORKDIR /app

COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar /app/demo.jar

EXPOSE 8080

CMD ["java", "-jar", "demo.jar"]
