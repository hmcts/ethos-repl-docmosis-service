package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseCreationException;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

@Slf4j
@Service("caseCreationForCaseWorkerService")
public class CaseCreationForCaseWorkerService {

    private static final String MESSAGE = "Failed to create new case for case id : ";
    private final CcdClient ccdClient;
    private final SingleReferenceService singleReferenceService;
    private final MultipleReferenceService multipleReferenceService;

    public static final String TRANSFERRED_IN_STATE = "Transferred_In";
    public static final String TRANSFERRED_OUT_STATE = "Transferred_Out";

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

    public void createCaseTransfer(CaseData caseData, String jurisdiction, String authToken) {

//        try {
//            log.info("Create a new case details. Transferred in");
//            CaseDetails newCaseTransferCaseDetails = createCaseDetailsCaseTransfer(caseData, jurisdiction);
//            log.info("Send this case to the new office");
//            ccdClient.submitCaseCreation(authToken,
//                    newCaseTransferCaseDetails,
//                    ccdClient.startCaseCreation(authToken, newCaseTransferCaseDetails));
//        } catch (Exception ex) {
//            throw new CaseCreationException(MESSAGE + caseData.getEthosCaseReference() + ex.getMessage());
//        }
//
//        log.info("Update the current caseDetails state to transferred out");
//        caseData.setState(TRANSFERRED_OUT_STATE);

    }

//    private CaseDetails createCaseDetailsCaseTransfer(CaseData caseData, String jurisdiction) {
//        CaseDetails newCaseTransferCaseDetails = new CaseDetails();
//        //newCaseTransferCaseDetails.setCaseTypeId(caseDetails.getCaseData().getCaseTransferOffice());
//        newCaseTransferCaseDetails.setJurisdiction(jurisdiction);
//        newCaseTransferCaseDetails.setCaseData(generateCaseDataCaseTransfer(caseData));
//        return newCaseTransferCaseDetails;
//    }

//    private CaseData generateCaseDataCaseTransfer(CaseData caseData) {
//        CaseData newCaseData = new CaseData();
//        newCaseData.setState(TRANSFERRED_IN_STATE);
//        newCaseData.setEthosCaseReference(caseData.getEthosCaseReference());
//        newCaseData.setClaimantType(caseData.getClaimantType());
//        newCaseData.setClaimant(caseData.getClaimant());
//        return newCaseData;
//    }

}