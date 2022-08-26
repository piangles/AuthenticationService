FROM adoptopenjdk/openjdk8:x86_64-alpine-jre8u232-b09
WORKDIR /
ADD ./target/AuthenticationService.jar AuthenticationService.jar
ENTRYPOINT ["java", "-Dprocess.name=AuthenticationService", "-jar", "AuthenticationService.jar"]
