package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.reports.hearingsbyhearingtype.HearingsByHearingTypeSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class HearingsByHearingTypeCcdReportDataSource implements HearingsByHearingTypeReportDataSource {

    private final String authToken;
    private final CcdClient ccdClient;

    @Override
    public List<HearingsByHearingTypeSubmitEvent> getData(ReportParams reportParams) {
        var caseTypeId = UtilHelper.getListingCaseTypeId(reportParams.getCaseTypeId());
        try {
            var query = HearingsByHearingTypeElasticSearchQuery.create(
                    reportParams.getDateFrom(), reportParams.getDateTo());
            return ccdClient.hearingsByHearingTypeSearch(authToken, caseTypeId, query);
        } catch (Exception e) {
            throw new ReportException(String.format(
                    "Failed to get hearings by hearing type search results for case type id %s", caseTypeId), e);
        }
    }
}
