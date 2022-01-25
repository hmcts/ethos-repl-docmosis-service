package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgementType;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicJudgements.NO_HEARINGS;

@Service("judgmentValidationService")
public class JudgmentValidationService {

    public void validateJudgments(CaseData caseData) throws ParseException {
        if (CollectionUtils.isNotEmpty(caseData.getJudgementCollection())) {
            for (JudgementTypeItem judgementTypeItem : caseData.getJudgementCollection()) {
                populateJudgmentDateOfHearing(judgementTypeItem.getValue());
            }
        }
    }

    private void populateJudgmentDateOfHearing(JudgementType judgementType) throws ParseException {
        if (NO.equals(judgementType.getNonHearingJudgment())
                && !NO_HEARINGS.equals(judgementType.getDynamicJudgementHearing().getValue().getLabel())) {
            var hearingDate = judgementType.getDynamicJudgementHearing().getValue().getLabel();
            hearingDate = hearingDate.substring(hearingDate.length() - 11);
            var simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
            var date = simpleDateFormat.parse(hearingDate);
            simpleDateFormat.applyPattern("yyyy-MM-dd");
            judgementType.setJudgmentHearingDate(simpleDateFormat.format(date));
        } else {
            judgementType.setDynamicJudgementHearing(null);
            judgementType.setJudgmentHearingDate(null);
        }
    }

}
