package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.items.AdhocReportTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.CasesAwaitingJudgmentReportData;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;

@Slf4j
public class ReportDocHelper {
    private static final String REPORT_LIST = "\"Report_List\":[\n";
    private static final String CASE_REFERENCE = "{\"Case_Reference\":\"";

    private ReportDocHelper() {
    }

    public static StringBuilder buildReportDocumentContent(ListingData listingData, String accessKey,
                                                           String templateName, UserDetails userDetails) {
        log.info("Building {} report document data", listingData.getReportType());

        var sb = new StringBuilder();

        sb.append("{\n");
        sb.append("\"accessKey\":\"").append(accessKey).append(NEW_LINE);
        sb.append("\"templateName\":\"").append(templateName).append(FILE_EXTENSION).append(NEW_LINE);
        sb.append("\"outputName\":\"").append(OUTPUT_FILE_NAME).append(NEW_LINE);

        sb.append("\"data\":{\n");

        if (CASES_AWAITING_JUDGMENT_REPORT.equals(listingData.getReportType())) {
            try {
                sb.append(getCasesAwaitingJudgmentReport(listingData));
            } catch (JsonProcessingException e) {
                throw new ReportException("Unable to create report data", e);
            }
        } else {
            sb.append(ListingHelper.getListingDate(listingData));

            if (listingData.getLocalReportsDetailHdr() != null) {
                sb.append("\"Report_Office\":\"").append(
                        nullCheck(listingData.getLocalReportsDetailHdr().getReportOffice())).append(NEW_LINE);
            }

            switch (listingData.getReportType()) {
                case CLAIMS_ACCEPTED_REPORT:
                    sb.append(getCasesAcceptedReport(listingData));
                    break;
                case LIVE_CASELOAD_REPORT:
                    sb.append(getLiveCaseLoadReport(listingData));
                    break;
                case CASES_COMPLETED_REPORT:
                    sb.append(getCasesCompletedReport(listingData));
                    break;
                case TIME_TO_FIRST_HEARING_REPORT:
                    sb.append(getTimeToFirstHearingReport(listingData));
                    break;
                default:
                    throw new IllegalStateException("Report type - Unexpected value: " + listingData.getReportType());
            }
            log.info(sb.toString());
        }

        String userName = nullCheck(userDetails.getFirstName() + " " + userDetails.getLastName());
        sb.append("\"Report_Clerk\":\"").append(nullCheck(userName)).append(NEW_LINE);

        sb.append("\"Today_date\":\"").append(UtilHelper.formatCurrentDate(LocalDate.now())).append("\"\n");

        sb.append("}\n");
        sb.append("}\n");
        return sb;
    }

    private static StringBuilder getCasesAwaitingJudgmentReport(ListingData listingData) throws JsonProcessingException {
        if (!(listingData instanceof CasesAwaitingJudgmentReportData)) {
            throw new IllegalStateException(("ListingData is not instanceof CasesAwaitingJudgmentReportData"));
        }
        var reportData = (CasesAwaitingJudgmentReportData) listingData;

        var sb = new StringBuilder();
        sb.append("\"Report_Office\":\"").append(reportData.getReportSummary().getOffice()).append(NEW_LINE);
        addJsonCollection("positionTypes", reportData.getReportSummary().getPositionTypes().iterator(), sb);
        addJsonCollection("reportDetails", reportData.getReportDetails().iterator(), sb);
        return sb;
    }

