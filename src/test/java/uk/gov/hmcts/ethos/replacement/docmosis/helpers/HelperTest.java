package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;

import static org.junit.Assert.*;

public class HelperTest {

    private CaseDetails caseDetails;
    private CaseDetails caseDetailsEmpty;

    @Before
    public void setUp() throws Exception {
        String json = "{"
                + " \"caseNote\" : \"1111\", "
                + " \"positionType\" : \"Single\", "
                + " \"representedType\": {\n" +
                    "\"if_represented\": \"Yes\",\n" +
                    "\"name_of_representative\": \"Batman & Robin\",\n" +
                    "\"name_of_organisation\": \"Batman & Robin Associates\",\n" +
                    "\"representative_phone_number\": \"0207 111 4477\",\n" +
                    "\"representative_fax_number\": \"0207 111 4478\",\n" +
                    "\"representative_dx_number\": \"123456 Gotham\",\n" +
                    "\"representative_email_address\": \"batman@gotham.com\",\n" +
                    "\"representative_reference\": \"PCODE 24\"},"
                + " \"userLocation\" : \"Bath\", "
                + " \"locationType\" : \"City\", "
                + " \"caseType\" : \"Single\", "
                + " \"feeGroupReference\" : \"1212\", "
                + " \"claimantType\": {\n" +
                    "\"claimant_title\": \"Mr \",\n" +
                    "\"claimant_first_name\": \"Sam \",\n" +
                    "\"claimant_initials\": \"S \",\n" +
                    "\"claimant_last_name\": \"Thornhill\",\n" +
                    "\"claimant_date_of_birth\": \"1990-02-25\",\n" +
                    "\"claimant_gender\": \"male\",\n" +
                    "\"claimant_phone_number\": \"0203 4445678\",\n" +
                    "\"claimant_mobile_number\": \"0208 111 2222\",\n" +
                    "\"claimant_fax_number\": \"\",\n" +
                    "\"claimant_email_address\": \"samt@gmail.com\",\n" +
                    "\"claimant_contact_preference\": \"By email\"},"
                + "\"scheduleType\": {\n" +
                    "\"scheduleDateTime\": \"2017-01-25T10:10:10.000\",\n" +
                    "\"scheduleClerk\": \"Clerk\",\n" +
                    "\"scheduleJudge\": \"Judge\"},"
                + " \"tribunalOffice\" : \"tribunalOffice\", "
                + "\"respondentType\": {\n" +
                    "\"respondent_name\": \"Brindley Pines Associates Limited\"}"
                + "} ";
        ObjectMapper mapper = new ObjectMapper();
        CaseData caseData = mapper.readValue(json, CaseData.class);
        caseDetails = new CaseDetails();
        caseDetails.setCaseId("2222");
        caseDetails.setCaseData(caseData);

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
                "\"ifRepresented\":\"true\",\n" +
                "\"nameOfRepresentative\":\"Batman & Robin\",\n" +
                "\"nameOfOrganisation\":\"Batman & Robin Associates\",\n" +
                "\"representativeAddress\":\"null\",\n" +
                "\"representativePhoneNumber\":\"0207 111 4477\",\n" +
                "\"representativeFaxNumber\":\"0207 111 4478\",\n" +
                "\"representativeDxNumber\":\"123456 Gotham\",\n" +
                "\"representativeEmailAddress\":\"batman@gotham.com\",\n" +
                "\"representativeReference\":\"PCODE 24\",\n" +
                "\"ifRefused\":\"false\",\n" +
                "\"hearingDate\":\"25 Jan 2017\",\n" +
                "\"clerk\":\"Clerk\",\n" +
                "\"judgeSurname\":\"Judge\",\n" +
                "\"createdDate\":\"\",\n" +
                "\"receivedDate\":\"\",\n" +
                "\"caseNo\":\"2222\",\n" +
                "\"claimant\":\"Thornhill\",\n" +
                "\"claimantTitle\":\"Mr  \",\n" +
                "\"claimantFirstName\":\"Sam  \",\n" +
                "\"claimantInitials\":\"S  \",\n" +
                "\"claimantLastName\":\"Thornhill\",\n" +
                "\"claimantDateOfBirth\":\"25 Feb 1990\",\n" +
                "\"claimantGender\":\"male\",\n" +
                "\"claimantAddressUK\":\"null\",\n" +
                "\"claimantPhoneNumber\":\"0203 4445678\",\n" +
                "\"claimantMobileNumber\":\"0208 111 2222\",\n" +
                "\"claimantFaxNumber\":\"\",\n" +
                "\"claimantEmailAddress\":\"samt@gmail.com\",\n" +
                "\"claimantContactPreference\":\"By email\",\n" +
                "\"respondent\":\"Brindley Pines Associates Limited\",\n" +
                "\"respondentName\":\"Brindley Pines Associates Limited\",\n" +
                "\"respondentAddress\":\"null\",\n" +
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
                "\"ifRefused\":\"false\",\n" +
                "\"createdDate\":\"\",\n" +
                "\"receivedDate\":\"\",\n" +
                "\"caseNo\":\"null\",\n" +
                "}\n" +
                "}\n";
        assertEquals(Helper.buildDocumentContent(caseDetailsEmpty, "template", "").toString(), result);
    }
}