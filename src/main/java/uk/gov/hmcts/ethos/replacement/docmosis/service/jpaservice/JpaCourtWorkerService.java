package uk.gov.hmcts.ethos.replacement.docmosis.service.jpaservice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.CourtWorkerRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class JpaCourtWorkerService {

    @Autowired
    private final CourtWorkerRepository courtWorkerRepository;

    public List<DynamicValueType> getCourtWorkerByTribunalOffice(String tribunalOffice, String lookUpId){
        return courtWorkerRepository.getCourtWorkersByOffice(
                tribunalOffice,
                lookUpId);
    }
}

