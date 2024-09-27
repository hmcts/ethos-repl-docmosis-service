package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.config.OAuth2Configuration;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.TokenRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.TokenResponse;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.HelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.IdamApi;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserIdamServiceTest {
    @InjectMocks
    private UserIdamService userIdamService;
    private UserDetails userDetails;

    private TokenResponse tokenResponse;
    private OAuth2Configuration oauth2Configuration;

    @BeforeEach
    public void setUp() {
        userDetails = HelperTest.getUserDetails();
        tokenResponse = HelperTest.getUserToken();
        IdamApi idamApi = new IdamApi() {
            @Override
            public UserDetails retrieveUserDetails(String authorisation) {
                return HelperTest.getUserDetails();
            }

            @Override
            public UserDetails getUserByUserId(String authorisation, String userId) {
                return HelperTest.getUserDetails();
            }

            @Override
            public TokenResponse generateOpenIdToken(TokenRequest tokenRequest) {
                return tokenResponse;
            }
        };

        mockOauth2Configuration();

        userIdamService = new UserIdamService(idamApi, oauth2Configuration);
    }

    @Test
    void shouldHaveUserDetails() {
        assertEquals(userIdamService.getUserDetails("TOKEN"), userDetails);
    }

    @Test
    void shouldCheckAllUserDetails() {
        assertEquals(userDetails, userIdamService.getUserDetails("TOKEN"));
        assertEquals("mail@mail.com", userIdamService.getUserDetails("TOKEN").getEmail());
        assertEquals("Mike", userIdamService.getUserDetails("TOKEN").getFirstName());
        assertEquals("Jordan", userIdamService.getUserDetails("TOKEN").getLastName());
        assertEquals(Collections.singletonList("role"), userIdamService.getUserDetails("TOKEN").getRoles());
        assertEquals(userDetails.toString(), userIdamService.getUserDetails("TOKEN").toString());
    }

    @Test
    void shouldGetUserById() {
        assertEquals(userDetails, userIdamService.getUserDetailsById("TOKEN", "id"));
    }

    @Test
    void shouldGetAccessToken() {
        assertEquals("abcefg", userIdamService.getAccessToken("John@email.com", "abc123"));
    }

    @Test
    void shouldReturnAccessTokenResponse() {
        assertEquals(tokenResponse, userIdamService.getAccessTokenResponse("John@email.com", "abc123"));
    }

    private void mockOauth2Configuration() {
        oauth2Configuration = mock(OAuth2Configuration.class);
        when(oauth2Configuration.getClientId()).thenReturn("111");
        when(oauth2Configuration.getClientSecret()).thenReturn("AAAAA");
        when(oauth2Configuration.getRedirectUri()).thenReturn("http://localhost:8080/test");
        when(oauth2Configuration.getClientScope()).thenReturn("roles");
    }
}
