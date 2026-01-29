package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.et.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.et.common.model.ccd.Address;
import uk.gov.hmcts.et.common.model.ccd.Document;
import uk.gov.hmcts.et.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.et.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.et.common.model.ccd.items.DepositTypeItem;
import uk.gov.hmcts.et.common.model.ccd.items.DocumentTypeItem;
import uk.gov.hmcts.et.common.model.ccd.items.EccCounterClaimTypeItem;
import uk.gov.hmcts.et.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.et.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.et.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.et.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.et.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.et.common.model.ccd.types.AdditionalCaseInfoType;
import uk.gov.hmcts.et.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.et.common.model.ccd.types.ClaimantHearingPreference;
import uk.gov.hmcts.et.common.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.et.common.model.ccd.types.ClaimantOtherType;
import uk.gov.hmcts.et.common.model.ccd.types.ClaimantType;
import uk.gov.hmcts.et.common.model.ccd.types.ClaimantWorkAddressType;
import uk.gov.hmcts.et.common.model.ccd.types.CompanyPremisesType;
import uk.gov.hmcts.et.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.et.common.model.ccd.types.DigitalCaseFileType;
import uk.gov.hmcts.et.common.model.ccd.types.HearingType;
import uk.gov.hmcts.et.common.model.ccd.types.JudgementType;
import uk.gov.hmcts.et.common.model.ccd.types.RepresentedTypeC;
import uk.gov.hmcts.et.common.model.ccd.types.RestrictedReportingType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.elasticsearch.common.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ET_ENGLAND_AND_WALES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ET_SCOTLAND;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.GLASGOW_OFFICE;
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
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.DocumentHelper.convertLegacyDocsToNewDocNaming;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.DocumentHelper.setDocumentTypeForDocumentCollection;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.JurisdictionHelper.JURISDICTION_OUTCOME_INPUT_IN_ERROR;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.ListingHelper.getHearingRoom;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.MISC;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.OTHER;

public class MigrateToReformHelper {

    private MigrateToReformHelper() {
        // Access through static methods
    }

    public static uk.gov.hmcts.et.common.model.ccd.CaseDetails reformCaseMapper(CaseDetails caseDetails) {
        var caseDetailsReform = new uk.gov.hmcts.et.common.model.ccd.CaseDetails();
        caseDetailsReform.setCaseTypeId(getReformCaseTypeId(caseDetails.getCaseTypeId()));
        caseDetailsReform.setJurisdiction(caseDetails.getJurisdiction());
        caseDetailsReform.setCaseData(getReformCaseData(caseDetails));
        caseDetailsReform.setState(caseDetails.getState());
        setJurisdictionDisposalDate(caseDetailsReform.getCaseData());
        return caseDetailsReform;
    }

