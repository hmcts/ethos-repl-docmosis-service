# Employment Tribunals CCD Callbacks Service

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-Wrapper-blue.svg)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE.md)

This application is responsible for handling all CCD callback requests for Employment Tribunal cases.

## Supported Versions

| Component | Version | Status |
|-----------|---------|--------|
| Java | 21 | ✅ Supported |
| Spring Boot | 3.3.0 | ✅ Current |
| Spring Security | 6.x | ✅ Current |
| Node.js | 18+ | ✅ Supported |
| Gradle | Wrapper | ✅ Current |

> ⚠️ **Breaking Change**: Java 21 is now required. Java 17 and earlier versions are no longer supported.

## Getting started

### Prerequisites

- [JDK 21](https://www.oracle.com/java)

### Building

The project uses [Gradle](https://gradle.org) as a build tool but you don't have to install it locally since there is a
`./gradlew` wrapper script.

To build project please execute the following command:

```bash
    ./gradlew build
```

To get the project to build in IntelliJ IDEA, you have to:

 - Install the Lombok plugin: Preferences -> Plugins
 - Enable Annotation Processing: Preferences -> Build, Execution, Deployment -> Compiler -> Annotation Processors

### Running

You can run the application by executing following command:

```bash
    ./gradlew bootRun
```

The application will start locally on `http://localhost:8081`

### Database migrations (Flyway)

Flyway migrations are stored in `src/main/resources/db/migration`.

If you already have an existing ethos schema (prod-like, no Flyway history yet), run migration first:

```bash
FLYWAY_URL=jdbc:postgresql://localhost:5050/ethos \
FLYWAY_USER=ethos \
FLYWAY_PASSWORD=ethos \
./gradlew migratePostgresDatabase
```

Then start the app without startup migration:

```bash
RUN_DB_MIGRATION_ON_STARTUP=false ./gradlew bootRun
```

If your database is fresh/empty (but database/user already exist), you can start directly:

```bash
./gradlew bootRun
```

`bootRun` (default `RUN_DB_MIGRATION_ON_STARTUP=true`) will apply Flyway migrations on startup for empty databases.

### API documentation

API documentation is provided with Swagger:
UI to interact with the API resources

```bash
    http://localhost:8081/swagger-ui.html
```

## Local development — CFTLib decentralised mode

### Background

When running locally with `bootWithCCD` in [decentralised CCD mode](https://github.com/hmcts/rse-cft-lib), the CCD SDK
Gradle plugin sets `CCD_SDK_DECENTRALISED=true` as an environment variable. Spring Boot maps this to the property
`ccd.sdk.decentralised=true`, which disables the CFTLib built-in `ESIndexer` bean (it only activates when the property
is `false`). As a result, new cases are never indexed into Elasticsearch and search returns no results.

### CFTLib ES Indexer script

`bin/cftlib-es-indexer.sh` is a shell script replacement for the built-in indexer. It:

- Polls the CCD `datastore` PostgreSQL database every second for cases where `marked_by_logstash = false`
- Atomically marks them as indexed and returns their data
- Bulk-indexes them into Elasticsearch, including a trimmed entry in the `global_search` index for cases with a `SearchCriteria` field

#### Prerequisites

- `psql` — PostgreSQL client
- `jq` — JSON processor (`brew install jq`)
- `curl`
- `bootWithCCD` must already be running (provides the DB and Elasticsearch)

#### Usage

Run in the foreground:

```bash
./bin/cftlib-es-indexer.sh
```

Run in the background for the current shell session:

```bash
./bin/cftlib-es-indexer.sh &
```

Run persistently (survives terminal close):

```bash
nohup ./bin/cftlib-es-indexer.sh > /tmp/es-indexer.log 2>&1 &
```

Stop a background instance:

```bash
kill $(pgrep -f cftlib-es-indexer)
```

#### Configuration

All connection details can be overridden via environment variables:

| Variable  | Default               | Description              |
|-----------|-----------------------|--------------------------|
| `DB_HOST` | `localhost`           | PostgreSQL host          |
| `DB_PORT` | `6432`                | PostgreSQL port          |
| `DB_NAME` | `datastore`           | Database name            |
| `DB_USER` | `postgres`            | Database user            |
| `DB_PASS` | `postgres`            | Database password        |
| `ES_URL`  | `http://localhost:9200` | Elasticsearch base URL |

#### Notes

- The script is safe to restart at any time — rows with `marked_by_logstash = true` are skipped.
- If Elasticsearch returns errors they are logged, but the script continues running.
- The database password is passed via `PGPASSWORD` and is never echoed to the terminal.

## Docker container

### Authenticating to ACR

Login to Azure CLI

```bash
    az login
```

Login to ACR

```bash
    az acr login --name hmctsprod
```

### Docker image

Build the docker image

```bash
    docker build . -t hmcts/ethos-repl-docmosis-service:latest
```

### Docker compose 

Run the service with all its dependencies

```bash
    docker-compose -f docker/app.yml up -d
```

To stop the service

```bash
    docker-compose -f docker/app.yml down
```


## Developing

### Unit tests

To run all unit tests please execute following command:

```bash
    ./gradlew test
```

### Coding style tests

To run all checks (including unit tests) please execute following command:

```bash
    ./gradlew check
```


## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
