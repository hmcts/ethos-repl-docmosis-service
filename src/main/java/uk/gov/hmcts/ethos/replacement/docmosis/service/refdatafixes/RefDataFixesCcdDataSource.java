package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

@RequiredArgsConstructor
@Slf4j
public class RefDataFixesCcdDataSource implements RefDataFixesDataSource {

    private final String authToken;

    @Override
    public List<SubmitEvent> getDataForJudges(String caseTypeId, String dateFrom, String dateTo, CcdClient ccdClient) {
        try {
            String query = RefDataFixesElasticSearchQuery.create(
                    dateFrom, dateTo);
            return ccdClient.executeElasticSearch(authToken, caseTypeId, query);
        } catch (Exception e) {
            throw new RefDataFixesException(String.format(
                    "Failed to get cases for case type id %s", caseTypeId), e);
        }
    }

    @Override
    public List<SubmitEvent> getDataForInsertClaimDate(String caseTypeId, String dateFrom, String dateTo, CcdClient ccdClient) {
        try {
            String query = RefDataFixesElasticSearchQuery.createForInsertClaimDate(
                    dateFrom, dateTo);
            return ccdClient.executeElasticSearch(authToken, caseTypeId, query);
        } catch (Exception e) {
            throw new RefDataFixesException(String.format(
                    "Failed to get cases for case type id %s", caseTypeId), e);
        }
    }
}
