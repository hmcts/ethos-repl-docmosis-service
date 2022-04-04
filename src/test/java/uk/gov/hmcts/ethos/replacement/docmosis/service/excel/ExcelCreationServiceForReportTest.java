package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportDetail;

public class ExcelCreationServiceForReportTest {

    ExcelCreationServiceForReport excelCreationServiceForReport;
    private ClaimsByHearingVenueReportData reportData;
    private static final String username = "Test User Name";

    @Before
    public void setUp() {
        excelCreationServiceForReport = new ExcelCreationServiceForReport();
        reportData = new ClaimsByHearingVenueReportData();
        var detailEntry = new ClaimsByHearingVenueReportDetail();
        detailEntry.setCaseReference("245000/2021");
        detailEntry.setRespondentET3Postcode("TE5 TE1");
        reportData.getReportDetails().add(detailEntry);
    }

    @Test
    public void shouldReturnReportExcelFileByteArray() {
        assertNotNull(excelCreationServiceForReport.getReportExcelFile(reportData, username));
    }

    @Test
    public void shouldReturnReportExcelFileEmptyByteArray() {
        assertNotNull(excelCreationServiceForReport.getReportExcelFile(new ClaimsByHearingVenueReportData(), username));
    }
}
