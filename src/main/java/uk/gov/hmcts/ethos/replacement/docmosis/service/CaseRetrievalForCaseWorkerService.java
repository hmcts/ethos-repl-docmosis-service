package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseCreationException;
import uk.gov.hmcts.ecm.common.exceptions.CaseRetrievalException;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("CaseRetrievalForCaseWorkerService")
public class CaseRetrievalForCaseWorkerService {

    private static final String MESSAGE = "Failed to retrieve case for : ";
    private static final String ETHOS_REF_DUPLICATES_RETRIEVAL_ERROR_MESSAGE =
            "Failed to retrieve cases with duplicate Ethos ref for : ";
    private final CcdClient ccdClient;

    @Autowired
    public CaseRetrievalForCaseWorkerService(CcdClient ccdClient) {
        this.ccdClient = ccdClient;
    }

    public SubmitEvent caseRetrievalRequest(String authToken, String caseTypeId, String jurisdiction, String caseId) {
        try {
            return ccdClient.retrieveCase(authToken, caseTypeId, jurisdiction, caseId);
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + caseId + ex.getMessage());
        }
    }

    public String caseRefRetrievalRequest(String authToken, String caseTypeId, String jurisdiction, String caseId) {
        try {
            log.info("In Case Retrieval Service - caseRefRetrievalRequest for case type: {} ",  caseTypeId);
            return ccdClient.retrieveTransferredCaseReference(authToken, caseTypeId, jurisdiction, caseId);
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + caseId + ex.getMessage());
        }
    }

    public List<SubmitEvent> casesRetrievalRequest(CCDRequest ccdRequest, String authToken) {
        var caseDetails = ccdRequest.getCaseDetails();
        log.info("EventId: " + ccdRequest.getEventId());
        try {
            return ccdClient.retrieveCases(authToken, caseDetails.getCaseTypeId(), caseDetails.getJurisdiction());
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + caseDetails.getCaseId() + ex.getMessage());
        }
    }

    public List<SubmitEvent> casesRetrievalESRequest(String currentCaseId, String authToken, String caseTypeId,
                                                     List<String> caseIds) {
        try {
            return ccdClient.retrieveCasesElasticSearch(authToken, caseTypeId, caseIds);
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + currentCaseId + ex.getMessage());
        }
    }

    public List<Pair<String, List<SubmitEvent>>>  transferSourceCaseRetrievalESRequest(
            String ethosCaseReference, String currentCaseTypeId, String authToken, List<String> caseTypeIdsToCheck) {
        log.info("About to retrieve ES result(in mtd transferSourceCaseRetrievalESRequest) for case with ethos ref: "
                + ethosCaseReference);
        List<Pair<String, List<SubmitEvent>>> listOfParis = new ArrayList<>();
        try {
            for (String targetOffice : caseTypeIdsToCheck) {
                if (currentCaseTypeId.equals(targetOffice)) {
                    continue;
                }
                List<SubmitEvent> submitEvents = ccdClient.retrieveCasesWithDuplicateEthosRefElasticSearch(authToken,
                        targetOffice, ethosCaseReference);
                if (!submitEvents.isEmpty()) {
                    log.info("In Case Retrieval Service - transferSourceCaseRetrievalESRequest for case type: {} ",
                            targetOffice);
                    log.info("SubmitEvents count: {}", submitEvents.size());

                    listOfParis.add(Pair.of(targetOffice, submitEvents));
                }
            }
            return listOfParis;
        } catch (Exception ex) {
            throw new CaseRetrievalException(ETHOS_REF_DUPLICATES_RETRIEVAL_ERROR_MESSAGE
                    + ethosCaseReference + ex.getMessage());
        }
    }
}