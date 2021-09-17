package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimpleJudgeService implements JudgeService {
    @Override
    public List<DynamicValueType> getJudges(String tribunalOffice) {
        var dynamicValueType = new ArrayList<DynamicValueType>();
        dynamicValueType.add(DynamicValueType.create(tribunalOffice + "judge1", tribunalOffice + " Judge 1"));
        dynamicValueType.add(DynamicValueType.create(tribunalOffice + "judge2", tribunalOffice + " Judge 2"));
        dynamicValueType.add(DynamicValueType.create(tribunalOffice + "judge3", tribunalOffice + " Judge 3"));

        return dynamicValueType;
    }
}
