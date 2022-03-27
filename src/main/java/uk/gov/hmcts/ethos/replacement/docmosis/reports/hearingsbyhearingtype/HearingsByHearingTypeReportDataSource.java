package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import uk.gov.hmcts.ecm.common.model.reports.hearingsbyhearingtype.HearingsByHearingTypeSubmitEvent;
import java.util.List;

public interface HearingsByHearingTypeReportDataSource {
    List<HearingsByHearingTypeSubmitEvent> getData(String caseTypeId, String listingDateFrom, String listingDateTo);
}
