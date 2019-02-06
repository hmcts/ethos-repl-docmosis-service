FROM hmcts/cnp-java-base:openjdk-8u181-jre-alpine3.8-1.0
LABEL maintainer="https://github.com/hmcts/ethos-repl-docmosis-service"

ENV APP ethos-repl-docmosis-service.jar
ENV APPLICATION_TOTAL_MEMORY 1024M
ENV APPLICATION_SIZE_ON_DISK_IN_MB 80

COPY build/libs/ethos-repl-docmosis-service*.jar /opt/app/$APP

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" wget -q --spider http://localhost:8081/health || exit 1

EXPOSE 8081

CMD java -jar ethos-repl-docmosis-service.jar
