package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.ethos.replacement.docmosis.config.OAuth2Configuration;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.TokenResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenServiceTest {

    @InjectMocks
    private AccessTokenService accessTokenService;
    @Mock
    private OAuth2Configuration oauth2Configuration;
    @Mock
    private RestTemplate restTemplate;

    @Before
    public void setUp() {
        oauth2Configuration = new OAuth2Configuration("redirectUri", "id", "secret");
        accessTokenService = new AccessTokenService(oauth2Configuration, restTemplate);
    }

    @Test
    public void shouldGetAccessToken() {
        String url = "http://sidam-api:5000/o/token";
        ReflectionTestUtils.setField(accessTokenService, "idamApiOidcUrl", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(getTokenRequestMap(), headers);

        ResponseEntity<TokenResponse> responseEntity = new ResponseEntity<>(new TokenResponse("accessToken"),
            HttpStatus.OK);

        when(restTemplate.postForEntity(eq(url), eq(httpEntity), eq(TokenResponse.class))).thenReturn(responseEntity);

        assertEquals("Bearer accessToken", accessTokenService.getAccessToken("Username", "Password"));
    }

    @Test
    public void shouldReturnEmptyWhenNoTokenResponseBody() {
        String url = "http://sidam-api:5000/o/token";
        ReflectionTestUtils.setField(accessTokenService, "idamApiOidcUrl", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(getTokenRequestMap(), headers);

        ResponseEntity<TokenResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.postForEntity(eq(url), eq(httpEntity), eq(TokenResponse.class))).thenReturn(responseEntity);

        assertEquals("", accessTokenService.getAccessToken("Username", "Password"));
    }

    private MultiValueMap<String, String> getTokenRequestMap() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", "id");
        map.add("client_secret", "secret");
        map.add("grant_type", "password");
        map.add("redirect_uri", "redirectUri");
        map.add("username", "Username");
        map.add("password", "Password");
        map.add("scope", "openid profile roles");
        map.add("refresh_token", null);
        map.add("code", null);
        return map;
    }
}
