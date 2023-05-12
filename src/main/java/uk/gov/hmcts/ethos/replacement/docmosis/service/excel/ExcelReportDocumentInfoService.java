package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction.BfActionReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportData;

@RequiredArgsConstructor
@Slf4j
@Service
public class ExcelReportDocumentInfoService {
    private final ClaimsByHearingVenueExcelReportCreationService claimsByHearingVenueExcelReportCreationService;
    private final ExcelDocManagementService excelDocManagementService;
    private final BfExcelReportService bfExcelReportService;
    private static final String CLAIMS_BY_HEARING_VENUE_FILE_NAME = "_Hearings_By_Venue_Report.xlsx";
    private static final String BROUGHT_FORWARD_REPORT_FILE_NAME = "_Brought_Forward_Report.xlsx";

    public DocumentInfo generateClaimsByHearingVenueExcelReportDocumentInfo(
            ClaimsByHearingVenueReportData reportData,
            String caseTypeId,
            String userToken) {
        byte[] excelBytes = claimsByHearingVenueExcelReportCreationService.getReportExcelFile(reportData);
        String outPutFileName = UtilHelper.getListingCaseTypeId(caseTypeId) + CLAIMS_BY_HEARING_VENUE_FILE_NAME;
        return excelDocManagementService.uploadExcelReportDocument(userToken, outPutFileName, excelBytes);
    }

    public DocumentInfo generateBfExcelReportDocumentInfo(BfActionReportData reportData, String caseTypeId,
                                                          String userToken) {
        byte[] excelBytes = bfExcelReportService.getReportExcelFile(reportData);
        String outPutFileName = UtilHelper.getListingCaseTypeId(caseTypeId) + BROUGHT_FORWARD_REPORT_FILE_NAME;
        return excelDocManagementService.uploadExcelReportDocument(userToken, outPutFileName, excelBytes);
    }
}
