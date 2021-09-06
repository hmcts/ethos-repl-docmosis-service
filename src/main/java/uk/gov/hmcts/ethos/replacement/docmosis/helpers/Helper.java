package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.SignificantItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceScotType;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceType;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.labels.LabelPayloadES;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.BF_ACTION_ACAS;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BF_ACTION_CASE_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BF_ACTION_CASE_PAPERS;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BF_ACTION_CASE_TRANSFERRED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BF_ACTION_DRAFT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BF_ACTION_ENQUIRY_ISSUED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BF_ACTION_ENQUIRY_RECEIVED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BF_ACTION_EXHIBITS;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BF_ACTION_INTERLOCUTORY;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BF_ACTION_IT3_RECEIVED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BF_ACTION_OTHER_ACTION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BF_ACTION_POSTPONEMENT_REQUESTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BF_ACTION_REFER_CHAIRMAN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BF_ACTION_REPLY_TO_ENQUIRY;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BF_ACTION_STRIKING_OUT_WARNING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASE_CLOSED_POSITION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_POSTPONED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_CASE_TYPE_ID;

@Slf4j
public class Helper {

    public static final String HEARING_CREATION_NUMBER_ERROR = "A new hearing can only "
            + "be added from the List Hearing menu item";
    public static final String HEARING_CREATION_DAY_ERROR = "A new day for a hearing can "
            + "only be added from the List Hearing menu item";

    private Helper() {
    }

    public static String nullCheck(String value) {
        Optional<String> opt = Optional.ofNullable(value);
        if (opt.isPresent()) {
            return value.replaceAll("\"", "'");
        } else {
            return "";
        }
    }

    public static SignificantItem generateSignificantItem(DocumentInfo documentInfo, List<String> errors) {
        log.info("generateSignificantItem for document: " + documentInfo);
        if (documentInfo == null) {
            errors.add("Error processing document");
            return new SignificantItem();
        } else {
            return SignificantItem.builder()
                    .url(documentInfo.getUrl())
                    .description(documentInfo.getDescription())
                    .type(SignificantItemType.DOCUMENT.name())
                    .build();
        }
    }

    private static List<DynamicValueType> createDynamicRespondentAddressFixedList(
            List<RespondentSumTypeItem> respondentCollection) {
        List<DynamicValueType> listItems = new ArrayList<>();
        if (respondentCollection != null) {
            for (RespondentSumTypeItem respondentSumTypeItem : respondentCollection) {
                var dynamicValueType = new DynamicValueType();
                var respondentSumType = respondentSumTypeItem.getValue();
                dynamicValueType.setCode(respondentSumType.getRespondentName());
                dynamicValueType.setLabel(respondentSumType.getRespondentName() + " - "
                        + respondentSumType.getRespondentAddress().toString());
                listItems.add(dynamicValueType);
            }
        }
        return listItems;
    }

    public static CaseData midRespondentAddress(CaseData caseData) {
        List<DynamicValueType> listItems = createDynamicRespondentAddressFixedList(caseData.getRespondentCollection());
        if (!listItems.isEmpty()) {
            if (caseData.getClaimantWorkAddressQRespondent() != null) {
                caseData.getClaimantWorkAddressQRespondent().setListItems(listItems);
            } else {
                var dynamicFixedListType = new DynamicFixedListType();
                dynamicFixedListType.setListItems(listItems);
                caseData.setClaimantWorkAddressQRespondent(dynamicFixedListType);
            }
            //Default dynamic list
            caseData.getClaimantWorkAddressQRespondent().setValue(listItems.get(0));
        }
        return caseData;
    }

    public static List<RespondentSumTypeItem> getActiveRespondents(CaseData caseData) {

        List<RespondentSumTypeItem> activeRespondents = new ArrayList<>();

        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
            activeRespondents = caseData.getRespondentCollection()
                    .stream()
                    .filter(respondentSumTypeItem -> respondentSumTypeItem.getValue().getResponseStruckOut() == null
                            || respondentSumTypeItem.getValue().getResponseStruckOut().equals(NO))
                    .collect(Collectors.toList());
        }

