package uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.jpaservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.JudgeRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice.TribunalOffice;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.JudgeService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class JpaJudgeService implements JudgeService {

    private final JudgeRepository judgeRepository;

    @Override
    public List<DynamicValueType> getJudges(TribunalOffice tribunalOffice) {
        return judgeRepository.findByTribunalOffice(tribunalOffice).stream()
                .map(j -> DynamicValueType.create(j.getCode(), j.getName()))
                .collect(Collectors.toList());
    }
}
