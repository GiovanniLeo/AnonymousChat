FROM alpine/git
WORKDIR /app
RUN git clone https://github.com/spagnuolocarmine/distributedsystems.git
WORKDIR ./distributedsystems
RUN git checkout CS

FROM maven:3.5-jdk-8-alpine
WORKDIR /app
COPY --from=0 /app/distributedsystems/submissions/CS/AnonymousChat /app
RUN mvn clean compile assembly:single

FROM openjdk:8-jre-alpine
WORKDIR /app
ENV MASTERIP=127.0.0.1
ENV ID=0
COPY --from=1 /app/target/AnonymousChat-1.0-SNAPSHOT-jar-with-dependencies.jar /app

CMD /usr/bin/java -jar AnonymousChat-1.0-SNAPSHOT-jar-with-dependencies.jar -m $MASTERIP -id $ID