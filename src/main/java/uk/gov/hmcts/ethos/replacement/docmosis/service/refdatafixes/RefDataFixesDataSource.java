package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes;

import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import java.util.List;

public interface RefDataFixesDataSource {
    List<SubmitEvent> getDataForJudges(String caseTypeId, String dateFrom, String dateTo, CcdClient ccdClient);

    List<SubmitEvent> getDataForInsertClaimDate(String caseTypeId, String dateFrom, String dateTo, CcdClient ccdClient);

}
