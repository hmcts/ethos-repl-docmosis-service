package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.listing.items.BFDateTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.BFDateType;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction.BfActionReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportDetail;
import java.util.List;
import java.util.UUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_LISTING_CASE_TYPE_ID;

@RunWith(MockitoJUnitRunner.class)
public class ExcelReportDocumentInfoServiceTest {
    @Mock
    ExcelReportDocumentInfoService excelReportDocInfService;
    @Mock
    ExcelDocManagementService excelDocManagementService;
    @Mock
    BfExcelReportService bfExcelReportService;
    @Mock
    ClaimsByHearingVenueExcelReportCreationService claimsByHearingVenueExcelReportCreationService;
    ClaimsByHearingVenueReportData claimsByHearingVenueReportData;
    BfActionReportData bfActionReportData;
    private DocumentInfo docInfo;

    private void setUpClaimsReport() {
        claimsByHearingVenueReportData = new ClaimsByHearingVenueReportData();
        ClaimsByHearingVenueReportDetail detailEntry = new ClaimsByHearingVenueReportDetail();
        detailEntry.setCaseReference("245000/2021");
        detailEntry.setRespondentET3Postcode("TE5 TE1");
        claimsByHearingVenueReportData.getReportDetails().add(detailEntry);
        excelReportDocInfService = new ExcelReportDocumentInfoService(
                claimsByHearingVenueExcelReportCreationService, excelDocManagementService, bfExcelReportService);
        docInfo = new DocumentInfo();
    }

    private void setUpBfReport() {
        bfActionReportData = new BfActionReportData();
        BFDateTypeItem bfDateTypeItem = new BFDateTypeItem();
        bfDateTypeItem.setId(UUID.randomUUID().toString());
        BFDateType bfDateType = new BFDateType();
        bfDateType.setCaseReference("245000/2021");
        bfDateType.setBroughtForwardDate("2023-01-01");
        bfDateType.setBroughtForwardAction("action");
        bfDateType.setBroughtForwardEnteredDate("2023-02-01");
        bfDateType.setBroughtForwardDateReason("reason");
        bfDateTypeItem.setValue(bfDateType);
        bfActionReportData.setBfDateCollection(List.of(bfDateTypeItem));
        excelReportDocInfService = new ExcelReportDocumentInfoService(
                claimsByHearingVenueExcelReportCreationService, excelDocManagementService, bfExcelReportService);
        docInfo = new DocumentInfo();
    }

    @Test
    public void shouldReturnNonNullExcelReportDocumentInfo() {
        setUpClaimsReport();
        when(claimsByHearingVenueExcelReportCreationService.getReportExcelFile(claimsByHearingVenueReportData))
                .thenReturn(new byte[0]);
        when(excelDocManagementService
                .uploadExcelReportDocument("dummyToken",
                        "Manchester_Hearings_By_Venue_Report.xlsx",
                        new byte[0]))
                .thenReturn(docInfo);

        DocumentInfo resultDocInfo = excelReportDocInfService.generateClaimsByHearingVenueExcelReportDocumentInfo(
                claimsByHearingVenueReportData, MANCHESTER_LISTING_CASE_TYPE_ID, "dummyToken");
        assertNotNull(resultDocInfo);
    }

    @Test
    public void shouldReturnCorrectCountOfDependenciesInvocation() {
        setUpClaimsReport();
        when(claimsByHearingVenueExcelReportCreationService.getReportExcelFile(claimsByHearingVenueReportData))
                .thenReturn(new byte[0]);
        when(excelDocManagementService
                .uploadExcelReportDocument("dummyToken",
                        "Manchester_Hearings_By_Venue_Report.xlsx",
                        new byte[0]))
                .thenReturn(docInfo);

        excelReportDocInfService.generateClaimsByHearingVenueExcelReportDocumentInfo(claimsByHearingVenueReportData,
                MANCHESTER_LISTING_CASE_TYPE_ID, "dummyToken");

        verify(claimsByHearingVenueExcelReportCreationService, times(1))
                .getReportExcelFile(claimsByHearingVenueReportData);
        verifyNoMoreInteractions(claimsByHearingVenueExcelReportCreationService);
        verify(excelDocManagementService, times(1))
                .uploadExcelReportDocument("dummyToken",
                        "Manchester_Hearings_By_Venue_Report.xlsx", new byte[0]);
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void shouldReturnCorrectCountOfDependenciesInvocationBfReport() {
        setUpBfReport();
        when(bfExcelReportService.getReportExcelFile(bfActionReportData))
                .thenReturn(new byte[0]);
        when(excelDocManagementService
                .uploadExcelReportDocument("dummyToken",
                        "Manchester_Brought_Forward_Report.xlsx",
                        new byte[0]))
                .thenReturn(docInfo);

        excelReportDocInfService.generateBfExcelReportDocumentInfo(bfActionReportData,
                MANCHESTER_LISTING_CASE_TYPE_ID, "dummyToken");

        verify(bfExcelReportService, times(1)).getReportExcelFile(bfActionReportData);
        verifyNoMoreInteractions(bfExcelReportService);
        verify(excelDocManagementService, times(1))
                .uploadExcelReportDocument("dummyToken",
                        "Manchester_Brought_Forward_Report.xlsx", new byte[0]);
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void shouldReturnNonNullExcelBfReportDocumentInfo() {
        setUpBfReport();
        when(bfExcelReportService.getReportExcelFile(bfActionReportData))
                .thenReturn(new byte[0]);
        when(excelDocManagementService
                .uploadExcelReportDocument("dummyToken",
                        "Manchester_Brought_Forward_Report.xlsx",
                        new byte[0]))
                .thenReturn(docInfo);

        DocumentInfo resultDocInfo = excelReportDocInfService.generateBfExcelReportDocumentInfo(
                bfActionReportData, MANCHESTER_LISTING_CASE_TYPE_ID, "dummyToken");
        assertNotNull(resultDocInfo);
    }
}
