FROM openjdk:8-jre

ADD cache/ /app/cache
ADD target/titan-1.0.jar /app/titan-1.0.jar
ADD config/ /app/config
ADD players/ /app/players
ADD modules /app/modules

# This module isn't compatible with headless though
RUN rm /app/modules/UniverseModule-1.0.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "/app/titan-1.0.jar"]