        return activeRespondents;
    }

    public static List<RespondentSumTypeItem> getActiveRespondentsLabels(LabelPayloadES labelPayloadES) {

        List<RespondentSumTypeItem> activeRespondents = new ArrayList<>();

        if (labelPayloadES.getRespondentCollection() != null && !labelPayloadES.getRespondentCollection().isEmpty()) {
            activeRespondents = labelPayloadES.getRespondentCollection()
                    .stream()
                    .filter(respondentSumTypeItem -> respondentSumTypeItem.getValue().getResponseStruckOut() == null
                            || respondentSumTypeItem.getValue().getResponseStruckOut().equals(NO))
                    .collect(Collectors.toList());
        }

        return activeRespondents;
    }

    public static String getDocumentName(CorrespondenceType correspondenceType,
                                         CorrespondenceScotType correspondenceScotType) {
        String ewSection = DocumentHelper.getEWSectionName(correspondenceType);
        String sectionName = ewSection.equals("")
                ? DocumentHelper.getScotSectionName(correspondenceScotType) : ewSection;
        return DocumentHelper.getTemplateName(correspondenceType, correspondenceScotType) + "_" + sectionName;
    }

    private static List<DynamicValueType> createDynamicRespondentNameList(
            List<RespondentSumTypeItem> respondentCollection) {
        List<DynamicValueType> listItems = new ArrayList<>();
        if (respondentCollection != null) {
            for (RespondentSumTypeItem respondentSumTypeItem : respondentCollection) {
                var respondentSumType = respondentSumTypeItem.getValue();
                if (respondentSumType.getResponseStruckOut() == null
                        || respondentSumType.getResponseStruckOut().equals(NO)) {
                    var dynamicValueType = new DynamicValueType();
                    dynamicValueType.setCode(respondentSumType.getRespondentName());
                    dynamicValueType.setLabel(respondentSumType.getRespondentName());
                    listItems.add(dynamicValueType);
                }
            }
        }
        return listItems;
    }

    public static void midRespondentECC(CaseData caseData, CaseData originalCaseData) {
        List<DynamicValueType> listItems = createDynamicRespondentNameList(originalCaseData.getRespondentCollection());
        if (!listItems.isEmpty()) {
            if (caseData.getRespondentECC() != null) {
                caseData.getRespondentECC().setListItems(listItems);
            } else {
                var dynamicFixedListType = new DynamicFixedListType();
                dynamicFixedListType.setListItems(listItems);
                caseData.setRespondentECC(dynamicFixedListType);
            }
            //Default dynamic list
            caseData.getRespondentECC().setValue(listItems.get(0));
        }
    }

    public static DynamicValueType getDynamicValue(String value) {

        var dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode(value);
        dynamicValueType.setLabel(value);

        return dynamicValueType;

    }

    public static DynamicValueType getDynamicCodeLabel(String code, String label) {

        var dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode(code);
        dynamicValueType.setLabel(label);

        return dynamicValueType;

    }

    public static List<DynamicValueType> getDefaultBfListItems() {
        return new ArrayList<>(Arrays.asList(
                getDynamicValue(BF_ACTION_ACAS),
                getDynamicValue(BF_ACTION_CASE_LISTED),
                getDynamicValue(BF_ACTION_CASE_PAPERS),
                getDynamicValue(BF_ACTION_CASE_TRANSFERRED),
                getDynamicValue(BF_ACTION_DRAFT),
                getDynamicValue(BF_ACTION_ENQUIRY_ISSUED),
                getDynamicValue(BF_ACTION_ENQUIRY_RECEIVED),
                getDynamicValue(BF_ACTION_EXHIBITS),
                getDynamicValue(BF_ACTION_INTERLOCUTORY),
                getDynamicValue(BF_ACTION_IT3_RECEIVED),
                getDynamicValue(BF_ACTION_OTHER_ACTION),
                getDynamicValue(BF_ACTION_POSTPONEMENT_REQUESTED),
                getDynamicValue(BF_ACTION_REFER_CHAIRMAN),
                getDynamicValue(BF_ACTION_REPLY_TO_ENQUIRY),
                getDynamicValue(BF_ACTION_STRIKING_OUT_WARNING)));
    }

    public static List<String> hearingMidEventValidation(CaseData caseData) {

        List<String> errors = new ArrayList<>();

        if (caseData.getHearingCollection() != null) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                if (hearingTypeItem.getValue().getHearingNumber() == null
                        || hearingTypeItem.getValue().getHearingNumber().isEmpty()) {
                    errors.add(HEARING_CREATION_NUMBER_ERROR);
                    return errors;
                }
                if (hearingTypeItem.getValue().getHearingDateCollection() != null) {
                    for (DateListedTypeItem dateListedTypeItem
                            : hearingTypeItem.getValue().getHearingDateCollection()) {
                        if (dateListedTypeItem.getValue().getListedDate() == null
                                || dateListedTypeItem.getValue().getListedDate().isEmpty()) {
                            errors.add(HEARING_CREATION_DAY_ERROR);
                            return  errors;
                        }
                    }
                }
            }
        }
        return errors;
    }

    public static void updatePositionTypeToClosed(CaseData caseData) {

        caseData.setPositionType(CASE_CLOSED_POSITION);
        caseData.setCurrentPosition(CASE_CLOSED_POSITION);

    }

    public static void updatePostponedDate(CaseData caseData) {

        if (caseData.getHearingCollection() != null) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {

                if (hearingTypeItem.getValue().getHearingDateCollection() != null) {
                    for (DateListedTypeItem dateListedTypeItem
                            : hearingTypeItem.getValue().getHearingDateCollection()) {

                        var dateListedType = dateListedTypeItem.getValue();
                        if (isHearingStatusPostponed(dateListedType) && dateListedType.getPostponedDate() == null) {
                            dateListedType.setPostponedDate(UtilHelper.formatCurrentDate2(LocalDate.now()));
                        }
                        if (dateListedType.getPostponedDate() != null
                                &&
                                (!isHearingStatusPostponed(dateListedType)
                                        || dateListedType.getHearingStatus() == null)) {
                            dateListedType.setPostponedDate(null);
                        }
                    }
                }
            }
        }

    }

    private static boolean isHearingStatusPostponed(DateListedType dateListedType) {
        return dateListedType.getHearingStatus() != null
                && dateListedType.getHearingStatus().equals(HEARING_STATUS_POSTPONED);
    }

    public static List<String> getAllOffices() {

        return new ArrayList<>(Arrays.asList(
                BRISTOL_CASE_TYPE_ID,
                LEEDS_CASE_TYPE_ID,
                LONDON_CENTRAL_CASE_TYPE_ID,
                LONDON_EAST_CASE_TYPE_ID,
                LONDON_SOUTH_CASE_TYPE_ID,
                MANCHESTER_CASE_TYPE_ID,
                MIDLANDS_EAST_CASE_TYPE_ID,
                MIDLANDS_WEST_CASE_TYPE_ID,
                NEWCASTLE_CASE_TYPE_ID,
                WALES_CASE_TYPE_ID,
                WATFORD_CASE_TYPE_ID,
                SCOTLAND_CASE_TYPE_ID
        ));
    }

    public static List<DynamicValueType> getAvailableOffices(String currentOffice) {

        List<DynamicValueType> offices = new ArrayList<>();

        for (String office : getAllOffices()) {

            if (!currentOffice.equals(office)) {

                offices.add(getDynamicValue(office));
            }
        }

        return offices;
    }

    public static void populateDynamicListOffices(CaseData caseData, String caseTypeId) {

        log.info("Populating dynamic list with offices");

        var dynamicFixedListType = new DynamicFixedListType();
        dynamicFixedListType.setListItems(getAvailableOffices(caseTypeId));

        caseData.setOfficeCT(dynamicFixedListType);

    }

    public static List<String> getJurCodesCollection(List<JurCodesTypeItem> jurCodesCollection) {

        return jurCodesCollection != null
                ? jurCodesCollection.stream()
                .map(jurCodesTypeItem -> jurCodesTypeItem.getValue().getJuridictionCodesList())
                .collect(Collectors.toList())
                : new ArrayList<>();
    }

}
