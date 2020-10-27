package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;

@Slf4j
@Service("multipleCasesSendingService")
public class MultipleCasesSendingService {

    private final CcdClient ccdClient;

    @Autowired
    public MultipleCasesSendingService(CcdClient ccdClient) {
        this.ccdClient = ccdClient;
    }

    public void sendUpdateToMultiple(String userToken, String caseTypeId, String jurisdiction,
                                     MultipleData multipleData, String caseId) {


        try {
            CCDRequest returnedRequest = ccdClient.startBulkAmendEventForCase(
                    userToken,
                    caseTypeId,
                    jurisdiction,
                    caseId);

            ccdClient.submitMultipleEventForCase(
                    userToken,
                    multipleData,
                    caseTypeId,
                    jurisdiction,
                    returnedRequest,
                    caseId);

        } catch (Exception ex) {

            log.error("Error sending update to multiple case: " + caseId);

            throw new RuntimeException("Error sending update to multiple case", ex);

        }
    }

}
