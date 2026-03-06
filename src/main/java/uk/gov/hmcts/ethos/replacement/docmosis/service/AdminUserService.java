package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminUserService {

    private final AccessTokenService accessTokenService;

    @Value("${caseWorkerUserName}")
    private String caseWorkerUserName;

    @Value("${caseWorkerPassword}")
    private String caseWorkerPassword;

    public AdminUserService(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    public String getAdminUserToken() {
        return accessTokenService.getAccessToken(caseWorkerUserName, caseWorkerPassword);
    }
}
