package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    public static final String BEARER = "Bearer";
    private final UserIdamService userIdamService;

    @Value("${ethos.system.username}")
    private String systemUserName;

    @Value("${ethos.system.password}")
    private String systemUserPassword;

    public String getAdminUserToken() {
        return String.join(" ", BEARER, userIdamService.getAccessToken(systemUserName, systemUserPassword));
    }

    public UserDetails getUserDetails(String userId) {
        return userIdamService.getUserDetailsById(getAdminUserToken(), userId);
    }
}
