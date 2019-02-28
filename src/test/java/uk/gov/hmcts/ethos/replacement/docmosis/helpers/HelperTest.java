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
    private CaseDetails caseDetailsEmpty;

    @Before
    public void setUp() throws Exception {
        caseDetails1 = generateCaseDetails("caseDetailsTest1.json");
        caseDetails2 = generateCaseDetails("caseDetailsTest2.json");

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
                "\"outputName\":\"myWelcome.docx\",\n" +
                "\"data\":{\n" +
                "\"add_name\":\"ClaimantRepresentative\",\n" +
                "\"add_add1\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"app_name\":\"ClaimantRepresentative\",\n" +
                "\"resp_name\":\"string\",\n" +
                "\"opp_name\":\"string\",\n" +
                "\"opp_add1\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"resp_others\":\"Antonio Vazquez\",\n" +
                "\"hearing_date\":\"Mon, 25 Nov 2019\",\n" +
                "\"hearing_time\":\"11:00 AM\",\n" +
                "\"hearing_venue\":\"Manchester\",\n" +
                "\"EstLengthOfHearing\":\"3\",\n" +
                "\"t1_2\":\"true\",\n" +
                "\"user_name\":\"Juan Diego\",\n" +
                "\"app_date\":\"Tue, 12 Mar 2019\",\n" +
                "\"curr_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"todayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"case_no_year\":\"123456789\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails1, "").toString(), result);
    }

    @Test
    public void buildDocumentContent2() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"Part_2.docx\",\n" +
                "\"outputName\":\"myWelcome.docx\",\n" +
                "\"data\":{\n" +
                "\"add_name\":\"Mr Anton Rodriguez\",\n" +
                "\"add_add1\":\"34, Low Street, Manchester, Lancashire, M3 6gw, UK\",\n" +
                "\"app_name\":\"Mr Anton Rodriguez\",\n" +
                "\"resp_name\":\"ClaimantRepresentative\",\n" +
                "\"opp_name\":\"ClaimantRepresentative\",\n" +
                "\"opp_add1\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"hearing_date\":\"Mon, 25 Nov 2019\",\n" +
                "\"hearing_time\":\"11:00 AM\",\n" +
                "\"hearing_venue\":\"Manchester\",\n" +
                "\"EstLengthOfHearing\":\"3\",\n" +
                "\"t2_2A\":\"true\",\n" +
                "\"user_name\":\"Juan Diego\",\n" +
                "\"app_date\":\"Tue, 12 Mar 2019\",\n" +
                "\"curr_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"todayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"case_no_year\":\"123456789\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails2, "").toString(), result);
    }

    @Test
    public void buildDocumentWithNotContent() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\".docx\",\n" +
                "\"outputName\":\"myWelcome.docx\",\n" +
                "\"data\":{\n" +
                "\"user_name\":\"null\",\n" +
                "\"app_date\":\"\",\n" +
                "\"curr_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\",\n" +
                "\"todayPlus28Days\":\"" + Helper.formatCurrentDatePlusDays(LocalDate.now(), 28) + "\",\n" +
                "\"case_no_year\":\"null\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetailsEmpty, "").toString(), result);
    }
}