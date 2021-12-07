package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.items.AdhocReportTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.items.ReportListingsTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;
import uk.gov.hmcts.ecm.common.model.listing.types.ClaimServedType;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportException;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.CasesAwaitingJudgmentReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments.HearingsToJudgmentsReportData;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASES_AWAITING_JUDGMENT_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASES_COMPLETED_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASE_SOURCE_LOCAL_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMS_ACCEPTED_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FILE_EXTENSION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARINGS_BY_HEARING_TYPE_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARINGS_TO_JUDGEMENTS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LIVE_CASELOAD_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_LINE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OUTPUT_FILE_NAME;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SERVING_CLAIMS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TIME_TO_FIRST_HEARING_REPORT;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;

@Slf4j
public class ReportDocHelper {
    private static final String REPORT_LIST = "\"Report_List\":[\n";
    private static final String DAY_1_LIST = "\"Day_1_List\":[\n";
    private static final String DAY_2_LIST = "\"Day_2_List\":[\n";
    private static final String DAY_3_LIST = "\"Day_3_List\":[\n";
    private static final String DAY_4_LIST = "\"Day_4_List\":[\n";
    private static final String DAY_5_LIST = "\"Day_5_List\":[\n";
    private static final String DAY_6_LIST = "\"Day_6_List\":[\n";
    private static final String CASE_REFERENCE = "{\"Case_Reference\":\"";
    private static final String REPORT_OFFICE = "\"Report_Office\":\"";

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
        } else if (HEARINGS_TO_JUDGEMENTS_REPORT.equals(listingData.getReportType())) {
            try {
                sb.append(ListingHelper.getListingDate(listingData));
                sb.append(getHearingsToJudgmentsReport(listingData));
            } catch (JsonProcessingException e) {
                throw new ReportException("Unable to create report data", e);
            }
        } else {
            sb.append(ListingHelper.getListingDate(listingData));

            if (listingData.getLocalReportsDetailHdr() != null) {
                sb.append(REPORT_OFFICE).append(
                        nullCheck(listingData.getLocalReportsDetailHdr().getReportOffice())).append(NEW_LINE);
            } else if (CollectionUtils.isNotEmpty(listingData.getLocalReportsSummary())) {
                sb.append(REPORT_OFFICE).append(
                        nullCheck(listingData.getLocalReportsSummary().get(0)
                                .getValue().getReportOffice())).append(NEW_LINE);
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
                case CASE_SOURCE_LOCAL_REPORT:
                    sb.append(getCaseSourceLocalReport(listingData));
                    break;
                case SERVING_CLAIMS_REPORT:
                    sb.append(getServedClaimsReport(listingData));
                    break;
                case HEARINGS_BY_HEARING_TYPE_REPORT:
                    sb.append(getHearingsByHearingTypeReport(listingData));
                    break;
                default:
                    throw new IllegalStateException("Report type - Unexpected value: " + listingData.getReportType());
            }
        }

        String userName = nullCheck(userDetails.getFirstName() + " " + userDetails.getLastName());
        sb.append("\"Report_Clerk\":\"").append(nullCheck(userName)).append(NEW_LINE);
        sb.append("\"Today_date\":\"").append(UtilHelper.formatCurrentDate(LocalDate.now())).append("\"\n");
        sb.append("}\n");
        sb.append("}\n");
        return sb;
    }

    private static StringBuilder getCasesAwaitingJudgmentReport(ListingData listingData)
            throws JsonProcessingException {
        if (!(listingData instanceof CasesAwaitingJudgmentReportData)) {
            throw new IllegalStateException(("ListingData is not instanceof CasesAwaitingJudgmentReportData"));
        }
        var reportData = (CasesAwaitingJudgmentReportData) listingData;
        var sb = new StringBuilder();
        sb.append(REPORT_OFFICE).append(reportData.getReportSummary().getOffice()).append(NEW_LINE);
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
            String singleOrMultiple = Boolean.TRUE.equals(localReportEntry.getKey()) ? "Multiples" : "Singles";
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

    private static StringBuilder getCaseSourceLocalReport(ListingData listingData) {
        var sb = new StringBuilder();
        if (CollectionUtils.isEmpty(listingData.getLocalReportsSummary())) {
            return sb;
        }
        var localReportSummary = listingData.getLocalReportsSummary().get(0).getValue();
        if (localReportSummary != null) {

            sb.append("\"Manually_Created\":\"").append(
                    nullCheck(localReportSummary.getManuallyCreatedTotalCases())).append(NEW_LINE);
            sb.append("\"Migration_Cases\":\"").append(
                    nullCheck(localReportSummary.getMigratedTotalCases())).append(NEW_LINE);
            sb.append("\"ET1_Online_Cases\":\"").append(
                    nullCheck(localReportSummary.getEt1OnlineTotalCases())).append(NEW_LINE);
            sb.append("\"ECC_Cases\":\"").append(
                    nullCheck(localReportSummary.getEccTotalCases())).append(NEW_LINE);
            sb.append("\"Manually_Created_Percent\":\"").append(
                    nullCheck(localReportSummary.getManuallyCreatedTotalCasesPercent())).append(NEW_LINE);
            sb.append("\"Migration_Cases_Percent\":\"").append(
                    nullCheck(localReportSummary.getMigratedTotalCasesPercent())).append(NEW_LINE);
            sb.append("\"ET1_Online_Cases_Percent\":\"").append(
                    nullCheck(localReportSummary.getEt1OnlineTotalCasesPercent())).append(NEW_LINE);
            sb.append("\"ECC_Cases_Percent\":\"").append(
                    nullCheck(localReportSummary.getEccTotalCasesPercent())).append(NEW_LINE);
        }
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

        if (CollectionUtils.isNotEmpty(listingData.getLocalReportsDetail())) {
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

        if (CollectionUtils.isNotEmpty(listingData.getLocalReportsDetail())) {
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

    private static StringBuilder getServedClaimsReport(ListingData listingData) {
        var reportContent = getServedClaimsReportSummary(listingData);
        var claimsServedDayListUpperBoundary = 5;

        if (CollectionUtils.isNotEmpty(listingData.getLocalReportsDetail())) {
            var listBlockOpeners = List.of(DAY_1_LIST, DAY_2_LIST,
                    DAY_3_LIST, DAY_4_LIST,
                    DAY_5_LIST, DAY_6_LIST);
            for (var dayIndex = 0; dayIndex <= claimsServedDayListUpperBoundary; dayIndex++) {
                addEntriesByServingDay(dayIndex, listBlockOpeners.get(dayIndex),
                        reportContent, listingData);
            }
        }

        return reportContent;
    }

    private static void addEntriesByServingDay(int dayNumber, String listBlockOpener,
                                               StringBuilder reportContent, ListingData listingData) {

        var itemsList = listingData.getLocalReportsDetail().get(0);
        var claimServedTypeItems = itemsList.getValue().getClaimServedItems()
                .stream()
                .filter(item -> Integer.parseInt(item.getValue().getReportedNumberOfDays()) == dayNumber)
                .collect(Collectors.toList());
        int claimServedTypeItemsCount = claimServedTypeItems.size();
        var claimServedTypeItemsListSize = String.valueOf(claimServedTypeItems.size());

        reportContent.append(listBlockOpener);

        if (claimServedTypeItemsCount == 0) {
            reportContent.append(CASE_REFERENCE).append(claimServedTypeItemsListSize).append(NEW_LINE);

            if (dayNumber >= 5) {
                reportContent.append("\"Actual_Number_Of_Days\":\"")
                        .append(claimServedTypeItemsListSize).append(NEW_LINE);
            }
            reportContent.append("\"Date_Of_Receipt\":\"").append(claimServedTypeItemsListSize).append(NEW_LINE);
            reportContent.append("\"Date_Of_Service\":\"").append(claimServedTypeItemsListSize);
            reportContent.append("\"}");
            reportContent.append(",\n");
        } else {
            for (var i = 0; i < claimServedTypeItemsCount; i++) {
                reportContent.append(getServedClaimsReportRow(claimServedTypeItems.get(i).getValue(), dayNumber));
                if (i != claimServedTypeItemsCount - 1) {
                    reportContent.append(",\n");
                }
            }
        }

        reportContent.append("],\n");
        var currentDayTotal = "\"day_" + (dayNumber + 1) + "_total_count\":\"";
        reportContent.append(currentDayTotal)
                .append(claimServedTypeItemsListSize).append(NEW_LINE);
    }

    private static StringBuilder getServedClaimsReportRow(ClaimServedType claimServedTypeItem, int dayNumber) {
        var reportRowContent = new StringBuilder();
        var claimsServedDayListUpperBoundary = 5;

        reportRowContent.append(CASE_REFERENCE)
                .append(nullCheck(claimServedTypeItem.getClaimServedCaseNumber())).append(NEW_LINE);

        if (dayNumber >= claimsServedDayListUpperBoundary) {
            reportRowContent.append("\"Actual_Number_Of_Days\":\"")
                    .append(nullCheck(claimServedTypeItem.getActualNumberOfDays())).append(NEW_LINE);
        }

        reportRowContent.append("\"Date_Of_Receipt\":\"")
                .append(nullCheck(claimServedTypeItem.getCaseReceiptDate())).append(NEW_LINE);
        reportRowContent.append("\"Date_Of_Service\":\"")
                .append(nullCheck(claimServedTypeItem.getClaimServedDate()));
        reportRowContent.append("\"}");

        return reportRowContent;
    }

    private static StringBuilder getServedClaimsReportSummary(ListingData listingData) {
        var reportSummaryContent = new StringBuilder();

        if (CollectionUtils.isNotEmpty(listingData.getLocalReportsDetail())) {
            var adhocReportTypeItem = listingData.getLocalReportsDetail().get(0);
            var adhocReportType = adhocReportTypeItem.getValue();

            reportSummaryContent.append("\"Day_1_Tot\":\"")
                    .append(adhocReportType.getClaimServedDay1Total()).append(NEW_LINE);
            reportSummaryContent.append("\"Day_1_Pct\":\"")
                    .append(adhocReportType.getClaimServedDay1Percent()).append(NEW_LINE);

            reportSummaryContent.append("\"Day_2_Tot\":\"")
                    .append(adhocReportType.getClaimServedDay2Total()).append(NEW_LINE);
            reportSummaryContent.append("\"Day_2_Pct\":\"")
                    .append(adhocReportType.getClaimServedDay2Percent()).append(NEW_LINE);

            reportSummaryContent.append("\"Day_3_Tot\":\"")
                    .append(adhocReportType.getClaimServedDay3Total()).append(NEW_LINE);
            reportSummaryContent.append("\"Day_3_Pct\":\"")
                    .append(adhocReportType.getClaimServedDay3Percent()).append(NEW_LINE);

            reportSummaryContent.append("\"Day_4_Tot\":\"")
                    .append(adhocReportType.getClaimServedDay4Total()).append(NEW_LINE);
            reportSummaryContent.append("\"Day_4_Pct\":\"")
                    .append(adhocReportType.getClaimServedDay4Percent()).append(NEW_LINE);

            reportSummaryContent.append("\"Day_5_Tot\":\"")
                    .append(adhocReportType.getClaimServedDay5Total()).append(NEW_LINE);
            reportSummaryContent.append("\"Day_5_Pct\":\"")
                    .append(adhocReportType.getClaimServedDay5Percent()).append(NEW_LINE);

            reportSummaryContent.append("\"Day_6_Plus_Tot\":\"")
                    .append(adhocReportType.getClaimServed6PlusDaysTotal()).append(NEW_LINE);
            reportSummaryContent.append("\"Day_6_Plus_Pct\":\"")
                    .append(adhocReportType.getClaimServed6PlusDaysPercent()).append(NEW_LINE);

            reportSummaryContent.append("\"Total_Claims\":\"")
                    .append(adhocReportType.getClaimServedTotal()).append(NEW_LINE);
        }

        return reportSummaryContent;
    }

    private static StringBuilder getHearingsToJudgmentsReport(ListingData listingData)
            throws JsonProcessingException {
        if (!(listingData instanceof HearingsToJudgmentsReportData)) {
            throw new IllegalStateException(("ListingData is not instanceof HearingsToJudgmentsReportData"));
        }
        var reportData = (HearingsToJudgmentsReportData) listingData;

        var sb = new StringBuilder();
        sb.append(REPORT_OFFICE).append(reportData.getReportSummary().getOffice()).append(NEW_LINE);
        sb.append("\"Total_Cases\":\"").append(
                nullCheck(reportData.getReportSummary().getTotalCases())).append(NEW_LINE);
        sb.append("\"Total_Within_4Weeks\":\"").append(
                nullCheck(reportData.getReportSummary().getTotal4Wk())).append(NEW_LINE);
        sb.append("\"Total_Percent_Within_4Weeks\":\"").append(
                nullCheck(reportData.getReportSummary().getTotal4WkPercent())).append(NEW_LINE);
        sb.append("\"Total_Not_Within_4Weeks\":\"").append(
                nullCheck(reportData.getReportSummary().getTotalX4Wk())).append(NEW_LINE);
        sb.append("\"Total_Percent_Not_Within_4Weeks\":\"").append(
                nullCheck(reportData.getReportSummary().getTotalX4WkPercent())).append(NEW_LINE);
        addJsonCollection("reportDetails", reportData.getReportDetails().iterator(), sb);
        return sb;
    }

    private static StringBuilder getHearingsByHearingTypeReport(ListingData listingData) {
        var sb = new StringBuilder();
        AdhocReportType localReportSummaryHdr = listingData.getLocalReportsSummaryHdr();
        AdhocReportType localReportSummaryHdr2 = listingData.getLocalReportsSummaryHdr2();
        if (localReportSummaryHdr != null) {
            sb.append(getHearingCountsHdr(localReportSummaryHdr));
        }
        List<AdhocReportTypeItem> localReportSummary = listingData.getLocalReportsSummary();

        if (CollectionUtils.isNotEmpty(localReportSummary)) {
            sb.append(REPORT_LIST);
            for (var i = 0; i < localReportSummary.size(); i++) {
                sb.append(getHearingsByHearingTypeLocalReportSummaryRow(
                        localReportSummary.get(i).getValue(), "localSummary"));
                if (i != localReportSummary.size() - 1) {
                    sb.append(",\n");
                }
            }
            sb.append("],\n");
        }
        List<AdhocReportTypeItem> localReportSummary2 = listingData.getLocalReportsSummary2();
        sb.append(getHearingByHearingTypeLocalReportSummaryHdr2(localReportSummaryHdr2));
        if (CollectionUtils.isNotEmpty(localReportSummary2)) {
            sb.append("\"reportSummary2\":[\n");
            for (var i = 0; i < localReportSummary2.size(); i++) {
                sb.append(getHearingsByHearingTypeLocalReportSummaryRow(
                        localReportSummary2.get(i).getValue(), "localSummary2"));
                if (i != localReportSummary2.size() - 1) {
                    sb.append(",\n");
                }
            }
            sb.append("],\n");
        }
        List<AdhocReportTypeItem> localReportSummaryDetail = listingData.getLocalReportsDetail();
        if (CollectionUtils.isNotEmpty(localReportSummaryDetail)) {
            sb.append("\"reportDetails\":[\n");
            for (var i = 0; i < localReportSummaryDetail.size(); i++) {
                sb.append(getHearingsByHearingTypeLocalReportDetailRow(
                        localReportSummaryDetail.get(i).getValue()));
                if (i != localReportSummaryDetail.size() - 1) {
                    sb.append(",\n");
                }
            }
            sb.append("],\n");
        }

        return sb;
    }

    private static  StringBuilder getHearingsByHearingTypeLocalReportDetailRow(
            AdhocReportType adhocReportType) {
        var sb = new StringBuilder();
        sb.append("{\"date_detail\":\"").append(nullCheck(adhocReportType.getDate())).append(NEW_LINE);
        sb.append("\"multiple_sub_detail\":\"").append(nullCheck(adhocReportType.getMultSub())).append(NEW_LINE);
        sb.append("\"case_no_detail\":\"").append(nullCheck(adhocReportType.getCaseReference())).append(NEW_LINE);
        sb.append("\"lead_detail\":\"").append(nullCheck(adhocReportType.getLeadCase())).append(NEW_LINE);
        sb.append("\"hear_no_detail\":\"").append(nullCheck(adhocReportType.getHearingNumber())).append(NEW_LINE);
        sb.append("\"type_detail\":\"").append(nullCheck(adhocReportType.getHearingType())).append(NEW_LINE);
        sb.append("\"tel_detail\":\"").append(nullCheck(adhocReportType.getHearingTelConf())).append(NEW_LINE);
        sb.append("\"jm_detail\":\"").append(nullCheck(adhocReportType.getJudicialMediation())).append(NEW_LINE);
        sb.append("\"dur_detail\":\"").append(nullCheck(adhocReportType.getHearingDuration())).append(NEW_LINE);
        sb.append("\"clerk_detail\":\"").append(nullCheck(adhocReportType.getHearingClerk())).append("\"}");
        return sb;
    }

    private static StringBuilder getHearingByHearingTypeLocalReportSummaryHdr2(AdhocReportType adhocReportType) {

        var sb = new StringBuilder();
        List<ReportListingsTypeItem> listingHistory = adhocReportType.getListingHistory();
        if (CollectionUtils.isEmpty(listingHistory)) {
            return sb;
        }
        sb.append("\"reportSummaryHdr2\":[\n");
        for (var item : listingHistory) {
            String s = item.getValue().getHearingNumber();
            List<String> fields = List.of(s.split("[|]").clone());
            if (CollectionUtils.isEmpty(fields) || fields.size() < 8) {
                continue;
            }
            var tempAdhocReportType = new AdhocReportType();
            tempAdhocReportType.setHearing(fields.get(0));
            tempAdhocReportType.setHearingPrelim(fields.get(1));
            tempAdhocReportType.setHearingCM(fields.get(2));
            tempAdhocReportType.setRemedy(fields.get(3));
            tempAdhocReportType.setReconsider(fields.get(4));
            tempAdhocReportType.setCosts(fields.get(5));
            tempAdhocReportType.setTotal(fields.get(6));
            tempAdhocReportType.setSubSplit(fields.get(7));
            sb.append(getHearingsByHearingTypeLocalReportSummaryRow(tempAdhocReportType, "localSummaryHdr2"));
            if (item != listingHistory.get(listingHistory.size() - 1)) {
                sb.append(",\n");
            }
        }
        sb.append("],\n");
        return sb;
    }

    private static StringBuilder getHearingsByHearingTypeLocalReportSummaryRow(
            AdhocReportType adhocReportType, String reportType) {
        var sb = new StringBuilder();
        if ("localSummary".equals(reportType)) {
            sb.append("{\"date_summary1_list\":\"").append(
                    nullCheck(adhocReportType.getDate())).append(NEW_LINE);
            sb.append(getHearingCountsSummary1(adhocReportType));
        } else if ("localSummaryHdr2".equals(reportType)) {
            sb.append("{\"subSplit_summary2\":\"").append(
                    nullCheck(adhocReportType.getSubSplit())).append(NEW_LINE);
            sb.append(getHearingCountsHdr2(adhocReportType));
        } else if ("localSummary2".equals(reportType)) {
            sb.append("{\"date_summary2_list\":\"").append(
                    nullCheck(adhocReportType.getDate())).append(NEW_LINE);
            sb.append("\"subSplit_summary2_list\":\"").append(
                    nullCheck(adhocReportType.getSubSplit())).append(NEW_LINE);
            sb.append(getHearingCountsSummary2(adhocReportType));
        }
        return sb;
    }

    private static StringBuilder getHearingCountsHdr(AdhocReportType adhocReportType) {
        var sb = new StringBuilder();
        sb.append("\"cm_summary1\":\"").append(
                nullCheck(adhocReportType.getHearingCM())).append(NEW_LINE);
        sb.append("\"costs_summary1\":\"").append(
                nullCheck(adhocReportType.getCosts())).append(NEW_LINE);
        sb.append("\"hearing_summary1\":\"").append(
                nullCheck(adhocReportType.getHearing())).append(NEW_LINE);
        sb.append("\"hearingPrelim_summary1\":\"").append(
                nullCheck(adhocReportType.getHearingPrelim())).append(NEW_LINE);
        sb.append("\"reconsider_summary1\":\"").append(
                nullCheck(adhocReportType.getReconsider())).append(NEW_LINE);
        sb.append("\"remedy_summary1\":\"").append(
                nullCheck(adhocReportType.getRemedy())).append(NEW_LINE);
        sb.append("\"total_summary1\":\"").append(
                nullCheck(adhocReportType.getTotal())).append(NEW_LINE);
        return sb;
    }

    private static StringBuilder getHearingCountsHdr2(AdhocReportType adhocReportType) {
        var sb = new StringBuilder();
        sb.append("\"cm_summary2\":\"").append(
                nullCheck(adhocReportType.getHearingCM())).append(NEW_LINE);
        sb.append("\"costs_summary2\":\"").append(
                nullCheck(adhocReportType.getCosts())).append(NEW_LINE);
        sb.append("\"hearing_summary2\":\"").append(
                nullCheck(adhocReportType.getHearing())).append(NEW_LINE);
        sb.append("\"hearingPrelim_summary2\":\"").append(
                nullCheck(adhocReportType.getHearingPrelim())).append(NEW_LINE);
        sb.append("\"reconsider_summary2\":\"").append(
                nullCheck(adhocReportType.getReconsider())).append(NEW_LINE);
        sb.append("\"remedy_summary2\":\"").append(
                nullCheck(adhocReportType.getRemedy())).append(NEW_LINE);
        sb.append("\"total_summary2\":\"").append(
                nullCheck(adhocReportType.getTotal())).append("\"}");
        return sb;
    }

    private static StringBuilder getHearingCountsSummary1(AdhocReportType adhocReportType) {
        var sb = new StringBuilder();
        sb.append("\"cm_summary1_list\":\"").append(
                nullCheck(adhocReportType.getHearingCM())).append(NEW_LINE);
        sb.append("\"costs_summary1_list\":\"").append(
                nullCheck(adhocReportType.getCosts())).append(NEW_LINE);
        sb.append("\"hearing_summary1_list\":\"").append(
                nullCheck(adhocReportType.getHearing())).append(NEW_LINE);
        sb.append("\"hearingPrelim_summary1_list\":\"").append(
                nullCheck(adhocReportType.getHearingPrelim())).append(NEW_LINE);
        sb.append("\"reconsider_summary1_list\":\"").append(
                nullCheck(adhocReportType.getReconsider())).append(NEW_LINE);
        sb.append("\"remedy_summary1_list\":\"").append(
                nullCheck(adhocReportType.getRemedy())).append(NEW_LINE);
        sb.append("\"total_summary1_list\":\"").append(
                nullCheck(adhocReportType.getTotal())).append("\"}");
        return sb;
    }

    private static StringBuilder getHearingCountsSummary2(AdhocReportType adhocReportType) {
        var sb = new StringBuilder();
        sb.append("\"cm_summary2_list\":\"").append(
                nullCheck(adhocReportType.getHearingCM())).append(NEW_LINE);
        sb.append("\"costs_summary2_list\":\"").append(
                nullCheck(adhocReportType.getCosts())).append(NEW_LINE);
        sb.append("\"hearing_summary2_list\":\"").append(
                nullCheck(adhocReportType.getHearing())).append(NEW_LINE);
        sb.append("\"hearingPrelim_summary2_list\":\"").append(
                nullCheck(adhocReportType.getHearingPrelim())).append(NEW_LINE);
        sb.append("\"reconsider_summary2_list\":\"").append(
                nullCheck(adhocReportType.getReconsider())).append(NEW_LINE);
        sb.append("\"remedy_summary2_list\":\"").append(
                nullCheck(adhocReportType.getRemedy())).append(NEW_LINE);
        sb.append("\"total_summary2_list\":\"").append(
                nullCheck(adhocReportType.getTotal())).append("\"}");
        return sb;
    }
}