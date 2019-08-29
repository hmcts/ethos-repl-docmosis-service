package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.models.UserDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.CorrespondenceScotType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.CorrespondenceType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.Assert.*;

public class HelperTest {

    private CaseDetails caseDetails1;
    private CaseDetails caseDetails2;
    private CaseDetails caseDetails3;
    private CaseDetails caseDetails4;
    private CaseDetails caseDetails5;
    private CaseDetails caseDetails6;
    private CaseDetails caseDetails7;
    private CaseDetails caseDetails8;
    private CaseDetails caseDetails9;
    private CaseDetails caseDetailsEmpty;
    private CaseDetails caseDetailsScot1;
    private CaseDetails caseDetailsScot2;
    private CaseDetails caseDetailsScot3;
    private UserDetails userDetails;

    @Before
    public void setUp() throws Exception {
        caseDetails1 = generateCaseDetails("caseDetailsTest1.json");
        caseDetails2 = generateCaseDetails("caseDetailsTest2.json");
        caseDetails3 = generateCaseDetails("caseDetailsTest3.json");
        caseDetails4 = generateCaseDetails("caseDetailsTest4.json");
        caseDetails5 = generateCaseDetails("caseDetailsTest5.json");
        caseDetails6 = generateCaseDetails("caseDetailsTest6.json");
        caseDetails7 = generateCaseDetails("caseDetailsTest7.json");
        caseDetails8 = generateCaseDetails("caseDetailsTest8.json");
        caseDetails9 = generateCaseDetails("caseDetailsTest9.json");
        caseDetailsScot1 = generateCaseDetails("caseDetailsScotTest1.json");
        caseDetailsScot2 = generateCaseDetails("caseDetailsScotTest2.json");
        caseDetailsScot3 = generateCaseDetails("caseDetailsScotTest3.json");

        caseDetailsEmpty = new CaseDetails();
        caseDetailsEmpty.setCaseData(new CaseData());
        userDetails = new UserDetails("1", "example@hotmail.com", "Mike", "Jordan", new ArrayList<>());

    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }

