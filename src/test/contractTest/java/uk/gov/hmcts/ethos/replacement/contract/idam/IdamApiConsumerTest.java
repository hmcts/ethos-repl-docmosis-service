package uk.gov.hmcts.ethos.replacement.contract.idam;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.config.HttpClientConfiguration;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.IdamApi;

@ExtendWith(SpringExtension.class)
@ExtendWith(PactConsumerTestExt.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactFolder("pacts")
@PactTestFor(providerName = "idamApi_oidc", port = "8889")
@ContextConfiguration(classes = {IdamApiConsumerApplication.class})
@TestPropertySource(locations = {"classpath:application.properties"}, properties = {"idam.api.url=localhost:8889"})
@Import(HttpClientConfiguration.class)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class IdamApiConsumerTest {
    @Autowired
    IdamApi idamApi;

    private static final String AUTH_TOKEN = "Bearer someAuthorizationToken";

    @Pact(provider = "idamApi_oidc", consumer = "ethos_replDocmosis")
    public RequestResponsePact generatePactFragment(PactDslWithProvider builder) {
        return builder
                .given("userinfo is requested")
                .uponReceiving("a request for a user")
                .path("/o/userinfo")
                .matchHeader(HttpHeaders.AUTHORIZATION, AUTH_TOKEN)
                .willRespondWith()
                .status(HttpStatus.SC_OK)
                .body(createUserDetailsResponse())
                .toPact();
    }

    private PactDslJsonBody createUserDetailsResponse() {
        return new PactDslJsonBody()
                .stringType("uid", "1111-2222-3333-4567")
                .stringValue("sub", "ia-caseofficer@fake.hmcts.net")
                .stringValue("givenName", "Case")
                .stringValue("familyName", "Officer")
                .minArrayLike("roles", 1, PactDslJsonRootValue.stringType("caseworker"), 1)
                .stringType("IDAM_ADMIN_USER", "idamAdminUser");
    }

    @Test
    @PactTestFor(pactMethod = "generatePactFragment")
    public void verifyPactResponse() {
        UserDetails userDetails = idamApi.retrieveUserDetails(AUTH_TOKEN);
        Assertions.assertEquals("ia-caseofficer@fake.hmcts.net", userDetails.getEmail());
    }
}
