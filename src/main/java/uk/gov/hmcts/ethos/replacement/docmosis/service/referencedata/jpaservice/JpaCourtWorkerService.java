package uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.jpaservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.CourtWorkerType;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.CourtWorkerRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice.TribunalOffice;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.CourtWorkerService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class JpaCourtWorkerService implements CourtWorkerService {

    private final CourtWorkerRepository courtWorkerRepository;

    public List<DynamicValueType> getCourtWorkerByTribunalOffice(TribunalOffice tribunalOffice,
                                                                 CourtWorkerType courtWorkerType) {
        return courtWorkerRepository.findByTribunalOfficeAndType(tribunalOffice, courtWorkerType)
                .stream()
                .map(cw -> DynamicValueType.create(cw.getCode(), cw.getName()))
                .collect(Collectors.toList());
    }
}

