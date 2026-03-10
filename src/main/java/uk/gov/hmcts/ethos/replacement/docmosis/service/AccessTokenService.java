package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.ethos.replacement.docmosis.config.OAuth2Configuration;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.TokenResponse;

@Component
public class AccessTokenService {

    private static final String BEARER = "Bearer";
    private static final String OPENID_GRANT_TYPE = "password";
    private static final String OPENID_SCOPE = "openid profile roles";

    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String GRANT_TYPE = "grant_type";
    private static final String REDIRECT_URI = "redirect_uri";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String SCOPE = "scope";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String CODE = "code";

    private final OAuth2Configuration oauth2Configuration;
    private final RestTemplate restTemplate;

    @Value("${idam.api.url.oidc}")
    private String idamApiOidcUrl;

    public AccessTokenService(OAuth2Configuration oauth2Configuration, RestTemplate restTemplate) {
        this.oauth2Configuration = oauth2Configuration;
        this.restTemplate = restTemplate;
    }

    public String getAccessToken(String username, String password) {
        ResponseEntity<TokenResponse> responseEntity = restTemplate.postForEntity(
            idamApiOidcUrl,
            new HttpEntity<>(getTokenRequest(username, password), getHeaders()),
            TokenResponse.class
        );

        TokenResponse body = responseEntity.getBody();
        if (body != null && body.accessToken != null) {
            return String.join(" ", BEARER, body.accessToken);
        }
        return "";
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private MultiValueMap<String, String> getTokenRequest(String username, String password) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(CLIENT_ID, oauth2Configuration.getClientId());
        map.add(CLIENT_SECRET, oauth2Configuration.getClientSecret());
        map.add(GRANT_TYPE, OPENID_GRANT_TYPE);
        map.add(REDIRECT_URI, oauth2Configuration.getRedirectUri());
        map.add(USERNAME, username);
        map.add(PASSWORD, password);
        map.add(SCOPE, OPENID_SCOPE);
        map.add(REFRESH_TOKEN, null);
        map.add(CODE, null);
        return map;
    }
}
