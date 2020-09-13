package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;

@Slf4j
public class MultiplesScheduleHelper {

    public static StringBuilder buildScheduleDocumentContent(MultipleData multipleData, String accessKey,
                                                             TreeMap<String, Object> multipleObjectsFiltered,
                                                             List<SubmitEvent> submitEvents) {
        StringBuilder sb = new StringBuilder();
        // Start building the instruction
        sb.append("{\n");
        sb.append("\"accessKey\":\"").append(accessKey).append(NEW_LINE);
        sb.append("\"templateName\":\"").append(BulkHelper.getScheduleDocName(multipleData.getScheduleDocName())).append(FILE_EXTENSION).append(NEW_LINE);
        sb.append("\"outputName\":\"").append(OUTPUT_FILE_NAME).append(NEW_LINE);
        // Building the document data
        sb.append("\"data\":{\n");
        sb.append("\"Multiple_No\":\"").append(multipleData.getMultipleReference()).append(NEW_LINE);
        sb.append("\"Multiple_title\":\"").append(multipleData.getMultipleName()).append(NEW_LINE);
        sb.append(getDocumentData(multipleData, multipleObjectsFiltered, submitEvents));
        sb.append("\"Today_date\":\"").append(UtilHelper.formatCurrentDate(LocalDate.now())).append("\"\n");
        sb.append("}\n");
        sb.append("}\n");
        return sb;
    }

    private static StringBuilder getDocumentData(MultipleData multipleData, TreeMap<String, Object> multipleObjectsFiltered,
                                                 List<SubmitEvent> submitEvents) {
        if (LIST_CASES_CONFIG.equals(multipleData.getScheduleDocName())) {
            return getScheduleBySubMultipleData(multipleData, multipleObjectsFiltered, submitEvents);
        } else if (Arrays.asList(MULTIPLE_SCHEDULE_CONFIG, MULTIPLE_SCHEDULE_DETAILED_CONFIG).contains(multipleData.getScheduleDocName())) {
            return getScheduleData(submitEvents);
        } else {
            return new StringBuilder();
        }
    }

    private static String getSubMultipleRef(MultipleData multipleData, String subMultipleName) {

        return multipleData.getSubMultipleCollection().stream()
                .filter(subMultipleTypeItem ->
                        subMultipleTypeItem.getValue().getSubMultipleName().equals(subMultipleName))
                .map(subMultipleTypeItem -> subMultipleTypeItem.getValue().getSubMultipleRef())
                .findFirst()
                .orElse("");

    }

    private static StringBuilder getScheduleBySubMultipleData(MultipleData multipleData,
                                                              TreeMap<String, Object> multipleObjectsFiltered,
                                                              List<SubmitEvent> submitEvents) {

        Map<String, SubmitEvent> submitEventMap = submitEvents.stream().collect(
                Collectors.toMap(submitEvent -> submitEvent.getCaseData().getEthosCaseReference(),
                        submitEvent -> submitEvent));

        Map<String, List<SubmitEvent>> subMultipleMap = getMultipleMap(multipleObjectsFiltered, submitEventMap);

        StringBuilder sb = new StringBuilder();

        if (!subMultipleMap.isEmpty()) {
            sb.append("\"subMultiple\":[\n");
            Iterator<Map.Entry<String, List<SubmitEvent>>> entries = new TreeMap<>(subMultipleMap).entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, List<SubmitEvent>> subMultipleEntry = entries.next();
                sb.append("{\"SubMultiple_No\":\"").append(getSubMultipleRef(multipleData, subMultipleEntry.getKey())).append(NEW_LINE);
                sb.append("\"SubMultiple_title\":\"").append(subMultipleEntry.getKey()).append(NEW_LINE);
                sb.append("\"multiple\":[\n");
                for (int i = 0; i < subMultipleEntry.getValue().size(); i++) {
                    sb.append(getMultipleTypeRow(subMultipleEntry.getValue().get(i)));
                    if (i != subMultipleEntry.getValue().size() - 1) {
                        sb.append(",\n");
                    }
                }
                sb.append("]\n");
                if (entries.hasNext()) {
                    sb.append("},\n");
                } else {
                    sb.append("}],\n");
                }
            }
        }
        return sb;
    }

    private static Map<String, List<SubmitEvent>> getMultipleMap(TreeMap<String, Object> multipleObjectsFiltered,
                                                                 Map<String, SubmitEvent> submitEventMap) {
        Map<String, List<SubmitEvent>> subMultipleMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : multipleObjectsFiltered.entrySet()) {

            List<String> caseIds = ((List<String>) entry.getValue());
            List<SubmitEvent> submitEvents = new ArrayList<>();

            for (String caseId : caseIds) {
                submitEvents.add(submitEventMap.get(caseId));
            }

            subMultipleMap.put(entry.getKey(), submitEvents);
        }

        return subMultipleMap;
    }

    private static StringBuilder getScheduleData(List<SubmitEvent> submitEvents) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"multiple\":[\n");
        for (int i = 0; i < submitEvents.size(); i++) {
            sb.append(getMultipleTypeRow(submitEvents.get(i)));
            if (i != submitEvents.size() - 1) {
                sb.append(",\n");
            }
        }
        sb.append("],\n");
        return sb;
    }

    private static StringBuilder getMultipleTypeRow(SubmitEvent submitEvent) {
        StringBuilder sb = new StringBuilder();
        CaseData caseData = submitEvent.getCaseData();
        RespondentSumType respondent = caseData.getRespondentCollection().get(0).getValue();
        sb.append("{\"Claimant\":\"").append(nullCheck(caseData.getClaimantIndType().claimantFullName())).append(NEW_LINE);
        sb.append("\"Respondent\":\"").append(nullCheck(respondent.getRespondentName())).append(NEW_LINE);
        sb.append("\"Current_position\":\"").append(nullCheck(caseData.getPositionType())).append(NEW_LINE);
        sb.append("\"Case_No\":\"").append(nullCheck(caseData.getEthosCaseReference())).append(NEW_LINE);
        sb.append("\"claimant_full_name\":\"").append(nullCheck(caseData.getClaimantIndType().claimantFullName())).append(NEW_LINE);
        sb.append("\"claimant_addressLine1\":\"").append(nullCheck(caseData.getClaimantType().getClaimantAddressUK().toString())).append(NEW_LINE);
        sb.append("\"claimant_postCode\":\"").append(nullCheck(caseData.getClaimantType().getClaimantAddressUK().getPostCode())).append(NEW_LINE);
        sb.append("\"respondent_full_name\":\"").append(nullCheck(respondent.getRespondentName())).append(NEW_LINE);
        sb.append("\"respondent_addressLine1\":\"").append(nullCheck(respondent.getRespondentAddress().toString())).append(NEW_LINE);
        sb.append("\"respondent_postCode\":\"").append(nullCheck(respondent.getRespondentAddress().getPostCode())).append("\"}");
        return sb;
    }
}
