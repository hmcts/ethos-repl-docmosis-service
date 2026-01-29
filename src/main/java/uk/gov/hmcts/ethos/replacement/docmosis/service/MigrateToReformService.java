package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.et.common.model.ccd.CCDRequest;
import uk.gov.hmcts.et.common.model.ccd.CaseData;
import uk.gov.hmcts.et.common.model.ccd.SubmitEvent;

import java.io.IOException;
import java.time.LocalDate;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MigrateToReformHelper.reformCaseMapper;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper.generateMarkUp;

@RequiredArgsConstructor
@Service
@Slf4j
public class MigrateToReformService {

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    private final CcdClient ccdClient;

    public void migrateToReform(String authToken, CaseDetails caseDetails) throws IOException {
        log.info("ECM Case {} retrieved for migration to Reform", caseDetails.getCaseId());
        tidyJudgmentData(caseDetails);
        var reformCaseDetails = reformCaseMapper(caseDetails);
        var ecmCase = ccdClient.retrieveCase(authToken, caseDetails.getCaseTypeId(),
                "EMPLOYMENT", caseDetails.getCaseId());
        CCDRequest ccdRequest = ccdClient.startCaseMigrationToReform(authToken,
                "EMPLOYMENT", reformCaseDetails.getCaseTypeId());
        CaseData reformCaseData = reformCaseDetails.getCaseData();
        reformCaseData.setStateAPI(ecmCase.getState());
        reformCaseData.setEcmCaseLink(setCaseLink(caseDetails.getCaseId(),
                caseDetails.getCaseData().getEthosCaseReference()));
        SubmitEvent submitEvent = ccdClient.submitCaseCaseReform(authToken, reformCaseDetails, ccdRequest);
        log.info("ECM Case {} migrated to Reform with caseId: {}", caseDetails.getCaseId(), submitEvent.getCaseId());
        caseDetails.getCaseData().setReformCaseLink(
                setCaseLink(String.valueOf(submitEvent.getCaseId()), reformCaseData.getEthosCaseReference()));
    }

    private void tidyJudgmentData(CaseDetails caseDetails) {
        var caseData = caseDetails.getCaseData();
        emptyIfNull(caseData.getJudgementCollection()).stream()
                .map(JudgementTypeItem::getValue)
                .filter(j -> !isNullOrEmpty(j.getDateJudgmentMade())
                             && !isNullOrEmpty(j.getDateJudgmentSent()))
                .filter(j -> LocalDate.parse(j.getDateJudgmentSent()).isBefore(
                    LocalDate.parse(j.getDateJudgmentMade())))
                .forEach(j -> j.setDateJudgmentSent(j.getDateJudgmentMade()));
    }

    private String setCaseLink(String caseId, String ethosCaseReference) {
        return generateMarkUp(ccdGatewayBaseUrl, caseId, ethosCaseReference);
    }
}
