package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@CacheConfig(cacheNames = {"adminUserToken"})
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

    @Cacheable("adminUserToken")
    public String getAdminUserToken() {
        return accessTokenService.getAccessToken(caseWorkerUserName, caseWorkerPassword);
    }

    @CacheEvict(value = "adminUserToken", allEntries = true)
    @Scheduled(fixedRateString = "${caching.adminUserService}")
    public void emptyAdminUserToken() {
        log.info("Emptying adminUserToken cache");
    }
}
