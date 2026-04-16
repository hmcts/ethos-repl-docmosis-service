ARG APP_INSIGHTS_AGENT_VERSION=3.5.1
FROM hmctsprod.azurecr.io/base/java:21-distroless as base
LABEL maintainer="https://github.com/hmcts/ethos-repl-docmosis-service"

COPY lib/applicationinsights.json /opt/app/
COPY build/libs/ethos-repl-docmosis-service.jar /opt/app/

EXPOSE 8081

CMD ["ethos-repl-docmosis-service.jar"]
