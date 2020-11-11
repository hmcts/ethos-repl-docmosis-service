package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ecm.common.model.helper.SchedulePayload;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;

import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_SCHEDULE_CONFIG;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_SCHEDULE_DETAILED_CONFIG;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;

@Slf4j
public class MultiplesScheduleHelper {

    public static final String SUB_ZERO = "/0";
    public static final String NOT_ALLOCATED = "Not_Allocated";

    public static SchedulePayload getSchedulePayloadFromSubmitEvent(SubmitEvent submitEvent) {

        CaseData caseData = submitEvent.getCaseData();

        RespondentSumType respondent = caseData.getRespondentCollection().get(0).getValue();

        return SchedulePayload.builder()
                .ethosCaseRef(nullCheck(caseData.getEthosCaseReference()))
                .claimantName(getClaimantName(caseData))
                .respondentName(nullCheck(respondent.getRespondentName()))
                .positionType(nullCheck(caseData.getPositionType()))
                .claimantAddressLine1(nullCheck(caseData.getClaimantType().getClaimantAddressUK().getAddressLine1()))
                .claimantPostCode(nullCheck(caseData.getClaimantType().getClaimantAddressUK().getPostCode()))
                .respondentAddressLine1(nullCheck(respondent.getRespondentAddress().getAddressLine1()))
                .respondentPostCode(nullCheck(respondent.getRespondentAddress().getPostCode()))
                .build();

    }

    private static String getClaimantName(CaseData caseData) {

        if (!isNullOrEmpty(caseData.getClaimantCompany())) {

            return caseData.getClaimantCompany();

        } else {

            if (caseData.getClaimantIndType() != null) {

                return caseData.getClaimantIndType().claimantFullNames();

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
