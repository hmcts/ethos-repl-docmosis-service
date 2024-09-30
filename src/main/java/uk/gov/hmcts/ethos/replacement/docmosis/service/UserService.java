package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.IdamApi;

@Component
public class UserService implements uk.gov.hmcts.ecm.common.service.UserService {

    private final IdamApi idamApi;

    public static final String OPENID_GRANT_TYPE = "password";

    public UserService(IdamApi idamApi) {
        this.idamApi = idamApi;
    }

    @Override
    public UserDetails getUserDetails(String authorisation) {
        return idamApi.retrieveUserDetails(authorisation);
    }

    @Override
    public UserDetails getUserDetailsById(String s, String s1) {
        return null;
    }
}
