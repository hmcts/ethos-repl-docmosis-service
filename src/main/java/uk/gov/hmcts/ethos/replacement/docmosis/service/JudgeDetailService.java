package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.judges_data.JudgeDetail;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.JudgeDetailRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service("judgeDetailService")
public class JudgeDetailService {

    @Autowired
    private final JudgeDetailRepository judgeDetailRepository;

    public List<JudgeDetail> getJudgeDetailsByTribunalOffice(String tribunalOffice){
        var results = judgeDetailRepository.getJudgeDetailByOffice(tribunalOffice);
        return results;
    }
}
