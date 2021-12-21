package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;

@Service("judgmentValidationService")
public class JudgmentValidationService {

    public void validateJudgments(CaseData caseData) throws ParseException {
        if (CollectionUtils.isNotEmpty(caseData.getJudgementCollection())) {
            for (JudgementTypeItem judgementTypeItem : caseData.getJudgementCollection()) {
                populateJudgmentDateOfHearing(caseData);
            }
        }
    }

    private void populateJudgmentDateOfHearing(CaseData caseData) throws ParseException {
        if (CollectionUtils.isNotEmpty(caseData.getJudgementCollection())) {
            for (JudgementTypeItem judgementTypeItem : caseData.getJudgementCollection()) {
                if (NO.equals(judgementTypeItem.getValue().getNonHearingJudgment())) {
                    var hearingDate = judgementTypeItem.getValue().getDynamicJudgementHearing().getValue().getLabel();
                    hearingDate = hearingDate.substring(hearingDate.length() - 11);
                    var simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
                    var date = simpleDateFormat.parse(hearingDate);
                    simpleDateFormat.applyPattern("yyyy-MM-dd");
                    judgementTypeItem.getValue().setJudgmentHearingDate(simpleDateFormat.format(date));
                } else {
                    judgementTypeItem.getValue().setDynamicJudgementHearing(null);
                    judgementTypeItem.getValue().setJudgmentHearingDate(null);
                }
            }
        }
    }
}
