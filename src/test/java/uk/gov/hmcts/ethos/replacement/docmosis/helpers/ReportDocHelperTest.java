package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.helper.Constants;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.CasesAwaitingJudgmentReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.PositionTypeSummary;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.ReportDetail;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.ReportSummary;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments.HearingsToJudgmentsReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments.HearingsToJudgmentsReportDetail;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments.HearingsToJudgmentsReportSummary;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class ReportDocHelperTest {

    private ListingDetails reportDetails;
    private ListingDetails reportDetails2;
    private ListingDetails reportDetails3;
    private ListingDetails reportDetails4;
    private ListingDetails reportDetails5;
    private ListingDetails reportDetails6;
    private ListingDetails reportDetailsClaimsServed;
    private UserDetails userDetails;

    @Before
    public void setUp() throws Exception {
        reportDetails = generateReportDetails("reportDetailsTest1.json");
        reportDetails2 = generateReportDetails("reportDetailsTest2.json");
        reportDetails3 = generateReportDetails("reportDetailsTest3.json");
        reportDetails4 = generateReportDetails("reportDetailsTest4.json");
        reportDetails5 = generateReportDetails("reportDetailsTest5.json");
        reportDetails6 = generateReportDetails("reportDetailsTest6.json");
        reportDetailsClaimsServed = generateReportDetails("reportDetailsTestClaimsServed.json");
        userDetails = HelperTest.getUserDetails();
    }

    private ListingDetails generateReportDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, ListingDetails.class);
    }

    @Test
    public void buildClaimsAcceptedReport() {
        String expected = "{\n"
                + "\"accessKey\":\"\",\n"
                + "\"templateName\":\"EM-TRB-SCO-ENG-00219.docx\",\n"
                + "\"outputName\":\"document.docx\",\n"
                + "\"data\":{\n"
                + "\"Listed_date_from\":\"1 December 2021\",\n"
                + "\"Listed_date_to\":\"3 December 2021\",\n"
                + "\"Report_Office\":\"Manchester\",\n"
                + "\"Multiple_Claims_Accepted\":\"1\",\n"
                + "\"Singles_Claims_Accepted\":\"2\",\n"
                + "\"Total_Claims_Accepted\":\"0\",\n"
                + "\"Local_Report_By_Type\":[\n"
                + "{\"Case_Type\":\"Singles\",\n"
                + "\"Claims_Number\":\"1\",\n"
                + "\"Report_List\":[\n"
                + "{\"Case_Reference\":\"2122323/2020\",\n"
                + "\"Date_Of_Acceptance\":\"\",\n"
                + "\"Multiple_Ref\":\"\",\n"
                + "\"Lead_Case\":\"\",\n"
                + "\"Position\":\"Position5\",\n"
                + "\"Date_To_Position\":\"\",\n"
                + "\"File_Location\":\"\",\n"
                + "\"Clerk\":\"Anne Fox2\"}]\n"
                + "},\n"
                + "{\"Case_Type\":\"Multiples\",\n"
                + "\"Claims_Number\":\"1\",\n"
                + "\"Report_List\":[\n"
                + "{\"Case_Reference\":\"2122324/2020\",\n"
                + "\"Date_Of_Acceptance\":\"\",\n"
                + "\"Multiple_Ref\":\"212323\",\n"
                + "\"Lead_Case\":\"Yes\",\n"
                + "\"Position\":\"Position2\",\n"
                + "\"Date_To_Position\":\"\",\n"
                + "\"File_Location\":\"\",\n"
                + "\"Clerk\":\"Anne Fox\"}]\n"
                + "}],\n"
                + "\"Report_Clerk\":\"Mike Jordan\",\n"
                + "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n"
                + "}\n"
                + "}\n";
        assertEquals(expected, ReportDocHelper.buildReportDocumentContent(reportDetails.getCaseData(), "",
                "EM-TRB-SCO-ENG-00219", userDetails).toString());
    }

    @Test
    public void buildLiveCaseLoadReport() {
        String expected = "{\n"
                + "\"accessKey\":\"\",\n"
                + "\"templateName\":\"EM-TRB-SCO-ENG-00220.docx\",\n"
                + "\"outputName\":\"document.docx\",\n"
                + "\"data\":{\n"
                + "\"Listed_date_from\":\"1 December 2021\",\n"
                + "\"Listed_date_to\":\"3 December 2021\",\n"
                + "\"Report_Office\":\"Manchester\",\n"
                + "\"Multiples_Total\":\"0\",\n"
                + "\"Singles_Total\":\"2\",\n"
                + "\"Total\":\"2\",\n"
                + "\"Report_List\":[\n"
                + "{\"Case_Reference\":\"2122324/2020\",\n"
                + "\"Date_Of_Acceptance\":\"\",\n"
                + "\"Multiple_Ref\":\"212323\",\n"
                + "\"Lead_Case\":\"Yes\",\n"
                + "\"Position\":\"Position2\",\n"
                + "\"Date_To_Position\":\"\",\n"
                + "\"File_Location\":\"\",\n"
                + "\"Clerk\":\"Anne Fox\"},\n"
                + "{\"Case_Reference\":\"2122323/2020\",\n"
                + "\"Date_Of_Acceptance\":\"\",\n"
                + "\"Multiple_Ref\":\"\",\n"
                + "\"Lead_Case\":\"\",\n"
                + "\"Position\":\"Position5\",\n"
                + "\"Date_To_Position\":\"\",\n"
                + "\"File_Location\":\"\",\n"
                + "\"Clerk\":\"Anne Fox2\"}],\n"
                + "\"Report_Clerk\":\"Mike Jordan\",\n"
                + "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n"
                + "}\n"
                + "}\n";
        assertEquals(expected, ReportDocHelper.buildReportDocumentContent(reportDetails2.getCaseData(), "",
                "EM-TRB-SCO-ENG-00220", userDetails).toString());
    }

    @Test
    public void buildCasesCompletedReport() {
        var expected = "{\n"
                + "\"accessKey\":\"\",\n"
                + "\"templateName\":\"EM-TRB-SCO-ENG-00221.docx\",\n"
                + "\"outputName\":\"document.docx\",\n"
                + "\"data\":{\n"
                + "\"Listed_date_from\":\"1 December 2021\",\n"
                + "\"Listed_date_to\":\"3 December 2021\",\n"
                + "\"Report_Office\":\"Manchester\",\n"
                + "\"Cases_Completed_Hearing\":\"2\",\n"
                + "\"Session_Days_Taken\":\"6\",\n"
                + "\"Completed_Per_Session_Day\":\"1\",\n"
                + "\"No_Conciliation_1\":\"2\",\n"
                + "\"No_Conciliation_2\":\"6\",\n"
                + "\"No_Conciliation_3\":\"1\",\n"
                + "\"Fast_Track_1\":\"8\",\n"
                + "\"Fast_Track_2\":\"3\",\n"
                + "\"Fast_Track_3\":\"\",\n"
                + "\"Standard_Track_1\":\"2\",\n"
                + "\"Standard_Track_2\":\"\",\n"
                + "\"Standard_Track_3\":\"\",\n"
                + "\"Open_Track_1\":\"\",\n"
                + "\"Open_Track_2\":\"\",\n"
                + "\"Open_Track_3\":\"\",\n"
                + "\"Report_List\":[\n"
                + "{\"Case_Reference\":\"2122324/2020\",\n"
                + "\"Position\":\"Position2\",\n"
                + "\"Conciliation_Track\":\"No Conciliation\",\n"
                + "\"Session_Days\":\"\",\n"
                + "\"Hearing_Number\":\"3\",\n"
                + "\"Hearing_Date\":\"12 October 2020\",\n"
                + "\"Hearing_Type\":\"Hearing Type\",\n"
                + "\"Hearing_Judge\":\"Judge Name2\",\n"
                + "\"Hearing_Clerk\":\"Hearing Clerk\"},\n"
                + "{\"Case_Reference\":\"2122323/2020\",\n"
                + "\"Position\":\"Position5\",\n"
                + "\"Conciliation_Track\":\"No Conciliation\",\n"
                + "\"Session_Days\":\"\",\n"
                + "\"Hearing_Number\":\"5\",\n"
                + "\"Hearing_Date\":\"15 October 2020\",\n"
                + "\"Hearing_Type\":\"Hearing Type2\",\n"
                + "\"Hearing_Judge\":\"Judge Name\",\n"
                + "\"Hearing_Clerk\":\"Hearing Clerk\"}],\n"
                + "\"Report_Clerk\":\"Mike Jordan\",\n"
                + "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n"
                + "}\n"
                + "}\n";
        assertEquals(expected, ReportDocHelper.buildReportDocumentContent(reportDetails3.getCaseData(), "",
                "EM-TRB-SCO-ENG-00221", userDetails).toString());
    }

    @Test
    public void buildCasesAwaitingJudgmentReport() throws URISyntaxException, IOException {
        var expectedJson = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("casesAwaitingJudgmentExpected.json")).toURI())));
        var today = UtilHelper.formatCurrentDate(LocalDate.now());
        expectedJson = expectedJson.replace("replace-with-current-date", today);
        var reportData = getCasesAwaitingJudgementReportData();
        var actualJson = ReportDocHelper.buildReportDocumentContent(reportData, "",
                "EM-TRB-SCO-ENG-00749", userDetails).toString();
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void buildTimeToFirstHearingReport() {
        String expected = "{\n"
                + "\"accessKey\":\"\",\n"
                + "\"templateName\":\"EM-TRB-SCO-ENG-00751.docx\",\n"
                + "\"outputName\":\"document.docx\",\n"
                + "\"data\":{\n"
                + "\"Listed_date_from\":\"1 December 2021\",\n"
                + "\"Listed_date_to\":\"3 December 2021\",\n"
                + "\"Report_Office\":\"Manchester\",\n"
                + "\"Total_Cases\":\"\",\n"
                + "\"Total_Within_26Weeks\":\"\",\n"
                + "\"Total_Percent_Within_26Weeks\":\"\",\n"
                + "\"Total_Not_Within_26Weeks\":\"\",\n"
                + "\"Total_Percent_Not_Within_26Weeks\":\"\",\n"
                + "\"ConNone_Total\":\"\",\n"
                + "\"ConNone_Total_26_Week\":\"\",\n"
                + "\"ConNone_Percent_26_Week\":\"\",\n"
                + "\"ConNone_Total_Not_26_Week\":\"\",\n"
                + "\"ConNone_Percent_Not_26_Week\":\"\",\n"
                + "\"ConFast_Total\":\"\",\n"
                + "\"ConFast_Total_26_Week\":\"\",\n"
                + "\"ConFast_Percent_26_Week\":\"\",\n"
                + "\"ConFast_Total_Not_26_Week\":\"\",\n"
                + "\"ConFast_Percent_Not_26_Week\":\"\",\n"
                + "\"ConStd_Total\":\"\",\n"
                + "\"ConStd_Total_26_Week\":\"\",\n"
                + "\"ConStd_Percent_26_Week\":\"\",\n"
                + "\"ConStd_Total_Not_26_Week\":\"\",\n"
                + "\"ConStd_Percent_Not_26_Week\":\"\",\n"
                + "\"ConOpen_Total\":\"\",\n"
                + "\"ConOpen_Total_26_Week\":\"\",\n"
                + "\"ConOpen_Percent_26_Week\":\"\",\n"
                + "\"ConOpen_Total_Not_26_Week\":\"\",\n"
                + "\"ConOpen_Percent_Not_26_Week\":\"\",\n"
                + "\"Report_List\":[\n"
                + "{\"Office\":\"Manchester\",\n"
                + "\"Case_Reference\":\"2122324/2020\",\n"
                + "\"Conciliation_Track\":\"\",\n"
                + "\"Receipt_Date\":\"\",\n"
                + "\"Hearing_Date\":\"2020-10-20T10:00:00.000\",\n"
                + "\"Days\":\"\""
                + "},\n"
                + "{\"Office\":\"Manchester\",\n"
                + "\"Case_Reference\":\"2122323/2020\",\n"
                + "\"Conciliation_Track\":\"\",\n"
                + "\"Receipt_Date\":\"\",\n"
                + "\"Hearing_Date\":\"2020-10-20T10:00:00.000\",\n"
                + "\"Days\":\"\"}],\n"
                + "\"Report_Clerk\":\"Mike Jordan\",\n"
                + "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n"
                + "}\n"
                + "}\n";
        assertEquals(expected, ReportDocHelper.buildReportDocumentContent(reportDetails4.getCaseData(), "",
                "EM-TRB-SCO-ENG-00751", userDetails).toString());
    }

    @Test
    public void buildCaseSourceLocalReport() {
        String expected = "{\n"
                + "\"accessKey\":\"\",\n"
                + "\"templateName\":\"EM-TRB-SCO-ENG-00783.docx\",\n"
                + "\"outputName\":\"document.docx\",\n"
                + "\"data\":{\n"
                + "\"Listed_date_from\":\"1 December 2021\",\n"
                + "\"Listed_date_to\":\"3 December 2021\",\n"
                + "\"Report_Office\":\"Manchester\",\n"
                + "\"Manually_Created\":\"\",\n"
                + "\"Migration_Cases\":\"\",\n"
                + "\"ET1_Online_Cases\":\"\",\n"
                + "\"ECC_Cases\":\"\",\n"
                + "\"Manually_Created_Percent\":\"\",\n"
                + "\"Migration_Cases_Percent\":\"\",\n"
                + "\"ET1_Online_Cases_Percent\":\"\",\n"
                + "\"ECC_Cases_Percent\":\"\",\n"
                + "\"Report_Clerk\":\"Mike Jordan\",\n"
                + "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n"
                + "}\n"
                + "}\n";
        assertEquals(expected, ReportDocHelper.buildReportDocumentContent(reportDetails5.getCaseData(), "",
                "EM-TRB-SCO-ENG-00783", userDetails).toString());
    }

    @Test
    public void buildServingClaimsReport() {
        String expected = "{\n"
                + "\"accessKey\":\"\",\n"
                + "\"templateName\":\"EM-TRB-SCO-ENG-00780.docx\",\n"
                + "\"outputName\":\"document.docx\",\n"
                + "\"data\":{\n"
                + "\"Listed_date\":\" Between 2 October 2021 and 28 October 2021\",\n"
                + "\"Day_1_Tot\":\"0\",\n"
                + "\"Day_1_Pct\":\"0\",\n"
                + "\"Day_2_Tot\":\"1\",\n"
                + "\"Day_2_Pct\":\"100\",\n"
                + "\"Day_3_Tot\":\"0\",\n"
                + "\"Day_3_Pct\":\"0\",\n"
                + "\"Day_4_Tot\":\"0\",\n"
                + "\"Day_4_Pct\":\"0\",\n"
                + "\"Day_5_Tot\":\"0\",\n"
                + "\"Day_5_Pct\":\"0\",\n"
                + "\"Day_6_Plus_Tot\":\"0\",\n"
                + "\"Day_6_Plus_Pct\":\"0\",\n"
                + "\"Total_Claims\":\"1\",\n"
                + "\"Day_1_List\":[\n"
                + "{\"Case_Reference\":\"0\",\n"
                + "\"Date_Of_Receipt\":\"0\",\n"
                + "\"Date_Of_Service\":\"0\"},\n"
                + "],\n"
                + "\"day_1_total_count\":\"0\",\n"
                + "\"Day_2_List\":[\n"
                + "{\"Case_Reference\":\"1800001/2021\",\n"
                + "\"Date_Of_Receipt\":\"2021-10-20\",\n"
                + "\"Date_Of_Service\":\"2021-10-21\"}],\n"
                + "\"day_2_total_count\":\"1\",\n"
                + "\"Day_3_List\":[\n"
                + "{\"Case_Reference\":\"0\",\n"
                + "\"Date_Of_Receipt\":\"0\",\n"
                + "\"Date_Of_Service\":\"0\"},\n"
                + "],\n"
                + "\"day_3_total_count\":\"0\",\n"
                + "\"Day_4_List\":[\n"
                + "{\"Case_Reference\":\"0\",\n"
                + "\"Date_Of_Receipt\":\"0\",\n"
                + "\"Date_Of_Service\":\"0\"},\n"
                + "],\n"
                + "\"day_4_total_count\":\"0\",\n"
                + "\"Day_5_List\":[\n"
                + "{\"Case_Reference\":\"0\",\n"
                + "\"Date_Of_Receipt\":\"0\",\n"
                + "\"Date_Of_Service\":\"0\"},\n"
                + "],\n"
                + "\"day_5_total_count\":\"0\",\n"
                + "\"Day_6_List\":[\n"
                + "{\"Case_Reference\":\"0\",\n"
                + "\"Actual_Number_Of_Days\":\"0\",\n"
                + "\"Date_Of_Receipt\":\"0\",\n"
                + "\"Date_Of_Service\":\"0\"},\n"
                + "],\n"
                + "\"day_6_total_count\":\"0\",\n"
                + "\"Report_Clerk\":\"Mike Jordan\",\n"
                + "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n"
                + "}\n"
                + "}\n";

        assertEquals(expected, ReportDocHelper.buildReportDocumentContent(reportDetailsClaimsServed.getCaseData(),
                "", "EM-TRB-SCO-ENG-00780", userDetails).toString());
    }

    @Test
    public void buildHearingsToJudgmentsReport() throws URISyntaxException, IOException {
        var expectedJson = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("hearingsToJudgmentsExpected.json")).toURI())));
        var today = UtilHelper.formatCurrentDate(LocalDate.now());
        expectedJson = expectedJson.replace("replace-with-current-date", today);
        var reportData = getHearingsToJudgmentsReportData();
        var actualJson = ReportDocHelper.buildReportDocumentContent(reportData, "",
                "EM-TRB-SCO-ENG-00786", userDetails).toString();
        assertEquals(expectedJson, actualJson);
    }

    private CasesAwaitingJudgmentReportData getCasesAwaitingJudgementReportData() {
        var reportSummary = new ReportSummary("Newcastle");
        reportSummary.getPositionTypes()
                .add(new PositionTypeSummary("Signed fair copy received", 1));
        reportSummary.getPositionTypes()
                .add(new PositionTypeSummary("Heard awaiting judgment being sent to the parties",
                        5));
        reportSummary.getPositionTypes()
                .add(new PositionTypeSummary("Draft with members", 10));

        var reportData = new CasesAwaitingJudgmentReportData(reportSummary);
        reportData.setReportType(Constants.CASES_AWAITING_JUDGMENT_REPORT);
        reportData.setDocumentName("TestDocument");

        var reportDetail = new ReportDetail();
        reportDetail.setWeeksSinceHearing(2);
        reportDetail.setDaysSinceHearing(16);
        reportDetail.setCaseNumber("250003/2021");
        reportDetail.setMultipleReference("250002/2021");
        reportDetail.setLastHeardHearingDate("12 Jul 2021");
        reportDetail.setHearingNumber("1");
        reportDetail.setHearingType("Preliminary Hearing");
        reportDetail.setJudge("Mrs White");
        reportDetail.setCurrentPosition("Manually Created");
        reportDetail.setDateToPosition("2 Jul 2021");
        reportDetail.setConciliationTrack("Open Track");
        reportData.getReportDetails().add(reportDetail);

        reportDetail = new ReportDetail();
        reportDetail.setWeeksSinceHearing(1);
        reportDetail.setDaysSinceHearing(8);
        reportDetail.setCaseNumber("250001/2021");
        reportDetail.setMultipleReference("0/0");
        reportDetail.setLastHeardHearingDate("20 Jul 2021");
        reportDetail.setHearingNumber("1");
        reportDetail.setHearingType("Judgment");
        reportDetail.setJudge("Mr Blue");
        reportDetail.setCurrentPosition("Manually Created");
        reportDetail.setDateToPosition("1 Jul 2021");
        reportDetail.setConciliationTrack("No Conciliation");
        reportData.getReportDetails().add(reportDetail);

        reportDetail = new ReportDetail();
        reportDetail.setWeeksSinceHearing(0);
        reportDetail.setDaysSinceHearing(1);
        reportDetail.setCaseNumber("250004/2021");
        reportDetail.setMultipleReference("0/0");
        reportDetail.setLastHeardHearingDate("27 Jul 2021");
        reportDetail.setHearingNumber("5");
        reportDetail.setHearingType("Judgment");
        reportDetail.setJudge("Mr Yellow");
        reportDetail.setCurrentPosition("Manually Created");
        reportDetail.setDateToPosition("10 Jul 2021");
        reportDetail.setConciliationTrack("Standard Track");
        reportData.getReportDetails().add(reportDetail);

        return reportData;
    }

    private HearingsToJudgmentsReportData getHearingsToJudgmentsReportData() {
        var reportSummary = new HearingsToJudgmentsReportSummary("Newcastle");
        reportSummary.setTotalCases("5");
        reportSummary.setTotal4Wk("2");
        reportSummary.setTotal4WkPercent("40.00");
        reportSummary.setTotalX4Wk("3");
        reportSummary.setTotalX4WkPercent("60.00");

        var reportData = new HearingsToJudgmentsReportData(reportSummary);
        reportData.setReportType(Constants.HEARINGS_TO_JUDGEMENTS_REPORT);
        reportData.setDocumentName("TestDocument");
        reportData.setHearingDateType(Constants.RANGE_HEARING_DATE_TYPE);
        reportData.setListingDateFrom("2021-06-20");
        reportData.setListingDateTo("2021-09-20");

        var reportDetail = new HearingsToJudgmentsReportDetail();
        reportDetail.setReportOffice("Newcastle");
        reportDetail.setCaseReference("250003/2021");
        reportDetail.setHearingDate("2021-07-03");
        reportDetail.setJudgementDateSent("2021-08-03");
        reportDetail.setTotalDays("30");
        reportDetail.setReservedHearing("Yes");
        reportDetail.setHearingJudge("judge one");
        reportData.getReportDetails().add(reportDetail);

        reportDetail = new HearingsToJudgmentsReportDetail();
        reportDetail.setReportOffice("Newcastle");
        reportDetail.setCaseReference("250004/2021");
        reportDetail.setHearingDate("2021-08-03");
        reportDetail.setJudgementDateSent("2021-09-03");
        reportDetail.setTotalDays("31");
        reportDetail.setReservedHearing("No");
        reportDetail.setHearingJudge("judge two");
        reportData.getReportDetails().add(reportDetail);

        reportDetail = new HearingsToJudgmentsReportDetail();
        reportDetail.setReportOffice("Newcastle");
        reportDetail.setCaseReference("250005/2021");
        reportDetail.setHearingDate("2021-09-03");
        reportDetail.setJudgementDateSent("2021-10-04");
        reportDetail.setTotalDays("32");
        reportDetail.setReservedHearing("Yes");
        reportDetail.setHearingJudge("judge three");
        reportData.getReportDetails().add(reportDetail);

        return reportData;
    }

    @Test
    public void buildHearingsByHearingType() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-SCO-ENG-00785.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"Listed_date_from\":\"1 December 2021\",\n" +
                "\"Listed_date_to\":\"3 December 2021\",\n" +
                "\"Report_Office\":\"\",\n" +
                "\"cm\":\"2\",\n" +
                "\"costs\":\"2\",\n" +
                "\"hearing\":\"2\",\n" +
                "\"hearingPrelim\":\"2\",\n" +
                "\"reconsider\":\"2\",\n" +
                "\"remedy\":\"2\",\n" +
                "\"total\":\"12\",\n" +
                "\"Report_List\":[\n" +
                "{\"date\":\"20 October 2021\",\n" +
                "\"cm\":\"2\",\n" +
                "\"costs\":\"2\",\n" +
                "\"hearing\":\"2\",\n" +
                "\"hearingPrelim\":\"2\",\n" +
                "\"reconsider\":\"2\",\n" +
                "\"remedy\":\"2\",\n" +
                "\"total\":\"12\"}],\n" +
                "\"Report_List\":[\n" +
                "{\"subSplit\":\"JM\",\n" +
                "\"cm\":\"1\",\n" +
                "\"costs\":\"1\",\n" +
                "\"hearing\":\"1\",\n" +
                "\"hearingPrelim\":\"1\",\n" +
                "\"reconsider\":\"1\",\n" +
                "\"remedy\":\"1\",\n" +
                "\"total\":\"6\"}],\n" +
                "\"Report_List\":[\n" +
                "{\"date\":\"20 October 2021\",\n" +
                "\"subSplit\":\"JM\",\n" +
                "\"cm\":\"2\",\n" +
                "\"costs\":\"2\",\n" +
                "\"hearing\":\"2\",\n" +
                "\"hearingPrelim\":\"2\",\n" +
                "\"reconsider\":\"2\",\n" +
                "\"remedy\":\"2\",\n" +
                "\"total\":\"12\"}],\n" +
                "\"Report_List\":[\n" +
                "{\"date\":\"2020-10-20T10:00:00.000\",\n" +
                "\"multiple_sub\":\"multSub\",\n" +
                "\"case_no\":\"1112\",\n" +
                "\"lead\":\"212323\",\n" +
                "\"hear_no\":\"1\",\n" +
                "\"type\":\"Hearing\",\n" +
                "\"tel\":\"Y\",\n" +
                "\"jm\":\"Y\",\n" +
                "\"dur\":\"430\",\n" +
                "\"clerk\":\"clerk1\"}],\n" +
                "\"Report_Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"" +
                "\n}\n}\n";

        assertEquals(expected, ReportDocHelper.buildReportDocumentContent(reportDetails6.getCaseData(), "",
                "EM-TRB-SCO-ENG-00785", userDetails).toString());
    }
}