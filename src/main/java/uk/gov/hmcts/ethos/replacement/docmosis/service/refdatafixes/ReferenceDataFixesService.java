package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseRetrievalException;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

@Slf4j
@RequiredArgsConstructor
@Service("referenceDataFixesService")
public class ReferenceDataFixesService {
    private static final String CASES_SEARCHED = "Cases searched: ";
    private static final String MESSAGE = "Failed to retrieve reference data for case id : ";
    private final CcdClient ccdClient;

    public CaseData updateJudgesItcoReferences(CaseDetails caseDetails, String authToken) {

        //var caseData = caseDetails.getCaseData();
        try {

            List<SubmitEvent> submitEvents = ccdClient.retrieveCases(authToken,
                    caseDetails.getCaseTypeId(), caseDetails.getJurisdiction());
            if (CollectionUtils.isNotEmpty(submitEvents)) {
                log.info(CASES_SEARCHED + submitEvents.size());
                for (SubmitEvent submitEvent : submitEvents) {
                    CaseData caseData = submitEvent.getCaseData();
                    caseData.getHearingCollection().stream().filter(i -> i.getValue().getJudge().)
                }
            }
           // return caseData;
        } catch (Exception ex) {
            throw new CaseRetrievalException(MESSAGE + caseDetails.getCaseId() + ex.getMessage());
        }
    }
}
