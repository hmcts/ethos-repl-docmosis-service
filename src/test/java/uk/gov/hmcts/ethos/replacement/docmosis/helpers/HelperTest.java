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
    private CaseDetails caseDetailsEmpty;

    @Before
    public void setUp() throws Exception {
        caseDetails1 = generateCaseDetails("caseDetailsTest1.json");
        caseDetails2 = generateCaseDetails("caseDetailsTest2.json");
        caseDetails3 = generateCaseDetails("caseDetailsTest3.json");
        caseDetails4 = generateCaseDetails("caseDetailsTest4.json");
        caseDetails5 = generateCaseDetails("caseDetailsTest5.json");

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
                "\"hearing_time\":\"11:00 AM\",\n" +
                "\"hearing_venue\":\"Manchester\",\n" +
                "\"EstLengthOfHearing\":\"2 days\",\n" +
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
                "\"hearing_time\":\"11:00 AM\",\n" +
                "\"hearing_venue\":\"Manchester\",\n" +
                "\"EstLengthOfHearing\":\"2 hours\",\n" +
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
                "\"respondent_reference\":\"3333333333\",\n" +
                "\"respondent_rep_reference\":\"3333333333\",\n" +
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
}