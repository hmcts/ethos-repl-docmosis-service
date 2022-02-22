package uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata;

import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.Judge;
import java.util.List;

public interface JudgeService {
    List<Judge> getJudges(String tribunalOffice);
}
