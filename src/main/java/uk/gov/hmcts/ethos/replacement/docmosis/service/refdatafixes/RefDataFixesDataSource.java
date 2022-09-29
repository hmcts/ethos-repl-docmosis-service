package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes;

import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import java.util.List;

public interface RefDataFixesDataSource {
    List<SubmitEvent> getData(String caseTypeId, String dateFrom, String dateTo, CcdClient ccdClient);
}
