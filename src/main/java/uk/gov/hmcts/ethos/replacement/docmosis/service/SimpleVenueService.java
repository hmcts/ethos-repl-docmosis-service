package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimpleVenueService implements VenueService {
    @Override
    public List<DynamicValueType> getVenues(String tribunalOffice) {
        var dynamicValueType = new ArrayList<DynamicValueType>();
        dynamicValueType.add(DynamicValueType.create("venue1", tribunalOffice + " 1"));
        dynamicValueType.add(DynamicValueType.create("venue2", tribunalOffice + " 2"));
        dynamicValueType.add(DynamicValueType.create("venue3", tribunalOffice + " 3"));

        return dynamicValueType;
    }
}
