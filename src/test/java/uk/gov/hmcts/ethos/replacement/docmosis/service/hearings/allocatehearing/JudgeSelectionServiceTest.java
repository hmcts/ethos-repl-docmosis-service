package uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.allocatehearing;

import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice.TribunalOffice;
import uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.SelectionServiceTestUtils;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.JudgeService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JudgeSelectionServiceTest {

    @Test
    public void testCreateJudgeSelectionNoSelectedJudge() {
        var tribunalOffice = TribunalOffice.ABERDEEN;
        var judgeService = mockJudgeService(tribunalOffice);
        var caseData = SelectionServiceTestUtils.createCaseData(tribunalOffice.name());
        var selectedHearing = mockHearing(null);

        var judgeSelectionService = new JudgeSelectionService(judgeService);
        var actualResult = judgeSelectionService.createJudgeSelection(caseData, selectedHearing);

        SelectionServiceTestUtils.verifyDynamicFixedListNoneSelected(actualResult, "judge", "Judge ");
    }

    @Test
    public void testCreateJudgeSelectionWithSelectedJudge() {
        var tribunalOffice = TribunalOffice.ABERDEEN;
        var judgeService = mockJudgeService(tribunalOffice);
        var caseData = SelectionServiceTestUtils.createCaseData(tribunalOffice.name());
        var selectedJudge = DynamicValueType.create("judge2", "Judge 2");
        var selectedHearing = mockHearing(selectedJudge);

        var judgeSelectionService = new JudgeSelectionService(judgeService);
        var actualResult = judgeSelectionService.createJudgeSelection(caseData, selectedHearing);

        SelectionServiceTestUtils.verifyDynamicFixedListSelected(actualResult, "judge", "Judge ", selectedJudge);
    }

    private JudgeService mockJudgeService(TribunalOffice tribunalOffice) {
        var dynamicValues = SelectionServiceTestUtils.createListItems("judge", "Judge ");

        var judgeService = mock(JudgeService.class);
        when(judgeService.getJudges(tribunalOffice)).thenReturn(dynamicValues);
        return judgeService;
    }

    private HearingType mockHearing(DynamicValueType selectedValue) {
        var hearing = mock(HearingType.class);
        when(hearing.hasHearingJudge()).thenReturn(selectedValue != null);
        if (selectedValue != null) {
            var dynamicFixedListType = mock(DynamicFixedListType.class);
            when(dynamicFixedListType.getValue()).thenReturn(selectedValue);
            when(hearing.getJudge()).thenReturn(dynamicFixedListType);
        }

        return hearing;
    }
}