    private static uk.gov.hmcts.et.common.model.ccd.CaseData getReformCaseData(CaseDetails caseDetails) {
        var reformCaseData = new uk.gov.hmcts.et.common.model.ccd.CaseData();
        CaseData caseData = caseDetails.getCaseData();
        convertLegacyDocsToNewDocNaming(caseData);
        setDocumentTypeForDocumentCollection(caseData);
        if (SCOTLAND_CASE_TYPE_ID.equals(caseDetails.getCaseTypeId())) {
            reformCaseData.setManagingOffice(caseData.getManagingOffice());
            reformCaseData.setAllocatedOffice(caseData.getAllocatedOffice());
            reformCaseData.setFileLocationAberdeen(createDynamicListFromFixedList(caseData.getFileLocationAberdeen()));
            reformCaseData.setFileLocationDundee(createDynamicListFromFixedList(caseData.getFileLocationDundee()));
            reformCaseData.setFileLocationEdinburgh(
                    createDynamicListFromFixedList(caseData.getFileLocationEdinburgh()));
            reformCaseData.setFileLocationGlasgow(createDynamicListFromFixedList(caseData.getFileLocationGlasgow()));
        } else {
            reformCaseData.setManagingOffice(getEnglandWalesTribunalOffice(caseDetails.getCaseTypeId()));
            reformCaseData.setFileLocation(createDynamicListFromFixedList(caseData.getFileLocation()));
        }
        reformCaseData.setMigratedFromEcm(YES);
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
        reformCaseData.setEcmFeeGroupReference(caseData.getFeeGroupReference());
        reformCaseData.setPositionType(getPositionType(caseData.getPositionType()));
        reformCaseData.setCaseNotes(caseData.getCaseNotes());
        reformCaseData.setConciliationTrack(caseData.getConciliationTrack());
        reformCaseData.setPreAcceptCase(
                (CasePreAcceptType) objectMapper(caseData.getPreAcceptCase(), CasePreAcceptType.class));
        reformCaseData.setClerkResponsible(createDynamicListFromFixedList(caseData.getClerkResponsible()));
        reformCaseData.setDocumentCollection(convertCaseDataDocumentCollection(caseData.getDocumentCollection()));
        reformCaseData.setAdrDocumentCollection(convertCaseDataDocumentCollection(caseData.getAdrDocumentCollection()));
        reformCaseData.setPiiDocumentCollection(convertCaseDataDocumentCollection(caseData.getPiiDocumentCollection()));
        reformCaseData.setAppealDocumentCollection(
                convertCaseDataDocumentCollection(caseData.getAppealDocumentCollection()));
        reformCaseData.setAdditionalCaseInfoType((AdditionalCaseInfoType)
                objectMapper(caseData.getAdditionalCaseInfoType(), AdditionalCaseInfoType.class));
        reformCaseData.setClaimantTypeOfClaimant(caseData.getClaimantTypeOfClaimant());
        reformCaseData.setClaimantCompany(caseData.getClaimantCompany());
        reformCaseData.setClaimantIndType(convertClaimantIndtype(caseData.getClaimantIndType()));
        reformCaseData.setClaimantType((ClaimantType) objectMapper(caseData.getClaimantType(), ClaimantType.class));
        reformCaseData.setClaimantOtherType(
                (ClaimantOtherType) objectMapper(caseData.getClaimantOtherType(), ClaimantOtherType.class));
        reformCaseData.setClaimantWorkAddressQuestion(caseData.getClaimantWorkAddressQuestion());
        reformCaseData.setClaimantWorkAddressQRespondent(
                convertDynamicList(caseData.getClaimantWorkAddressQRespondent()));
        reformCaseData.setClaimantWorkAddress((ClaimantWorkAddressType)
                objectMapper(caseData.getClaimantWorkAddress(), ClaimantWorkAddressType.class));
        reformCaseData.setCompanyPremises((CompanyPremisesType)
                objectMapper(caseData.getCompanyPremises(), CompanyPremisesType.class));
        reformCaseData.setClaimantRepresentedQuestion(caseData.getClaimantRepresentedQuestion());
        reformCaseData.setRepresentativeClaimantType((RepresentedTypeC)
                objectMapper(caseData.getRepresentativeClaimantType(), RepresentedTypeC.class));
        reformCaseData.setRespondentCollection(convertRespondentCollection(caseData.getRespondentCollection()));
        reformCaseData.setRepCollection(convertRepCollection(caseData.getRepCollection()));
        reformCaseData.setJurCodesCollection(convertJurCodesCollection(caseData.getJurCodesCollection()));
        reformCaseData.setHearingCollection(
                convertHearingCollection(caseData.getHearingCollection(), caseDetails.getCaseTypeId()));
        reformCaseData.setJudgementCollection(covertJudgementCollection(caseData.getJudgementCollection()));
        reformCaseData.setDepositCollection(convertDepositCollection(caseData.getDepositCollection()));
        reformCaseData.setBfActions(convertBfActions(caseData.getBfActions()));
        reformCaseData.setRestrictedReporting((RestrictedReportingType)
                objectMapper(caseData.getRestrictedReporting(), RestrictedReportingType.class));
        reformCaseData.setCaseSource(caseData.getCaseSource());
        reformCaseData.setTargetHearingDate(caseData.getTargetHearingDate());
        reformCaseData.setClaimServedDate(caseData.getClaimServedDate());
        reformCaseData.setEccCases(convertEccCases(caseData.getEccCases()));
        reformCaseData.setCounterClaim(caseData.getCounterClaim());
        reformCaseData.setCaseRefECC(caseData.getCaseRefECC());
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
        reformCaseData.setStateAPI(caseDetails.getState());
        reformCaseData.setReasonForCT(caseData.getReasonForCT());
        reformCaseData.setRelatedCaseCT(caseData.getRelatedCaseCT());
        reformCaseData.setPositionTypeCT(caseData.getPositionTypeCT());
        reformCaseData.setLinkedCaseCT(caseData.getLinkedCaseCT());
        reformCaseData.setTransferredCaseLink(caseData.getTransferredCaseLink());
        reformCaseData.setTransferredCaseLinkSourceCaseId(caseData.getTransferredCaseLinkSourceCaseId());
        reformCaseData.setTransferredCaseLinkSourceCaseTypeId(caseData.getTransferredCaseLinkSourceCaseTypeId());
        reformCaseData.setDigitalCaseFile((DigitalCaseFileType)
                objectMapper(caseData.getDigitalCaseFile(), DigitalCaseFileType.class));
        reformCaseData.setClaimantHearingPreference((ClaimantHearingPreference)
                objectMapper(caseData.getClaimantHearingPreference(), ClaimantHearingPreference.class));
        return reformCaseData;
    }

