package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.service.UserService;
import uk.gov.hmcts.ethos.replacement.docmosis.config.OAuth2Configuration;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.TokenRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.TokenResponse;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.IdamApi;

@Component
public class UserIdamService implements UserService {

    private final IdamApi idamApi;
    private final OAuth2Configuration oauth2Configuration;
    public static final String OPENID_GRANT_TYPE = "password";

    @Autowired
    public UserIdamService(IdamApi idamApi, OAuth2Configuration oauth2Configuration) {
        this.idamApi = idamApi;
        this.oauth2Configuration = oauth2Configuration;
    }

    @Override
    public UserDetails getUserDetails(String authorisation) {
        return idamApi.retrieveUserDetails(authorisation);
    }

    @Override
    public UserDetails getUserDetailsById(String authToken, String userId) {
        return idamApi.getUserByUserId(authToken, userId);
    }

    public TokenResponse getAccessTokenResponse(String username, String password) {
        return idamApi.generateOpenIdToken(
                new TokenRequest(
                        oauth2Configuration.getClientId(),
                        oauth2Configuration.getClientSecret(),
                        OPENID_GRANT_TYPE,
                        oauth2Configuration.getRedirectUri(),
                        username,
                        password,
                        oauth2Configuration.getClientScope(),
                        null,
                        null
                ));
    }

    public String getAccessToken(String username, String password) {
        return getAccessTokenResponse(username, password).accessToken;
    }
}
