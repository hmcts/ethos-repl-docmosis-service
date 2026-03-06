package uk.gov.hmcts.ethos.replacement.docmosis.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class OAuth2Configuration {

    private final String clientId;
    private final String redirectUri;
    private final String clientSecret;

    public OAuth2Configuration(
        @Value("${idam.client.redirect_uri:}") String redirectUri,
        @Value("${idam.client.id:}") String clientId,
        @Value("${idam.client.secret:}") String clientSecret
    ) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.clientSecret = clientSecret;
    }
}