    private static void addJsonCollection(String name, Iterator<?> iterator, StringBuilder sb)
            throws JsonProcessingException {
        sb.append("\"").append(name).append("\":[\n");
        var objectMapper = new ObjectMapper();
        while (iterator.hasNext()) {
            sb.append(objectMapper.writeValueAsString(iterator.next()));
            if (iterator.hasNext()) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("],\n");
    }

    private static StringBuilder getCasesAcceptedReport(ListingData listingData) {
        var sb = new StringBuilder();
        AdhocReportType localReportDetailHdr = listingData.getLocalReportsDetailHdr();
        if (localReportDetailHdr != null) {
            sb.append("\"Multiple_Claims_Accepted\":\"").append(
                    nullCheck(localReportDetailHdr.getMultiplesTotal())).append(NEW_LINE);
            sb.append("\"Singles_Claims_Accepted\":\"").append(
                    nullCheck(localReportDetailHdr.getSinglesTotal())).append(NEW_LINE);
            sb.append("\"Total_Claims_Accepted\":\"").append(
                    nullCheck(localReportDetailHdr.getTotal())).append(NEW_LINE);
        }

        if (listingData.getLocalReportsDetail() != null && !listingData.getLocalReportsDetail().isEmpty()) {
            sb.append(getClaimsAcceptedByCaseType(listingData));
        }
        return sb;
    }

    private static StringBuilder getClaimsAcceptedByCaseType(ListingData listingData) {
        var sb = new StringBuilder();
        Map<Boolean, List<AdhocReportTypeItem>> unsortedMap = listingData.getLocalReportsDetail().stream()
                .collect(Collectors.partitioningBy(localReportDetail ->
                        localReportDetail.getValue().getMultipleRef() != null));
        sb.append("\"Local_Report_By_Type\":[\n");
        Iterator<Map.Entry<Boolean, List<AdhocReportTypeItem>>> entries =
                new TreeMap<>(unsortedMap).entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Boolean, List<AdhocReportTypeItem>> localReportEntry = entries.next();
            String singleOrMultiple = localReportEntry.getKey() ? "Multiples" : "Singles";
            sb.append("{\"Case_Type\":\"").append(singleOrMultiple).append(NEW_LINE);
            sb.append("\"Claims_Number\":\"").append(localReportEntry.getValue().size()).append(NEW_LINE);
            sb.append(REPORT_LIST);
            for (var i = 0; i < localReportEntry.getValue().size(); i++) {
                sb.append(getAdhocReportCommonTypeRow(localReportEntry.getValue().get(i).getValue()));
                if (i != localReportEntry.getValue().size() - 1) {
                    sb.append(",\n");
                }
            }
            sb.append("]\n");
            if (entries.hasNext()) {
                sb.append("},\n");
            } else {
                sb.append("}],\n");
            }
        }
        return sb;
    }

    private static StringBuilder getAdhocReportCommonTypeRow(AdhocReportType adhocReportType) {
        var sb = new StringBuilder();
        sb.append(CASE_REFERENCE).append(
                nullCheck(adhocReportType.getCaseReference())).append(NEW_LINE);
        sb.append("\"Date_Of_Acceptance\":\"").append(
                nullCheck(adhocReportType.getDateOfAcceptance())).append(NEW_LINE);
        sb.append("\"Multiple_Ref\":\"").append(
                nullCheck(adhocReportType.getMultipleRef())).append(NEW_LINE);
        sb.append("\"Lead_Case\":\"").append(
                nullCheck(adhocReportType.getLeadCase())).append(NEW_LINE);
        sb.append("\"Position\":\"").append(
                nullCheck(adhocReportType.getPosition())).append(NEW_LINE);
        sb.append("\"Date_To_Position\":\"").append(
                nullCheck(adhocReportType.getDateToPosition())).append(NEW_LINE);
        sb.append("\"File_Location\":\"").append(
                nullCheck(adhocReportType.getFileLocation())).append(NEW_LINE);
        sb.append("\"Clerk\":\"").append(
                nullCheck(adhocReportType.getClerk())).append("\"}");
        return sb;
    }

