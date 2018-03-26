FROM openjdk:8-jdk-alpine as builder

RUN apk add --no-cache gradle inkscape make

ENV GRADLE_OPTS "-Dorg.gradle.daemon=false"

RUN mkdir /build
WORKDIR /build

RUN gradle wrapper --gradle-version 4.6
RUN ./gradlew wrapper --gradle-version 4.6

COPY . /build
RUN ./gradlew build -x integrationTest

RUN mkdir -p /build-result/lablie /build-result/usr/bin/ \
    && cp /build/tool/build/libs/lablie-tool-*-executable.jar /build-result/lablie/lablie.jar \
    && echo -e "#!/bin/sh\njava -jar /lablie/lablie.jar \"\$@\"" > /build-result/usr/bin/lablie \
    && chmod +x /build-result/usr/bin/lablie

# TODO: make integrationTest stable, order of files differs!!


FROM openjdk:8-jre-alpine

RUN apk add --no-cache inkscape make

COPY --from=builder /build-result /

