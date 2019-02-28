package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.IdamApi;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.models.*;

@Component
public class UserService {

    private final IdamApi idamApi;

    @Autowired
    public UserService(IdamApi idamApi) {
        this.idamApi = idamApi;
    }

    UserDetails getUserDetails(String authorisation) {
        return idamApi.retrieveUserDetails(authorisation);
    }
}
