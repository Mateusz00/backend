FROM eclipse-temurin:17-alpine

COPY build/libs/demo.jar .
ENTRYPOINT ["sh", "-c", "java -jar demo.jar"]