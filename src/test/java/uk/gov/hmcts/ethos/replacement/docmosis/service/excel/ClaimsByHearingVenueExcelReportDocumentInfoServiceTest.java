package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportDetail;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_LISTING_CASE_TYPE_ID;

@RunWith(MockitoJUnitRunner.class)
public class ClaimsByHearingVenueExcelReportDocumentInfoServiceTest {
    @Mock
    ClaimsByHearingVenueExcelReportDocumentInfoService excelReportDocInfService;
    @Mock
    ExcelDocManagementService excelDocManagementService;
    @Mock
    ClaimsByHearingVenueExcelReportCreationService reportCreationService;
    ClaimsByHearingVenueReportData reportData;
    private DocumentInfo docInfo;
    @Before
    public void setUp() {
        reportData = new ClaimsByHearingVenueReportData();
        var detailEntry = new ClaimsByHearingVenueReportDetail();
        detailEntry.setCaseReference("245000/2021");
        detailEntry.setRespondentET3Postcode("TE5 TE1");
        reportData.getReportDetails().add(detailEntry);
        excelReportDocInfService = new ClaimsByHearingVenueExcelReportDocumentInfoService(
                reportCreationService, excelDocManagementService);
        docInfo = new DocumentInfo();
    }

    @Test
    public void shouldReturnNonNullExcelReportDocumentInfo() {
        when(reportCreationService.getReportExcelFile(reportData))
                .thenReturn(new byte[0]);
        when(excelDocManagementService
                .uploadExcelReportDocument("dummyToken",
                        "Leeds_Listings_Hearings_By_Venue_Report.xlsx",
                        new byte[0]))
                .thenReturn(docInfo);

       var resultDocInfo = excelReportDocInfService.generateExcelReportDocumentInfo(reportData,
               LEEDS_LISTING_CASE_TYPE_ID, "dummyToken");
        assertNotNull(resultDocInfo);
    }

    @Test
    public void shouldReturnCorrectCountOfDependenciesInvocation() {
        when(reportCreationService.getReportExcelFile(reportData))
                .thenReturn(new byte[0]);
        when(excelDocManagementService
                .uploadExcelReportDocument("dummyToken",
                        "Leeds_Listings_Hearings_By_Venue_Report.xlsx",
                        new byte[0]))
                .thenReturn(docInfo);

        var resultDocInfo = excelReportDocInfService.generateExcelReportDocumentInfo(reportData,
                LEEDS_LISTING_CASE_TYPE_ID, "dummyToken");

        verify(reportCreationService, times(1)).getReportExcelFile(reportData);
        verifyNoMoreInteractions(reportCreationService);
        verify(excelDocManagementService, times(1))
                .uploadExcelReportDocument("dummyToken",
                        "Leeds_Listings_Hearings_By_Venue_Report.xlsx", new byte[0]);
        verifyNoMoreInteractions(excelDocManagementService);
    }
}
