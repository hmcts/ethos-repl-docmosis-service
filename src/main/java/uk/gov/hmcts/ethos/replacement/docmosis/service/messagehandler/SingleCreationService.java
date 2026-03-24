package uk.gov.hmcts.ethos.replacement.docmosis.service.messagehandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.compat.common.client.CcdClient;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.UpdateCaseMsg;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.datamodel.CreationSingleDataModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.CLOSED_STATE;

@Slf4j
@RequiredArgsConstructor
@Service
@ConditionalOnProperty(prefix = "queue", name = "enabled", havingValue = "true")
public class SingleCreationService {

    private final CcdClient ccdClient;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    public void sendCreation(SubmitEvent oldSubmitEvent, String accessToken,
                             UpdateCaseMsg updateCaseMsg) throws IOException {

        CreationSingleDataModel creationSingleDataModel =
            ((CreationSingleDataModel) updateCaseMsg.getDataModelParent());
        String caseTypeId = creationSingleDataModel.getOfficeCT();
        String positionTypeCT = creationSingleDataModel.getPositionTypeCT();
        String ccdGatewayBaseUrl = creationSingleDataModel.getCcdGatewayBaseUrl();
        String jurisdiction = updateCaseMsg.getJurisdiction();
        String caseId = String.valueOf(oldSubmitEvent.getCaseId());

        log.info("Retrieve single case and check if it exists");
        SubmitEvent caseDestinationOffice =
            existCaseDestinationOffice(accessToken, oldSubmitEvent.getCaseData().getEthosCaseReference(), caseTypeId);

        if (caseDestinationOffice != null) {
            log.info("Amend case state as it is returned");
            updateExistingCase(caseDestinationOffice, oldSubmitEvent, caseId, caseTypeId, jurisdiction, accessToken,
                               ccdGatewayBaseUrl, positionTypeCT);
        } else {
            log.info("Transferring new case");
            transferNewCase(oldSubmitEvent, caseId, caseTypeId, ccdGatewayBaseUrl, positionTypeCT,
                            jurisdiction, accessToken);
        }
    }

    private void updateExistingCase(SubmitEvent caseDestinationOffice, SubmitEvent oldSubmitEvent, String caseId,
                                    String caseTypeId, String jurisdiction, String accessToken,
                                    String ccdGatewayBaseUrl, String positionTypeCT) throws IOException {

        String destinationCaseId = String.valueOf(caseDestinationOffice.getCaseId());
        CCDRequest returnedRequest = ccdClient.returnCaseCreationTransfer(accessToken,
                                                                          caseTypeId,
                                                                          jurisdiction,
                                                                          destinationCaseId);
        ccdClient.submitEventForCase(accessToken,
                                     generateCaseDataCaseTransfer(caseDestinationOffice.getCaseData(),
                                                                  oldSubmitEvent.getCaseData(),
                                                                  caseId,
                                                                  ccdGatewayBaseUrl,
                                                                  positionTypeCT,
                                                                  oldSubmitEvent.getState()),
                                     caseTypeId,
                                     jurisdiction,
                                     returnedRequest,
                                     destinationCaseId);
    }

    private void transferNewCase(SubmitEvent oldSubmitEvent, String caseId, String caseTypeId, String ccdGatewayBaseUrl,
                                 String positionTypeCT, String jurisdiction, String accessToken) throws IOException {

        CaseDetails newCaseDetailsCT = createCaseDetailsCaseTransfer(
            oldSubmitEvent.getCaseData(), caseId, caseTypeId,
            ccdGatewayBaseUrl, positionTypeCT, jurisdiction, oldSubmitEvent.getState());
        CCDRequest returnedRequest = ccdClient.startCaseCreationTransfer(accessToken, newCaseDetailsCT);
        SubmitEvent newCase = ccdClient.submitCaseCreation(accessToken, newCaseDetailsCT, returnedRequest);
        if (newCase != null) {
            updateSourceCaseTransferLink(newCase, oldSubmitEvent, accessToken, caseTypeId,
                                         jurisdiction, caseId);
        }
    }

