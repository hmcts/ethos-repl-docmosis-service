package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import java.util.List;
import uk.gov.hmcts.ecm.common.model.reports.hearingsbyhearingtype.HearingsByHearingTypeSubmitEvent;
import uk.gov.hmcts.ecm.common.model.reports.sessiondays.SessionDaysSubmitEvent;

public interface HearingsByHearingTypeReportDataSource {
    List<HearingsByHearingTypeSubmitEvent> getData(String caseTypeId, String listingDateFrom, String listingDateTo);
}
