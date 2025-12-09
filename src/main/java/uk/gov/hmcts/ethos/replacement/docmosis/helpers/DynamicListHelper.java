package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_TITLE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESPONDENT_TITLE;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicJudgements.NO_HEARINGS;

public class DynamicListHelper {

    private DynamicListHelper() {
    }

    public static List<DynamicValueType> createDynamicRespondentName(List<RespondentSumTypeItem> respondentCollection) {
        List<DynamicValueType> listItems = new ArrayList<>();
        if (respondentCollection == null) {
            return listItems;
        }
        respondentCollection.forEach(respondentSumTypeItem -> {
            var dynamicValueType = new DynamicValueType();
            var respondentSumType = respondentSumTypeItem.getValue();
            dynamicValueType.setCode("R: " + respondentSumType.getRespondentName());
            dynamicValueType.setLabel(respondentSumType.getRespondentName());
            listItems.add(dynamicValueType);
        });
        return listItems;
    }

    public static DynamicValueType getDynamicValue(String value) {
        return getDynamicCodeLabel(value, value);
    }

    public static DynamicValueType getDynamicCodeLabel(String code, String label) {
        var dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode(code);
        dynamicValueType.setLabel(label);
        return dynamicValueType;
    }

    public static DynamicValueType getDynamicValueParty(CaseData caseData, List<DynamicValueType> listItems,
                                                        String party) {
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
        if (CollectionUtils.isNotEmpty(caseData.getHearingCollection())) {
            caseData.getHearingCollection().forEach(hearingTypeItem -> {
                var hearingNumber = hearingTypeItem.getValue().getHearingNumber();
                var dateListedType = hearingTypeItem.getValue().getHearingDateCollection().getFirst().getValue();
                var listedDate = dateListedType.getListedDate().substring(0, 10);
                LocalDate date = LocalDate.parse(listedDate);
                String hearingData = hearingNumber
                                     + " : " + hearingTypeItem.getValue().getHearingType()
                                     + " - " + hearingTypeItem.getValue().getHearingVenue()
                                     + " - " + date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
                listItems.add(getDynamicCodeLabel(hearingNumber, hearingData));
            });
        } else {
            listItems.add(getDynamicValue(NO_HEARINGS));
        }
        return listItems;
    }

    public static List<DynamicValueType> createDynamicJurisdictionCodes(CaseData caseData) {
        List<DynamicValueType> listItems = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(caseData.getJurCodesCollection())) {
            listItems = caseData.getJurCodesCollection().stream()
                .map(jurCodesTypeItem -> getDynamicValue(jurCodesTypeItem.getValue().getJuridictionCodesList()))
                .toList();
        }
        return listItems;
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
}