    private void updateSourceCaseTransferLink(SubmitEvent newCase, SubmitEvent oldSubmitEvent, String accessToken,
                                              String caseTypeId, String jurisdiction, String caseId) throws
        IOException {
        String url = ccdGatewayBaseUrl + "/cases/case-details/" + newCase.getCaseId();
        String transferredCaseLink = "<a target=\"_blank\" href=\"" + url + "\">"
            + oldSubmitEvent.getCaseData().getEthosCaseReference() + "</a>";
        CCDRequest updateCCDRequest = ccdClient.startEventForCase(accessToken, caseTypeId, jurisdiction, caseId);
        updateCCDRequest.getCaseDetails().getCaseData().setTransferredCaseLink(transferredCaseLink);
        ccdClient.submitEventForCase(accessToken, updateCCDRequest.getCaseDetails().getCaseData(), caseTypeId,
                                     jurisdiction, updateCCDRequest, caseId);
    }

    private SubmitEvent existCaseDestinationOffice(String accessToken, String ethosCaseReference,
                                                   String destinationCaseTypeId) throws IOException {
        List<SubmitEvent> submitEvents = retrieveDestinationCase(accessToken,
                                                                 ethosCaseReference, destinationCaseTypeId);
        return !submitEvents.isEmpty() ? submitEvents.get(0) : null;
    }

    private List<SubmitEvent> retrieveDestinationCase(String authToken, String ethosCaseReference,
                                                      String destinationCaseTypeId) throws IOException {
        return ccdClient.retrieveCasesElasticSearch(
            authToken,
            destinationCaseTypeId,
            new ArrayList<>(Collections.singletonList(ethosCaseReference)));
    }

    private CaseDetails createCaseDetailsCaseTransfer(CaseData oldCaseData, String caseId, String caseTypeId,
                                                      String ccdGatewayBaseUrl, String positionTypeCT,
                                                      String jurisdiction, String state) {
        CaseDetails newCaseTransferCaseDetails = new CaseDetails();
        newCaseTransferCaseDetails.setCaseTypeId(caseTypeId);
        newCaseTransferCaseDetails.setJurisdiction(jurisdiction);
        newCaseTransferCaseDetails.setCaseData(
            generateNewCaseDataCaseTransfer(oldCaseData, caseId, ccdGatewayBaseUrl, positionTypeCT, state));
        return newCaseTransferCaseDetails;
    }

    private CaseData generateNewCaseDataCaseTransfer(CaseData oldCaseData, String caseId,
                                                     String ccdGatewayBaseUrl, String positionTypeCT,
                                                     String state) {
        return copyCaseData(oldCaseData, new CaseData(), caseId, ccdGatewayBaseUrl, positionTypeCT, state);

    }

    private CaseData generateCaseDataCaseTransfer(CaseData newCaseData, CaseData oldCaseData, String caseId,
                                                  String ccdGatewayBaseUrl, String positionTypeCT,
                                                  String state) {
        return copyCaseData(oldCaseData, newCaseData, caseId, ccdGatewayBaseUrl, positionTypeCT, state);
    }