    private static StringBuilder getTimeToFirstHearingReport(ListingData listingData) {
        var sb = new StringBuilder();
        AdhocReportType localReportDetailHdr = listingData.getLocalReportsDetailHdr();
        AdhocReportType localReportSummary = listingData.getLocalReportsSummary().get(0).getValue();

        if (localReportDetailHdr != null) {
            sb.append("\"Total_Cases\":\"").append(
                    nullCheck(localReportDetailHdr.getTotalCases())).append(NEW_LINE);
            sb.append("\"Total_Within_26Weeks\":\"").append(
                    nullCheck(localReportDetailHdr.getTotal26wk())).append(NEW_LINE);
            sb.append("\"Total_Percent_Within_26Weeks\":\"").append(
                    nullCheck(localReportDetailHdr.getTotal26wkPerCent())).append(NEW_LINE);
            sb.append("\"Total_Not_Within_26Weeks\":\"").append(
                    nullCheck(localReportDetailHdr.getTotalx26wk())).append(NEW_LINE);
            sb.append("\"Total_Percent_Not_Within_26Weeks\":\"").append(
                    nullCheck(localReportDetailHdr.getTotalx26wkPerCent())).append(NEW_LINE);
            sb.append("\"ConNone_Total\":\"").append(
                    nullCheck(localReportSummary.getConNoneTotal())).append(NEW_LINE);
            sb.append("\"ConNone_Total_26_Week\":\"").append(
                    nullCheck(localReportSummary.getConNone26wkTotal())).append(NEW_LINE);
            sb.append("\"ConNone_Percent_26_Week\":\"").append(
                    nullCheck(localReportSummary.getConNone26wkTotalPerCent())).append(NEW_LINE);
            sb.append("\"ConNone_Total_Not_26_Week\":\"").append(
                    nullCheck(localReportSummary.getXConNone26wkTotal())).append(NEW_LINE);
            sb.append("\"ConNone_Percent_Not_26_Week\":\"").append(
                    nullCheck(localReportSummary.getXConNone26wkTotalPerCent())).append(NEW_LINE);
            sb.append("\"ConFast_Total\":\"").append(
                    nullCheck(localReportSummary.getConFastTotal())).append(NEW_LINE);
            sb.append("\"ConFast_Total_26_Week\":\"").append(
                    nullCheck(localReportSummary.getConFast26wkTotal())).append(NEW_LINE);
            sb.append("\"ConFast_Percent_26_Week\":\"").append(
                    nullCheck(localReportSummary.getConFast26wkTotalPerCent())).append(NEW_LINE);
            sb.append("\"ConFast_Total_Not_26_Week\":\"").append(
                    nullCheck(localReportSummary.getXConFast26wkTotal())).append(NEW_LINE);
            sb.append("\"ConFast_Percent_Not_26_Week\":\"").append(
                    nullCheck(localReportSummary.getXConFast26wkTotalPerCent())).append(NEW_LINE);
            sb.append("\"ConStd_Total\":\"").append(
                    nullCheck(localReportSummary.getConStdTotal())).append(NEW_LINE);
            sb.append("\"ConStd_Total_26_Week\":\"").append(
                    nullCheck(localReportSummary.getConStd26wkTotal())).append(NEW_LINE);
            sb.append("\"ConStd_Percent_26_Week\":\"").append(
                    nullCheck(localReportSummary.getConStd26wkTotalPerCent())).append(NEW_LINE);
            sb.append("\"ConStd_Total_Not_26_Week\":\"").append(
                    nullCheck(localReportSummary.getXConStd26wkTotal())).append(NEW_LINE);
            sb.append("\"ConStd_Percent_Not_26_Week\":\"").append(
                    nullCheck(localReportSummary.getXConStd26wkTotalPerCent())).append(NEW_LINE);
            sb.append("\"ConOpen_Total\":\"").append(
                    nullCheck(localReportSummary.getConOpenTotal())).append(NEW_LINE);
            sb.append("\"ConOpen_Total_26_Week\":\"").append(
                    nullCheck(localReportSummary.getConOpen26wkTotal())).append(NEW_LINE);
            sb.append("\"ConOpen_Percent_26_Week\":\"").append(
                    nullCheck(localReportSummary.getConOpen26wkTotalPerCent())).append(NEW_LINE);
            sb.append("\"ConOpen_Total_Not_26_Week\":\"").append(
                    nullCheck(localReportSummary.getXConOpen26wkTotal())).append(NEW_LINE);
            sb.append("\"ConOpen_Percent_Not_26_Week\":\"").append(
                    nullCheck(localReportSummary.getXConOpen26wkTotalPerCent())).append(NEW_LINE);

        }

        if (!CollectionUtils.isEmpty(listingData.getLocalReportsDetail())) {
            var adhocReportTypeItems = listingData.getLocalReportsDetail();
            sb.append(REPORT_LIST);
            for (var i = 0; i < adhocReportTypeItems.size(); i++) {
                sb.append(getTimeToFirstHearingAdhocReportTypeRow(adhocReportTypeItems.get(i).getValue()));
                if (i != adhocReportTypeItems.size() - 1) {
                    sb.append(",\n");
                }
            }
            sb.append("],\n");
        }
        log.info(sb.toString());
        return sb;
    }
    private static StringBuilder getCasesCompletedReport(ListingData listingData) {
        var sb = new StringBuilder();
        AdhocReportType localReportDetailHdr = listingData.getLocalReportsDetailHdr();
        if (localReportDetailHdr != null) {
            sb.append("\"Cases_Completed_Hearing\":\"").append(
                    nullCheck(localReportDetailHdr.getCasesCompletedHearingTotal())).append(NEW_LINE);
            sb.append("\"Session_Days_Taken\":\"").append(
                    nullCheck(localReportDetailHdr.getSessionDaysTotal())).append(NEW_LINE);
            sb.append("\"Completed_Per_Session_Day\":\"").append(
                    nullCheck(localReportDetailHdr.getCompletedPerSessionTotal())).append(NEW_LINE);

            sb.append("\"No_Conciliation_1\":\"").append(
                    nullCheck(localReportDetailHdr.getConNoneCasesCompletedHearing())).append(NEW_LINE);
            sb.append("\"No_Conciliation_2\":\"").append(
                    nullCheck(localReportDetailHdr.getConNoneSessionDays())).append(NEW_LINE);
            sb.append("\"No_Conciliation_3\":\"").append(
                    nullCheck(localReportDetailHdr.getConNoneCompletedPerSession())).append(NEW_LINE);

            sb.append("\"Fast_Track_1\":\"").append(
                    nullCheck(localReportDetailHdr.getConFastCasesCompletedHearing())).append(NEW_LINE);
            sb.append("\"Fast_Track_2\":\"").append(
                    nullCheck(localReportDetailHdr.getConFastSessionDays())).append(NEW_LINE);
            sb.append("\"Fast_Track_3\":\"").append(
                    nullCheck(localReportDetailHdr.getConFastCompletedPerSession())).append(NEW_LINE);

            sb.append("\"Standard_Track_1\":\"").append(
                    nullCheck(localReportDetailHdr.getConStdCasesCompletedHearing())).append(NEW_LINE);
            sb.append("\"Standard_Track_2\":\"").append(
                    nullCheck(localReportDetailHdr.getConStdSessionDays())).append(NEW_LINE);
            sb.append("\"Standard_Track_3\":\"").append(
                    nullCheck(localReportDetailHdr.getConStdCompletedPerSession())).append(NEW_LINE);

            sb.append("\"Open_Track_1\":\"").append(
                    nullCheck(localReportDetailHdr.getConOpenCasesCompletedHearing())).append(NEW_LINE);
            sb.append("\"Open_Track_2\":\"").append(
                    nullCheck(localReportDetailHdr.getConOpenSessionDays())).append(NEW_LINE);
            sb.append("\"Open_Track_3\":\"").append(
                    nullCheck(localReportDetailHdr.getConOpenCompletedPerSession())).append(NEW_LINE);
        }

        if (listingData.getLocalReportsDetail() != null && !listingData.getLocalReportsDetail().isEmpty()) {
            List<AdhocReportTypeItem> adhocReportTypeItems = listingData.getLocalReportsDetail();
            sb.append(REPORT_LIST);
            for (var i = 0; i < adhocReportTypeItems.size(); i++) {
                sb.append(getAdhocReportCompletedTypeRow(adhocReportTypeItems.get(i).getValue()));
                if (i != adhocReportTypeItems.size() - 1) {
                    sb.append(",\n");
                }
            }
            sb.append("],\n");
        }
        return sb;
    }

