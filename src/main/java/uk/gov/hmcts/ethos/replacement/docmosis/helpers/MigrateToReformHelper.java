package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.et.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.et.common.model.ccd.Address;
import uk.gov.hmcts.et.common.model.ccd.items.DocumentTypeItem;
import uk.gov.hmcts.et.common.model.ccd.types.AdditionalCaseInfoType;
import uk.gov.hmcts.et.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.et.common.model.ccd.types.ClaimantIndType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ET_ENGLAND_AND_WALES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ET_SCOTLAND;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_CASE_TYPE_ID;

public class MigrateToReformHelper {
    private MigrateToReformHelper() {
    }

    public static void caseMapper(CaseDetails caseDetails) {
        uk.gov.hmcts.et.common.model.ccd.CaseDetails caseDetailsReform = new uk.gov.hmcts.et.common.model.ccd.CaseDetails();
        caseDetailsReform.setCaseTypeId(getReformCaseTypeId(caseDetails.getCaseTypeId()));
        caseDetailsReform.setCaseData(getReformCaseData(caseDetails.getCaseData()));
    }

    private static uk.gov.hmcts.et.common.model.ccd.CaseData getReformCaseData(CaseData caseData) {
        uk.gov.hmcts.et.common.model.ccd.CaseData reformCaseData = new uk.gov.hmcts.et.common.model.ccd.CaseData();
        reformCaseData.setTribunalCorrespondenceAddress(addressMapper(caseData.getTribunalCorrespondenceAddress()));
        reformCaseData.setTribunalCorrespondenceTelephone(caseData.getTribunalCorrespondenceTelephone());
        reformCaseData.setTribunalCorrespondenceFax(caseData.getTribunalCorrespondenceFax());
        reformCaseData.setTribunalCorrespondenceDX(caseData.getTribunalCorrespondenceDX());
        reformCaseData.setTribunalCorrespondenceEmail(caseData.getTribunalCorrespondenceEmail());
        reformCaseData.setEthosCaseReference(caseData.getEthosCaseReference());
        reformCaseData.setEcmCaseType(caseData.getEcmCaseType());
        reformCaseData.setMultipleFlag(caseData.getMultipleFlag());
        reformCaseData.setMultipleReference(caseData.getMultipleReference());
        reformCaseData.setMultipleReferenceLinkMarkUp(caseData.getMultipleReferenceLinkMarkUp());
        reformCaseData.setLeadClaimant(caseData.getLeadClaimant());
        reformCaseData.setSubMultipleName(caseData.getSubMultipleName());
        reformCaseData.setNextListedDate(caseData.getNextListedDate());
        reformCaseData.setReceiptDate(caseData.getReceiptDate());
        reformCaseData.setFeeGroupReference(caseData.getFeeGroupReference());
        reformCaseData.setPositionType(caseData.getPositionType());
        reformCaseData.setFileLocation(
                DynamicFixedListType.from(caseData.getFileLocation(), caseData.getFileLocation(), true));
        reformCaseData.setCaseNotes(caseData.getCaseNotes());
        reformCaseData.setConciliationTrack(caseData.getConciliationTrack());
        reformCaseData.setPreAcceptCase((CasePreAcceptType) objectMapper(caseData.getPreAcceptCase(), CasePreAcceptType.class));
        reformCaseData.setClerkResponsible(
                DynamicFixedListType.from(caseData.getClerkResponsible(), caseData.getClerkResponsible(), true));
        );
        reformCaseData.setDocumentCollection(convertCaseDataDocumentCollection(caseData.getDocumentCollection()));
        reformCaseData.setAdrDocumentCollection(convertCaseDataDocumentCollection(caseData.getAdrDocumentCollection()));
        reformCaseData.setPiiDocumentCollection(convertCaseDataDocumentCollection(caseData.getPiiDocumentCollection()));
        reformCaseData.setAppealDocumentCollection(convertCaseDataDocumentCollection(caseData.getAppealDocumentCollection()));
        reformCaseData.setAdditionalCaseInfoType((AdditionalCaseInfoType) objectMapper(caseData.getAdditionalCaseInfoType(), AdditionalCaseInfoType.class));
        reformCaseData.setClaimantTypeOfClaimant(caseData.getClaimantTypeOfClaimant());
        reformCaseData.setClaimantCompany(caseData.getClaimantCompany());
        reformCaseData.setClaimantIndType(convertClaimantIndtype(caseData.getClaimantIndType()));
        reformCaseData.setClaimantType(caseData.getClaimantType());
        reformCaseData.setClaimantOtherType(caseData.getClaimantOtherType());
        reformCaseData.setClaimantWorkAddressQuestion(caseData.getClaimantWorkAddressQuestion());
        reformCaseData.setClaimantWorkAddressQRespondent(caseData.getClaimantWorkAddressQRespondent());
        reformCaseData.setClaimantWorkAddress(caseData.getClaimantWorkAddress());
        reformCaseData.setCompanyPremises(caseData.getCompanyPremises());
        reformCaseData.setClaimantRepresentedQuestion(caseData.getClaimantRepresentedQuestion());
        reformCaseData.setRepresentativeClaimantType(caseData.getRepresentativeClaimantType());
        reformCaseData.setRespondentCollection(caseData.getRespondentCollection());
        reformCaseData.setRepCollection(caseData.getRepCollection());
        reformCaseData.setJurCodesCollection(caseData.getJurCodesCollection());
        reformCaseData.setHearingCollection(caseData.getHearingCollection());
        reformCaseData.setJudgementCollection(caseData.getJudgementCollection());
        reformCaseData.setDepositCollection(caseData.getDepositCollection());
        reformCaseData.setBfActions(caseData.getBfActions());
        reformCaseData.setRestrictedReporting(caseData.getRestrictedReporting());
        reformCaseData.setCaseSource(caseData.getCaseSource()); // Migration?
        reformCaseData.setTargetHearingDate(caseData.getTargetHearingDate());
        reformCaseData.setClaimServedDate(caseData.getClaimServedDate());
        reformCaseData.setEccCases(caseData.getEccCases());
        reformCaseData.setCounterClaim(caseData.getCounterClaim());
        reformCaseData.setCaseRefECC(caseData.getCaseRefECC());
        reformCaseData.setRespondentECC(caseData.getRespondentECC());
        reformCaseData.setCcdID(caseData.getCcdID());
        reformCaseData.setCaseRefNumberCount(caseData.getCaseRefNumberCount());
        reformCaseData.setStartCaseRefNumber(caseData.getStartCaseRefNumber());
        reformCaseData.setMultipleRefNumber(caseData.getMultipleRefNumber());
        reformCaseData.setClaimant(caseData.getClaimant());
        reformCaseData.setRespondent(caseData.getRespondent());
        reformCaseData.setFlagsImageFileName(caseData.getFlagsImageFileName());
        reformCaseData.setFlagsImageAltText(caseData.getFlagsImageAltText());
        reformCaseData.setCurrentPosition(caseData.getCurrentPosition());
        reformCaseData.setDateToPosition(caseData.getDateToPosition());
        reformCaseData.setStateAPI(caseData.getStateAPI());
        reformCaseData.setOfficeCT(caseData.getOfficeCT());
        reformCaseData.setReasonForCT(caseData.getReasonForCT());
        reformCaseData.setRelatedCaseCT(caseData.getRelatedCaseCT());
        reformCaseData.setPositionTypeCT(caseData.getPositionTypeCT());
        reformCaseData.setLinkedCaseCT(caseData.getLinkedCaseCT());
        reformCaseData.setTransferredCaseLink(caseData.getTransferredCaseLink());
        reformCaseData.setTransferredCaseLinkSourceCaseId(caseData.getTransferredCaseLinkSourceCaseId());
        reformCaseData.setTransferredCaseLinkSourceCaseTypeId(caseData.getTransferredCaseLinkSourceCaseTypeId());
        reformCaseData.setDigitalCaseFiles(caseData.getDigitalCaseFiles());
        reformCaseData.setClaimantHearingPreference(caseData.getClaimantHearingPreference());
        return reformCaseData;
    }

    private static ClaimantIndType convertClaimantIndtype(uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantIndType claimantIndType) {
        ClaimantIndType reformClaimantIndType = new ClaimantIndType();
        reformClaimantIndType.setClaimantFirstNames(claimantIndType.getClaimantFirstNames());
        reformClaimantIndType.setClaimantLastName(claimantIndType.getClaimantLastName());
        reformClaimantIndType.setClaimantDateOfBirth(claimantIndType.getClaimantDateOfBirth());
        // TODO check claimant title mappings
        reformClaimantIndType.setClaimantTitle(claimantIndType.getClaimantTitle());
        reformClaimantIndType.setClaimantTitleOther(claimantIndType.getClaimantTitleOther());
        // TODO gender
        return reformClaimantIndType;
    }

    private static List<DocumentTypeItem> convertCaseDataDocumentCollection(
            List<uk.gov.hmcts.ecm.common.model.ccd.items.DocumentTypeItem> documentCollection) {
        return emptyIfNull(documentCollection).stream()
                .map(doc -> (DocumentTypeItem) objectMapper(doc, DocumentTypeItem.class)).toList();
    }

    private static Address addressMapper(uk.gov.hmcts.ecm.common.model.ccd.Address ecmAddress) {
        Address reformAddress = new Address();
        reformAddress.setAddressLine1(ecmAddress.getAddressLine1());
        reformAddress.setAddressLine2(ecmAddress.getAddressLine2());
        reformAddress.setAddressLine3(ecmAddress.getAddressLine3());
        reformAddress.setPostTown(ecmAddress.getPostTown());
        reformAddress.setCounty(ecmAddress.getCounty());
        reformAddress.setPostCode(ecmAddress.getPostCode());
        reformAddress.setCountry(ecmAddress.getCountry());
        return reformAddress;
    }

    private static String getReformCaseTypeId(String caseTypeId) {
        return switch (caseTypeId) {
            case BRISTOL_CASE_TYPE_ID, LEEDS_CASE_TYPE_ID, LONDON_CENTRAL_CASE_TYPE_ID, LONDON_EAST_CASE_TYPE_ID,
                 LONDON_SOUTH_CASE_TYPE_ID, MANCHESTER_CASE_TYPE_ID, MIDLANDS_EAST_CASE_TYPE_ID,
                 MIDLANDS_WEST_CASE_TYPE_ID, NEWCASTLE_CASE_TYPE_ID, WALES_CASE_TYPE_ID, WATFORD_CASE_TYPE_ID
                    -> ET_ENGLAND_AND_WALES;
            case SCOTLAND_CASE_TYPE_ID -> ET_SCOTLAND;
            default -> throw new IllegalArgumentException("Case type not supported");
        };
    }

    public static Object objectMapper(Object object, Class<?> classType) {
        var mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.convertValue(object, classType);
    }

}
