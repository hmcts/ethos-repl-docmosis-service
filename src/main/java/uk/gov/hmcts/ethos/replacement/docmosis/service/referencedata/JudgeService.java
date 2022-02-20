package uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata;

import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.Judge;

public interface JudgeService {
    Judge getJudge(String name);
}
