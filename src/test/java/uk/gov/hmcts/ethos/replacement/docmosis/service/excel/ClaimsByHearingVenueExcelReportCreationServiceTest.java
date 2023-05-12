package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportDetail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class ClaimsByHearingVenueExcelReportCreationServiceTest {
    @Mock
    ClaimsByHearingVenueExcelReportCreationService service;
    ExcelCreationService excelCreationService;
    ClaimsByHearingVenueReportData reportData;

    @Before
    public void setUp() {
        reportData = new ClaimsByHearingVenueReportData();
        ClaimsByHearingVenueReportDetail detailEntry = new ClaimsByHearingVenueReportDetail();
        detailEntry.setCaseReference("245000/2021");
        detailEntry.setRespondentET3Postcode("TE5 TE1");
        reportData.getReportDetails().add(detailEntry);
        excelCreationService = mock(ExcelCreationService.class);
        doAnswer((i) -> null).when(excelCreationService).initializeReportHeaders(anyString(),
                anyString(), any(), any(), any());
        doAnswer((i) -> null).when(excelCreationService).addReportAdminDetails(
                any(), any(), anyInt(), anyString(), anyInt());
        service = new ClaimsByHearingVenueExcelReportCreationService(excelCreationService);
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
