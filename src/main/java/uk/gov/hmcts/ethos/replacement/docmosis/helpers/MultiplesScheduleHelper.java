package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.ccd.Address;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.helper.SchedulePayload;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.schedule.SchedulePayloadES;
import uk.gov.hmcts.ecm.common.model.schedule.types.ScheduleClaimantIndType;
import uk.gov.hmcts.ecm.common.model.schedule.types.ScheduleClaimantType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_SCHEDULE_CONFIG;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_SCHEDULE_DETAILED_CONFIG;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;

@Slf4j
public class MultiplesScheduleHelper {

    public static final String SUB_ZERO = "/0";
    public static final String NOT_ALLOCATED = "Not_Allocated";
    public static final String RESPONDENT_NAME = "RespondentName";
    public static final String ADDRESS_LINE1 = "AddressLine1";
    public static final String ADDRESS_LINE2 = "AddressLine2";
    public static final String ADDRESS_LINE3 = "AddressLine3";
    public static final String TOWN = "Town";
    public static final String POSTCODE = "PostCode";

    private MultiplesScheduleHelper() {
    }

    public static SchedulePayload getSchedulePayloadFromSchedulePayloadES(SchedulePayloadES submitEventES) {
        return SchedulePayload.builder()
                .ethosCaseRef(nullCheck(submitEventES.getEthosCaseReference()))
                .claimantName(nullCheck(getClaimantName(submitEventES.getClaimantCompany(),
                        submitEventES.getClaimantIndType())))
                .respondentName(nullCheck(getRespondentData(submitEventES.getRespondentCollection(), RESPONDENT_NAME)))
                .positionType(nullCheck(submitEventES.getPositionType()))
                .claimantAddressLine1(nullCheck(getClaimantData(submitEventES.getClaimantType(), ADDRESS_LINE1)))
                .claimantAddressLine2(nullCheck(getClaimantData(submitEventES.getClaimantType(), ADDRESS_LINE2)))
                .claimantAddressLine3(nullCheck(getClaimantData(submitEventES.getClaimantType(), ADDRESS_LINE3)))
                .claimantTown(nullCheck(getClaimantData(submitEventES.getClaimantType(), TOWN)))
                .claimantPostCode(nullCheck(getClaimantData(submitEventES.getClaimantType(), POSTCODE)))
                .respondentAddressLine1(nullCheck(getRespondentData(submitEventES.getRespondentCollection(),
                        ADDRESS_LINE1)))
                .respondentAddressLine2(nullCheck(getRespondentData(submitEventES.getRespondentCollection(),
                        ADDRESS_LINE2)))
                .respondentAddressLine3(nullCheck(getRespondentData(submitEventES.getRespondentCollection(),
                        ADDRESS_LINE3)))
                .respondentTown(nullCheck(getRespondentData(submitEventES.getRespondentCollection(), TOWN)))
                .respondentPostCode(nullCheck(getRespondentData(submitEventES.getRespondentCollection(), POSTCODE)))
                .build();
    }

    private static String getRespondentData(List<RespondentSumTypeItem> respondentCollection, String field) {
        if (respondentCollection != null && !respondentCollection.isEmpty()) {
            if (field.equals(RESPONDENT_NAME)) {
                String respondentName = respondentCollection.get(0).getValue().getRespondentName();

                return respondentCollection.size() > 1
                        ? respondentName + " & Others"
                        : respondentName;
            }
            var address = DocumentHelper.getRespondentAddressET3(respondentCollection.get(0).getValue());

            return getScheduleAddress(field, address);
        } else {
            return "";
        }
    }

    private static String getClaimantData(ScheduleClaimantType scheduleClaimantType, String field) {
        if (scheduleClaimantType != null) {
            var address = scheduleClaimantType.getClaimantAddressUK();
            return getScheduleAddress(field, address);
        } else {
            return "";
        }
    }

    private static String getScheduleAddress(String field, Address address) {
        switch (field) {
            case ADDRESS_LINE1:
                return address.getAddressLine1() != null
                        ? address.getAddressLine1()
                        : "";
            case ADDRESS_LINE2:
                return address.getAddressLine2() != null
                        ? address.getAddressLine2()
                        : "";
            case ADDRESS_LINE3:
                return address.getAddressLine3() != null
                        ? address.getAddressLine3()
                        : "";
            case TOWN:
                return address.getPostTown() != null
                        ? address.getPostTown()
                        : "";
            default:
                return address != null
                        ? address.getPostCode()
                        : "";
        }
    }

    private static String getClaimantName(String claimantCompany, ScheduleClaimantIndType scheduleClaimantIndType) {
        if (!isNullOrEmpty(claimantCompany)) {
            return claimantCompany;
        } else {
            if (scheduleClaimantIndType != null) {
                return scheduleClaimantIndType.claimantFullNames();
            } else {
                return "";
            }
        }
    }

    public static List<String> getSubMultipleCaseIds(SortedMap<String, Object> multipleObjects) {
        List<String> caseIds = new ArrayList<>();

        for (Map.Entry<String, Object> entry : multipleObjects.entrySet()) {
            caseIds.addAll((List<String>) entry.getValue());
        }

        return caseIds;
    }

    public static SortedMap<String, SortedMap<String, SortedMap<String, Object>>> getMultipleTreeMap(
            SortedMap<String, Object> multipleObjectsFiltered, Map<String, SchedulePayload> scheduleEventMap) {

        TreeMap<String, SortedMap<String, SortedMap<String, Object>>> subMultipleTreeMap = new TreeMap<>();

        for (Map.Entry<String, Object> entry : multipleObjectsFiltered.entrySet()) {
            List<String> caseIds = ((List<String>) entry.getValue());
            SortedMap<String, SortedMap<String, Object>> scheduleEvents = new TreeMap<>();

            for (String caseId : caseIds) {
                MultiplesHelper.addObjectToCollectionOrderedByCaseRef(scheduleEvents,
                        scheduleEventMap.get(caseId), caseId);
            }

            subMultipleTreeMap.put(entry.getKey(), scheduleEvents);
        }

        return subMultipleTreeMap;
    }

    public static String generateScheduleDocumentName(MultipleData multipleData) {
        return multipleData.getMultipleReference() + " - " + multipleData.getScheduleDocName() + ".xlsx";
    }

    public static FilterExcelType getFilterExcelTypeByScheduleDoc(MultipleData multipleData) {
        if (Arrays.asList(MULTIPLE_SCHEDULE_CONFIG, MULTIPLE_SCHEDULE_DETAILED_CONFIG)
                .contains(multipleData.getScheduleDocName())) {
            return FilterExcelType.FLAGS;
        } else {
            return FilterExcelType.SUB_MULTIPLE;
        }
    }

}
