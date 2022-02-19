package uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.allocatehearing;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.JudgeService;

@Service
public class JudgeSelectionService {

    private final JudgeService judgeService;

    public JudgeSelectionService(JudgeService judgeService) {
        this.judgeService = judgeService;
    }

    public DynamicFixedListType createJudgeSelection(String tribunalOffice, HearingType selectedHearing) {
        var listItems = judgeService.getJudges(tribunalOffice);
        var selectedJudge = selectedHearing.getJudge();
        return DynamicFixedListType.from(listItems, selectedJudge);
    }
}
