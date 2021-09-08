FROM java:8
WORKDIR /
ADD ./target/AuthenticationService.jar AuthenticationService.jar
ENTRYPOINT ["java", "-Dprocess.name=AuthenticationService", "-jar", "AuthenticationService.jar"]