package uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.allocatehearing;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ethos.replacement.docmosis.service.JudgeService;

@Service
public class JudgeSelectionService {

    private final JudgeService judgeService;

    public JudgeSelectionService(JudgeService judgeService) {
        this.judgeService = judgeService;
    }

    public DynamicFixedListType createJudgeSelection(CaseData caseData, HearingType selectedHearing) {
        var dynamicFixedListType = new DynamicFixedListType();
        dynamicFixedListType.setListItems(judgeService.getJudges(caseData.getOwningOffice()));

        if (selectedHearing.hasHearingJudge()) {
            dynamicFixedListType.setValue(selectedHearing.getJudge().getValue());
        }

        return dynamicFixedListType;
    }
}