    @SuppressWarnings("PMD.NcssCount")
    private CaseData copyCaseData(CaseData oldCaseData, CaseData newCaseData, String caseId,
                                  String ccdGatewayBaseUrl, String positionTypeCT, String state) {
        newCaseData.setEthosCaseReference(oldCaseData.getEthosCaseReference());
        newCaseData.setEcmCaseType(oldCaseData.getEcmCaseType());
        newCaseData.setClaimantTypeOfClaimant(oldCaseData.getClaimantTypeOfClaimant());
        newCaseData.setClaimantCompany(oldCaseData.getClaimantCompany());
        newCaseData.setClaimantIndType(oldCaseData.getClaimantIndType());
        newCaseData.setClaimantType(oldCaseData.getClaimantType());
        newCaseData.setClaimantOtherType(oldCaseData.getClaimantOtherType());
        newCaseData.setPreAcceptCase(oldCaseData.getPreAcceptCase());
        newCaseData.setReceiptDate(oldCaseData.getReceiptDate());
        newCaseData.setFeeGroupReference(oldCaseData.getFeeGroupReference());
        newCaseData.setClaimantWorkAddressQuestion(oldCaseData.getClaimantWorkAddressQuestion());
        newCaseData.setClaimantWorkAddressQRespondent(oldCaseData.getClaimantWorkAddressQRespondent());
        newCaseData.setRepresentativeClaimantType(oldCaseData.getRepresentativeClaimantType());
        newCaseData.setRespondentCollection(oldCaseData.getRespondentCollection());
        newCaseData.setRepCollection(oldCaseData.getRepCollection());
        newCaseData.setPositionType(oldCaseData.getPositionTypeCT());
        newCaseData.setDateToPosition(oldCaseData.getDateToPosition());
        newCaseData.setCurrentPosition(oldCaseData.getCurrentPosition());
        newCaseData.setDepositCollection(oldCaseData.getDepositCollection());
        newCaseData.setJudgementCollection(oldCaseData.getJudgementCollection());
        newCaseData.setJurCodesCollection(oldCaseData.getJurCodesCollection());
        newCaseData.setBfActions(oldCaseData.getBfActions());
        newCaseData.setUserLocation(oldCaseData.getUserLocation());
        newCaseData.setDocumentCollection(oldCaseData.getDocumentCollection());
        newCaseData.setAdditionalCaseInfoType(oldCaseData.getAdditionalCaseInfoType());
        newCaseData.setCaseNotes(oldCaseData.getCaseNotes());
        newCaseData.setClaimantWorkAddress(oldCaseData.getClaimantWorkAddress());
        newCaseData.setClaimantRepresentedQuestion(oldCaseData.getClaimantRepresentedQuestion());
        newCaseData.setCaseSource(oldCaseData.getCaseSource());
        newCaseData.setConciliationTrack(oldCaseData.getConciliationTrack());
        newCaseData.setCounterClaim(oldCaseData.getCounterClaim());
        newCaseData.setEccCases(oldCaseData.getEccCases());
        newCaseData.setRestrictedReporting(oldCaseData.getRestrictedReporting());
        newCaseData.setRespondent(oldCaseData.getRespondent());
        newCaseData.setClaimant(oldCaseData.getClaimant());
        newCaseData.setCaseRefECC(oldCaseData.getCaseRefECC());
        newCaseData.setCcdID(oldCaseData.getCcdID());
        newCaseData.setFlagsImageAltText(oldCaseData.getFlagsImageAltText());
        newCaseData.setCompanyPremises(oldCaseData.getCompanyPremises());

        if (state != null && !state.equals(CLOSED_STATE)) {
            newCaseData.setPositionType(positionTypeCT);
        }

        newCaseData.setMultipleReference(oldCaseData.getMultipleReference());
        log.info("setLeadClaimant is set to {}", oldCaseData.getLeadClaimant());
        newCaseData.setLeadClaimant(oldCaseData.getLeadClaimant());
        newCaseData.setReasonForCT(oldCaseData.getReasonForCT());
        log.info("ReasonForCT is set to {}", oldCaseData.getReasonForCT());
        newCaseData.setLinkedCaseCT(generateMarkUp(ccdGatewayBaseUrl, caseId, oldCaseData.getEthosCaseReference()));
        return newCaseData;
    }

    private String generateMarkUp(String ccdGatewayBaseUrl, String caseId, String ethosCaseRef) {
        String url = ccdGatewayBaseUrl + "/cases/case-details/" + caseId;
        return "<a target=\"_blank\" href=\"" + url + "\">" + ethosCaseRef + "</a>";
    }
}
