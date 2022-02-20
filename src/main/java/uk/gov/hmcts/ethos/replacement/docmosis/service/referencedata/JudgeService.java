package uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata;

import java.util.List;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.Judge;

public interface JudgeService {
    List<Judge> getJudges(String tribunalOffice);
}
