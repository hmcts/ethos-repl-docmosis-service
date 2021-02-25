package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.*;

import java.time.LocalDate;
import java.util.*;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;

@Slf4j
public class ECCHelper {

    private static final String JURISDICTION_CODE_ECC = "BOC";
    private static final String EMPLOYER_CONTRACT_CLAIM_CODE = "ECC";
    private static final String WRONG_CASE_STATE_MESSAGE = "An Employment Counterclaim Case can only be raised against a case that has a state of Accepted.";
    private static final String ET3_RESPONSE_NOT_FOUND_MESSAGE = "An Employment Counterclaim Case can only be raised against a case that has an ET3 response.";

    public static void createECCLogic(CaseData caseData, CaseData originalCaseData, String originalId) {
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
        populateRepresentativeClaimantDetails(caseData, originalCaseData);
        populateRepCollectionDetails(caseData, originalCaseData);
        FlagsImageHelper.buildFlagsImageFileName(caseData);
    }

    public static boolean validCaseForECC(SubmitEvent submitEvent, List<String> errors) {
        boolean validCaseForECC = true;
        if(!submitEvent.getState().equals(ACCEPTED_STATE)) {
            errors.add(WRONG_CASE_STATE_MESSAGE);
            validCaseForECC = false;
        }
        if (!et3Received(submitEvent)) {
            errors.add(ET3_RESPONSE_NOT_FOUND_MESSAGE);
            validCaseForECC = false;
        }
        return validCaseForECC;
    }

