package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.launchdarkly.FeatureToggleApi;

@Service
public class FeatureToggleService {

    private final FeatureToggleApi featureToggleApi;

    @Autowired
    public FeatureToggleService(FeatureToggleApi featureToggleApi) {
        this.featureToggleApi = featureToggleApi;
    }

    public boolean isUpdateTransferredCaseLinksEnabled() {
        return this.featureToggleApi.isFeatureEnabled("updateTransferredCaseLinks");
    }
}
