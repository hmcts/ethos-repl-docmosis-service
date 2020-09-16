package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;

import java.util.*;

@Slf4j
@Service("singleCasesReadingService")
public class SingleCasesReadingService {

    private final CcdClient ccdClient;

    @Autowired
    public SingleCasesReadingService(CcdClient ccdClient) {
        this.ccdClient = ccdClient;
    }

    public List<SubmitEvent> retrieveSingleCases(String userToken, String caseTypeId,
                                                  TreeMap<String, Object> multipleObjects,
                                                  FilterExcelType filterExcelType) {

        List<SubmitEvent> submitEvents = new ArrayList<>();

        try {
            submitEvents = ccdClient.retrieveCasesElasticSearch(userToken,
                    UtilHelper.getCaseTypeId(caseTypeId),
                    filterExcelType.equals(FilterExcelType.FLAGS) ?
                            new ArrayList<>(multipleObjects.keySet()) :
                            getSubMultipleCaseIds(multipleObjects));

        } catch (Exception ex) {

            log.error("Error retrieving single cases");

        }

        return submitEvents;
    }

    private List<String> getSubMultipleCaseIds(TreeMap<String, Object> multipleObjects) {

        List<String> caseIds = new ArrayList<>();

        for (Map.Entry<String, Object> entry : multipleObjects.entrySet()) {
            caseIds.addAll((List<String>) entry.getValue());
        }

        return caseIds;
    }

    public SubmitEvent retrieveSingleCase(String userToken, String caseTypeId, String caseId) {

        List<SubmitEvent> submitEvents = new ArrayList<>();

        try {
            submitEvents = ccdClient.retrieveCasesElasticSearch(userToken,
                    UtilHelper.getCaseTypeId(caseTypeId),
                    new ArrayList<>(Collections.singletonList(caseId)));

        } catch (Exception ex) {

            log.error("Error retrieving single cases");

        }

        return submitEvents.isEmpty() ? null : submitEvents.get(0);
    }

}
