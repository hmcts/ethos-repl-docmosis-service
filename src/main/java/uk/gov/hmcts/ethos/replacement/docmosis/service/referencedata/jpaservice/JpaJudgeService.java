package uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.jpaservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.Judge;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.JudgeRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.JudgeService;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class JpaJudgeService implements JudgeService {

    private final JudgeRepository judgeRepository;

    @Override
    public List<Judge> getJudges(String tribunalOffice) {
        return judgeRepository.findByTribunalOffice(tribunalOffice);
    }
}