    private static String getPositionType(String positionType) {
        if (isNullOrEmpty(positionType) || "Case transferred to Reform ECM".equals(positionType)) {
            return null;
        } else {
            return positionType;
        }
    }

    private static List<HearingTypeItem> convertHearingCollection(
            List<uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem> hearingCollection, String caseTypeId) {
        if (isEmpty(hearingCollection)) {
            return List.of();
        }
        List<HearingTypeItem> hc = new ArrayList<>();
        for (var hearingTypeItem : hearingCollection) {
            HearingType reformHearingType = new HearingType();
            var hearingType = hearingTypeItem.getValue();
            reformHearingType.setHearingType(hearingType.getHearingType());
            reformHearingType.setHearingNotesDocument((Document)
                    objectMapper(hearingType.getHearingNotesDocument(), Document.class));
            reformHearingType.setHearingFormat(hearingType.getHearingFormat());
            reformHearingType.setJudicialMediation(hearingType.getJudicialMediation());
            reformHearingType.setHearingPublicPrivate(hearingType.getHearingPublicPrivate());
            reformHearingType.setHearingNumber(hearingType.getHearingNumber());
            reformHearingType.setHearingEstLengthNum(hearingType.getHearingEstLengthNum());
            reformHearingType.setHearingEstLengthNumType(hearingType.getHearingEstLengthNumType());
            reformHearingType.setHearingSitAlone(hearingType.getHearingSitAlone());
            reformHearingType.setHearingStage(hearingType.getHearingStage());
            reformHearingType.setHearingNotes(hearingType.getHearingNotes());
            reformHearingType.setJudge(createDynamicListFromFixedList(hearingType.getJudge()));
            reformHearingType.setHearingEEMember(createDynamicListFromFixedList(hearingType.getHearingEEMember()));
            reformHearingType.setHearingERMember(createDynamicListFromFixedList(hearingType.getHearingERMember()));

            if (SCOTLAND_CASE_TYPE_ID.equals(caseTypeId)) {
                reformHearingType.setHearingVenueScotland(hearingType.getHearingVenue());
                reformHearingType.setHearingAberdeen(createDynamicListFromFixedList(hearingType.getHearingAberdeen()));
                reformHearingType.setHearingDundee(createDynamicListFromFixedList(hearingType.getHearingDundee()));
                reformHearingType.setHearingEdinburgh(
                        createDynamicListFromFixedList(hearingType.getHearingEdinburgh()));
                reformHearingType.setHearingGlasgow(createDynamicListFromFixedList(hearingType.getHearingGlasgow()));
            } else {
                reformHearingType.setHearingVenue(createDynamicListFromFixedList(hearingType.getHearingVenue()));
            }

            reformHearingType.setHearingDateCollection(
                    createHearingDateCollection(hearingType.getHearingDateCollection(), caseTypeId));

            HearingTypeItem reformHearingTypeItem = new HearingTypeItem();
            reformHearingTypeItem.setId(hearingTypeItem.getId());
            reformHearingTypeItem.setValue(reformHearingType);
            hc.add(reformHearingTypeItem);
        }

        return hc;
    }

