# Ethos Replacement Doc Generation Service (ECM)

This application generates documents from templates using Docmosis -> Tornado.

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

### API documentation

API documentation is provided with Swagger:
UI to interact with the API resources

```bash
    http://localhost:8081/swagger-ui.html
```

## Docker container

### Authenticating to ACR

Login to Azure CLI

```bash
    az login
```

Login to ACR

```bash
    az acr login --name hmctspublic
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
