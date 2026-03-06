package uk.gov.hmcts.ethos.replacement.docmosis.service.messagehandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ecm.compat.common.client.CcdClient;
import uk.gov.hmcts.ecm.compat.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.UpdateCaseMsg;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.datamodel.CloseDataModel;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.datamodel.PreAcceptDataModel;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.datamodel.RejectDataModel;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.datamodel.UpdateDataModel;
import uk.gov.hmcts.ethos.replacement.docmosis.service.ConciliationTrackService;

import java.io.IOException;
import java.util.List;

import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.YES;

@Slf4j
@RequiredArgsConstructor
@Service
@ConditionalOnProperty(prefix = "queue", name = "enabled", havingValue = "true")
public class SingleUpdateService {

    private final CcdClient ccdClient;
    private final ConciliationTrackService conciliationTrackService;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    public void sendUpdate(SubmitEvent submitEvent, String accessToken,
                           UpdateCaseMsg updateCaseMsg) throws IOException {

        String caseTypeId = UtilHelper.getCaseTypeId(updateCaseMsg.getCaseTypeId());
        String jurisdiction = updateCaseMsg.getJurisdiction();
        String caseId = String.valueOf(submitEvent.getCaseId());

        updateMultipleReferenceLinkMarkUp(submitEvent, accessToken, updateCaseMsg);
        CCDRequest returnedRequest = getReturnedRequest(accessToken, caseTypeId, jurisdiction, caseId, updateCaseMsg);
        submitEvent.setCaseData(returnedRequest.getCaseDetails().getCaseData());
        updateCaseMsg.runTask(submitEvent);
        conciliationTrackService.populateConciliationTrackForJurisdiction(submitEvent.getCaseData());
        ccdClient.submitEventForCase(accessToken,
                                     submitEvent.getCaseData(),
                                     caseTypeId,
                                     jurisdiction,
                                     returnedRequest,
                                     caseId);
    }

    private CCDRequest getReturnedRequest(String accessToken, String caseTypeId, String jurisdiction,
                                          String caseId, UpdateCaseMsg updateCaseMsg) throws IOException {

        if (updateCaseMsg.getDataModelParent() instanceof PreAcceptDataModel
            || updateCaseMsg.getDataModelParent() instanceof RejectDataModel) {
            return ccdClient.startEventForCasePreAcceptBulkSingle(
                accessToken,
                caseTypeId,
                jurisdiction,
                caseId);
        } else if (updateCaseMsg.getDataModelParent() instanceof UpdateDataModel
            && YES.equals(((UpdateDataModel) updateCaseMsg.getDataModelParent()).getIsRespondentRepRemovalUpdate())) {
            return ccdClient.startEventForCase(
                accessToken,
                caseTypeId,
                jurisdiction,
                caseId);
        } else if (updateCaseMsg.getDataModelParent() instanceof CloseDataModel) {
            return ccdClient.startDisposeEventForCase(
                accessToken,
                caseTypeId,
                jurisdiction,
                caseId);
        } else {
            return ccdClient.startEventForCaseAPIRole(
                accessToken,
                caseTypeId,
                jurisdiction,
                caseId);
        }
    }

    private void updateMultipleReferenceLinkMarkUp(SubmitEvent submitEvent, String accessToken,
                                                   UpdateCaseMsg updateCaseMsg) throws IOException {

        List<SubmitMultipleEvent> submitMultipleEvents = retrieveMultipleCase(accessToken, updateCaseMsg);
        log.info("size of submitMultipleEvent is:{}",
                 CollectionUtils.isEmpty(submitMultipleEvents) ? 0 : submitMultipleEvents.size());
        if (!CollectionUtils.isEmpty(submitMultipleEvents)) {
            submitEvent.getCaseData().setMultipleReferenceLinkMarkUp(
                generateMarkUp(ccdGatewayBaseUrl,
                               String.valueOf(submitMultipleEvents.get(0).getCaseId()),
                               submitEvent.getCaseData().getMultipleReference()));
        }
    }

    private List<SubmitMultipleEvent> retrieveMultipleCase(String authToken,
                                                           UpdateCaseMsg updateCaseMsg) throws IOException {

        return ccdClient.retrieveMultipleCasesElasticSearchWithRetries(
            authToken,
            updateCaseMsg.getCaseTypeId(),
            updateCaseMsg.getMultipleRef());
    }

    private String generateMarkUp(String ccdGatewayBaseUrl, String caseId, String ethosCaseRef) {

        String url = ccdGatewayBaseUrl + "/cases/case-details/" + caseId;
        return "<a target=\"_blank\" href=\"" + url + "\">" + ethosCaseRef + "</a>";
    }
}
