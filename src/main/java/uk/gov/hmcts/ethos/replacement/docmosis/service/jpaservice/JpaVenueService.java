package uk.gov.hmcts.ethos.replacement.docmosis.service.jpaservice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.VenueRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VenueService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class JpaVenueService implements VenueService {

    @Autowired
    private final VenueRepository venueRepository;

    public List<DynamicValueType> getVenues(String office){
        return venueRepository.getVenuesByOffice(office);
    }
}