    private static StringBuilder getAdhocReportCompletedTypeRow(AdhocReportType adhocReportType) {
        var sb = new StringBuilder();
        sb.append(CASE_REFERENCE).append(
                nullCheck(adhocReportType.getCaseReference())).append(NEW_LINE);
        sb.append("\"Position\":\"").append(
                nullCheck(adhocReportType.getPosition())).append(NEW_LINE);
        sb.append("\"Conciliation_Track\":\"").append(
                nullCheck(adhocReportType.getConciliationTrack())).append(NEW_LINE);
        sb.append("\"Session_Days\":\"").append(
                nullCheck(adhocReportType.getSessionDays())).append(NEW_LINE);
        sb.append("\"Hearing_Number\":\"").append(
                nullCheck(adhocReportType.getHearingNumber())).append(NEW_LINE);
        sb.append("\"Hearing_Date\":\"").append(
                UtilHelper.formatLocalDate(adhocReportType.getHearingDate())).append(NEW_LINE);
        sb.append("\"Hearing_Type\":\"").append(
                nullCheck(adhocReportType.getHearingType())).append(NEW_LINE);
        sb.append("\"Hearing_Judge\":\"").append(
                nullCheck(adhocReportType.getHearingJudge())).append(NEW_LINE);
        sb.append("\"Hearing_Clerk\":\"").append(
                nullCheck(adhocReportType.getHearingClerk())).append("\"}");
        return sb;
    }

