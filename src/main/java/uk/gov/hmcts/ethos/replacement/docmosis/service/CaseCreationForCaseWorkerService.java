package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseCreationException;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.io.IOException;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Slf4j
@Service("caseCreationForCaseWorkerService")
public class CaseCreationForCaseWorkerService {

    private static final String MESSAGE = "Failed to create new case for case id : ";
    private final CcdClient ccdClient;
    private final SingleReferenceService singleReferenceService;
    private final MultipleReferenceService multipleReferenceService;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    @Autowired
    public CaseCreationForCaseWorkerService(CcdClient ccdClient, SingleReferenceService singleReferenceService,
                                            MultipleReferenceService multipleReferenceService) {
        this.ccdClient = ccdClient;
        this.singleReferenceService = singleReferenceService;
        this.multipleReferenceService = multipleReferenceService;
    }

    public SubmitEvent caseCreationRequest(CCDRequest ccdRequest, String authToken) {
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        log.info("EventId: " + ccdRequest.getEventId());
        try {
            return ccdClient.submitCaseCreation(authToken, caseDetails,
                    ccdClient.startCaseCreation(authToken, caseDetails));
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + caseDetails.getCaseId() + ex.getMessage());
        }
    }

    public CaseData generateCaseRefNumbers(CCDRequest ccdRequest) {
        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();
        if (caseData.getCaseRefNumberCount() != null && Integer.parseInt(caseData.getCaseRefNumberCount()) > 0) {
            log.info("Case Type: " + ccdRequest.getCaseDetails().getCaseTypeId());
            log.info("Count: " + Integer.parseInt(caseData.getCaseRefNumberCount()));
            caseData.setStartCaseRefNumber(singleReferenceService.createReference(ccdRequest.getCaseDetails().getCaseTypeId(),
                    Integer.parseInt(caseData.getCaseRefNumberCount())));
            caseData.setMultipleRefNumber(multipleReferenceService.createReference(UtilHelper.getBulkCaseTypeId(ccdRequest.getCaseDetails().getCaseTypeId()), 1));
        }
        return caseData;
    }

    public void createCaseTransfer(CaseDetails caseDetails, String authToken) {

        CaseData caseData = caseDetails.getCaseData();

        SubmitEvent submitEvent;

        try {

            log.info("Create a new case details to move to the new office");

            CaseDetails newCaseTransferCaseDetails = createCaseDetailsCaseTransfer(caseDetails);

            log.info("Send this case to the new office");

            CCDRequest ccdRequest = getStartCaseCreationByState(authToken, caseData, newCaseTransferCaseDetails);
            submitEvent = ccdClient.submitCaseCreation(authToken, newCaseTransferCaseDetails, ccdRequest);

        } catch (Exception ex) {

            log.error("Error creating new case transfer: " + ex.getMessage());

            throw new CaseCreationException(MESSAGE + caseData.getEthosCaseReference() + ex.getMessage());
        }

        caseData.setLinkedCaseCT(MultiplesHelper.generateMarkUp(
                ccdGatewayBaseUrl, String.valueOf(submitEvent.getCaseId()), caseData.getEthosCaseReference()));
        caseData.setPositionType(caseData.getPositionTypeCT());

        log.info("Clearing the CT payload");

        caseData.setOfficeCT(null);
        caseData.setPositionTypeCT(null);
        caseData.setReasonForCT(null);

    }

    private CaseDetails createCaseDetailsCaseTransfer(CaseDetails caseDetails) {

        CaseData caseData = caseDetails.getCaseData();
        CaseDetails newCaseTransferCaseDetails = new CaseDetails();
        newCaseTransferCaseDetails.setCaseTypeId(caseData.getOfficeCT().getValue().getCode());
        newCaseTransferCaseDetails.setJurisdiction(caseDetails.getJurisdiction());
        newCaseTransferCaseDetails.setCaseData(generateCaseDataCaseTransfer(caseData, caseDetails.getCaseId()));
        return newCaseTransferCaseDetails;

    }

    private CaseData generateCaseDataCaseTransfer(CaseData caseData, String caseId) {

        CaseData newCaseData = new CaseData();
        newCaseData.setEthosCaseReference(caseData.getEthosCaseReference());
        newCaseData.setCaseType(caseData.getCaseType());
        newCaseData.setClaimantTypeOfClaimant(caseData.getClaimantTypeOfClaimant());
        newCaseData.setClaimantCompany(caseData.getClaimantCompany());
        newCaseData.setClaimantIndType(caseData.getClaimantIndType());
        newCaseData.setClaimantType(caseData.getClaimantType());
        newCaseData.setClaimantOtherType(caseData.getClaimantOtherType());
        newCaseData.setPreAcceptCase(caseData.getPreAcceptCase());
        newCaseData.setReceiptDate(caseData.getReceiptDate());
        newCaseData.setFeeGroupReference(caseData.getFeeGroupReference());
        newCaseData.setClaimantWorkAddressQuestion(caseData.getClaimantWorkAddressQuestion());
        newCaseData.setClaimantWorkAddressQRespondent(caseData.getClaimantWorkAddressQRespondent());
        newCaseData.setRepresentativeClaimantType(caseData.getRepresentativeClaimantType());
        newCaseData.setRespondentCollection(caseData.getRespondentCollection());
        newCaseData.setRepCollection(caseData.getRepCollection());
        newCaseData.setPositionType(caseData.getPositionTypeCT());
        newCaseData.setDateToPosition(caseData.getDateToPosition());
        newCaseData.setCurrentPosition(caseData.getCurrentPosition());
        newCaseData.setDepositCollection(caseData.getDepositCollection());
        newCaseData.setJudgementCollection(caseData.getJudgementCollection());
        newCaseData.setJurCodesCollection(caseData.getJurCodesCollection());
        newCaseData.setBfActions(caseData.getBfActions());
        newCaseData.setUserLocation(caseData.getUserLocation());
        newCaseData.setDocumentCollection(caseData.getDocumentCollection());
        newCaseData.setAdditionalCaseInfoType(caseData.getAdditionalCaseInfoType());
        newCaseData.setCaseNotes(caseData.getCaseNotes());
        newCaseData.setClaimantWorkAddress(caseData.getClaimantWorkAddress());
        newCaseData.setClaimantRepresentedQuestion(caseData.getClaimantRepresentedQuestion());
        newCaseData.setCaseSource(caseData.getCaseSource());
        newCaseData.setEt3Received(caseData.getEt3Received());
        newCaseData.setConciliationTrack(caseData.getConciliationTrack());
        newCaseData.setCounterClaim(caseData.getCounterClaim());
        newCaseData.setRestrictedReporting(caseData.getRestrictedReporting());
        newCaseData.setRespondent(caseData.getRespondent());
        newCaseData.setClaimant(caseData.getClaimant());
        newCaseData.setCaseRefECC(caseData.getCaseRefECC());
        newCaseData.setCcdID(caseData.getCcdID());
        newCaseData.setFlagsImageAltText(caseData.getFlagsImageAltText());
        newCaseData.setCompanyPremises(caseData.getCompanyPremises());

        newCaseData.setLinkedCaseCT(MultiplesHelper.generateMarkUp(ccdGatewayBaseUrl, caseId, caseData.getEthosCaseReference()));

        return newCaseData;

    }

    private CCDRequest getStartCaseCreationByState(String authToken, CaseData caseData,
                                                   CaseDetails newCaseTransferCaseDetails) throws IOException {

        if (caseData.getPreAcceptCase() != null
                && caseData.getPreAcceptCase().getCaseAccepted() != null
                && caseData.getPreAcceptCase().getCaseAccepted().equals(YES)) {

            log.info("MOVING TO ACCEPTED STATE");
            return ccdClient.startCaseCreationAccepted(authToken, newCaseTransferCaseDetails);

        } else {

            log.info("MOVING TO SUBMITTED STATE");
            return ccdClient.startCaseCreation(authToken, newCaseTransferCaseDetails);

        }
    }

}