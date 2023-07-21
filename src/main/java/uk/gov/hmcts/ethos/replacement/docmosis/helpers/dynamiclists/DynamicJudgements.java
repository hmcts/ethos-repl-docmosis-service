package uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgementType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class DynamicJudgements {
    private DynamicJudgements() {
    }

    public static final String NO_HEARINGS = "No Hearings";

    public static void dynamicJudgements(CaseData caseData) {
        var listHearings = DynamicListHelper.createDynamicHearingList(caseData);
        var caseParties = DynamicListHelper.createDynamicRespondentName(
                caseData.getRespondentCollection());
        caseParties.add(DynamicListHelper.getDynamicCodeLabel("C: " + caseData.getClaimant(), caseData.getClaimant()));
        populateDynamicJudgements(caseData, listHearings, caseParties);
    }

    private static void populateDynamicJudgements(CaseData caseData, List<DynamicValueType> listHearings,
                                                  List<DynamicValueType> caseParties) {
        if (!caseParties.isEmpty()) {
            var hearingDynamicList = new DynamicFixedListType();
            hearingDynamicList.setListItems(listHearings);
            var parties = new DynamicFixedListType();
            parties.setListItems(caseParties);

            if (CollectionUtils.isNotEmpty(caseData.getJudgementCollection())) {
                var judgementCollection = caseData.getJudgementCollection();
                for (JudgementTypeItem judgementTypeItem : judgementCollection) {
                    dynamicHearingDate(caseData, hearingDynamicList, judgementTypeItem.getValue());
                }
            } else {
                createDynamicJudgment(caseData, hearingDynamicList);
            }

        }
    }

    private static void createDynamicJudgment(CaseData caseData, DynamicFixedListType hearingDynamicList) {
        var judgmentType = new JudgementType();
        judgmentType.setDynamicJudgementHearing(hearingDynamicList);
        var judgmentTypeItem = new JudgementTypeItem();
        judgmentTypeItem.setValue(judgmentType);
        List<JudgementTypeItem> judgementTypeList = new ArrayList<>();
        judgementTypeList.add(judgmentTypeItem);
        caseData.setJudgementCollection(judgementTypeList);
    }

    private static void dynamicHearingDate(CaseData caseData, DynamicFixedListType hearingDynamicList,
                                           JudgementType judgementType) {
        DynamicValueType dynamicValueType;
        if (judgementType.getDynamicJudgementHearing() == null) {
            judgementType.setDynamicJudgementHearing(hearingDynamicList);
            if (StringUtils.isNotEmpty(judgementType.getJudgmentHearingDate())) {
                var judgementHearingDate = judgementType.getJudgmentHearingDate();
                var hearingNumber = HearingsHelper.findHearingNumber(caseData, judgementHearingDate);
                if (isNullOrEmpty(hearingNumber)) { // Check needed if hearing number cannot be found
                    judgementType.setJudgmentHearingDate(null);
                    return;
                } else {
                    dynamicValueType = DynamicListHelper.findDynamicValue(hearingDynamicList.getListItems(),
                            hearingNumber);
                }
            } else {
                dynamicValueType = hearingDynamicList.getListItems().get(0);
            }
        } else {
            dynamicValueType = judgementType.getDynamicJudgementHearing().getValue();
            judgementType.getDynamicJudgementHearing().setListItems(hearingDynamicList.getListItems());

        }
        judgementType.getDynamicJudgementHearing().setValue(dynamicValueType);
    }

}