    private static List<DateListedTypeItem> createHearingDateCollection(
            List<uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem> hearingDateCollection,
            String caseTypeId) {
        if (isEmpty(hearingDateCollection)) {
            return List.of();
        }

        List<DateListedTypeItem> hd = new ArrayList<>();
        for (var dateListedTypeItem : hearingDateCollection) {
            DateListedType reformDateListedType = new DateListedType();
            var ecmDateListedType = dateListedTypeItem.getValue();

            reformDateListedType.setListedDate(ecmDateListedType.getListedDate());
            reformDateListedType.setHearingStatus(ecmDateListedType.getHearingStatus());
            reformDateListedType.setPostponedBy(ecmDateListedType.getPostponedBy());
            reformDateListedType.setPostponedDate(ecmDateListedType.getPostponedDate());
            if (SCOTLAND_CASE_TYPE_ID.equals(caseTypeId)) {
                reformDateListedType.setHearingTypeReadingDeliberation(
                        ecmDateListedType.getHearingTypeReadingDeliberation());
                reformDateListedType.setHearingVenueDayScotland(ecmDateListedType.getHearingVenueDay());
                switch (ecmDateListedType.getHearingVenueDay()) {
                    case GLASGOW_OFFICE -> {
                        reformDateListedType.setHearingGlasgow(
                                createDynamicListFromFixedList(ecmDateListedType.getHearingGlasgow()));
                        setGlasgowHearingRoom(ecmDateListedType, reformDateListedType);
                    }
                    case "Aberdeen" -> {
                        reformDateListedType.setHearingAberdeen(
                                createDynamicListFromFixedList(ecmDateListedType.getHearingAberdeen()));
                        setAberdeenHearingRoom(ecmDateListedType, reformDateListedType);
                    }
                    case "Dundee" -> {
                        reformDateListedType.setHearingDundee(
                                createDynamicListFromFixedList(ecmDateListedType.getHearingDundee()));
                        switch (ecmDateListedType.getHearingDundee()) {
                            case "Dundee" -> reformDateListedType.setHearingRoom(
                                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomDundee()));
                            case "Tribunal" -> reformDateListedType.setHearingRoom(
                                    createDynamicListFromFixedList(ecmDateListedType.getRoomDundeeTribunal()));
                            default -> {
                                // No action needed, as the hearingRoom is already set to null
                            }
                        }
                    }
                    case "Edinburgh" -> {
                        reformDateListedType.setHearingEdinburgh(
                                createDynamicListFromFixedList(ecmDateListedType.getHearingEdinburgh()));
                        reformDateListedType.setHearingRoom(
                                createDynamicListFromFixedList(ecmDateListedType.getHearingRoomEdinburgh()));
                    }
                    default -> {
                        // No action needed, as the hearingVenueDayScotland is already set
                    }
                }
            } else {
                reformDateListedType.setHearingVenueDay(
                        createDynamicListFromFixedList(ecmDateListedType.getHearingVenueDay()));
                String hearingRoom = getHearingRoom(ecmDateListedType);
                if (!isNullOrEmpty(hearingRoom.trim())) {
                    reformDateListedType.setHearingRoom(createDynamicListFromFixedList(hearingRoom));
                }
            }
            reformDateListedType.setHearingClerk(createDynamicListFromFixedList(ecmDateListedType.getHearingClerk()));
            reformDateListedType.setHearingCaseDisposed(ecmDateListedType.getHearingCaseDisposed());
            reformDateListedType.setHearingPartHeard(ecmDateListedType.getHearingPartHeard());
            reformDateListedType.setHearingReservedJudgement(ecmDateListedType.getHearingReservedJudgement());
            reformDateListedType.setAttendeeClaimant(ecmDateListedType.getAttendeeClaimant());
            reformDateListedType.setAttendeeNonAttendees(ecmDateListedType.getAttendeeNonAttendees());
            reformDateListedType.setAttendeeRespNoRep(ecmDateListedType.getAttendeeRespNoRep());
            reformDateListedType.setAttendeeRepOnly(ecmDateListedType.getAttendeeRepOnly());
            reformDateListedType.setHearingTimingStart(ecmDateListedType.getHearingTimingStart());
            reformDateListedType.setHearingTimingBreak(ecmDateListedType.getHearingTimingBreak());
            reformDateListedType.setHearingTimingFinish(ecmDateListedType.getHearingTimingFinish());
            reformDateListedType.setHearingTimingResume(ecmDateListedType.getHearingTimingResume());
            reformDateListedType.setHearingTimingDuration(ecmDateListedType.getHearingTimingDuration());
            reformDateListedType.setHearingNotes2(ecmDateListedType.getHearingNotes2());

            DateListedTypeItem reformDateListedTypeItem = new DateListedTypeItem();
            reformDateListedTypeItem.setId(dateListedTypeItem.getId());
            reformDateListedTypeItem.setValue(reformDateListedType);
            hd.add(reformDateListedTypeItem);
        }

