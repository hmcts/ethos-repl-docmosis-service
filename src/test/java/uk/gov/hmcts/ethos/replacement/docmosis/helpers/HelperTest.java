package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.CorrespondenceScotType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
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
    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }

    @Test
    public void buildDocumentContent1() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00026.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_rep_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"representative_reference\":\"1111111\",\n" +
                "\"claimant_rep_reference\":\"1111111\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"Respondent_name\":\"Francisco\",\n" +
                "\"respondent_full_name\":\"Francisco\",\n" +
                "\"respondent_rep_full_name\":\"Francisco\",\n" +
                "\"respondent_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"respondent_rep_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez, Juan Garcia\",\n" +
                "\"Respondent\":\"Francisco\",\n" +
                "\"hearing_date\":\"Mon, 25 Nov 2019\",\n" +
                "\"Hearing_Date\":\"Mon, 25 Nov 2019\",\n" +
                "\"Hearing_date_time\":\"Mon, 25 Nov 2019 10:11:00\",\n" +
                "\"Hearing_Date_Time\":\"Mon, 25 Nov 2019 10:11:00\",\n" +
                "\"hearing_date_time\":\"Mon, 25 Nov 2019 10:11:00\",\n" +
                "\"Hearing_venue\":\"Manchester\",\n" +
                "\"hearing_address\":\"Manchester\",\n" +
                "\"Hearing_Address\":\"Manchester\",\n" +
                "\"EstLengthOfHearing\":\"2 days\",\n" +
                "\"Hearing_Duration\":\"2 days\",\n" +
                "\"hearing_duration\":\"2 days\",\n" +
                "\"hearing_length\":\"2 days\",\n" +
                "\"t1_2\":\"true\",\n" +
                "\"Court_Address\":\"35 La Nava S3 6AD, Southampton\",\n" +
                "\"Court_Telephone\":\"03577131270\",\n" +
                "\"Court_Fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i1_2_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails1, "").toString(), result);
    }

    @Test
    public void buildDocumentContent2() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00027.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_addressUK\":\"34, Low Street, Manchester, Lancashire, M3 6gw, UK\",\n" +
                "\"claimant_rep_addressUK\":\"34, Low Street, Manchester, Lancashire, M3 6gw, UK\",\n" +
                "\"Claimant_name\":\"Mr A Rodriguez\",\n" +
                "\"claimant_full_name\":\"Mr A Rodriguez\",\n" +
                "\"claimant_rep_full_name\":\"Mr A Rodriguez\",\n" +
                "\"Claimant\":\"Mr A Rodriguez\",\n" +
                "\"Respondent_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_full_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_representative\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_rep_full_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"respondent_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"respondent_reference\":\"1111111\",\n" +
                "\"respondent_rep_reference\":\"1111111\",\n" +
                "\"Respondent\":\"Francisco\",\n" +
                "\"hearing_date\":\"Mon, 25 Nov 2019\",\n" +
                "\"Hearing_Date\":\"Mon, 25 Nov 2019\",\n" +
                "\"Hearing_date_time\":\"Mon, 25 Nov 2019 12:11:00\",\n" +
                "\"Hearing_Date_Time\":\"Mon, 25 Nov 2019 12:11:00\",\n" +
                "\"hearing_date_time\":\"Mon, 25 Nov 2019 12:11:00\",\n" +
                "\"Hearing_venue\":\"Manchester\",\n" +
                "\"hearing_address\":\"Manchester\",\n" +
                "\"Hearing_Address\":\"Manchester\",\n" +
                "\"EstLengthOfHearing\":\"2 hours\",\n" +
                "\"Hearing_Duration\":\"2 hours\",\n" +
                "\"hearing_duration\":\"2 hours\",\n" +
                "\"hearing_length\":\"2 hours\",\n" +
                "\"t2_2A\":\"true\",\n" +
                "\"Court_Address\":\"35 La Nava S3 6AD, Southampton\",\n" +
                "\"Court_Telephone\":\"03577131270\",\n" +
                "\"Court_Fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i2_2A_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails2, "").toString(), result);
    }

    @Test
    public void buildDocumentContent3() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00028.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_addressUK\":\"34, Low Street, Manchester, Lancashire, M3 6gw, UK\",\n" +
                "\"claimant_rep_addressUK\":\"34, Low Street, Manchester, Lancashire, M3 6gw, UK\",\n" +
                "\"Claimant_name\":\"Mr A J Rodriguez\",\n" +
                "\"claimant_full_name\":\"Mr A J Rodriguez\",\n" +
                "\"claimant_rep_full_name\":\"Mr A J Rodriguez\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"Respondent_name\":\"Francisco\",\n" +
                "\"respondent_full_name\":\"Francisco\",\n" +
                "\"respondent_rep_full_name\":\"Francisco\",\n" +
                "\"respondent_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"respondent_rep_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez\",\n" +
                "\"Respondent\":\"Francisco\",\n" +
                "\"t3_2\":\"true\",\n" +
                "\"Court_Address\":\"35 La Nava S3 6AD, Southampton\",\n" +
                "\"Court_Telephone\":\"03577131270\",\n" +
                "\"Court_Fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i3_2_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails3, "").toString(), result);
    }

    @Test
    public void buildDocumentContent4() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00029.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_rep_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"representative_reference\":\"1111111\",\n" +
                "\"claimant_rep_reference\":\"1111111\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"Respondent_name\":\"Francisco\",\n" +
                "\"respondent_full_name\":\"Francisco\",\n" +
                "\"respondent_rep_full_name\":\"Francisco\",\n" +
                "\"respondent_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"respondent_rep_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez\",\n" +
                "\"Respondent\":\"Francisco\",\n" +
                "\"t4_2\":\"true\",\n" +
                "\"Court_Address\":\"35 La Nava S3 6AD, Southampton\",\n" +
                "\"Court_Telephone\":\"03577131270\",\n" +
                "\"Court_Fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i4_2_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails4, "").toString(), result);
    }

    @Test
    public void buildDocumentContent5() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00030.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_rep_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"representative_reference\":\"1111111\",\n" +
                "\"claimant_rep_reference\":\"1111111\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"Respondent_name\":\"RepresentativeNameRespondent1\",\n" +
                "\"respondent_full_name\":\"RepresentativeNameRespondent1\",\n" +
                "\"respondent_representative\":\"RepresentativeNameRespondent1\",\n" +
                "\"respondent_rep_full_name\":\"RepresentativeNameRespondent1\",\n" +
                "\"respondent_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"respondent_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"respondent_reference\":\"3333333333\",\n" +
                "\"respondent_rep_reference\":\"3333333333\",\n" +
                "\"resp_others\":\"Antonio Vazquez\",\n" +
                "\"Respondent\":\"Francisco\",\n" +
                "\"t5_2\":\"true\",\n" +
                "\"Court_Address\":\"35 La Nava S3 6AD, Southampton\",\n" +
                "\"Court_Telephone\":\"03577131270\",\n" +
                "\"Court_Fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i5_2_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails5, "").toString(), result);
    }

    @Test
    public void buildDocumentContent6() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00031.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_rep_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"representative_reference\":\"1111111\",\n" +
                "\"claimant_rep_reference\":\"1111111\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"Respondent_name\":\"RepresentativeNameRespondent1\",\n" +
                "\"respondent_full_name\":\"RepresentativeNameRespondent1\",\n" +
                "\"respondent_representative\":\"RepresentativeNameRespondent1\",\n" +
                "\"respondent_rep_full_name\":\"RepresentativeNameRespondent1\",\n" +
                "\"respondent_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"respondent_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"respondent_reference\":\"3333333333\",\n" +
                "\"respondent_rep_reference\":\"3333333333\",\n" +
                "\"resp_others\":\"Antonio Vazquez\",\n" +
                "\"Respondent\":\"RespondentName\",\n" +
                "\"t6_2\":\"true\",\n" +
                "\"Court_Address\":\"35 La Nava S3 6AD, Southampton\",\n" +
                "\"Court_Telephone\":\"03577131270\",\n" +
                "\"Court_Fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i6_2_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails6, "").toString(), result);
    }

    @Test
    public void buildDocumentContent7() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00032.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_rep_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"representative_reference\":\"1111111\",\n" +
                "\"claimant_rep_reference\":\"1111111\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"Respondent_name\":\"RepresentativeNameRespondent1\",\n" +
                "\"respondent_full_name\":\"RepresentativeNameRespondent1\",\n" +
                "\"respondent_representative\":\"RepresentativeNameRespondent1\",\n" +
                "\"respondent_rep_full_name\":\"RepresentativeNameRespondent1\",\n" +
                "\"respondent_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"respondent_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"respondent_reference\":\"3333333333\",\n" +
                "\"respondent_rep_reference\":\"3333333333\",\n" +
                "\"resp_others\":\"Antonio Vazquez\",\n" +
                "\"Respondent\":\"Antonio Rodriguez\",\n" +
                "\"hearing_date\":\"\",\n" +
                "\"Hearing_Date\":\"\",\n" +
                "\"Hearing_date_time\":\"\",\n" +
                "\"Hearing_Date_Time\":\"\",\n" +
                "\"hearing_date_time\":\"\",\n" +
                "\"Hearing_venue\":\"Manchester\",\n" +
                "\"hearing_address\":\"Manchester\",\n" +
                "\"Hearing_Address\":\"Manchester\",\n" +
                "\"EstLengthOfHearing\":\"2 hours\",\n" +
                "\"Hearing_Duration\":\"2 hours\",\n" +
                "\"hearing_duration\":\"2 hours\",\n" +
                "\"hearing_length\":\"2 hours\",\n" +
                "\"t7_2\":\"true\",\n" +
                "\"Court_Address\":\"35 La Nava S3 6AD, Southampton\",\n" +
                "\"Court_Telephone\":\"03577131270\",\n" +
                "\"Court_Fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i7_2_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails7, "").toString(), result);
    }

    @Test
    public void buildDocumentContent8() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00033.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_rep_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"representative_reference\":\"1111111\",\n" +
                "\"claimant_rep_reference\":\"1111111\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"Respondent_name\":\"Joan Zamorano\",\n" +
                "\"respondent_full_name\":\"Joan Zamorano\",\n" +
                "\"respondent_rep_full_name\":\"Joan Zamorano\",\n" +
                "\"respondent_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"respondent_rep_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez, Mikey McCollier\",\n" +
                "\"Respondent\":\"Joan Zamorano\",\n" +
                "\"hearing_date\":\"\",\n" +
                "\"Hearing_Date\":\"\",\n" +
                "\"Hearing_date_time\":\"\",\n" +
                "\"Hearing_Date_Time\":\"\",\n" +
                "\"hearing_date_time\":\"\",\n" +
                "\"Hearing_venue\":\"Manchester\",\n" +
                "\"hearing_address\":\"Manchester\",\n" +
                "\"Hearing_Address\":\"Manchester\",\n" +
                "\"EstLengthOfHearing\":\"2 hours\",\n" +
                "\"Hearing_Duration\":\"2 hours\",\n" +
                "\"hearing_duration\":\"2 hours\",\n" +
                "\"hearing_length\":\"2 hours\",\n" +
                "\"t10_2\":\"true\",\n" +
                "\"Court_Address\":\"35 La Nava S3 6AD, Southampton\",\n" +
                "\"Court_Telephone\":\"03577131270\",\n" +
                "\"Court_Fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i10_2_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails8, "").toString(), result);
    }

    @Test
    public void buildDocumentContent9() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-EGW-ENG-00034.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_rep_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"representative_reference\":\"1111111\",\n" +
                "\"claimant_rep_reference\":\"1111111\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"Respondent_name\":\"Raul Gonzalez\",\n" +
                "\"respondent_full_name\":\"Raul Gonzalez\",\n" +
                "\"respondent_rep_full_name\":\"Raul Gonzalez\",\n" +
                "\"respondent_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"respondent_rep_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez\",\n" +
                "\"Respondent\":\"Raul Gonzalez\",\n" +
                "\"hearing_date\":\"\",\n" +
                "\"Hearing_Date\":\"\",\n" +
                "\"Hearing_date_time\":\"\",\n" +
                "\"Hearing_Date_Time\":\"\",\n" +
                "\"hearing_date_time\":\"\",\n" +
                "\"Hearing_venue\":\"Manchester\",\n" +
                "\"hearing_address\":\"Manchester\",\n" +
                "\"Hearing_Address\":\"Manchester\",\n" +
                "\"EstLengthOfHearing\":\"2 hours\",\n" +
                "\"Hearing_Duration\":\"2 hours\",\n" +
                "\"hearing_duration\":\"2 hours\",\n" +
                "\"hearing_length\":\"2 hours\",\n" +
                "\"t9_2\":\"true\",\n" +
                "\"Court_Address\":\"35 La Nava S3 6AD, Southampton\",\n" +
                "\"Court_Telephone\":\"03577131270\",\n" +
                "\"Court_Fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"i9_2_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails9, "").toString(), result);
    }

    @Test
    public void buildDocumentWithNotContent() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\".docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"Court_Address\":\"null\",\n" +
                "\"Court_Telephone\":\"null\",\n" +
                "\"Court_Fax\":\"null\",\n" +
                "\"Court_DX\":\"null\",\n" +
                "\"Court_Email\":\"null\",\n" +
                "\"i_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"null\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"null\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetailsEmpty, "").toString(), result);
    }

    @Test
    public void buildDocumentContentScot1() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-SCO-ENG-00042.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_rep_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"representative_reference\":\"1111111\",\n" +
                "\"claimant_rep_reference\":\"1111111\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"Respondent_name\":\"string\",\n" +
                "\"respondent_full_name\":\"string\",\n" +
                "\"respondent_rep_full_name\":\"string\",\n" +
                "\"respondent_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"respondent_rep_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez, Juan Garcia\",\n" +
                "\"Respondent\":\"string\",\n" +
                "\"hearing_date\":\"Mon, 25 Nov 2019\",\n" +
                "\"Hearing_Date\":\"Mon, 25 Nov 2019\",\n" +
                "\"Hearing_date_time\":\"Mon, 25 Nov 2019 10:11:00\",\n" +
                "\"Hearing_Date_Time\":\"Mon, 25 Nov 2019 10:11:00\",\n" +
                "\"hearing_date_time\":\"Mon, 25 Nov 2019 10:11:00\",\n" +
                "\"Hearing_venue\":\"Manchester\",\n" +
                "\"hearing_address\":\"Manchester\",\n" +
                "\"Hearing_Address\":\"Manchester\",\n" +
                "\"EstLengthOfHearing\":\"2 days\",\n" +
                "\"Hearing_Duration\":\"2 days\",\n" +
                "\"hearing_duration\":\"2 days\",\n" +
                "\"hearing_length\":\"2 days\",\n" +
                "\"t_Scot_7_1\":\"true\",\n" +
                "\"Court_Address\":\"35 High Landing G3 6AD, Glasgow\",\n" +
                "\"Court_Telephone\":\"03577123270\",\n" +
                "\"Court_Fax\":\"07127126570\",\n" +
                "\"Court_DX\":\"1234567\",\n" +
                "\"Court_Email\":\"GlasgowOfficeET@hmcts.gov.uk\",\n" +
                "\"i_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot7_1_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetailsScot1, "").toString(), result);
    }

    @Test
    public void buildDocumentContentScot2() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-SCO-ENG-00043.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_addressUK\":\"34, Low Street, Manchester, Lancashire, M3 6gw, UK\",\n" +
                "\"claimant_rep_addressUK\":\"34, Low Street, Manchester, Lancashire, M3 6gw, UK\",\n" +
                "\"Claimant_name\":\"Mr A Rodriguez\",\n" +
                "\"claimant_full_name\":\"Mr A Rodriguez\",\n" +
                "\"claimant_rep_full_name\":\"Mr A Rodriguez\",\n" +
                "\"Claimant\":\"Mr A Rodriguez\",\n" +
                "\"Respondent_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_full_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_representative\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_rep_full_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"respondent_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"respondent_reference\":\"1111111\",\n" +
                "\"respondent_rep_reference\":\"1111111\",\n" +
                "\"Respondent\":\"string\",\n" +
                "\"hearing_date\":\"Mon, 25 Nov 2019\",\n" +
                "\"Hearing_Date\":\"Mon, 25 Nov 2019\",\n" +
                "\"Hearing_date_time\":\"Mon, 25 Nov 2019 12:11:00\",\n" +
                "\"Hearing_Date_Time\":\"Mon, 25 Nov 2019 12:11:00\",\n" +
                "\"hearing_date_time\":\"Mon, 25 Nov 2019 12:11:00\",\n" +
                "\"Hearing_venue\":\"Manchester\",\n" +
                "\"hearing_address\":\"Manchester\",\n" +
                "\"Hearing_Address\":\"Manchester\",\n" +
                "\"EstLengthOfHearing\":\"2 hours\",\n" +
                "\"Hearing_Duration\":\"2 hours\",\n" +
                "\"hearing_duration\":\"2 hours\",\n" +
                "\"hearing_length\":\"2 hours\",\n" +
                "\"t_Scot_24\":\"true\",\n" +
                "\"Court_Address\":\"35 High Landing G3 6AD, Glasgow\",\n" +
                "\"Court_Telephone\":\"03577123270\",\n" +
                "\"Court_Fax\":\"07127126570\",\n" +
                "\"Court_DX\":\"1234567\",\n" +
                "\"Court_Email\":\"GlasgowOfficeET@hmcts.gov.uk\",\n" +
                "\"i_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot24_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetailsScot2, "").toString(), result);
    }

    @Test
    public void buildDocumentContentScot3() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-SCO-ENG-00044.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_addressUK\":\"34, Low Street, Manchester, Lancashire, M3 6gw, UK\",\n" +
                "\"claimant_rep_addressUK\":\"34, Low Street, Manchester, Lancashire, M3 6gw, UK\",\n" +
                "\"Claimant_name\":\"Mr A J Rodriguez\",\n" +
                "\"claimant_full_name\":\"Mr A J Rodriguez\",\n" +
                "\"claimant_rep_full_name\":\"Mr A J Rodriguez\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"Respondent_name\":\"string\",\n" +
                "\"respondent_full_name\":\"string\",\n" +
                "\"respondent_rep_full_name\":\"string\",\n" +
                "\"respondent_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"respondent_rep_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez\",\n" +
                "\"Respondent\":\"string\",\n" +
                "\"t_Scot_34\":\"true\",\n" +
                "\"Court_Address\":\"35 High Landing G3 6AD, Glasgow\",\n" +
                "\"Court_Telephone\":\"03577123270\",\n" +
                "\"Court_Fax\":\"07127126570\",\n" +
                "\"Court_DX\":\"1234567\",\n" +
                "\"Court_Email\":\"GlasgowOfficeET@hmcts.gov.uk\",\n" +
                "\"i_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot34_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetailsScot3, "").toString(), result);
    }

    @Test
    public void buildDocumentTemplates() {
        CaseDetails caseDetailsTemplates = new CaseDetails();
        CaseData caseData = new CaseData();
        CorrespondenceScotType correspondenceScotType = new CorrespondenceScotType();
        String topLevel = "Part_3_Scot";
        String part = "32";
        correspondenceScotType.setTopLevelScotDocuments(topLevel);
        correspondenceScotType.setPart3ScotDocuments(part);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        caseDetailsTemplates.setCaseData(caseData);
        assertEquals(Helper.buildDocumentContent(caseDetailsTemplates, "").toString(), getJson(topLevel, part));
        topLevel = "Part_4_Scot";
        part = "42";
        correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments(topLevel);
        correspondenceScotType.setPart4ScotDocuments(part);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        caseDetailsTemplates.setCaseData(caseData);
        assertEquals(Helper.buildDocumentContent(caseDetailsTemplates, "").toString(), getJson(topLevel, part));
        topLevel = "Part_5_Scot";
        part = "52";
        correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments(topLevel);
        correspondenceScotType.setPart5ScotDocuments(part);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        caseDetailsTemplates.setCaseData(caseData);
        assertEquals(Helper.buildDocumentContent(caseDetailsTemplates, "").toString(), getJson(topLevel, part));
        topLevel = "Part_6_Scot";
        part = "62";
        correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments(topLevel);
        correspondenceScotType.setPart6ScotDocuments(part);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        caseDetailsTemplates.setCaseData(caseData);
        assertEquals(Helper.buildDocumentContent(caseDetailsTemplates, "").toString(), getJson(topLevel, part));
        topLevel = "Part_7_Scot";
        part = "72";
        correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments(topLevel);
        correspondenceScotType.setPart7ScotDocuments(part);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        caseDetailsTemplates.setCaseData(caseData);
        assertEquals(Helper.buildDocumentContent(caseDetailsTemplates, "").toString(), getJson(topLevel, part));
        topLevel = "Part_15_Scot";
        part = "152";
        correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments(topLevel);
        correspondenceScotType.setPart15ScotDocuments(part);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        caseDetailsTemplates.setCaseData(caseData);
        assertEquals(Helper.buildDocumentContent(caseDetailsTemplates, "").toString(), getJson(topLevel, part));
        topLevel = "Part_16_Scot";
        part = "162";
        correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments(topLevel);
        correspondenceScotType.setPart16ScotDocuments(part);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        caseDetailsTemplates.setCaseData(caseData);
        assertEquals(Helper.buildDocumentContent(caseDetailsTemplates, "").toString(), getJson(topLevel, part));
    }

    private String getJson(String topLevel, String part) {
        return "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"" + topLevel + ".docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"t_Scot_" + part + "\":\"true\",\n" +
                "\"Court_Address\":\"null\",\n" +
                "\"Court_Telephone\":\"null\",\n" +
                "\"Court_Fax\":\"null\",\n" +
                "\"Court_DX\":\"null\",\n" +
                "\"Court_Email\":\"null\",\n" +
                "\"i_enhmcts\":\"[userImage:enhmcts.png]\",\n" +
                "\"iScot"+ part +"_schmcts\":\"[userImage:schmcts.png]\",\n" +
                "\"Clerk\":\"null\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"null\",\n" +
                "}\n" +
                "}\n";
    }
}