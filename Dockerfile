FROM eclipse-temurin:17-jre-alpine
WORKDIR /
ADD ./target/AuthenticationService.jar AuthenticationService.jar
ENTRYPOINT ["java", "-Dprocess.name=AuthenticationService", "-jar", "AuthenticationService.jar"]