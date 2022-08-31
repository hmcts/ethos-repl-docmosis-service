package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes;

import java.util.List;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;

public interface RefDataFixesDataSource {
    List<SubmitEvent> getData(String caseTypeId, String dateFrom, String dateTo, CcdClient ccdClient);
}
