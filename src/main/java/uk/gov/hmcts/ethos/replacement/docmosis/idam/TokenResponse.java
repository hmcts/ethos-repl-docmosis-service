package uk.gov.hmcts.ethos.replacement.docmosis.idam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {

    public final transient String accessToken;

    public TokenResponse(@JsonProperty("access_token") String accessToken) {
        this.accessToken = accessToken;
    }
}