        return hd;
    }

    private static void setAberdeenHearingRoom(uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType ecmDateListedType,
                                               DateListedType reformDateListedType) {
        switch (ecmDateListedType.getHearingAberdeen()) {
            case "Aberdeen" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomAberdeen()));
            case "I J C" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomIJC()));
            case "Inverness" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomInverness()));
            case "Kirkwall" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomKirkawall()));
            case "Lerwick" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomLerwick()));
            case "Portree" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomPortree()));
            case "Shetland" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomRRShetland()));
            case "Stornoway" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomStornoway()));
            case "Wick" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomWick()));
            default -> {
                // No action needed, as the hearingRoom is already set to null
            }
        }
    }

    private static void setGlasgowHearingRoom(uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType ecmDateListedType,
                                              DateListedType reformDateListedType) {
        switch (ecmDateListedType.getHearingGlasgow()) {
            case "Glasgow COET" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomGlasgow()));
            case "GTC" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomGTC()));
            case "Cambeltown HC" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomCambeltown()));
            case "Dumfries HC" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomDumfries()));
            case "Fort William SC" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomFortWilliam()));
            case "Kirkcudbright" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomKirkcubright()));
            case "Lochmaddy HC" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomLockmaddy()));
            case "Oban HC" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomOban()));
            case "Portree HC" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomPortree()));
            case "Stirling SC" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomStirling()));
            case "Stornoway HC" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomStornoway()));
            case "Stranraer HC" -> reformDateListedType.setHearingRoom(
                    createDynamicListFromFixedList(ecmDateListedType.getHearingRoomStranraer()));
            default -> {
                // No action needed, as the hearingRoom is already set to null
            }
        }
    }

    private static List<JudgementTypeItem> covertJudgementCollection(
            List<uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem> judgementCollection) {
        return emptyIfNull(judgementCollection).stream()
                .map(judgement -> (JudgementTypeItem) objectMapper(judgement, JudgementTypeItem.class)).toList();
    }

    private static List<EccCounterClaimTypeItem> convertEccCases(
            List<uk.gov.hmcts.ecm.common.model.ccd.items.EccCounterClaimTypeItem> eccCases) {
        return emptyIfNull(eccCases).stream()
            .map(eccCase -> (EccCounterClaimTypeItem) objectMapper(eccCase, EccCounterClaimTypeItem.class)).toList();
    }

    private static DynamicFixedListType convertDynamicList(
            uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType dynamicList) {
        if (ObjectUtils.isEmpty(dynamicList) || ObjectUtils.isEmpty(dynamicList.getValue())) {
            return null;
        } else {
            return DynamicFixedListType.from(dynamicList.getValue().getCode(), dynamicList.getValue().getLabel(), true);
        }
    }

    private static List<DepositTypeItem> convertDepositCollection(
            List<uk.gov.hmcts.ecm.common.model.ccd.items.DepositTypeItem> depositCollection) {
        return emptyIfNull(depositCollection).stream()
                .map(deposit -> (DepositTypeItem) objectMapper(deposit, DepositTypeItem.class)).toList();
    }

    private static List<RepresentedTypeRItem> convertRepCollection(
            List<uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem> repCollection) {
        return emptyIfNull(repCollection).stream()
                .map(rep -> (RepresentedTypeRItem) objectMapper(rep, RepresentedTypeRItem.class)).toList();
    }

    private static DynamicFixedListType createDynamicListFromFixedList(String fixedListValue) {
        if (isNullOrEmpty(fixedListValue)) {
            return null;
        }

        return DynamicFixedListType.from(fixedListValue, fixedListValue, true);
    }

    private static String getEnglandWalesTribunalOffice(String caseTypeId) {
        return switch (caseTypeId) {
            case BRISTOL_CASE_TYPE_ID -> "Bristol";
            case LEEDS_CASE_TYPE_ID -> "Leeds";
            case LONDON_CENTRAL_CASE_TYPE_ID -> "London Central";
            case LONDON_EAST_CASE_TYPE_ID -> "London East";
            case LONDON_SOUTH_CASE_TYPE_ID -> "London South";
            case MANCHESTER_CASE_TYPE_ID -> "Manchester";
            case MIDLANDS_EAST_CASE_TYPE_ID -> "Midlands East";
            case MIDLANDS_WEST_CASE_TYPE_ID -> "Midlands West";
            case NEWCASTLE_CASE_TYPE_ID -> "Newcastle";
            case WALES_CASE_TYPE_ID -> "Wales";
            case WATFORD_CASE_TYPE_ID -> "Watford";
            default -> throw new IllegalArgumentException("Case type not supported");
        };
    }

    private static List<RespondentSumTypeItem> convertRespondentCollection(
            List<uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem> respondentCollection) {
        return emptyIfNull(respondentCollection).stream()
            .map(respondent -> (RespondentSumTypeItem) objectMapper(respondent, RespondentSumTypeItem.class)).toList();
    }

    private static List<BFActionTypeItem> convertBfActions(
            List<uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem> bfActions) {
        return emptyIfNull(bfActions).stream()
                .map(bfAction -> (BFActionTypeItem) objectMapper(bfAction, BFActionTypeItem.class)).toList();
    }

    private static List<JurCodesTypeItem> convertJurCodesCollection(
            List<uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem> jurCodesCollection) {
        return emptyIfNull(jurCodesCollection).stream()
                .filter(j -> ObjectUtils.isNotEmpty(j.getValue())
                        && !JURISDICTION_OUTCOME_INPUT_IN_ERROR.equals(j.getValue().getJudgmentOutcome()))
                .map(j -> (JurCodesTypeItem) objectMapper(j, JurCodesTypeItem.class))
                .toList();
    }

    private static ClaimantIndType convertClaimantIndtype(
            uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantIndType claimantIndType) {
        if (ObjectUtils.isEmpty(claimantIndType)) {
            return null;
        }
        ClaimantIndType reformClaimantIndType = new ClaimantIndType();
        reformClaimantIndType.setClaimantFirstNames(claimantIndType.getClaimantFirstNames());
        reformClaimantIndType.setClaimantLastName(claimantIndType.getClaimantLastName());
        reformClaimantIndType.setClaimantDateOfBirth(claimantIndType.getClaimantDateOfBirth());
        switch (defaultIfEmpty(claimantIndType.getClaimantTitle(), "")) {
            case "Mr", "Mrs", "Miss", "Ms", "Mx" ->
                reformClaimantIndType.setClaimantPreferredTitle(claimantIndType.getClaimantTitle());
            case OTHER -> {
                reformClaimantIndType.setClaimantPreferredTitle(OTHER);
                reformClaimantIndType.setClaimantTitleOther(claimantIndType.getClaimantTitleOther());
            }
            case "Dr", "Prof", "Sir", "Lord", "Lady", "Dame", "Capt", "Rev" -> {
                reformClaimantIndType.setClaimantPreferredTitle(OTHER);
                reformClaimantIndType.setClaimantTitleOther(claimantIndType.getClaimantTitle());
            }
            default -> {
                // Do nothing on unmatched cases
            }
        }
        if (List.of("Male", "Female").contains(defaultIfEmpty(claimantIndType.getClaimantGender(), ""))) {
            reformClaimantIndType.setClaimantSex(claimantIndType.getClaimantGender());
        }

        return reformClaimantIndType;
    }

    /**
     * Convert the document collection from ECM to Reform. If the document type is empty, set it to "Needs updating".
     * @param documentCollection The document collection from ECM
     * @return The document collection for Reform
     */
    private static List<DocumentTypeItem> convertCaseDataDocumentCollection(
            List<uk.gov.hmcts.ecm.common.model.ccd.items.DocumentTypeItem> documentCollection) {

        List<DocumentTypeItem> list = new ArrayList<>();
        emptyIfNull(documentCollection).stream()
            .map(doc -> (DocumentTypeItem) objectMapper(doc, DocumentTypeItem.class))
            .forEach(documentTypeItem -> {
                if (isNullOrEmpty(documentTypeItem.getValue().getDocumentType())) {
                    documentTypeItem.getValue().setTopLevelDocuments(MISC);
                    documentTypeItem.getValue().setMiscDocuments("Needs updating");
                    documentTypeItem.getValue().setDocumentType("Needs updating");
                }
                list.add(documentTypeItem);
            });
        return list;
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
                 MIDLANDS_WEST_CASE_TYPE_ID, NEWCASTLE_CASE_TYPE_ID, WALES_CASE_TYPE_ID, WATFORD_CASE_TYPE_ID ->
                ET_ENGLAND_AND_WALES;
            case SCOTLAND_CASE_TYPE_ID -> ET_SCOTLAND;
            default -> throw new IllegalArgumentException("Case type not supported");
        };
    }

    private static Object objectMapper(Object object, Class<?> classType) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper.convertValue(object, classType);
    }

    private static void calculateJurisdictionDisposalDate(JudgementType judgement, String jurCode,
                                                          Map<String, LocalDate> judgmentDisposalDate) {
        if (!judgmentDisposalDate.containsKey(jurCode)) {
            judgmentDisposalDate.put(jurCode, LocalDate.parse(judgement.getDateJudgmentSent()));
        } else {
            LocalDate existingDate = judgmentDisposalDate.get(jurCode);
            if (LocalDate.parse(judgement.getDateJudgmentSent()).isAfter(existingDate)) {
                judgmentDisposalDate.put(jurCode, LocalDate.parse(judgement.getDateJudgmentSent()));
            }
        }
    }

    private static void setJurisdictionDisposalDate(uk.gov.hmcts.et.common.model.ccd.CaseData caseData) {
        if (isEmpty(caseData.getJurCodesCollection()) || isEmpty(caseData.getJudgementCollection())) {
            return;
        }

        Map<String, LocalDate> judgmentDisposalDate = new java.util.HashMap<>();
        caseData.getJudgementCollection().stream()
                .map(JudgementTypeItem::getValue)
                .filter(judgement -> isNotEmpty(judgement.getJurisdictionCodes())
                                     && !isNullOrEmpty(judgement.getDateJudgmentSent()))
                .forEach(judgement -> judgement.getJurisdictionCodes().stream()
                        .map(jur -> jur.getValue().getJuridictionCodesList())
                        .forEach(jurCode -> calculateJurisdictionDisposalDate(judgement, jurCode,
                            judgmentDisposalDate)));

        caseData.getJurCodesCollection().stream()
            .map(JurCodesTypeItem::getValue)
            .filter(jurCodesType -> isNullOrEmpty(jurCodesType.getDisposalDate()))
            .forEach(jurCodesType -> caseData.getJudgementCollection().stream()
                .map(JudgementTypeItem::getValue)
                .filter(judgement -> isNotEmpty(judgement.getJurisdictionCodes())
                        && !isNullOrEmpty(judgement.getDateJudgmentMade()))
                .filter(judgementType -> judgementType.getJurisdictionCodes().stream()
                    .anyMatch(jurCode ->
                        jurCode.getValue().getJuridictionCodesList().equals(jurCodesType.getJuridictionCodesList())))
                .forEach(judgementType ->
                        jurCodesType.setDisposalDate(judgmentDisposalDate.get(jurCodesType.getJuridictionCodesList())
                                .toString())));
    }

}
