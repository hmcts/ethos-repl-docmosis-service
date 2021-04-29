ARG APP_INSIGHTS_AGENT_VERSION=2.5.1
FROM hmctspublic.azurecr.io/base/java:openjdk-11-distroless-1.2
LABEL maintainer="https://github.com/hmcts/ethos-repl-docmosis-service"

COPY lib/AI-Agent.xml /opt/app/
COPY build/libs/ethos-repl-docmosis-service.jar /opt/app/

EXPOSE 8081

CMD ["ethos-repl-docmosis-service.jar"]
