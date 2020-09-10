package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("multipleCasesReadingService")
public class MultipleCasesReadingService {

    private final CcdClient ccdClient;

    @Autowired
    public MultipleCasesReadingService(CcdClient ccdClient) {
        this.ccdClient = ccdClient;
    }

    public List<SubmitMultipleEvent> retrieveMultipleCases(String userToken,
                                                           String multipleCaseTypeId,
                                                           String multipleReference) {

        List<SubmitMultipleEvent> submitMultipleEvents = new ArrayList<>();

        try {
            submitMultipleEvents = ccdClient.retrieveMultipleCasesElasticSearch(
                    userToken,
                    multipleCaseTypeId,
                    multipleReference);

        } catch (Exception ex) {
            log.error("Error retrieving multiple cases");
        }

        return submitMultipleEvents;
    }

}
