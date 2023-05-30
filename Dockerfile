FROM openjdk:11
WORKDIR /app
COPY target/card-*.jar card.jar
CMD ["java", "-jar", "card.jar"]
