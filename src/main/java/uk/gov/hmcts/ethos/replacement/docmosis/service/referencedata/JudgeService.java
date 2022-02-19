package uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata;

import java.util.List;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;

public interface JudgeService {
    List<DynamicValueType> getJudges(String tribunalOffice);
}
