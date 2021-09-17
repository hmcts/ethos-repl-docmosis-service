package uk.gov.hmcts.ethos.replacement.docmosis.service;


import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimpleListValuesService implements ListValuesService {
    @Override
    public List<DynamicValueType> getValues(String tribunalOffice, String listType) {
        var values = new ArrayList<DynamicValueType>();

        switch (listType) {
            case "CLERK":
                values.add(DynamicValueType.create("clerk1", "Boris"));
                values.add(DynamicValueType.create("clerk2", "Doris"));
                values.add(DynamicValueType.create("clerk3", "Morris"));
                break;
            case "EMPLOYER_MEMBER":
                values.add(DynamicValueType.create("er1", "Mr Employer Member"));
                values.add(DynamicValueType.create("er2", "Miss Employer Member"));
                values.add(DynamicValueType.create("er3", "Mrs Employer Member"));
                break;
            case "EMPLOYEE_MEMBER":
                values.add(DynamicValueType.create("ee1", "Mrs Employee Member"));
                values.add(DynamicValueType.create("ee2", "Miss Employee Member"));
                values.add(DynamicValueType.create("ee3", "Mr Employee Member"));
                break;
            default:
                break;
        }

        return values;
    }
}