    private static boolean et3Received(SubmitEvent submitEvent) {
        CaseData caseData = submitEvent.getCaseData();
        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
            for (RespondentSumTypeItem respondentSumTypeItem : caseData.getRespondentCollection()) {
                RespondentSumType respondentSumType = respondentSumTypeItem.getValue();
                if (respondentSumType.getResponseReceived() != null && respondentSumType.getResponseReceived().equals(YES)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void populateClaimantDetails(CaseData caseData, RespondentSumType respondentSumType) {
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

    private static void populateRespondentCollectionDetails(CaseData caseData, ClaimantIndType originalClaimantIndType, ClaimantType originalClaimantType) {
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName(originalClaimantIndType.claimantFullName());
        respondentSumType.setRespondentACASNo(EMPLOYER_CONTRACT_CLAIM_CODE);
        respondentSumType.setRespondentACASQuestion(NO);
        respondentSumType.setRespondentAddress(originalClaimantType.getClaimantAddressUK());

        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setValue(respondentSumType);
        caseData.setRespondentCollection(new ArrayList<>(Collections.singleton(respondentSumTypeItem)));
    }

    private static void populateJurCodesCollection(CaseData caseData) {
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJuridictionCodesList(JURISDICTION_CODE_ECC);
        JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
        jurCodesTypeItem.setId(JURISDICTION_CODE_ECC);
        jurCodesTypeItem.setValue(jurCodesType);
        caseData.setJurCodesCollection(new ArrayList<>(Collections.singleton(jurCodesTypeItem)));
    }

    private static void populatePreAcceptCaseDetails(CaseData caseData) {
        CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
        casePreAcceptType.setCaseAccepted(YES);
        casePreAcceptType.setDateAccepted(UtilHelper.formatCurrentDate2(LocalDate.now()));
        caseData.setPreAcceptCase(casePreAcceptType);
    }

    private static void populateTribunalCorrespondenceDetails(CaseData caseData, CaseData originalCaseData) {
        caseData.setTribunalCorrespondenceAddress(originalCaseData.getTribunalCorrespondenceAddress());
        caseData.setTribunalCorrespondenceDX(originalCaseData.getTribunalCorrespondenceDX());
        caseData.setTribunalCorrespondenceEmail(originalCaseData.getTribunalCorrespondenceEmail());
        caseData.setTribunalCorrespondenceFax(originalCaseData.getTribunalCorrespondenceFax());
        caseData.setTribunalCorrespondenceTelephone(originalCaseData.getTribunalCorrespondenceTelephone());
    }

    private static void populateCaseDataDetails(CaseData caseData, CaseData originalCaseData, String originalId) {
        caseData.setFeeGroupReference(originalCaseData.getFeeGroupReference());
        caseData.setCaseType(SINGLE_CASE_TYPE);
        caseData.setCaseSource(originalCaseData.getCaseSource());
        caseData.setCounterClaim(originalCaseData.getEthosCaseReference());
        caseData.setCcdID(originalId);
        caseData.setManagingOffice(originalCaseData.getManagingOffice() != null ? originalCaseData.getManagingOffice() : "");
        caseData.setAllocatedOffice(originalCaseData.getAllocatedOffice() != null ? originalCaseData.getAllocatedOffice() : "");
        caseData.setState(ACCEPTED_STATE);
    }

    private static void populateRepresentativeClaimantDetails (CaseData caseData, CaseData originalCaseData) {
        if (originalCaseData.getRepCollection() != null && !originalCaseData.getRepCollection().isEmpty()) {
            for (RepresentedTypeRItem representedTypeRItem : originalCaseData.getRepCollection()) {
                RepresentedTypeR representedTypeR = representedTypeRItem.getValue();
                if (representedTypeR.getRespRepName() != null && representedTypeR.getRespRepName().equals(caseData.getClaimantCompany())) {
                    RepresentedTypeC representedTypeC = new RepresentedTypeC();
                    representedTypeC.setNameOfRepresentative(nullCheck(representedTypeR.getNameOfRepresentative()));
                    representedTypeC.setNameOfOrganisation(nullCheck(representedTypeR.getNameOfOrganisation()));
                    representedTypeC.setRepresentativeReference(nullCheck(representedTypeR.getRepresentativeReference()));
                    representedTypeC.setRepresentativeOccupation(nullCheck(representedTypeR.getRepresentativeOccupation()));
                    representedTypeC.setRepresentativeOccupationOther(nullCheck(representedTypeR.getRepresentativeOccupationOther()));
                    representedTypeC.setRepresentativeAddress(representedTypeR.getRepresentativeAddress());
                    representedTypeC.setRepresentativePhoneNumber(nullCheck(representedTypeR.getRepresentativePhoneNumber()));
                    representedTypeC.setRepresentativeMobileNumber(nullCheck(representedTypeR.getRepresentativeMobileNumber()));
                    representedTypeC.setRepresentativeEmailAddress(nullCheck(representedTypeR.getRepresentativeEmailAddress()));
                    representedTypeC.setRepresentativePreference(nullCheck(representedTypeR.getRepresentativePreference()));
                    caseData.setRepresentativeClaimantType(representedTypeC);
                    caseData.setClaimantRepresentedQuestion(YES);
                    break;
                }
            }
        }
    }

    private static void populateRepCollectionDetails(CaseData caseData, CaseData originalCaseData) {
        RepresentedTypeC representativeClaimantType = originalCaseData.getRepresentativeClaimantType();
        if (representativeClaimantType != null && originalCaseData.getClaimantRepresentedQuestion().equals(YES)) {
            RepresentedTypeR representedTypeR = new RepresentedTypeR();
            representedTypeR.setRespRepName(caseData.getRespondentCollection().get(0).getValue().getRespondentName());
            representedTypeR.setNameOfRepresentative(nullCheck(representativeClaimantType.getNameOfRepresentative()));
            representedTypeR.setNameOfOrganisation(nullCheck(representativeClaimantType.getNameOfOrganisation()));
            representedTypeR.setRepresentativeReference(nullCheck(representativeClaimantType.getRepresentativeReference()));
            representedTypeR.setRepresentativeOccupation(nullCheck(representativeClaimantType.getRepresentativeOccupation()));
            representedTypeR.setRepresentativeOccupationOther(nullCheck(representativeClaimantType.getRepresentativeOccupationOther()));
            representedTypeR.setRepresentativeAddress(representativeClaimantType.getRepresentativeAddress());
            representedTypeR.setRepresentativePhoneNumber(nullCheck(representativeClaimantType.getRepresentativePhoneNumber()));
            representedTypeR.setRepresentativeMobileNumber(nullCheck(representativeClaimantType.getRepresentativeMobileNumber()));
            representedTypeR.setRepresentativeEmailAddress(nullCheck(representativeClaimantType.getRepresentativeEmailAddress()));
            representedTypeR.setRepresentativePreference(nullCheck(representativeClaimantType.getRepresentativePreference()));
            RepresentedTypeRItem representedTypeRItem = new RepresentedTypeRItem();
            representedTypeRItem.setValue(representedTypeR);
            caseData.setRepCollection(new ArrayList<>(Collections.singleton(representedTypeRItem)));
        }
    }

}
