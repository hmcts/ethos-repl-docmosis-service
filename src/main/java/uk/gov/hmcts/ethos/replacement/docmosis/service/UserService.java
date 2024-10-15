package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.IdamApi;

@Component
public class UserService implements uk.gov.hmcts.ecm.common.service.UserService {

    private final IdamApi idamApi;

    @Autowired
    public UserService(IdamApi idamApi) {
        this.idamApi = idamApi;
    }

    @Override
    public UserDetails getUserDetails(String authorisation) {
        return idamApi.retrieveUserDetails(authorisation);
    }

    @Override
    public UserDetails getUserDetailsById(String var1, String var2) {
        return idamApi.getUserByUserId(var1, var2);
    }
}
