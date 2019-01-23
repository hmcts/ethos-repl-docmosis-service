FROM hmcts/cnp-java-base:openjdk-jre-8-alpine-1.4
LABEL maintainer="https://github.com/hmcts/docmosis"

ENV APP docmosis*.jar
ENV APPLICATION_TOTAL_MEMORY 1024M
ENV APPLICATION_SIZE_ON_DISK_IN_MB 80

COPY build/libs/docmosis*.jar /opt/app/

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" wget -q --spider http://localhost:8081/health || exit 1

EXPOSE 8081

CMD java -jar docmosis*.jar
