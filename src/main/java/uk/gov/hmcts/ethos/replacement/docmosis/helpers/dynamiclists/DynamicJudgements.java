package uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists;

import com.microsoft.applicationinsights.boot.dependencies.apachecommons.lang3.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgementType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgmentReconsiderationType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.HearingsHelper;

import java.util.ArrayList;
import java.util.List;

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
                    dynamicReconsiderations(caseData, parties, judgementTypeItem.getValue());
                }
            } else {
                createDynamicJudgment(caseData, hearingDynamicList, parties);
            }

        }
    }

    private static void createDynamicJudgment(CaseData caseData, DynamicFixedListType hearingDynamicList,
                                              DynamicFixedListType partiesDynamicList) {
        var judgmentType = new JudgementType();
        judgmentType.setDynamicJudgementHearing(hearingDynamicList);
        var reconsiderationType = new JudgmentReconsiderationType();
        reconsiderationType.setDynamicReconsiderationPartyInitiative(partiesDynamicList);
        judgmentType.setJudgementReconsiderations(reconsiderationType);
        var judgmentTypeItem = new JudgementTypeItem();
        judgmentTypeItem.setValue(judgmentType);
        List<JudgementTypeItem> judgementTypeList = new ArrayList<>();
        judgementTypeList.add(judgmentTypeItem);
        caseData.setJudgementCollection(judgementTypeList);
    }

    private static void dynamicReconsiderations(CaseData caseData, DynamicFixedListType parties,
                                                JudgementType judgementType) {
        DynamicValueType dynamicValueType;
        JudgmentReconsiderationType reconsiderationType;
        if (judgementType.getJudgementReconsiderations() == null) {
            reconsiderationType = new JudgmentReconsiderationType();
            reconsiderationType.setDynamicReconsiderationPartyInitiative(parties);
            dynamicValueType = parties.getListItems().get(0);
            reconsiderationType.getDynamicReconsiderationPartyInitiative().setValue(dynamicValueType);
            judgementType.setJudgementReconsiderations(reconsiderationType);
        } else {
            reconsiderationType = judgementType.getJudgementReconsiderations();
            if (reconsiderationType.getDynamicReconsiderationPartyInitiative() == null) {
                reconsiderationType.setDynamicReconsiderationPartyInitiative(parties);
                if (StringUtils.isNotEmpty(reconsiderationType.getReconsiderationPartyInitiative())) {
                    dynamicValueType = DynamicListHelper.getDynamicValueParty(caseData, parties.getListItems(),
                            reconsiderationType.getReconsiderationPartyInitiative());
                } else {
                    dynamicValueType = parties.getListItems().get(0);
                }
            } else {
                dynamicValueType = reconsiderationType.getDynamicReconsiderationPartyInitiative().getValue();
                reconsiderationType.setDynamicReconsiderationPartyInitiative(parties);
            }
            reconsiderationType.getDynamicReconsiderationPartyInitiative().setValue(dynamicValueType);
        }
    }

    private static void dynamicHearingDate(CaseData caseData, DynamicFixedListType hearingDynamicList,
                                           JudgementType judgementType) {
        DynamicValueType dynamicValueType;
        if (judgementType.getDynamicJudgementHearing() == null) {
            judgementType.setDynamicJudgementHearing(hearingDynamicList);
            if (StringUtils.isNotEmpty(judgementType.getJudgmentHearingDate())) {
                var judgementHearingDate = judgementType.getJudgmentHearingDate();
                var hearingNumber = HearingsHelper.findHearingNumber(caseData, judgementHearingDate);
                dynamicValueType = DynamicListHelper.findDynamicValue(hearingDynamicList.getListItems(), hearingNumber);
            } else {
                dynamicValueType = hearingDynamicList.getListItems().get(0);
            }
        } else {
            dynamicValueType = judgementType.getDynamicJudgementHearing().getValue();
            judgementType.setDynamicJudgementHearing(hearingDynamicList);
        }
        judgementType.getDynamicJudgementHearing().setValue(dynamicValueType);
    }

}