    @Test
    public void buildDocumentContent1() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00026.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressLine1\":\"56 Block C\",\n" +
                "\"claimant_addressLine2\":\"Ellesmere Street\",\n" +
                "\"claimant_addressLine3\":\"\",\n" +
                "\"claimant_town\":\"Manchester\",\n" +
                "\"claimant_county\":\"Lancashire\",\n" +
                "\"claimant_postCode\":\"M3 KJR\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"respondent_full_name\":\"Antonio Vazquez\",\n" +
                "\"respondent_addressLine1\":\"11 Small Street\",\n" +
                "\"respondent_addressLine2\":\"22 House\",\n" +
                "\"respondent_addressLine3\":\"\",\n" +
                "\"respondent_town\":\"Manchester\",\n" +
                "\"respondent_county\":\"North West\",\n" +
                "\"respondent_postCode\":\"M12 42R\",\n" +
                "\"resp_others\":\"2. Juan Garcia\\n3. Mike Jordan\",\n" +
                "\"resp_address\":\"1. 11 Small Street, 22 House, Manchester, North West, M12 42R, UK\\n" +
                "2. 12 Small Street, 24 House, Manchester, North West, M12 4ED, UK\\n" +
                "3. 11 Small Street, 22 House, Manchester, North West, M12 42R, UK\",\n" +
                "\"Respondent\":\"1. Antonio Vazquez\",\n" +
                "\"Hearing_date\":\"25 November 2019\",\n" +
                "\"Hearing_date_time\":\"25 November 2019 10:11\",\n" +
                "\"Hearing_venue\":\"Manchester\",\n" +
                "\"Hearing_duration\":\"2 days\",\n" +
                "\"t1_2\":\"true\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal,\",\n" +
                "\"Court_addressLine2\":\"Alexandra House,\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage,\",\n" +
                "\"Court_town\":\"Manchester,\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i1_2_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"i1_2_enhmcts1\":\"[userImage:enhmcts.png]\",\n" +
                "\"i1_2_enhmcts2\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts1\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts2\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, Helper.buildDocumentContent(caseDetails1.getCaseData(), "", userDetails).toString());
    }

    @Test
    public void buildDocumentContent2() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00027.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"Orlando LTD\",\n" +
                "\"Claimant\":\"Orlando LTD\",\n" +
                "\"claimant_addressLine1\":\"34\",\n" +
                "\"claimant_addressLine2\":\"Low Street\",\n" +
                "\"claimant_addressLine3\":\"\",\n" +
                "\"claimant_town\":\"Manchester\",\n" +
                "\"claimant_county\":\"Lancashire\",\n" +
                "\"claimant_postCode\":\"M3 6gw\",\n" +
                "\"respondent_full_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_addressLine1\":\"56 Block C\",\n" +
                "\"respondent_addressLine2\":\"Ellesmere Street\",\n" +
                "\"respondent_addressLine3\":\"\",\n" +
                "\"respondent_town\":\"Manchester\",\n" +
                "\"respondent_county\":\"Lancashire\",\n" +
                "\"respondent_postCode\":\"M3 KJR\",\n" +
                "\"respondent_reference\":\"1111111\",\n" +
                "\"Respondent\":\"\",\n" +
                "\"resp_others\":\"\",\n" +
                "\"resp_address\":\"\",\n" +
                "\"Hearing_date\":\"\",\n" +
                "\"Hearing_date_time\":\"\",\n" +
                "\"Hearing_venue\":\"Manchester\",\n" +
                "\"Hearing_duration\":\"\",\n" +
                "\"t2_2A\":\"true\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal,\",\n" +
                "\"Court_addressLine2\":\"Alexandra House,\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage,\",\n" +
                "\"Court_town\":\"Manchester,\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i2_2A_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"i2_2A_enhmcts1\":\"[userImage:enhmcts.png]\",\n" +
                "\"i2_2A_enhmcts2\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts1\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts2\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, Helper.buildDocumentContent(caseDetails2.getCaseData(), "", userDetails).toString());
    }

    @Test
    public void buildDocumentContent3() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00028.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"Mr A J Rodriguez\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"claimant_addressLine1\":\"34\",\n" +
                "\"claimant_addressLine2\":\"Low Street\",\n" +
                "\"claimant_addressLine3\":\"\",\n" +
                "\"claimant_town\":\"Manchester\",\n" +
                "\"claimant_county\":\"Lancashire\",\n" +
                "\"claimant_postCode\":\"M3 6gw\",\n" +
                "\"respondent_full_name\":\"Antonio Vazquez\",\n" +
                "\"respondent_addressLine1\":\"11 Small Street\",\n" +
                "\"respondent_addressLine2\":\"22 House\",\n" +
                "\"respondent_addressLine3\":\"\",\n" +
                "\"respondent_town\":\"Manchester\",\n" +
                "\"respondent_county\":\"North West\",\n" +
                "\"respondent_postCode\":\"M12 42R\",\n" +
                "\"resp_others\":\"\",\n" +
                "\"resp_address\":\"11 Small Street, 22 House, Manchester, North West, M12 42R, UK\",\n" +
                "\"Respondent\":\"Antonio Vazquez\",\n" +
                "\"Hearing_date\":\"\",\n" +
                "\"Hearing_date_time\":\"\",\n" +
                "\"Hearing_venue\":\"\",\n" +
                "\"Hearing_duration\":\"\",\n" +
                "\"t3_2\":\"true\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal,\",\n" +
                "\"Court_addressLine2\":\"Alexandra House,\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage,\",\n" +
                "\"Court_town\":\"Manchester,\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i3_2_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"i3_2_enhmcts1\":\"[userImage:enhmcts.png]\",\n" +
                "\"i3_2_enhmcts2\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts1\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts2\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, Helper.buildDocumentContent(caseDetails3.getCaseData(), "", userDetails).toString());
    }

    @Test
    public void buildDocumentContent4() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00029.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressLine1\":\"56 Block C\",\n" +
                "\"claimant_addressLine2\":\"Ellesmere Street\",\n" +
                "\"claimant_addressLine3\":\"\",\n" +
                "\"claimant_town\":\"Manchester\",\n" +
                "\"claimant_county\":\"Lancashire\",\n" +
                "\"claimant_postCode\":\"M3 KJR\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"respondent_full_name\":\"Antonio Vazquez\",\n" +
                "\"respondent_addressLine1\":\"11 Small Street\",\n" +
                "\"respondent_addressLine2\":\"22 House\",\n" +
                "\"respondent_addressLine3\":\"\",\n" +
                "\"respondent_town\":\"Manchester\",\n" +
                "\"respondent_county\":\"North West\",\n" +
                "\"respondent_postCode\":\"M12 42R\",\n" +
                "\"resp_others\":\"\",\n" +
                "\"resp_address\":\"11 Small Street, 22 House, Manchester, North West, M12 42R, UK\",\n" +
                "\"Respondent\":\"Antonio Vazquez\",\n" +
                "\"Hearing_date\":\"\",\n" +
                "\"Hearing_date_time\":\"\",\n" +
                "\"Hearing_venue\":\"\",\n" +
                "\"Hearing_duration\":\"\",\n" +
                "\"t4_2\":\"true\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal,\",\n" +
                "\"Court_addressLine2\":\"Alexandra House,\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage,\",\n" +
                "\"Court_town\":\"Manchester,\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i4_2_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"i4_2_enhmcts1\":\"[userImage:enhmcts.png]\",\n" +
                "\"i4_2_enhmcts2\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts1\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts2\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, Helper.buildDocumentContent(caseDetails4.getCaseData(), "", userDetails).toString());
    }

    @Test
    public void buildDocumentContent5() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00030.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressLine1\":\"56 Block C\",\n" +
                "\"claimant_addressLine2\":\"Ellesmere Street\",\n" +
                "\"claimant_addressLine3\":\"\",\n" +
                "\"claimant_town\":\"Manchester\",\n" +
                "\"claimant_county\":\"Lancashire\",\n" +
                "\"claimant_postCode\":\"M3 KJR\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"respondent_full_name\":\"RepresentativeNameRespondent1\",\n" +
                "\"respondent_addressLine1\":\"56 Block C\",\n" +
                "\"respondent_addressLine2\":\"Ellesmere Street\",\n" +
                "\"respondent_addressLine3\":\"\",\n" +
                "\"respondent_town\":\"Manchester\",\n" +
                "\"respondent_county\":\"Lancashire\",\n" +
                "\"respondent_postCode\":\"M3 KJR\",\n" +
                "\"respondent_reference\":\"3333333333\",\n" +
                "\"resp_others\":\"\",\n" +
                "\"resp_address\":\"11 Small Street, 22 House, Manchester, North West, M12 42R, UK\",\n" +
                "\"Respondent\":\"Antonio Vazquez\",\n" +
                "\"Hearing_date\":\"\",\n" +
                "\"Hearing_date_time\":\"\",\n" +
                "\"Hearing_venue\":\"\",\n" +
                "\"Hearing_duration\":\"\",\n" +
                "\"t5_2\":\"true\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal,\",\n" +
                "\"Court_addressLine2\":\"Alexandra House,\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage,\",\n" +
                "\"Court_town\":\"Manchester,\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i5_2_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"i5_2_enhmcts1\":\"[userImage:enhmcts.png]\",\n" +
                "\"i5_2_enhmcts2\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts1\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts2\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, Helper.buildDocumentContent(caseDetails5.getCaseData(), "", userDetails).toString());
    }

    @Test
    public void buildDocumentContent6() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00031.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressLine1\":\"56 Block C\",\n" +
                "\"claimant_addressLine2\":\"Ellesmere Street\",\n" +
                "\"claimant_addressLine3\":\"\",\n" +
                "\"claimant_town\":\"Manchester\",\n" +
                "\"claimant_county\":\"Lancashire\",\n" +
                "\"claimant_postCode\":\"M3 KJR\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"respondent_full_name\":\"RepresentativeNameRespondent1\",\n" +
                "\"respondent_addressLine1\":\"56 Block C\",\n" +
                "\"respondent_addressLine2\":\"Ellesmere Street\",\n" +
                "\"respondent_addressLine3\":\"\",\n" +
                "\"respondent_town\":\"Manchester\",\n" +
                "\"respondent_county\":\"Lancashire\",\n" +
                "\"respondent_postCode\":\"M3 KJR\",\n" +
                "\"respondent_reference\":\"3333333333\",\n" +
                "\"resp_others\":\"\",\n" +
                "\"resp_address\":\"11 Small Street, 22 House, Manchester, North West, M12 42R, UK\",\n" +
                "\"Respondent\":\"Antonio Vazquez\",\n" +
                "\"Hearing_date\":\"\",\n" +
                "\"Hearing_date_time\":\"\",\n" +
                "\"Hearing_venue\":\"\",\n" +
                "\"Hearing_duration\":\"\",\n" +
                "\"t6_2\":\"true\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal,\",\n" +
                "\"Court_addressLine2\":\"Alexandra House,\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage,\",\n" +
                "\"Court_town\":\"Manchester,\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i6_2_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"i6_2_enhmcts1\":\"[userImage:enhmcts.png]\",\n" +
                "\"i6_2_enhmcts2\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts1\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts2\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, Helper.buildDocumentContent(caseDetails6.getCaseData(), "", userDetails).toString());
    }

    @Test
    public void buildDocumentContent7() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00032.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressLine1\":\"56 Block C\",\n" +
                "\"claimant_addressLine2\":\"Ellesmere Street\",\n" +
                "\"claimant_addressLine3\":\"\",\n" +
                "\"claimant_town\":\"Manchester\",\n" +
                "\"claimant_county\":\"Lancashire\",\n" +
                "\"claimant_postCode\":\"M3 KJR\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"respondent_full_name\":\"RepresentativeNameRespondent1\",\n" +
                "\"respondent_addressLine1\":\"56 Block C\",\n" +
                "\"respondent_addressLine2\":\"Ellesmere Street\",\n" +
                "\"respondent_addressLine3\":\"\",\n" +
                "\"respondent_town\":\"Manchester\",\n" +
                "\"respondent_county\":\"Lancashire\",\n" +
                "\"respondent_postCode\":\"M3 KJR\",\n" +
                "\"respondent_reference\":\"3333333333\",\n" +
                "\"resp_others\":\"\",\n" +
                "\"resp_address\":\"11 Small Street, 22 House, Manchester, North West, M12 42R, UK\",\n" +
                "\"Respondent\":\"Antonio Vazquez\",\n" +
                "\"Hearing_date\":\"\",\n" +
                "\"Hearing_date_time\":\"\",\n" +
                "\"Hearing_venue\":\"Manchester\",\n" +
                "\"Hearing_duration\":\"2 hours\",\n" +
                "\"t7_2\":\"true\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal,\",\n" +
                "\"Court_addressLine2\":\"Alexandra House,\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage,\",\n" +
                "\"Court_town\":\"Manchester,\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i7_2_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"i7_2_enhmcts1\":\"[userImage:enhmcts.png]\",\n" +
                "\"i7_2_enhmcts2\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts1\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts2\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, Helper.buildDocumentContent(caseDetails7.getCaseData(), "", userDetails).toString());
    }

    @Test
    public void buildDocumentContent8() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00033.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressLine1\":\"56 Block C\",\n" +
                "\"claimant_addressLine2\":\"Ellesmere Street\",\n" +
                "\"claimant_addressLine3\":\"\",\n" +
                "\"claimant_town\":\"Manchester\",\n" +
                "\"claimant_county\":\"Lancashire\",\n" +
                "\"claimant_postCode\":\"M3 KJR\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"respondent_full_name\":\"Antonio Vazquez\",\n" +
                "\"respondent_addressLine1\":\"11 Small Street\",\n" +
                "\"respondent_addressLine2\":\"22 House\",\n" +
                "\"respondent_addressLine3\":\"\",\n" +
                "\"respondent_town\":\"Manchester\",\n" +
                "\"respondent_county\":\"North West\",\n" +
                "\"respondent_postCode\":\"M12 42R\",\n" +
                "\"resp_others\":\"2. Mikey McCollier\",\n" +
                "\"resp_address\":\"1. 11 Small Street, 22 House, Manchester, North West, M12 42R, UK\\n" +
                "2. 1333 Small Street, 22222 House, Liverpool, North West, L12 42R, UK\",\n" +
                "\"Respondent\":\"1. Antonio Vazquez\",\n" +
                "\"Hearing_date\":\"\",\n" +
                "\"Hearing_date_time\":\"\",\n" +
                "\"Hearing_venue\":\"Manchester\",\n" +
                "\"Hearing_duration\":\"2 hours\",\n" +
                "\"t10_2\":\"true\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal,\",\n" +
                "\"Court_addressLine2\":\"Alexandra House,\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage,\",\n" +
                "\"Court_town\":\"Manchester,\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i10_2_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"i10_2_enhmcts1\":\"[userImage:enhmcts.png]\",\n" +
                "\"i10_2_enhmcts2\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts1\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts2\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, Helper.buildDocumentContent(caseDetails8.getCaseData(), "", userDetails).toString());
    }

    @Test
    public void buildDocumentContent9() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00034.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressLine1\":\"56 Block C\",\n" +
                "\"claimant_addressLine2\":\"Ellesmere Street\",\n" +
                "\"claimant_addressLine3\":\"\",\n" +
                "\"claimant_town\":\"Manchester\",\n" +
                "\"claimant_county\":\"Lancashire\",\n" +
                "\"claimant_postCode\":\"M3 KJR\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"respondent_full_name\":\"Antonio Vazquez\",\n" +
                "\"respondent_addressLine1\":\"11 Small Street\",\n" +
                "\"respondent_addressLine2\":\"22 House\",\n" +
                "\"respondent_addressLine3\":\"\",\n" +
                "\"respondent_town\":\"Manchester\",\n" +
                "\"respondent_county\":\"North West\",\n" +
                "\"respondent_postCode\":\"M12 42R\",\n" +
                "\"resp_others\":\"\",\n" +
                "\"resp_address\":\"11 Small Street, 22 House, Manchester, North West, M12 42R, UK\",\n" +
                "\"Respondent\":\"Antonio Vazquez\",\n" +
                "\"Hearing_date\":\"\",\n" +
                "\"Hearing_date_time\":\"\",\n" +
                "\"Hearing_venue\":\"Manchester\",\n" +
                "\"Hearing_duration\":\"2 hours\",\n" +
                "\"t9_2\":\"true\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal,\",\n" +
                "\"Court_addressLine2\":\"Alexandra House,\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage,\",\n" +
                "\"Court_town\":\"Manchester,\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i9_2_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"i9_2_enhmcts1\":\"[userImage:enhmcts.png]\",\n" +
                "\"i9_2_enhmcts2\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts1\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts2\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, Helper.buildDocumentContent(caseDetails9.getCaseData(), "", userDetails).toString());
    }

    @Test
    public void buildDocumentWithNotContent() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\".docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"respondent_full_name\":\"\",\n" +
                "\"Respondent\":\"\",\n" +
                "\"resp_others\":\"\",\n" +
                "\"resp_address\":\"\",\n" +
                "\"Hearing_date\":\"\",\n" +
                "\"Hearing_date_time\":\"\",\n" +
                "\"Hearing_venue\":\"\",\n" +
                "\"Hearing_duration\":\"\",\n" +
                "\"Court_telephone\":\"\",\n" +
                "\"Court_fax\":\"\",\n" +
                "\"Court_DX\":\"\",\n" +
                "\"Court_Email\":\"\",\n" +
                "\"i_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"i_enhmcts1\":\"[userImage:enhmcts.png]\",\n" +
                "\"i_enhmcts2\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts1\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts2\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"\",\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, Helper.buildDocumentContent(caseDetailsEmpty.getCaseData(), "", userDetails).toString());
    }

    @Test
    public void buildDocumentContentScot1() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-SCO-ENG-00042.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressLine1\":\"56 Block C\",\n" +
                "\"claimant_addressLine2\":\"Ellesmere Street\",\n" +
                "\"claimant_addressLine3\":\"\",\n" +
                "\"claimant_town\":\"Manchester\",\n" +
                "\"claimant_county\":\"Lancashire\",\n" +
                "\"claimant_postCode\":\"M3 KJR\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"respondent_full_name\":\"Antonio Vazquez\",\n" +
                "\"respondent_addressLine1\":\"11 Small Street\",\n" +
                "\"respondent_addressLine2\":\"22 House\",\n" +
                "\"respondent_addressLine3\":\"\",\n" +
                "\"respondent_town\":\"Manchester\",\n" +
                "\"respondent_county\":\"North West\",\n" +
                "\"respondent_postCode\":\"M12 42R\",\n" +
                "\"resp_others\":\"2. Juan Garcia\",\n" +
                "\"resp_address\":\"1. 11 Small Street, 22 House, Manchester, North West, M12 42R, UK\\n" +
                "2. 12 Small Street, 24 House, Manchester, North West, M12 4ED, UK\",\n" +
                "\"Respondent\":\"1. Antonio Vazquez\",\n" +
                "\"Hearing_date\":\"25 November 2019\",\n" +
                "\"Hearing_date_time\":\"25 November 2019 10:11\",\n" +
                "\"Hearing_venue\":\"Manchester\",\n" +
                "\"Hearing_duration\":\"2 days\",\n" +
                "\"t_Scot_7_1\":\"true\",\n" +
                "\"Court_addressLine1\":\"Eagle Building,\",\n" +
                "\"Court_addressLine2\":\"215 Bothwell Street,\",\n" +
                "\"Court_addressLine3\":\"\",\n" +
                "\"Court_town\":\"Glasgow,\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"G2 7TS\",\n" +
                "\"Court_telephone\":\"03577123270\",\n" +
                "\"Court_fax\":\"07127126570\",\n" +
                "\"Court_DX\":\"1234567\",\n" +
                "\"Court_Email\":\"GlasgowOfficeET@hmcts.gov.uk\",\n" +
                "\"i_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"i_enhmcts1\":\"[userImage:enhmcts.png]\",\n" +
                "\"i_enhmcts2\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot7_1_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot7_1_schmcts1\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot7_1_schmcts2\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, Helper.buildDocumentContent(caseDetailsScot1.getCaseData(), "", userDetails).toString());
    }

    @Test
    public void buildDocumentContentScot2() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-SCO-ENG-00043.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"Orlando LTD\",\n" +
                "\"Claimant\":\"Orlando LTD\",\n" +
                "\"claimant_addressLine1\":\"34\",\n" +
                "\"claimant_addressLine2\":\"Low Street\",\n" +
                "\"claimant_addressLine3\":\"\",\n" +
                "\"claimant_town\":\"Manchester\",\n" +
                "\"claimant_county\":\"Lancashire\",\n" +
                "\"claimant_postCode\":\"M3 6gw\",\n" +
                "\"respondent_full_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_addressLine1\":\"56 Block C\",\n" +
                "\"respondent_addressLine2\":\"Ellesmere Street\",\n" +
                "\"respondent_addressLine3\":\"\",\n" +
                "\"respondent_town\":\"Manchester\",\n" +
                "\"respondent_county\":\"Lancashire\",\n" +
                "\"respondent_postCode\":\"M3 KJR\",\n" +
                "\"respondent_reference\":\"1111111\",\n" +
                "\"Respondent\":\"\",\n" +
                "\"resp_others\":\"\",\n" +
                "\"resp_address\":\"\",\n" +
                "\"Hearing_date\":\"25 November 2019\",\n" +
                "\"Hearing_date_time\":\"25 November 2019 12:11\",\n" +
                "\"Hearing_venue\":\"Manchester\",\n" +
                "\"Hearing_duration\":\"2 hours\",\n" +
                "\"t_Scot_24\":\"true\",\n" +
                "\"Court_addressLine1\":\"Eagle Building,\",\n" +
                "\"Court_addressLine2\":\"215 Bothwell Street,\",\n" +
                "\"Court_addressLine3\":\"\",\n" +
                "\"Court_town\":\"Glasgow,\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"G2 7TS\",\n" +
                "\"Court_telephone\":\"03577123270\",\n" +
                "\"Court_fax\":\"07127126570\",\n" +
                "\"Court_DX\":\"1234567\",\n" +
                "\"Court_Email\":\"GlasgowOfficeET@hmcts.gov.uk\",\n" +
                "\"i_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"i_enhmcts1\":\"[userImage:enhmcts.png]\",\n" +
                "\"i_enhmcts2\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot24_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot24_schmcts1\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot24_schmcts2\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, Helper.buildDocumentContent(caseDetailsScot2.getCaseData(), "", userDetails).toString());
    }

    @Test
    public void buildDocumentContentScot3() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-SCO-ENG-00044.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"Mr A J Rodriguez\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"claimant_addressLine1\":\"34\",\n" +
                "\"claimant_addressLine2\":\"Low Street\",\n" +
                "\"claimant_addressLine3\":\"\",\n" +
                "\"claimant_town\":\"Manchester\",\n" +
                "\"claimant_county\":\"Lancashire\",\n" +
                "\"claimant_postCode\":\"M3 6gw\",\n" +
                "\"respondent_full_name\":\"Antonio Vazquez\",\n" +
                "\"respondent_addressLine1\":\"11 Small Street\",\n" +
                "\"respondent_addressLine2\":\"22 House\",\n" +
                "\"respondent_addressLine3\":\"\",\n" +
                "\"respondent_town\":\"Manchester\",\n" +
                "\"respondent_county\":\"North West\",\n" +
                "\"respondent_postCode\":\"M12 42R\",\n" +
                "\"resp_others\":\"\",\n" +
                "\"resp_address\":\"11 Small Street, 22 House, Manchester, North West, M12 42R, UK\",\n" +
                "\"Respondent\":\"Antonio Vazquez\",\n" +
                "\"Hearing_date\":\"\",\n" +
                "\"Hearing_date_time\":\"\",\n" +
                "\"Hearing_venue\":\"\",\n" +
                "\"Hearing_duration\":\"\",\n" +
                "\"t_Scot_34\":\"true\",\n" +
                "\"Court_addressLine1\":\"Eagle Building,\",\n" +
                "\"Court_addressLine2\":\"215 Bothwell Street,\",\n" +
                "\"Court_addressLine3\":\"\",\n" +
                "\"Court_town\":\"Glasgow,\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"G2 7TS\",\n" +
                "\"Court_telephone\":\"03577123270\",\n" +
                "\"Court_fax\":\"07127126570\",\n" +
                "\"Court_DX\":\"1234567\",\n" +
                "\"Court_Email\":\"GlasgowOfficeET@hmcts.gov.uk\",\n" +
                "\"i_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"i_enhmcts1\":\"[userImage:enhmcts.png]\",\n" +
                "\"i_enhmcts2\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot34_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot34_schmcts1\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot34_schmcts2\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, Helper.buildDocumentContent(caseDetailsScot3.getCaseData(), "", userDetails).toString());
    }

    @Test
    public void buildScotDocumentTemplates() {
        CaseDetails caseDetailsTemplates = new CaseDetails();
        CaseData caseData = new CaseData();
        CorrespondenceScotType correspondenceScotType = new CorrespondenceScotType();
        String topLevel = "Part_3_Scot";
        String part = "32";
        correspondenceScotType.setTopLevelScotDocuments(topLevel);
        correspondenceScotType.setPart3ScotDocuments(part);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        caseDetailsTemplates.setCaseData(caseData);
        assertEquals(getJson(topLevel, part), Helper.buildDocumentContent(caseDetailsTemplates.getCaseData(), "", userDetails).toString());
        topLevel = "Part_4_Scot";
        part = "42";
        correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments(topLevel);
        correspondenceScotType.setPart4ScotDocuments(part);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        caseDetailsTemplates.setCaseData(caseData);
        assertEquals(getJson(topLevel, part), Helper.buildDocumentContent(caseDetailsTemplates.getCaseData(), "", userDetails).toString());
        topLevel = "Part_5_Scot";
        part = "52";
        correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments(topLevel);
        correspondenceScotType.setPart5ScotDocuments(part);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        caseDetailsTemplates.setCaseData(caseData);
        assertEquals(getJson(topLevel, part), Helper.buildDocumentContent(caseDetailsTemplates.getCaseData(), "", userDetails).toString());
        topLevel = "Part_6_Scot";
        part = "62";
        correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments(topLevel);
        correspondenceScotType.setPart6ScotDocuments(part);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        caseDetailsTemplates.setCaseData(caseData);
        assertEquals(getJson(topLevel, part), Helper.buildDocumentContent(caseDetailsTemplates.getCaseData(), "", userDetails).toString());
        topLevel = "Part_7_Scot";
        part = "72";
        correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments(topLevel);
        correspondenceScotType.setPart7ScotDocuments(part);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        caseDetailsTemplates.setCaseData(caseData);
        assertEquals(getJson(topLevel, part), Helper.buildDocumentContent(caseDetailsTemplates.getCaseData(), "", userDetails).toString());
        topLevel = "Part_15_Scot";
        part = "152";
        correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments(topLevel);
        correspondenceScotType.setPart15ScotDocuments(part);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        caseDetailsTemplates.setCaseData(caseData);
        assertEquals(getJson(topLevel, part), Helper.buildDocumentContent(caseDetailsTemplates.getCaseData(), "", userDetails).toString());
        topLevel = "Part_16_Scot";
        part = "162";
        correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments(topLevel);
        correspondenceScotType.setPart16ScotDocuments(part);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        caseDetailsTemplates.setCaseData(caseData);
        assertEquals(getJson(topLevel, part), Helper.buildDocumentContent(caseDetailsTemplates.getCaseData(), "", userDetails).toString());
    }

    @Test
    public void buildDocumentTemplates() {
        CaseDetails caseDetailsTemplates = new CaseDetails();
        CaseData caseData = new CaseData();
        CorrespondenceType correspondenceType = new CorrespondenceType();
        String topLevel = "Part_18";
        String part = "18A";
        correspondenceType.setTopLevelDocuments(topLevel);
        correspondenceType.setPart18Documents(part);
        caseData.setCorrespondenceType(correspondenceType);
        caseDetailsTemplates.setCaseData(caseData);
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"Part_18.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"respondent_full_name\":\"\",\n" +
                "\"Respondent\":\"\",\n" +
                "\"resp_others\":\"\",\n" +
                "\"resp_address\":\"\",\n" +
                "\"Hearing_date\":\"\",\n" +
                "\"Hearing_date_time\":\"\",\n" +
                "\"Hearing_venue\":\"\",\n" +
                "\"Hearing_duration\":\"\",\n" +
                "\"t18A\":\"true\",\n" +
                "\"Court_telephone\":\"\",\n" +
                "\"Court_fax\":\"\",\n" +
                "\"Court_DX\":\"\",\n" +
                "\"Court_Email\":\"\",\n" +
                "\"i18A_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"i18A_enhmcts1\":\"[userImage:enhmcts.png]\",\n" +
                "\"i18A_enhmcts2\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts1\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot_schmcts2\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"\",\n" +
                "}\n" +
                "}\n";
        assertEquals(result, Helper.buildDocumentContent(caseDetailsTemplates.getCaseData(), "", userDetails).toString());
    }

    private String getJson(String topLevel, String part) {
        return "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"" + topLevel + ".docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"respondent_full_name\":\"\",\n" +
                "\"Respondent\":\"\",\n" +
                "\"resp_others\":\"\",\n" +
                "\"resp_address\":\"\",\n" +
                "\"Hearing_date\":\"\",\n" +
                "\"Hearing_date_time\":\"\",\n" +
                "\"Hearing_venue\":\"\",\n" +
                "\"Hearing_duration\":\"\",\n" +
                "\"t_Scot_" + part + "\":\"true\",\n" +
                "\"Court_telephone\":\"\",\n" +
                "\"Court_fax\":\"\",\n" +
                "\"Court_DX\":\"\",\n" +
                "\"Court_Email\":\"\",\n" +
                "\"i_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"i_enhmcts1\":\"[userImage:enhmcts.png]\",\n" +
                "\"i_enhmcts2\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot"+ part +"_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot"+ part +"_schmcts1\":\"[userImage:schmcts.png]\",\n" +
                "\"iScot"+ part +"_schmcts2\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"\",\n" +
                "}\n" +
                "}\n";
    }

    @Test
    public void getDocumentName() {
        String expected = "EM-TRB-EGW-ENG-00029_4.2";
        assertEquals(expected, Helper.getDocumentName(caseDetails4.getCaseData()));
    }
}