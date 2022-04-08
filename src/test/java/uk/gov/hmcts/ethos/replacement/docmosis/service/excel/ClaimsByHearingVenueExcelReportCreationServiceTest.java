package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportDetail;

public class ClaimsByHearingVenueExcelReportCreationServiceTest {
    @Mock
    ClaimsByHearingVenueExcelReportCreationService service;
    ClaimsByHearingVenueReportData reportData;

    @Before
    public void setUp() {
        service = new ClaimsByHearingVenueExcelReportCreationService();
        reportData = new ClaimsByHearingVenueReportData();
        var detailEntry = new ClaimsByHearingVenueReportDetail();
        detailEntry.setCaseReference("245000/2021");
        detailEntry.setRespondentET3Postcode("TE5 TE1");
        reportData.getReportDetails().add(detailEntry);
    }

    @Test
    public void shouldReturnReportExcelFileDocumentInfo() {
        assertNotNull(service.getReportExcelFile(reportData));
    }

    @Test
    public void shouldReturnReportExcelFileEmptyByteArray() {
        assertNotNull(service.getReportExcelFile(
                new ClaimsByHearingVenueReportData()));
    }
}
