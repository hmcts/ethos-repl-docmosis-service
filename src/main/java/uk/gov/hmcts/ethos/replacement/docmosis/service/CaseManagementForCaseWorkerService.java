package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.CaseCreationException;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@Slf4j
@Service("caseManagementForCaseWorkerService")
public class CaseManagementForCaseWorkerService {

    private final CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService;
    private final CcdClient ccdClient;
    private static final String MESSAGE = "Failed to link ECC case for case id : ";
    private static final String JURISDICTION_CODE_ECC = "BOC";
    private static final String EMPLOYER_CONTRACT_CLAIM_CODE = "ECC";

    @Autowired
    public CaseManagementForCaseWorkerService(CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService, CcdClient ccdClient) {
        this.caseRetrievalForCaseWorkerService = caseRetrievalForCaseWorkerService;
        this.ccdClient = ccdClient;
    }

    public void struckOutDefaults(CaseData caseData) {
        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
            ListIterator<RespondentSumTypeItem> itr = caseData.getRespondentCollection().listIterator();
            while (itr.hasNext()) {
                itr.next().getValue().setResponseStruckOut(NO);
            }
        }
    }

    public CaseData preAcceptCase(CCDRequest ccdRequest) {
        CaseData caseData = getCaseData(ccdRequest);
        if (caseData.getPreAcceptCase() != null) {
            if (caseData.getPreAcceptCase().getCaseAccepted().equals(YES)) {
                log.info("Accepting preAcceptCase");
                caseData.setState(ACCEPTED_STATE);
            } else {
                caseData.setState(REJECTED_STATE);
            }
        }
        return caseData;
    }

    public CaseData struckOutRespondents(CCDRequest ccdRequest) {
        CaseData caseData = getCaseData(ccdRequest);

        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {

            List<RespondentSumTypeItem> activeRespondent = new ArrayList<>();
            List<RespondentSumTypeItem> struckRespondent = new ArrayList<>();

            ListIterator<RespondentSumTypeItem> itr = caseData.getRespondentCollection().listIterator();

            while (itr.hasNext()) {

                RespondentSumTypeItem respondentSumTypeItem = itr.next();
                RespondentSumType respondentSumType = respondentSumTypeItem.getValue();

                if (respondentSumType.getResponseStruckOut() != null) {
                    if (respondentSumType.getResponseStruckOut().equals(YES)) {
                        struckRespondent.add(respondentSumTypeItem);
                    }
                    else {
                        activeRespondent.add(respondentSumTypeItem);
                    }
                }
                else{
                    respondentSumType.setResponseStruckOut(NO);
                    activeRespondent.add(respondentSumTypeItem);
                }
            }

            caseData.setRespondentCollection(Stream.concat(activeRespondent.stream(), struckRespondent.stream()).collect(Collectors.toList()));
        }

        return caseData;
    }

    private CaseData getCaseData(CCDRequest ccdRequest) {
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        log.info("EventId: " + ccdRequest.getEventId());
        return caseDetails.getCaseData();
    }

    public CaseData createECC(CaseDetails caseDetails, String authToken, List<String> errors, String callback) {
        CaseData currentCaseData = caseDetails.getCaseData();
        List<SubmitEvent> submitEvents = getCasesES(caseDetails, authToken);
        if (submitEvents != null && !submitEvents.isEmpty()) {
            SubmitEvent submitEvent = submitEvents.get(0);
            switch (callback) {
                case MID_EVENT_CALLBACK:
                    Helper.midRespondentECC(currentCaseData, submitEvent.getCaseData());
                    break;
                case ABOUT_TO_SUBMIT_EVENT_CALLBACK:
                    createECCLogic(currentCaseData, submitEvent.getCaseData(), String.valueOf(submitEvent.getCaseId()));
                    currentCaseData.setRespondentECC(null);
                    break;
                default:
                    sendUpdateSingleCaseECC(authToken, caseDetails, submitEvent.getCaseData(), String.valueOf(submitEvent.getCaseId()));
            }
        } else {
            errors.add("Case Reference Number not found");
        }
        return currentCaseData;
    }

    private List<SubmitEvent> getCasesES(CaseDetails caseDetails, String authToken) {
//        return new ArrayList<>(Collections.singleton(caseRetrievalForCaseWorkerService.caseRetrievalRequest(authToken,
//                caseDetails.getCaseTypeId(), caseDetails.getJurisdiction(), "1584620660814572")));
        return caseRetrievalForCaseWorkerService.casesRetrievalESRequest(caseDetails.getCaseId(), authToken,
                caseDetails.getCaseTypeId(), new ArrayList<>(Collections.singleton(caseDetails.getCaseData().getCaseRefECC())));
    }

    private void createECCLogic(CaseData caseData, CaseData originalCaseData, String originalId) {
        if (originalCaseData.getRespondentCollection() != null) {
            Optional<RespondentSumTypeItem> respondentChosen = originalCaseData.getRespondentCollection()
                    .stream()
                    .filter(respondentSumTypeItem -> respondentSumTypeItem.getValue().getRespondentName()
                            .equals(caseData.getRespondentECC().getValue().getCode()))
                    .findFirst();
            respondentChosen.ifPresent(respondentSumTypeItem ->
                    populateClaimantDetails(caseData, respondentSumTypeItem.getValue()));
        }
        populatePreAcceptCaseDetails(caseData);
        populateJurCodesCollection(caseData);
        populateRespondentCollectionDetails(caseData, originalCaseData.getClaimantIndType(), originalCaseData.getClaimantType());
        populateTribunalCorrespondenceDetails(caseData, originalCaseData);
        populateCaseDataDetails(caseData, originalCaseData, originalId);
    }

    private void populateClaimantDetails(CaseData caseData, RespondentSumType respondentSumType) {
        ClaimantType claimantType = new ClaimantType();
        claimantType.setClaimantAddressUK(respondentSumType.getRespondentAddress());
        caseData.setClaimantType(claimantType);

        ClaimantWorkAddressType claimantWorkAddressType = new ClaimantWorkAddressType();
        claimantWorkAddressType.setClaimantWorkAddress(respondentSumType.getRespondentAddress());
        caseData.setClaimantWorkAddress(claimantWorkAddressType);

        caseData.setClaimantTypeOfClaimant(COMPANY_TYPE_CLAIMANT);
        caseData.setClaimantCompany(respondentSumType.getRespondentName());
        caseData.setClaimantWorkAddressQuestion(YES);
        caseData.setReceiptDate(respondentSumType.getResponseReceivedDate());
    }

    private void populateRespondentCollectionDetails(CaseData caseData, ClaimantIndType originalClaimantIndType, ClaimantType originalClaimantType) {
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName(originalClaimantIndType.claimantFullName());
        respondentSumType.setRespondentACASNo(EMPLOYER_CONTRACT_CLAIM_CODE);
        respondentSumType.setRespondentACASQuestion(NO);
        respondentSumType.setRespondentAddress(originalClaimantType.getClaimantAddressUK());

        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setValue(respondentSumType);
        caseData.setRespondentCollection(new ArrayList<>(Collections.singleton(respondentSumTypeItem)));
    }

    private void populateJurCodesCollection(CaseData caseData) {
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJuridictionCodesList(JURISDICTION_CODE_ECC);
        JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
        jurCodesTypeItem.setId(JURISDICTION_CODE_ECC);
        jurCodesTypeItem.setValue(jurCodesType);
        caseData.setJurCodesCollection(new ArrayList<>(Collections.singleton(jurCodesTypeItem)));
    }

    private void populatePreAcceptCaseDetails(CaseData caseData) {
        CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
        casePreAcceptType.setCaseAccepted(YES);
        casePreAcceptType.setDateAccepted(Helper.formatCurrentDate2(LocalDate.now()));
        caseData.setPreAcceptCase(casePreAcceptType);
    }

    private void populateTribunalCorrespondenceDetails(CaseData caseData, CaseData originalCaseData) {
        caseData.setTribunalCorrespondenceAddress(originalCaseData.getTribunalCorrespondenceAddress());
        caseData.setTribunalCorrespondenceDX(originalCaseData.getTribunalCorrespondenceDX());
        caseData.setTribunalCorrespondenceEmail(originalCaseData.getTribunalCorrespondenceEmail());
        caseData.setTribunalCorrespondenceFax(originalCaseData.getTribunalCorrespondenceFax());
        caseData.setTribunalCorrespondenceTelephone(originalCaseData.getTribunalCorrespondenceTelephone());
    }

    private void populateCaseDataDetails(CaseData caseData, CaseData originalCaseData, String originalId) {
        caseData.setFeeGroupReference(originalCaseData.getFeeGroupReference());
        caseData.setCaseType(SINGLE_CASE_TYPE);
        caseData.setCaseSource(originalCaseData.getCaseSource());
        caseData.setCounterClaim(originalCaseData.getEthosCaseReference());
        caseData.setCcdID(originalId);
        caseData.setManagingOffice(originalCaseData.getManagingOffice() != null ? originalCaseData.getManagingOffice() : "");
        caseData.setAllocatedOffice(originalCaseData.getAllocatedOffice() != null ? originalCaseData.getAllocatedOffice() : "");
        caseData.setState(ACCEPTED_STATE);
    }

    private void sendUpdateSingleCaseECC(String authToken, CaseDetails currentCaseDetails, CaseData originalCaseData, String caseIdToLink) {
        try {
            originalCaseData.setCcdID(currentCaseDetails.getCaseId());
            originalCaseData.setCounterClaim(currentCaseDetails.getCaseData().getEthosCaseReference());
            CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, currentCaseDetails.getCaseTypeId(),
                    currentCaseDetails.getJurisdiction(), caseIdToLink);
            ccdClient.submitEventForCase(authToken, originalCaseData, currentCaseDetails.getCaseTypeId(),
                    currentCaseDetails.getJurisdiction(), returnedRequest, caseIdToLink);
        } catch (Exception e) {
            throw new CaseCreationException(MESSAGE + caseIdToLink + e.getMessage());
        }
    }

}
