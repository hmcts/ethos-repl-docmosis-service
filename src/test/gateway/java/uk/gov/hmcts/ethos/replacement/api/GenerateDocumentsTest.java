package uk.gov.hmcts.ethos.replacement.api;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Base64;
import java.util.Optional;

import static io.restassured.config.SSLConfig.sslConfig;
import static org.assertj.core.api.Assertions.assertThat;

public class GenerateDocumentsTest {

    private static final String SUBSCRIPTION_KEY_HEADER_NAME = "Ocp-Apim-Subscription-Key";
    private static final String GET_GENERATE_DOC_PATH = "/generateDocument";
    private static final String PASSWORD_FOR_UNRECOGNISED_CLIENT_CERT = "testcert";

    private final Config config = ConfigFactory.load();

    @Test
    public void should_accept_request_with_valid_certificate_and_subscription_key() throws Exception {
        Response response = callAllRecipesEndpoint(
            Optional.of(getValidClientKeyStore()),
            Optional.of(getValidSubscriptionKey())
        )
            .thenReturn();

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.body().jsonPath().getList("generateDocument")).isNotNull();
    }

    @Test
    public void should_reject_request_with_invalid_subscription_key() throws Exception {
        Response response = callAllRecipesEndpoint(
            Optional.of(getValidClientKeyStore()),
            Optional.of("invalid-subscription-key123")
        )
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.body().asString()).contains("Access denied due to invalid subscription key");
    }

    @Test
    public void should_reject_request_lacking_subscription_key() throws Exception {
        Optional<String> subscriptionKey = Optional.empty();

        Response response = callAllRecipesEndpoint(
            Optional.of(getValidClientKeyStore()),
            subscriptionKey
        )
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.body().asString()).contains("Access denied due to missing subscription key");
    }

    @Test
    public void should_reject_request_with_unrecognised_client_certificate() throws Exception {
        Response response = callAllRecipesEndpoint(
            Optional.of(getUnrecognisedClientKeyStore()),
            Optional.of(getValidSubscriptionKey())
        )
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.body().asString()).isEqualTo("Invalid client certificate");
    }

    @Test
    public void should_reject_request_lacking_client_certificate() throws Exception {
        Optional<KeyStoreWithPassword> keyStore = Optional.empty();

        Response response = callAllRecipesEndpoint(
            keyStore,
            Optional.of(getValidSubscriptionKey())
        )
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.body().asString()).isEqualTo("Missing client certificate");
    }

    @Test
    public void should_not_expose_http_version() {
        Response response = RestAssured
            .given()
            .baseUri(getApiGatewayUrl().replace("https://", "http://"))
            .header(SUBSCRIPTION_KEY_HEADER_NAME, getValidSubscriptionKey())
            .when()
            .get(GET_GENERATE_DOC_PATH)
            .thenReturn();

        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(response.body().asString()).contains("Resource not found");
    }

    private Response callAllRecipesEndpoint(
        Optional<KeyStoreWithPassword> clientKeyStore,
        Optional<String> subscriptionKey
    ) throws Exception {
        RequestSpecification request = RestAssured.given().baseUri(getApiGatewayUrl());

        if (clientKeyStore.isPresent()) {
            request = request.config(
                getSslConfigForClientCertificate(
                    clientKeyStore.get().keyStore,
                    clientKeyStore.get().password
                )
            );
        }

        if (subscriptionKey.isPresent()) {
            request = request.header(SUBSCRIPTION_KEY_HEADER_NAME, subscriptionKey.get());
        }

        return request.get(GET_GENERATE_DOC_PATH);
    }

    private RestAssuredConfig getSslConfigForClientCertificate(
        KeyStore clientKeyStore,
        String clientKeyStorePassword
    ) throws Exception {
        SSLConfig sslConfig = sslConfig()
            .allowAllHostnames()
            .sslSocketFactory(new SSLSocketFactory(clientKeyStore, clientKeyStorePassword));

        return RestAssured.config().sslConfig(sslConfig);
    }

    private KeyStoreWithPassword getValidClientKeyStore() throws Exception {
        byte[] clientKeyStore = Base64.getDecoder().decode(
            config.getString("client.key-store.content")
        );

        String clientKeyStorePassword = config.getString("client.key-store.password");

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new ByteArrayInputStream(clientKeyStore), clientKeyStorePassword.toCharArray());

        return new KeyStoreWithPassword(keyStore, clientKeyStorePassword);
    }

    private KeyStoreWithPassword getUnrecognisedClientKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        try (
            InputStream keyStoreStream =
                this.getClass().getClassLoader().getResourceAsStream("unrecognised-client-certificate.pfx")
        ) {
            // loading from null stream would cause a quiet failure
            assertThat(keyStoreStream).isNotNull();

            keyStore.load(keyStoreStream, PASSWORD_FOR_UNRECOGNISED_CLIENT_CERT.toCharArray());
        }

        return new KeyStoreWithPassword(keyStore, PASSWORD_FOR_UNRECOGNISED_CLIENT_CERT);
    }

    private String getValidSubscriptionKey() {
        String subscriptionKey = config.getString("client.subscription-key");
        assertThat(subscriptionKey).as("Subscription key").isNotEmpty();
        return subscriptionKey;
    }

    private String getApiGatewayUrl() {
        String apiUrl = config.getString("api.gateway-url");
        assertThat(apiUrl).as("API gateway URL").isNotEmpty();
        return apiUrl;
    }

    private static class KeyStoreWithPassword {
        public final KeyStore keyStore;
        public final String password;

        public KeyStoreWithPassword(KeyStore keyStore, String password) {
            this.keyStore = keyStore;
            this.password = password;
        }
    }
}
