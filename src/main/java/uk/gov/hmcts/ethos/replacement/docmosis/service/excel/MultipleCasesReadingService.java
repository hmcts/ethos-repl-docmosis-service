package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;

import java.util.List;

@Slf4j
@Service("multipleCasesReadingService")
public class MultipleCasesReadingService {

    private final CcdClient ccdClient;

    @Autowired
    public MultipleCasesReadingService(CcdClient ccdClient) {
        this.ccdClient = ccdClient;
    }

    public List<SubmitMultipleEvent> retrieveMultipleCasesWithRetries(String userToken,
                                                           String multipleCaseTypeId,
                                                           String multipleReference) {

        List<SubmitMultipleEvent> submitMultipleEvents;

        try {
            submitMultipleEvents = ccdClient.retrieveMultipleCasesElasticSearchWithRetries(
                    userToken,
                    multipleCaseTypeId,
                    multipleReference);

        } catch (Exception ex) {

            log.error("Error retrieving multiple cases with retries");

            throw new RuntimeException("Error retrieving multiple cases with retries", ex);

        }

        return submitMultipleEvents;

    }

    public List<SubmitMultipleEvent> retrieveMultipleCases(String userToken,
                                                                      String multipleCaseTypeId,
                                                                      String multipleReference) {

        List<SubmitMultipleEvent> submitMultipleEvents;

        try {
            submitMultipleEvents = ccdClient.retrieveMultipleCasesElasticSearch(
                    userToken,
                    multipleCaseTypeId,
                    multipleReference);

        } catch (Exception ex) {

            log.error("Error retrieving multiple cases");

            throw new RuntimeException("Error retrieving multiple cases", ex);

        }

        return submitMultipleEvents;

    }

}
