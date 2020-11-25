package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.helper.SchedulePayload;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.schedule.SchedulePayloadES;
import uk.gov.hmcts.ecm.common.model.schedule.items.ScheduleRespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.schedule.types.ScheduleClaimantIndType;
import uk.gov.hmcts.ecm.common.model.schedule.types.ScheduleRespondentSumType;

import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_SCHEDULE_CONFIG;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_SCHEDULE_DETAILED_CONFIG;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;

@Slf4j
public class MultiplesScheduleHelper {

    public static final String SUB_ZERO = "/0";
    public static final String NOT_ALLOCATED = "Not_Allocated";

    public static SchedulePayload getSchedulePayloadFromSchedulePayloadES(SchedulePayloadES submitEventES) {

        ScheduleRespondentSumType respondent = submitEventES.getRespondentCollection().get(0).getValue();

        return SchedulePayload.builder()
                .ethosCaseRef(nullCheck(submitEventES.getEthosCaseReference()))
                .claimantName(getClaimantName(submitEventES.getClaimantCompany(), submitEventES.getClaimantIndType()))
                .respondentName(nullCheck(getRespondentName(submitEventES.getRespondentCollection())))
                .positionType(nullCheck(submitEventES.getPositionType()))
                .claimantAddressLine1(nullCheck(submitEventES.getClaimantType().getClaimantAddressUK().getAddressLine1()))
                .claimantPostCode(nullCheck(submitEventES.getClaimantType().getClaimantAddressUK().getPostCode()))
                .respondentAddressLine1(nullCheck(respondent.getRespondentAddress().getAddressLine1()))
                .respondentPostCode(nullCheck(respondent.getRespondentAddress().getPostCode()))
                .build();

    }

    private static String getRespondentName(List<ScheduleRespondentSumTypeItem> respondentCollection) {

        String respondentName = respondentCollection.get(0).getValue().getRespondentName();

        return respondentCollection.size() > 1
                ? respondentName + " & Others"
                : respondentName;

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