    private static StringBuilder getTimeToFirstHearingAdhocReportTypeRow(AdhocReportType adhocReportType) {
        var sb = new StringBuilder();
        sb.append("{\"Office\":\"").append(
                nullCheck(adhocReportType.getReportOffice())).append(NEW_LINE);
        sb.append("\"Case_Reference\":\"").append(
                nullCheck(adhocReportType.getCaseReference())).append(NEW_LINE);
        sb.append("\"Conciliation_Track\":\"").append(
                nullCheck(adhocReportType.getConciliationTrack())).append(NEW_LINE);
        sb.append("\"Receipt_Date\":\"").append(
                nullCheck(adhocReportType.getReceiptDate())).append(NEW_LINE);
        sb.append("\"Hearing_Date\":\"").append(
                nullCheck(adhocReportType.getHearingDate())).append(NEW_LINE);
        sb.append("\"Days\":\"").append(
                nullCheck(adhocReportType.getDelayedDaysForFirstHearing())).append("\"}");
        return sb;
    }

    private static StringBuilder getLiveCaseLoadReportSummaryHdr(ListingData listingData) {
        var sb = new StringBuilder();
        var singlesTotal = "0";
        var multiplesTotal = "0";
        var total = "0";
        var summaryHdr = listingData.getLocalReportsSummaryHdr();

        if (summaryHdr != null) {
            singlesTotal = nullCheck(summaryHdr.getSinglesTotal());
            multiplesTotal = nullCheck(summaryHdr.getMultiplesTotal());
            total = nullCheck(summaryHdr.getTotal());
        }

        sb.append("\"Multiples_Total\":\"").append(multiplesTotal).append(NEW_LINE);
        sb.append("\"Singles_Total\":\"").append(singlesTotal).append(NEW_LINE);
        sb.append("\"Total\":\"").append(total).append(NEW_LINE);

        return sb;
    }

    private static StringBuilder getLiveCaseLoadReport(ListingData listingData) {
        var sb = getLiveCaseLoadReportSummaryHdr(listingData);

        if (!CollectionUtils.isEmpty(listingData.getLocalReportsDetail())) {
            List<AdhocReportTypeItem> adhocReportTypeItems = listingData.getLocalReportsDetail();
            sb.append(REPORT_LIST);
            for (var i = 0; i < adhocReportTypeItems.size(); i++) {
                sb.append(getAdhocReportCommonTypeRow(adhocReportTypeItems.get(i).getValue()));
                if (i != adhocReportTypeItems.size() - 1) {
                    sb.append(",\n");
                }
            }
            sb.append("],\n");
        }
        return sb;
    }

}