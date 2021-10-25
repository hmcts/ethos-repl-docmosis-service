package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_TITLE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESPONDENT_TITLE;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DynamicListHelper {

    private DynamicListHelper() {
    }

    public static List<DynamicValueType> createDynamicRespondentName(List<RespondentSumTypeItem> respondentCollection) {
        List<DynamicValueType> listItems = new ArrayList<>();
        if (respondentCollection != null) {
            for (RespondentSumTypeItem respondentSumTypeItem : respondentCollection) {
                var respondentSumType = respondentSumTypeItem.getValue();
                listItems.add(getDynamicCodeLabel("R: " + respondentSumType.getRespondentName(), respondentSumType.getRespondentName()));
            }
        }
        return listItems;
    }

    public static DynamicValueType getDynamicValueType(CaseData caseData, List<DynamicValueType> listItems, String party) {
        DynamicValueType dynamicValueType;
        if (party.equals(CLAIMANT_TITLE)) {
            dynamicValueType = getDynamicCodeLabel("C: " + caseData.getClaimant(), caseData.getClaimant());
        } else if (party.equals(RESPONDENT_TITLE)) {
            dynamicValueType = listItems.get(0);
        } else {
            dynamicValueType = getDynamicValue(party);
        }
        return dynamicValueType;
    }

    public static List<DynamicValueType> createDynamicHearingList(CaseData caseData) {
        List<DynamicValueType> listItems = new ArrayList<>();
        if (caseData.getHearingCollection() != null) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                var hearingNumber = hearingTypeItem.getValue().getHearingNumber();
                var dateListedType = hearingTypeItem.getValue().getHearingDateCollection().get(0).getValue();
                var listedDate = dateListedType.getListedDate().substring(0, 10);
                LocalDate date = LocalDate.parse(listedDate);
                String format = hearingNumber + " : " +
                        hearingTypeItem.getValue().getHearingType() + " - " +
                        hearingTypeItem.getValue().getHearingVenue() + " - " +
                        date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
                listItems.add(getDynamicCodeLabel(hearingNumber, format));
            }
        }
        return listItems;
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

    public static DynamicValueType findDynamicValue(List<DynamicValueType> listItems, String code) {
        var dynamicValue = new DynamicValueType();
        for (DynamicValueType dynamicValueType : listItems) {
            if (dynamicValueType.getCode().equals(code)) {
                dynamicValue.setCode(code);
                dynamicValue.setLabel(dynamicValueType.getLabel());
                return dynamicValue;
            }
        }
        return dynamicValue;
    }

    public static DynamicValueType findDynamicCode(List<DynamicValueType> listItems, String label) {
        var dynamicValue = new DynamicValueType();
        for (DynamicValueType dynamicValueType : listItems) {
            if (dynamicValueType.getLabel().equals(label)) {
                dynamicValue.setCode(dynamicValueType.getCode());
                dynamicValue.setLabel(label);
                return dynamicValue;
            }
        }
        return dynamicValue;
    }
}
