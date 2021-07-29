package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.helper.Constants;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.CasesAwaitingJudgmentReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.PositionTypeSummary;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.ReportDetail;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.ReportSummary;

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
    private UserDetails userDetails;

    @Before
    public void setUp() throws Exception {
        reportDetails = generateReportDetails("reportDetailsTest1.json");
        reportDetails2 = generateReportDetails("reportDetailsTest2.json");
        reportDetails3 = generateReportDetails("reportDetailsTest3.json");
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
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-SCO-ENG-00220.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"Listed_date_from\":\"1 December 2021\",\n" +
                "\"Listed_date_to\":\"3 December 2021\",\n" +
                "\"Report_Office\":\"Manchester\",\n" +
                "\"Report_List\":[\n" +
                "{\"Case_Reference\":\"2122324/2020\",\n" +
                "\"Date_Of_Acceptance\":\"\",\n" +
                "\"Multiple_Ref\":\"212323\",\n" +
                "\"Lead_Case\":\"Yes\",\n" +
                "\"Position\":\"Position2\",\n" +
                "\"Date_To_Position\":\"\",\n" +
                "\"File_Location\":\"\",\n" +
                "\"Clerk\":\"Anne Fox\"},\n" +
                "{\"Case_Reference\":\"2122323/2020\",\n" +
                "\"Date_Of_Acceptance\":\"\",\n" +
                "\"Multiple_Ref\":\"\",\n" +
                "\"Lead_Case\":\"\",\n" +
                "\"Position\":\"Position5\",\n" +
                "\"Date_To_Position\":\"\",\n" +
                "\"File_Location\":\"\",\n" +
                "\"Clerk\":\"Anne Fox2\"}],\n" +
                "\"Report_Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, ReportDocHelper.buildReportDocumentContent(reportDetails2.getCaseData(), "",
                "EM-TRB-SCO-ENG-00220", userDetails).toString());
    }

    @Test
    public void buildCasesCompletedReport() {
        var expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-SCO-ENG-00221.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"Listed_date_from\":\"1 December 2021\",\n" +
                "\"Listed_date_to\":\"3 December 2021\",\n" +
                "\"Report_Office\":\"Manchester\",\n" +
                "\"Cases_Completed_Hearing\":\"2\",\n" +
                "\"Session_Days_Taken\":\"6\",\n" +
                "\"Completed_Per_Session_Day\":\"1\",\n" +
                "\"No_Conciliation_1\":\"2\",\n" +
                "\"No_Conciliation_2\":\"6\",\n" +
                "\"No_Conciliation_3\":\"1\",\n" +
                "\"Fast_Track_1\":\"8\",\n" +
                "\"Fast_Track_2\":\"3\",\n" +
                "\"Fast_Track_3\":\"\",\n" +
                "\"Standard_Track_1\":\"2\",\n" +
                "\"Standard_Track_2\":\"\",\n" +
                "\"Standard_Track_3\":\"\",\n" +
                "\"Open_Track_1\":\"\",\n" +
                "\"Open_Track_2\":\"\",\n" +
                "\"Open_Track_3\":\"\",\n" +
                "\"Report_List\":[\n" +
                "{\"Case_Reference\":\"2122324/2020\",\n" +
                "\"Position\":\"Position2\",\n" +
                "\"Conciliation_Track\":\"No Conciliation\",\n" +
                "\"Session_Days\":\"\",\n" +
                "\"Hearing_Number\":\"3\",\n" +
                "\"Hearing_Date\":\"12 October 2020\",\n" +
                "\"Hearing_Type\":\"Hearing Type\",\n" +
                "\"Hearing_Judge\":\"Judge Name2\",\n" +
                "\"Hearing_Clerk\":\"Hearing Clerk\"},\n" +
                "{\"Case_Reference\":\"2122323/2020\",\n" +
                "\"Position\":\"Position5\",\n" +
                "\"Conciliation_Track\":\"No Conciliation\",\n" +
                "\"Session_Days\":\"\",\n" +
                "\"Hearing_Number\":\"5\",\n" +
                "\"Hearing_Date\":\"15 October 2020\",\n" +
                "\"Hearing_Type\":\"Hearing Type2\",\n" +
                "\"Hearing_Judge\":\"Judge Name\",\n" +
                "\"Hearing_Clerk\":\"Hearing Clerk\"}],\n" +
                "\"Report_Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n" +
                "}\n" +
                "}\n";
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
        var actualJson = ReportDocHelper.buildReportDocumentContent(reportData, "", "EM-TRB-SCO-ENG-00749", userDetails).toString();
        assertEquals(expectedJson, actualJson);
    }

    private CasesAwaitingJudgmentReportData getCasesAwaitingJudgementReportData() {
        var reportSummary = new ReportSummary("Newcastle", "Lurleen Lumpkin", LocalDate.now());
        reportSummary.getPositionTypes().add(new PositionTypeSummary("Signed fair copy received", 1));
        reportSummary.getPositionTypes().add(new PositionTypeSummary("Heard awaiting judgment being sent to the parties", 5));
        reportSummary.getPositionTypes().add(new PositionTypeSummary("Draft with members", 10));

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
}