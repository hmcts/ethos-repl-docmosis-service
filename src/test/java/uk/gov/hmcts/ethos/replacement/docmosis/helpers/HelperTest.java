package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;

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
                "\"templateName\":\"Part_1.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_rep_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_email_address\":\"example@gmail.com\",\n" +
                "\"claimant_rep_email_address\":\"example@gmail.com\",\n" +
                "\"representative_reference\":\"1111111\",\n" +
                "\"claimant_rep_reference\":\"1111111\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Respondent\":\"string\",\n" +
                "\"Respondent_name\":\"string\",\n" +
                "\"respondent_full_name\":\"string\",\n" +
                "\"respondent_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez, Juan Garcia\",\n" +
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
                "\"Court_Address\":\"13th floor, Centre City Tower, 5-7 Hill Street, Manchester, M5 4UU\",\n" +
                "\"Court_Telephone\":\"0121 600 7780\",\n" +
                "\"Court_Fax\":\"01264 347 999\",\n" +
                "\"Court_DX\":\"123456789\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456789\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails1, "").toString(), result);
    }

    @Test
    public void buildDocumentContent2() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"Part_2.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_addressUK\":\"34, Low Street, Manchester, Lancashire, M3 6gw, UK\",\n" +
                "\"claimant_email_address\":\"anton@gmail.com\",\n" +
                "\"claimant_full_name\":\"Mr A Rodriguez\",\n" +
                "\"Claimant_name\":\"Mr A Rodriguez\",\n" +
                "\"Claimant\":\"Mr A Rodriguez\",\n" +
                "\"Respondent\":\"RepresentativeNameRespondent\",\n" +
                "\"Respondent_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_full_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_representative\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_rep_full_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"respondent_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"respondent_reference\":\"1111111\",\n" +
                "\"respondent_rep_reference\":\"1111111\",\n" +
                "\"respondent_email_address\":\"example@gmail.com\",\n" +
                "\"respondent_rep_email_address\":\"example@gmail.com\",\n" +
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
                "\"Court_Address\":\"13th floor, Centre City Tower, 5-7 Hill Street, Manchester, M5 4UU\",\n" +
                "\"Court_Telephone\":\"0121 600 7780\",\n" +
                "\"Court_Fax\":\"01264 347 999\",\n" +
                "\"Court_DX\":\"123456789\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456789\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails2, "").toString(), result);
    }

    @Test
    public void buildDocumentContent3() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"Part_3.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_addressUK\":\"34, Low Street, Manchester, Lancashire, M3 6gw, UK\",\n" +
                "\"claimant_email_address\":\"anton@gmail.com\",\n" +
                "\"claimant_full_name\":\"Mr A J Rodriguez\",\n" +
                "\"Claimant_name\":\"Mr A J Rodriguez\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"Respondent\":\"string\",\n" +
                "\"Respondent_name\":\"string\",\n" +
                "\"respondent_full_name\":\"string\",\n" +
                "\"respondent_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez\",\n" +
                "\"t3_2\":\"true\",\n" +
                "\"Court_Address\":\"13th floor, Centre City Tower, 5-7 Hill Street, Manchester, M5 4UU\",\n" +
                "\"Court_Telephone\":\"0121 600 7780\",\n" +
                "\"Court_Fax\":\"01264 347 999\",\n" +
                "\"Court_DX\":\"123456789\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456789\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails3, "").toString(), result);
    }

    @Test
    public void buildDocumentContent4() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"Part_4.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_rep_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_email_address\":\"example@gmail.com\",\n" +
                "\"claimant_rep_email_address\":\"example@gmail.com\",\n" +
                "\"representative_reference\":\"1111111\",\n" +
                "\"claimant_rep_reference\":\"1111111\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Respondent\":\"string\",\n" +
                "\"Respondent_name\":\"string\",\n" +
                "\"respondent_full_name\":\"string\",\n" +
                "\"respondent_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez\",\n" +
                "\"t4_2\":\"true\",\n" +
                "\"Court_Address\":\"13th floor, Centre City Tower, 5-7 Hill Street, Manchester, M5 4UU\",\n" +
                "\"Court_Telephone\":\"0121 600 7780\",\n" +
                "\"Court_Fax\":\"01264 347 999\",\n" +
                "\"Court_DX\":\"123456789\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456789\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails4, "").toString(), result);
    }

    @Test
    public void buildDocumentContent5() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"Part_5.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_rep_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_email_address\":\"example@gmail.com\",\n" +
                "\"claimant_rep_email_address\":\"example@gmail.com\",\n" +
                "\"representative_reference\":\"1111111\",\n" +
                "\"claimant_rep_reference\":\"1111111\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Respondent\":\"RepresentativeNameRespondent\",\n" +
                "\"Respondent_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_full_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_representative\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_rep_full_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"respondent_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"respondent_reference\":\"1111111\",\n" +
                "\"respondent_rep_reference\":\"1111111\",\n" +
                "\"respondent_email_address\":\"example@gmail.com\",\n" +
                "\"respondent_rep_email_address\":\"example@gmail.com\",\n" +
                "\"resp_others\":\"Antonio Vazquez\",\n" +
                "\"t5_2\":\"true\",\n" +
                "\"Court_Address\":\"13th floor, Centre City Tower, 5-7 Hill Street, Manchester, M5 4UU\",\n" +
                "\"Court_Telephone\":\"0121 600 7780\",\n" +
                "\"Court_Fax\":\"01264 347 999\",\n" +
                "\"Court_DX\":\"123456789\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456789\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails5, "").toString(), result);
    }

    @Test
    public void buildDocumentContent6() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"Part_6.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_rep_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_email_address\":\"example@gmail.com\",\n" +
                "\"claimant_rep_email_address\":\"example@gmail.com\",\n" +
                "\"representative_reference\":\"1111111\",\n" +
                "\"claimant_rep_reference\":\"1111111\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Respondent\":\"RespondentName\",\n" +
                "\"Respondent_name\":\"RespondentName\",\n" +
                "\"respondent_full_name\":\"RespondentName\",\n" +
                "\"respondent_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez\",\n" +
                "\"t6_2\":\"true\",\n" +
                "\"Court_Address\":\"13th floor, Centre City Tower, 5-7 Hill Street, Manchester, M5 4UU\",\n" +
                "\"Court_Telephone\":\"0121 600 7780\",\n" +
                "\"Court_Fax\":\"01264 347 999\",\n" +
                "\"Court_DX\":\"123456789\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456789\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails6, "").toString(), result);
    }

    @Test
    public void buildDocumentContent7() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"Part_7.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_rep_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_email_address\":\"example@gmail.com\",\n" +
                "\"claimant_rep_email_address\":\"example@gmail.com\",\n" +
                "\"representative_reference\":\"1111111\",\n" +
                "\"claimant_rep_reference\":\"1111111\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Respondent\":\"Antonio Rodriguez\",\n" +
                "\"Respondent_name\":\"Antonio Rodriguez\",\n" +
                "\"respondent_full_name\":\"Antonio Rodriguez\",\n" +
                "\"respondent_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez\",\n" +
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
                "\"Court_Address\":\"13th floor, Centre City Tower, 5-7 Hill Street, Manchester, M5 4UU\",\n" +
                "\"Court_Telephone\":\"0121 600 7780\",\n" +
                "\"Court_Fax\":\"01264 347 999\",\n" +
                "\"Court_DX\":\"123456789\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456789\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails7, "").toString(), result);
    }

    @Test
    public void buildDocumentContent8() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"Part_8.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_rep_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_email_address\":\"example@gmail.com\",\n" +
                "\"claimant_rep_email_address\":\"example@gmail.com\",\n" +
                "\"representative_reference\":\"1111111\",\n" +
                "\"claimant_rep_reference\":\"1111111\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Respondent\":\"Joan Zamorano\",\n" +
                "\"Respondent_name\":\"Joan Zamorano\",\n" +
                "\"respondent_full_name\":\"Joan Zamorano\",\n" +
                "\"respondent_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez, Mikey McCollier\",\n" +
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
                "\"t8_2\":\"true\",\n" +
                "\"Court_Address\":\"13th floor, Centre City Tower, 5-7 Hill Street, Manchester, M5 4UU\",\n" +
                "\"Court_Telephone\":\"0121 600 7780\",\n" +
                "\"Court_Fax\":\"01264 347 999\",\n" +
                "\"Court_DX\":\"123456789\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456789\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails8, "").toString(), result);
    }

    @Test
    public void buildDocumentContent9() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"Part_9.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_rep_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_email_address\":\"example@gmail.com\",\n" +
                "\"claimant_rep_email_address\":\"example@gmail.com\",\n" +
                "\"representative_reference\":\"1111111\",\n" +
                "\"claimant_rep_reference\":\"1111111\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Respondent\":\"Raul Gonzalez\",\n" +
                "\"Respondent_name\":\"Raul Gonzalez\",\n" +
                "\"respondent_full_name\":\"Raul Gonzalez\",\n" +
                "\"respondent_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez\",\n" +
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
                "\"Court_Address\":\"13th floor, Centre City Tower, 5-7 Hill Street, Manchester, M5 4UU\",\n" +
                "\"Court_Telephone\":\"0121 600 7780\",\n" +
                "\"Court_Fax\":\"01264 347 999\",\n" +
                "\"Court_DX\":\"123456789\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456789\",\n" +
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
                "\"Court_Address\":\"13th floor, Centre City Tower, 5-7 Hill Street, Manchester, M5 4UU\",\n" +
                "\"Court_Telephone\":\"0121 600 7780\",\n" +
                "\"Court_Fax\":\"01264 347 999\",\n" +
                "\"Court_DX\":\"123456789\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
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
                "\"templateName\":\"Part_1_Scot.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_rep_full_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant_name\":\"RepresentativeNameClaimant\",\n" +
                "\"Claimant\":\"RepresentativeNameClaimant\",\n" +
                "\"claimant_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"claimant_email_address\":\"example@gmail.com\",\n" +
                "\"claimant_rep_email_address\":\"example@gmail.com\",\n" +
                "\"representative_reference\":\"1111111\",\n" +
                "\"claimant_rep_reference\":\"1111111\",\n" +
                "\"claimant_reference\":\"1111111\",\n" +
                "\"Respondent\":\"string\",\n" +
                "\"Respondent_name\":\"string\",\n" +
                "\"respondent_full_name\":\"string\",\n" +
                "\"respondent_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez, Juan Garcia\",\n" +
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
                "\"Court_Address\":\"13th floor, Centre City Tower, 5-7 Hill Street, Manchester, M5 4UU\",\n" +
                "\"Court_Telephone\":\"0121 600 7780\",\n" +
                "\"Court_Fax\":\"01264 347 999\",\n" +
                "\"Court_DX\":\"123456789\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456789\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetailsScot1, "").toString(), result);
    }

    @Test
    public void buildDocumentContentScot2() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"Part_2_Scot.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_addressUK\":\"34, Low Street, Manchester, Lancashire, M3 6gw, UK\",\n" +
                "\"claimant_email_address\":\"anton@gmail.com\",\n" +
                "\"claimant_full_name\":\"Mr A Rodriguez\",\n" +
                "\"Claimant_name\":\"Mr A Rodriguez\",\n" +
                "\"Claimant\":\"Mr A Rodriguez\",\n" +
                "\"Respondent\":\"RepresentativeNameRespondent\",\n" +
                "\"Respondent_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_full_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_representative\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_rep_full_name\":\"RepresentativeNameRespondent\",\n" +
                "\"respondent_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"respondent_rep_addressUK\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"respondent_reference\":\"1111111\",\n" +
                "\"respondent_rep_reference\":\"1111111\",\n" +
                "\"respondent_email_address\":\"example@gmail.com\",\n" +
                "\"respondent_rep_email_address\":\"example@gmail.com\",\n" +
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
                "\"Court_Address\":\"13th floor, Centre City Tower, 5-7 Hill Street, Manchester, M5 4UU\",\n" +
                "\"Court_Telephone\":\"0121 600 7780\",\n" +
                "\"Court_Fax\":\"01264 347 999\",\n" +
                "\"Court_DX\":\"123456789\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456789\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetailsScot2, "").toString(), result);
    }

    @Test
    public void buildDocumentContentScot3() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"Part_3_Scot.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"claimant_addressUK\":\"34, Low Street, Manchester, Lancashire, M3 6gw, UK\",\n" +
                "\"claimant_email_address\":\"anton@gmail.com\",\n" +
                "\"claimant_full_name\":\"Mr A J Rodriguez\",\n" +
                "\"Claimant_name\":\"Mr A J Rodriguez\",\n" +
                "\"Claimant\":\"Mr A J Rodriguez\",\n" +
                "\"Respondent\":\"string\",\n" +
                "\"Respondent_name\":\"string\",\n" +
                "\"respondent_full_name\":\"string\",\n" +
                "\"respondent_addressUK\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez\",\n" +
                "\"t_Scot_34\":\"true\",\n" +
                "\"Court_Address\":\"13th floor, Centre City Tower, 5-7 Hill Street, Manchester, M5 4UU\",\n" +
                "\"Court_Telephone\":\"0121 600 7780\",\n" +
                "\"Court_Fax\":\"01264 347 999\",\n" +
                "\"Court_DX\":\"123456789\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"Clerk\":\"Juan Diego\",\n" +
                "\"TODAY_DATE\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"TodayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"Case_No\":\"123456789\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetailsScot3, "").toString(), result);
    }
}