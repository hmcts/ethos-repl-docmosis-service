package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.helper.Constants;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.AdhocReportTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.AdhocReportType;
import uk.gov.hmcts.ecm.common.model.listing.types.ClaimServedType;
import uk.gov.hmcts.ecm.common.model.listing.types.ClaimServedTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.CasesAwaitingJudgmentReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.PositionTypeSummary;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.ReportDetail;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.ReportSummary;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.eccreport.EccReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.eccreport.EccReportDetail;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype.HearingsByHearingTypeReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype.HearingsByHearingTypeReportDetail;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype.HearingsByHearingTypeReportSummary;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype.HearingsByHearingTypeReportSummary2;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype.HearingsByHearingTypeReportSummary2Hdr;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype.HearingsByHearingTypeReportSummaryHdr;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype.ReportFields;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments.HearingsToJudgmentsReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments.HearingsToJudgmentsReportDetail;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments.HearingsToJudgmentsReportSummary;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays.MemberDaySummaryItem;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays.MemberDaysReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays.MemberDaysReportDetail;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition.NoPositionChangeReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition.NoPositionChangeReportDetailMultiple;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition.NoPositionChangeReportDetailSingle;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition.NoPositionChangeReportSummary;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport.RespondentsReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport.RespondentsReportDetail;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport.RespondentsReportSummary;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays.SessionDaysReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays.SessionDaysReportDetail;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays.SessionDaysReportSummary;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays.SessionDaysReportSummary2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARINGS_BY_HEARING_TYPE_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MEMBER_DAYS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RANGE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SERVING_CLAIMS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SESSION_DAYS_REPORT;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.ECC_REPORT;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.NO_CHANGE_IN_CURRENT_POSITION_REPORT;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.RESPONDENTS_REPORT;

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
    public void buildMembersDayLocalReport() {
        var todaysDate = UtilHelper.formatCurrentDate(LocalDate.now());
        var  listingData = new MemberDaysReportData();
        listingData.setReportDate(todaysDate);
        listingData.setReportType(MEMBER_DAYS_REPORT);

        var detailItem = new MemberDaysReportDetail();
        detailItem.setHearingDate("15 September 2021");
        detailItem.setEmployeeMember("EE Member");
        detailItem.setEmployerMember("ER Member");
        detailItem.setCaseReference("1800003/2021");
        detailItem.setHearingNumber("33");
        detailItem.setHearingType("Preliminary Hearing");
        detailItem.setHearingClerk("Tester Clerk");
        detailItem.setHearingDuration("420");
        listingData.getReportDetails().add(detailItem);
        listingData.setListingDate("2021-09-15");
        listingData.setOffice("MukeraCity");
        listingData.setHalfDaysTotal("0");
        listingData.setFullDaysTotal("2");
        listingData.setTotalDays("2.0");

        var memberDaySummaryItem = new MemberDaySummaryItem();
        memberDaySummaryItem.setHearingDate("15 September 2021");
        memberDaySummaryItem.setFullDays("2");
        memberDaySummaryItem.setHalfDays("0");
        memberDaySummaryItem.setTotalDays("2");

        listingData.getMemberDaySummaryItems().add(memberDaySummaryItem);
        var userName = nullCheck(userDetails.getFirstName() + " " + userDetails.getLastName());
        String expected = "{\n"
            + "\"accessKey\":\"\",\n"
            + "\"templateName\":\"EM-TRB-SCO-ENG-00800.docx\",\n"
            + "\"outputName\":\"document.docx\",\n"
            + "\"data\":{\n"
            + "\"Listed_date\":\"15 September 2021\",\n"
            + "\"Report_Office\":\"MukeraCity\",\n"
            + "\"Total_Full_Days\":\"2\",\n"
            + "\"Total_Half_Days\":\"0\",\n"
            + "\"Total_Days\":\"2.0\",\n"
            + "\"memberDaySummaryItems\":[\n"
            + "{\n"
            + "\"Hearing_Date\":\"15 September 2021\",\n"
            + "\"Full_Days\":\"2\",\n"
            + "\"Half_Days\":\"0\",\n"
            + "\"Total_Days\":\"2\"\n"
            + "}],\n"
            + "\"reportDetails\":[\n"
            + "{\n"
            + "\"Detail_Hearing_Date\":\"15 September 2021\",\n"
            + "\"Employee_Member\":\"EE Member\",\n"
            + "\"Employer_Member\":\"ER Member\",\n"
            + "\"Case_Reference\":\"1800003/2021\",\n"
            + "\"Hearing_Number\":\"33\",\n"
            + "\"Hearing_Type\":\"Preliminary Hearing\",\n"
            + "\"Hearing_Clerk\":\"Tester Clerk\",\n"
            + "\"Hearing_Duration\":\""
            + detailItem.getHearingDuration()
            + "\"\n"
            + "}],\n"
            + "\"Report_Clerk\":\""
            + userName
            + "\",\n"
            + "\"Today_date\":\""
            + todaysDate
            + "\"\n"
            + "}\n"
            + "}\n";

        assertEquals(expected, ReportDocHelper.buildReportDocumentContent(listingData, "",
            "EM-TRB-SCO-ENG-00800", userDetails).toString());
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
    public void buildServingClaimsReportWithDay6EntriesSorted() throws URISyntaxException, IOException {
        ListingData listingData = new ListingData();
        listingData.setReportType(SERVING_CLAIMS_REPORT);
        listingData.setHearingDateType(RANGE_HEARING_DATE_TYPE);
        listingData.setListingDateFrom("2021-10-02");
        listingData.setListingDateTo("2021-10-28");

        var adHocReportType = new AdhocReportType();
        adHocReportType.setReportOffice("Leeds");
        listingData.setLocalReportsDetailHdr(adHocReportType);

        listingData.setLocalReportsDetail(new ArrayList<>());
        var adhocReportTypeItem = new AdhocReportTypeItem();
        var adhocReportType = new AdhocReportType();

        adhocReportType.setClaimServed6PlusDaysTotal("3");
        adhocReportType.setClaimServed6PlusDaysPercent("100");
        adhocReportType.setTotalCases("3");
        adhocReportType.setClaimServedTotal("3");
        adhocReportType.setClaimServedItems(new ArrayList<>());

        var claimServedType3 = new ClaimServedType();
        claimServedType3.setReportedNumberOfDays(String.valueOf(5));
        claimServedType3.setActualNumberOfDays(String.valueOf(365));
        claimServedType3.setCaseReceiptDate("2020-10-12");
        claimServedType3.setClaimServedDate("2021-10-12");
        var claimServedTypeItem3 = new ClaimServedTypeItem();
        claimServedTypeItem3.setId(String.valueOf(UUID.randomUUID()));
        claimServedTypeItem3.setValue(claimServedType3);

        var claimServedType = new ClaimServedType();
        claimServedType.setReportedNumberOfDays(String.valueOf(5));
        claimServedType.setActualNumberOfDays(String.valueOf(46));
        claimServedType.setCaseReceiptDate("2021-09-02");
        claimServedType.setClaimServedDate("2021-10-16");
        claimServedType.setClaimServedCaseNumber("0098");

        var claimServedTypeItem = new ClaimServedTypeItem();
        claimServedTypeItem.setId(String.valueOf(UUID.randomUUID()));
        claimServedTypeItem.setValue(claimServedType);

        var claimServedType2 = new ClaimServedType();
        claimServedType2.setReportedNumberOfDays(String.valueOf(5));
        claimServedType2.setActualNumberOfDays(String.valueOf(189));
        claimServedType2.setCaseReceiptDate("2021-08-12");
        claimServedType2.setClaimServedDate("2021-02-17");
        claimServedType2.setClaimServedCaseNumber("185");

        var claimServedTypeItem2 = new ClaimServedTypeItem();
        claimServedTypeItem2.setId(String.valueOf(UUID.randomUUID()));
        claimServedTypeItem2.setValue(claimServedType2);

        adhocReportType.getClaimServedItems().add(claimServedTypeItem);
        adhocReportType.getClaimServedItems().add(claimServedTypeItem2);
        adhocReportType.getClaimServedItems().add(claimServedTypeItem3);
        adhocReportType.setTotal("3");

        adhocReportTypeItem.setId(String.valueOf(UUID.randomUUID()));
        adhocReportTypeItem.setValue(adhocReportType);

        listingData.setLocalReportsDetail(new ArrayList<>());
        listingData.getLocalReportsDetail().add(adhocReportTypeItem);
        var expectedJson = getExpectedResult("servingClaimsDay6EntriesSorted.json");
        var today = UtilHelper.formatCurrentDate(LocalDate.now());
        expectedJson = expectedJson.replace("current-date-placeholder", today);
        var actualJson = ReportDocHelper.buildReportDocumentContent(listingData,
            "", "EM-TRB-SCO-ENG-00781", userDetails).toString();
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void buildCorrectServingClaimsReportDocForProvidedAllDaysEntries() throws URISyntaxException, IOException  {
        var expectedJson = getExpectedResult("servingClaimsAllDaysEntries.json");
        var today = UtilHelper.formatCurrentDate(LocalDate.now());
        var expected = expectedJson.replace("current-date-placeholder", today);
        assertEquals(expected, ReportDocHelper.buildReportDocumentContent(reportDetailsClaimsServed.getCaseData(),
                "", "EM-TRB-SCO-ENG-00781", userDetails).toString());
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

    @Test
    public void buildRespondentsReport() throws URISyntaxException, IOException {
        var expectedJson = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("respondentsReportExpected.json")).toURI())));
        var today = UtilHelper.formatCurrentDate(LocalDate.now());
        expectedJson = expectedJson.replace("current-date", today);
        var reportData = getRespondentsReportData();
        var actualJson = ReportDocHelper.buildReportDocumentContent(reportData, "",
                "EM-TRB-SCO-ENG-00815", userDetails).toString();
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void buildSessionDaysReport() throws URISyntaxException, IOException {
        var expectedJson = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("sessionDaysExpected.json")).toURI())));
        var today = UtilHelper.formatCurrentDate(LocalDate.now());
        expectedJson = expectedJson.replace("current-date", today);
        var reportData = getSessionDaysReportData();
        var actualJson = ReportDocHelper.buildReportDocumentContent(reportData, "",
                "EM-TRB-SCO-ENG-00817", userDetails).toString();
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void buildEccReport() throws URISyntaxException, IOException {
        var expectedJson = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("eccExpected.json")).toURI())));
        var today = UtilHelper.formatCurrentDate(LocalDate.now());
        expectedJson = expectedJson.replace("current-date", today);
        var reportData = getEccReportData();
        var actualJson = ReportDocHelper.buildReportDocumentContent(reportData, "",
                "EM-TRB-SCO-ENG-00818", userDetails).toString();
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

    private HearingsByHearingTypeReportData getHearingsByHearingTypeReportData() {
        var reportSummaryHdr = new HearingsByHearingTypeReportSummaryHdr();
        ReportFields fields = new ReportFields();
        fields.setTotal("6");
        fields.setHearingCount("1");
        fields.setRemedyCount("1");
        fields.setReconsiderCount("1");
        fields.setCostsCount("1");
        fields.setCmCount("1");
        fields.setHearingPrelimCount("1");
        reportSummaryHdr.setFields(fields);
        reportSummaryHdr.setOffice("Manchester");
        var reportData = new HearingsByHearingTypeReportData(reportSummaryHdr);
        reportData.setReportType(HEARINGS_BY_HEARING_TYPE_REPORT);
        var reportSummary = new HearingsByHearingTypeReportSummary();
        reportSummary.setFields(fields);
        reportSummary.getFields().setDate("12/02/2022");
        reportData.addReportSummaryList(Collections.singletonList(reportSummary));
        var reportSummary2Hdr = new HearingsByHearingTypeReportSummary2Hdr();
        reportSummary2Hdr.setFields(fields);
        reportSummary2Hdr.getFields().setSubSplit("Stage 1");
        reportData.addReportSummary2HdrList(Collections.singletonList(reportSummary2Hdr));
        var reportSummary2 = new HearingsByHearingTypeReportSummary2();
        reportSummary2.setFields(fields);
        reportSummary2.getFields().setSubSplit("Stage 1");
        reportSummary2.getFields().setDate("12/02/2022");
        reportData.addReportSummary2List(Collections.singletonList(reportSummary2));
        var reportDetail = new HearingsByHearingTypeReportDetail();
        reportDetail.setTel("Y");
        reportDetail.setDuration("20");
        reportDetail.setCaseReference("1111");
        reportDetail.setJm("Y");
        reportDetail.setHearingNo("1");
        reportDetail.setHearingClerk("Clerk A");
        reportDetail.setDetailDate("12/02/2022");
        reportDetail.setHearingType("Hearing");
        reportDetail.setLead("Y");
        reportDetail.setMultiSub("multiSub");
        reportData.addReportDetail(Collections.singletonList(reportDetail));
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

    private RespondentsReportData getRespondentsReportData() {
        var reportSummary = new RespondentsReportSummary();
        reportSummary.setTotalCasesWithMoreThanOneRespondent("2");
        reportSummary.setOffice("Manchester");

        var reportData = new RespondentsReportData(reportSummary);
        reportData.setReportType(RESPONDENTS_REPORT);
        reportData.setDocumentName("TestDocument");
        reportData.setHearingDateType(Constants.RANGE_HEARING_DATE_TYPE);
        reportData.setListingDateFrom("2022-01-01");
        reportData.setListingDateTo("2022-01-10");

        var reportDetail1 = new RespondentsReportDetail();
        reportDetail1.setCaseNumber("110001/2022");
        reportDetail1.setRespondentName("Resp1");
        reportDetail1.setRepresentativeHasMoreThanOneRespondent("Y");
        reportDetail1.setRepresentativeName("Rep1");
        var reportDetail2 = new RespondentsReportDetail();
        reportDetail2.setCaseNumber("110002/2022");
        reportDetail2.setRespondentName("Resp2");
        reportDetail2.setRepresentativeHasMoreThanOneRespondent("N");
        reportDetail2.setRepresentativeName("N/A");
        reportData.addReportDetail(Arrays.asList(reportDetail1, reportDetail2));
        return reportData;
    }

    private SessionDaysReportData getSessionDaysReportData() {
        var reportSummary = new SessionDaysReportSummary("Manchester");
        reportSummary.setFtSessionDaysTotal("1");
        reportSummary.setPtSessionDaysTotal("1");
        reportSummary.setOtherSessionDaysTotal("1");
        reportSummary.setSessionDaysTotal("3");
        reportSummary.setPtSessionDaysPerCent("33");

        var reportData = new SessionDaysReportData(reportSummary);
        reportData.setReportType(SESSION_DAYS_REPORT);
        reportData.setDocumentName("TestDocument");
        reportData.setHearingDateType(Constants.RANGE_HEARING_DATE_TYPE);
        reportData.setListingDateFrom("2022-01-01");
        reportData.setListingDateTo("2022-01-10");

        var summary2 = new SessionDaysReportSummary2();

        summary2.setDate("20-1-2022");
        summary2.setFtSessionDays("1");
        summary2.setPtSessionDays("1");
        summary2.setOtherSessionDays("1");
        summary2.setSessionDaysTotalDetail("3");
        reportData.addReportSummary2List(Collections.singletonList(summary2));

        var reportDetail1 = new SessionDaysReportDetail();
        reportDetail1.setSessionType("Full Day");
        reportDetail1.setHearingTelConf("Y");
        reportDetail1.setHearingSitAlone("Y");
        reportDetail1.setJudgeType("Salaried");
        reportDetail1.setHearingDuration("200");
        reportDetail1.setHearingJudge("Judge X");
        reportDetail1.setHearingType("hearing type");
        reportDetail1.setHearingDate("20-1-2022");
        reportDetail1.setHearingClerk("Clerk X");
        reportDetail1.setHearingNumber("1");
        reportDetail1.setCaseReference("1111/2022");
        reportData.addReportDetail(Collections.singletonList(reportDetail1));
        return reportData;
    }

    private EccReportData getEccReportData() {
        var reportData = new EccReportData("Manchester");
        reportData.setReportType(ECC_REPORT);
        reportData.setDocumentName("TestDocument");
        reportData.setHearingDateType(Constants.RANGE_HEARING_DATE_TYPE);
        reportData.setListingDateFrom("2022-01-01");
        reportData.setListingDateTo("2022-01-10");

        var reportDetail1 = new EccReportDetail();
        reportDetail1.setRespondentsCount("2");
        reportDetail1.setEccCaseList("ecc1\necc2");
        reportDetail1.setEccCasesCount("2");
        reportDetail1.setState("Accepted");
        reportDetail1.setDate("20-1-2022");
        reportDetail1.setCaseNumber("1111212/2022");
        reportData.addReportDetail(Collections.singletonList(reportDetail1));
        return reportData;
    }

    @Test
    public void buildHearingsByHearingTypeReport() throws URISyntaxException, IOException {
        var expectedJson = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("hearingsByHearingTypeExpected.json")).toURI())));
        var today = UtilHelper.formatCurrentDate(LocalDate.now());
        expectedJson = expectedJson.replace("replace-with-current-date", today);
        var reportData = getHearingsByHearingTypeReportData();
        var actualJson = ReportDocHelper.buildReportDocumentContent(reportData, "",
                "EM-TRB-SCO-ENG-00785", userDetails).toString();
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void buildNoPositionChangeReport() throws URISyntaxException, IOException {
        var expectedJson = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("noChangeInCurrentPositionExpected.json")).toURI())));
        var today = UtilHelper.formatCurrentDate(LocalDate.now());
        expectedJson = expectedJson.replace("replace-with-current-date", today);
        var reportData = getNoPositionChangeReportData();
        var actualJson = ReportDocHelper.buildReportDocumentContent(reportData, "",
                "EM-TRB-SCO-ENG-00794", userDetails).toString();
        assertEquals(expectedJson, actualJson);
    }

    private NoPositionChangeReportData getNoPositionChangeReportData() {
        var reportSummary = new NoPositionChangeReportSummary("Newcastle");
        reportSummary.setTotalCases("3");
        reportSummary.setTotalSingleCases("2");
        reportSummary.setTotalMultipleCases("1");

        var reportData = new NoPositionChangeReportData(reportSummary, "2021-06-20");
        reportData.setReportType(NO_CHANGE_IN_CURRENT_POSITION_REPORT);
        reportData.setDocumentName("TestDocument");

        var reportDetailSingle = new NoPositionChangeReportDetailSingle();
        reportDetailSingle.setCaseReference("250003/2021");
        reportDetailSingle.setYear("2021");
        reportDetailSingle.setCurrentPosition("Test1");
        reportDetailSingle.setDateToPosition("2021-08-03");
        reportDetailSingle.setRespondent("Resp1");
        reportData.getReportDetailsSingle().add(reportDetailSingle);

        reportDetailSingle = new NoPositionChangeReportDetailSingle();
        reportDetailSingle.setCaseReference("250004/2021");
        reportDetailSingle.setYear("2022");
        reportDetailSingle.setCurrentPosition("Test2");
        reportDetailSingle.setDateToPosition("2021-09-03");
        reportDetailSingle.setRespondent("Resp2 & Others");
        reportData.getReportDetailsSingle().add(reportDetailSingle);

        var reportDetailMultiple = new NoPositionChangeReportDetailMultiple();
        reportDetailMultiple.setCaseReference("250005/2021");
        reportDetailMultiple.setYear("2023");
        reportDetailMultiple.setCurrentPosition("Test3");
        reportDetailMultiple.setDateToPosition("2021-10-04");
        reportDetailMultiple.setMultipleName("Multi1");
        reportData.getReportDetailsMultiple().add(reportDetailMultiple);

        return reportData;
    }

    private String getExpectedResult(String resourceFileName) throws URISyntaxException, IOException {
        var expectedJson = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
            .getResource(resourceFileName)).toURI())));
        var today = UtilHelper.formatCurrentDate(LocalDate.now());
        expectedJson = expectedJson.replace("current-date-placeholder", today);
        return expectedJson;
    }
}