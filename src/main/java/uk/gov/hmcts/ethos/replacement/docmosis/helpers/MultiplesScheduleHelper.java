package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.ccd.Address;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.helper.SchedulePayload;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.schedule.ScheduleAddress;
import uk.gov.hmcts.ecm.common.model.schedule.SchedulePayloadES;
import uk.gov.hmcts.ecm.common.model.schedule.types.ScheduleClaimantIndType;
import uk.gov.hmcts.ecm.common.model.schedule.types.ScheduleClaimantType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    public static final String POSTCODE = "PostCode";

    private MultiplesScheduleHelper() {
    }

    public static SchedulePayload getSchedulePayloadFromSchedulePayloadES(SchedulePayloadES submitEventES) {

        return SchedulePayload.builder()
                .ethosCaseRef(nullCheck(submitEventES.getEthosCaseReference()))
                .claimantName(getClaimantName(submitEventES.getClaimantCompany(), submitEventES.getClaimantIndType()))
                .respondentName(getRespondentData(submitEventES.getRespondentCollection(), RESPONDENT_NAME))
                .positionType(nullCheck(submitEventES.getPositionType()))
                .claimantAddressLine1(getClaimantData(submitEventES.getClaimantType(), ADDRESS_LINE1))
                .claimantPostCode(getClaimantData(submitEventES.getClaimantType(), POSTCODE))
                .respondentAddressLine1(getRespondentData(submitEventES.getRespondentCollection(), ADDRESS_LINE1))
                .respondentPostCode(getRespondentData(submitEventES.getRespondentCollection(), POSTCODE))
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

            ScheduleAddress scheduleAddress = new ScheduleAddress();
            Address address = DocumentHelper.getRespondentAddressET3(respondentCollection.get(0).getValue());
            scheduleAddress.setAddressLine1(address.getAddressLine1());
            scheduleAddress.setPostCode(address.getPostCode());

            return getScheduleAddress(field, scheduleAddress);

        } else {

            return "";

        }

    }

    private static String getClaimantData(ScheduleClaimantType scheduleClaimantType, String field) {

        if (scheduleClaimantType != null) {

            ScheduleAddress scheduleAddress = scheduleClaimantType.getClaimantAddressUK();

            return getScheduleAddress(field, scheduleAddress);

        } else {

            return "";

        }

    }

    private static String getScheduleAddress(String field, ScheduleAddress scheduleAddress) {

        if (field.equals(ADDRESS_LINE1)) {

            return scheduleAddress.getAddressLine1() != null
                    ? scheduleAddress.getAddressLine1()
                    : "";

        } else {

            return scheduleAddress != null
                    ? scheduleAddress.getPostCode()
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

    public static List<String> getSubMultipleCaseIds(TreeMap<String, Object> multipleObjects) {

        List<String> caseIds = new ArrayList<>();

        for (Map.Entry<String, Object> entry : multipleObjects.entrySet()) {

            caseIds.addAll((List<String>) entry.getValue());

        }

        return caseIds;

    }

    public static TreeMap<String, List<SchedulePayload>> getMultipleTreeMap(
            TreeMap<String, Object> multipleObjectsFiltered, Map<String, SchedulePayload> scheduleEventMap) {

        TreeMap<String, List<SchedulePayload>> subMultipleTreeMap = new TreeMap<>();

        for (Map.Entry<String, Object> entry : multipleObjectsFiltered.entrySet()) {

            List<String> caseIds = ((List<String>) entry.getValue());
            List<SchedulePayload> scheduleEvents = new ArrayList<>();

            for (String caseId : caseIds) {
                scheduleEvents.add(scheduleEventMap.get(caseId));
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
