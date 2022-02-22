package uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.jpaservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.Judge;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.JudgeRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.JudgeService;

@Slf4j
@RequiredArgsConstructor
@Service
public class JpaJudgeService implements JudgeService {

    private final JudgeRepository judgeRepository;

    @Override
    public Judge getJudge(String office, String name) {
        return judgeRepository.findByTribunalOfficeAndName(office, name);
    }
}
