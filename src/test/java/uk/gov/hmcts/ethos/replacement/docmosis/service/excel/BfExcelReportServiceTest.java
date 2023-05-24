package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.hmcts.ecm.common.model.listing.items.BFDateTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.BFDateType;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction.BfActionReportData;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class BfExcelReportServiceTest {
    @Mock
    BfExcelReportService service;
    BfActionReportData reportData;
    ExcelCreationService excelCreationService;

    @Before
    public void setUp() {
        reportData = new BfActionReportData();
        reportData.setReportPeriodDescription("Period description");
        reportData.setReportPrintedOnDescription("Printed On Description");
        reportData.setOffice("Office");
        reportData.setReportDate("2022-01-01");
        BFDateTypeItem bfDateTypeItem = new BFDateTypeItem();
        BFDateType bfDateType = new BFDateType();
        bfDateTypeItem.setId(UUID.randomUUID().toString());
        bfDateType.setBroughtForwardDateReason("reason");
        bfDateType.setBroughtForwardAction("action");
        bfDateType.setBroughtForwardDate("202-01-03");
        bfDateType.setCaseReference("111000/2023");
        bfDateType.setBroughtForwardDateCleared("cleared date");
        bfDateType.setBroughtForwardEnteredDate("entered date");
        bfDateTypeItem.setValue(bfDateType);
        reportData.setBfDateCollection(List.of(bfDateTypeItem));

        excelCreationService = mock(ExcelCreationService.class);
        doAnswer((i) -> null).when(excelCreationService).initializeReportHeaders(anyString(),
                anyString(), any(), any(), any());
        doAnswer((i) -> null).when(excelCreationService).addReportAdminDetails(
                any(), any(), anyInt(), anyString(), anyInt());
        service = new BfExcelReportService(excelCreationService);
    }

    @Test
    public void shouldReturnReportExcelFileDocumentInfo() {
        assertNotNull(service.getReportExcelFile(reportData));
    }

    @Test
    public void shouldReturnReportExcelFileEmptyByteArray() {
        assertNotNull(service.getReportExcelFile(
                new BfActionReportData()));
    }
}
