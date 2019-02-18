package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.Assert.*;

public class HelperTest {

    private CaseDetails caseDetails;
    private CaseDetails caseDetailsEmpty;

    @Before
    public void setUp() throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("caseDetailsTest.json")).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        caseDetails = mapper.readValue(json, CaseDetails.class);

        CaseData caseData1 = new CaseData();
        caseDetailsEmpty = new CaseDetails();
        caseDetailsEmpty.setCaseData(caseData1);
    }

    @Test
    public void buildDocumentContent() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"template\",\n" +
                "\"outputName\":\"myWelcome.doc\",\n" +
                "\"data\":{\n" +
                "\"add_name\":\"ClaimantRepresentative\",\n" +
                "\"add_add1\":\"56 Block C, Ellesmere Street, Manchester, Lancashire, M3 KJR, UK\",\n" +
                "\"app_name\":\"ClaimantRepresentative\",\n" +
                "\"resp_name\":\"string\",\n" +
                "\"opp_name\":\"string\",\n" +
                "\"opp_add1\":\"54 Ellesmere Street, 62 Mere House, Manchester, North West, M15 4QR, UK\",\n" +
                "\"hearing_date\":\"Mon, 25 Nov 2019\",\n" +
                "\"hearing_time\":\"10:10 AM\",\n" +
                "\"EstLengthOfHearing\":\"3\",\n" +
                "\"user_name\":\"Juan Diego\",\n" +
                "\"curr_date\":\"Tue, 12 Mar 2019\",\n" +
                "\"todayPlus28Days\":\"Tue, 9 Apr 2019\",\n" +
                "\"case_no_year\":\"123456789\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetails, "template", "").toString(), result);
    }

    @Test
    public void buildDocumentWithNotContent() {
        String result = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"template\",\n" +
                "\"outputName\":\"myWelcome.doc\",\n" +
                "\"data\":{\n" +
                "\"user_name\":\"null\",\n" +
                "\"curr_date\":\"\",\n" +
                "\"todayPlus28Days\":\"\",\n" +
                "\"case_no_year\":\"null\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetailsEmpty, "template", "").toString(), result);
    }
}