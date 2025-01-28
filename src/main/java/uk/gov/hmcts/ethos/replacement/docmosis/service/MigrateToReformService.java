package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MigrateToReformHelper;

import java.io.IOException;

import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper.generateMarkUp;

@RequiredArgsConstructor
@Service
@Slf4j
public class MigrateToReformService {

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    private final CcdClient ccdClient;

    public void migrateToReform(String authToken, CaseDetails caseDetails) throws IOException {
        uk.gov.hmcts.et.common.model.ccd.CaseDetails reformCaseDetails = MigrateToReformHelper.reformCaseMapper(caseDetails);
        CCDRequest ccdRequest = ccdClient.startCaseCreationTransfer(authToken, reformCaseDetails);
        reformCaseDetails.getCaseData().setEcmCaseLink(
                setCaseLink(caseDetails.getCaseId(), caseDetails.getCaseData().getEthosCaseReference()));
        SubmitEvent submitEvent = ccdClient.submitCaseCreation(authToken, reformCaseDetails, ccdRequest);
        caseDetails.getCaseData().setReformCaseLink(
                setCaseLink(String.valueOf(submitEvent.getCaseId()), reformCaseDetails.getCaseData().getEthosCaseReference()));
    }

    private String setCaseLink(String caseId, String ethosCaseReference) {
        // TODO replace with ccdGatewayBaseUrl
        return generateMarkUp("http://localhost:3000", caseId, ethosCaseReference);
    }
}
