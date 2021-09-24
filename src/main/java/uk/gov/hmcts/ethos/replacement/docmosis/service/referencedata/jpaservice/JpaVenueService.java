package uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.jpaservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.VenueRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice.TribunalOffice;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.VenueService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class JpaVenueService implements VenueService {

    private final VenueRepository venueRepository;

    @Override
    public List<DynamicValueType> getVenues(TribunalOffice tribunalOffice) {
        return venueRepository.findByTribunalOffice(tribunalOffice).stream()
                .map(venue -> DynamicValueType.create(venue.getCode(), venue.getName()))
                .collect(Collectors.toList());
    }
}
