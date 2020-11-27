package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.helper.SchedulePayload;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.schedule.ScheduleAddress;
import uk.gov.hmcts.ecm.common.model.schedule.SchedulePayloadES;
import uk.gov.hmcts.ecm.common.model.schedule.types.ScheduleClaimantType;

import java.util.*;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_SCHEDULE_CONFIG;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_SCHEDULE_DETAILED_CONFIG;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;

@Slf4j
public class MultiplesScheduleHelper {

    public static final String SUB_ZERO = "/0";
    public static final String NOT_ALLOCATED = "Not_Allocated";
    public static final String ADDRESS_LINE1 = "AddressLine1";
    public static final String POSTCODE = "PostCode";

    public static SchedulePayload getSchedulePayloadFromSchedulePayloadES(SchedulePayloadES submitEventES) {

        if (submitEventES == null) {

            log.info("submitEventES is empty");

            return SchedulePayload.builder().build();

        }

        return SchedulePayload.builder()
                .ethosCaseRef(nullCheck(submitEventES.getEthosCaseReference()))
//                .claimantName(getClaimantName(submitEventES.getClaimantCompany(), submitEventES.getClaimantIndType()))
//                .respondentName(getRespondentData(submitEventES.getRespondentCollection(), RESPONDENT_NAME))
                .positionType(nullCheck(submitEventES.getPositionType()))
                .claimantAddressLine1(getClaimantData(submitEventES.getClaimantType(), ADDRESS_LINE1))
                .claimantPostCode(getClaimantData(submitEventES.getClaimantType(), POSTCODE))
//                .respondentAddressLine1(getRespondentData(submitEventES.getRespondentCollection(), ADDRESS_LINE1))
//                .respondentPostCode(getRespondentData(submitEventES.getRespondentCollection(), POSTCODE))
                .build();

    }

    private static String getClaimantData(ScheduleClaimantType scheduleClaimantType, String field) {

        if (scheduleClaimantType != null) {

            ScheduleAddress scheduleAddress = scheduleClaimantType.getClaimantAddressUK();

            if (field.equals(ADDRESS_LINE1)) {

                return scheduleAddress.getAddressLine1() != null
                        ? scheduleAddress.getAddressLine1()
                        : "";

            }

            else {

                return scheduleAddress != null
                        ? scheduleAddress.getPostCode()
                        : "";

            }

        } else {

            return "ClaimantEmpty";

        }

    }

    public static List<String> getSubMultipleCaseIds(TreeMap<String, Object> multipleObjects) {

        List<String> caseIds = new ArrayList<>();

        for (Map.Entry<String, Object> entry : multipleObjects.entrySet()) {

            caseIds.addAll((List<String>) entry.getValue());

        }

        return caseIds;

    }

    public static TreeMap<String, List<SchedulePayload>> getMultipleTreeMap(TreeMap<String, Object> multipleObjectsFiltered,
                                                                            Map<String, SchedulePayload> scheduleEventMap) {

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
