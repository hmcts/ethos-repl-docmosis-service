package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import uk.gov.hmcts.ethos.replacement.docmosis.config.CaseDefaultValuesConfiguration;
import uk.gov.hmcts.ethos.replacement.docmosis.config.TribunalOfficesConfiguration;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice.ContactDetails;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ABERDEEN_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DUNDEE_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.EDINBURGH_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.GLASGOW_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_USERS_CASE_TYPE_ID;

@RunWith(Parameterized.class)
@SpringBootTest(classes = {
    TribunalOfficesService.class,
})
@EnableConfigurationProperties({CaseDefaultValuesConfiguration.class, TribunalOfficesConfiguration.class})
public class TribunalOfficeServiceTest {

    @ClassRule public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
    @Rule public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    TribunalOfficesService tribunalOfficesService;

    private static final Object[][] TEST_CASES = new Object[][] {
            { MANCHESTER_DEV_CASE_TYPE_ID, null, "M3 2JA" },
            { MANCHESTER_USERS_CASE_TYPE_ID, null, "M3 2JA" },
            { MANCHESTER_CASE_TYPE_ID, null, "M3 2JA" },
            { BRISTOL_DEV_CASE_TYPE_ID, null, "BS1 6GR" },
            { BRISTOL_USERS_CASE_TYPE_ID, null, "BS1 6GR" },
            { BRISTOL_CASE_TYPE_ID, null, "BS1 6GR" },
            { LEEDS_DEV_CASE_TYPE_ID, null, "LS1 2RP" },
            { LEEDS_USERS_CASE_TYPE_ID, null, "LS1 2RP" },
            { LEEDS_CASE_TYPE_ID, null, "LS1 2RP" },
            { LONDON_CENTRAL_DEV_CASE_TYPE_ID, null, "WC2B 6EX" },
            { LONDON_CENTRAL_USERS_CASE_TYPE_ID, null, "WC2B 6EX" },
            { LONDON_CENTRAL_CASE_TYPE_ID, null, "WC2B 6EX" },
            { LONDON_EAST_DEV_CASE_TYPE_ID, null, "E14 2BE" },
            { LONDON_EAST_USERS_CASE_TYPE_ID, null, "E14 2BE" },
            { LONDON_EAST_CASE_TYPE_ID, null, "E14 2BE" },
            { LONDON_SOUTH_DEV_CASE_TYPE_ID, null, "CR0 2RF" },
            { LONDON_SOUTH_USERS_CASE_TYPE_ID, null, "CR0 2RF" },
            { LONDON_SOUTH_CASE_TYPE_ID, null, "CR0 2RF" },
            { MIDLANDS_EAST_DEV_CASE_TYPE_ID, null, "NG2 1EE" },
            { MIDLANDS_EAST_USERS_CASE_TYPE_ID, null, "NG2 1EE" },
            { MIDLANDS_EAST_CASE_TYPE_ID, null, "NG2 1EE" },
            { MIDLANDS_WEST_DEV_CASE_TYPE_ID, null, "B5 4UU" },
            { MIDLANDS_WEST_USERS_CASE_TYPE_ID, null, "B5 4UU" },
            { MIDLANDS_WEST_CASE_TYPE_ID, null, "B5 4UU" },
            { NEWCASTLE_DEV_CASE_TYPE_ID, null, "NE1 8QF" },
            { NEWCASTLE_USERS_CASE_TYPE_ID, null, "NE1 8QF" },
            { NEWCASTLE_CASE_TYPE_ID, null, "NE1 8QF" },
            { WALES_DEV_CASE_TYPE_ID, null, "CF24 0RZ" },
            { WALES_USERS_CASE_TYPE_ID, null, "CF24 0RZ" },
            { WALES_CASE_TYPE_ID, null, "CF24 0RZ" },
            { WATFORD_DEV_CASE_TYPE_ID, null, "WD17 1HP" },
            { WATFORD_USERS_CASE_TYPE_ID, null, "WD17 1HP" },
            { WATFORD_CASE_TYPE_ID, null, "WD17 1HP" },
            { SCOTLAND_DEV_CASE_TYPE_ID, null, "G2 8GT" },
            { SCOTLAND_USERS_CASE_TYPE_ID, null, "G2 8GT" },
            { SCOTLAND_CASE_TYPE_ID, null, "G2 8GT" },
            { SCOTLAND_DEV_CASE_TYPE_ID, "Unknown office", "G2 8GT" },
            { SCOTLAND_USERS_CASE_TYPE_ID, "Unknown office", "G2 8GT" },
            { SCOTLAND_CASE_TYPE_ID, "Unknown office", "G2 8GT" },
            { SCOTLAND_DEV_CASE_TYPE_ID, GLASGOW_OFFICE, "G2 8GT" },
            { SCOTLAND_USERS_CASE_TYPE_ID, GLASGOW_OFFICE, "G2 8GT" },
            { SCOTLAND_CASE_TYPE_ID, GLASGOW_OFFICE, "G2 8GT" },
            { SCOTLAND_DEV_CASE_TYPE_ID, ABERDEEN_OFFICE, "AB10 1SH" },
            { SCOTLAND_USERS_CASE_TYPE_ID, ABERDEEN_OFFICE, "AB10 1SH" },
            { SCOTLAND_CASE_TYPE_ID, ABERDEEN_OFFICE, "AB10 1SH" },
            { SCOTLAND_DEV_CASE_TYPE_ID, DUNDEE_OFFICE, "DD1 4QB" },
            { SCOTLAND_USERS_CASE_TYPE_ID, DUNDEE_OFFICE, "DD1 4QB" },
            { SCOTLAND_CASE_TYPE_ID, DUNDEE_OFFICE, "DD1 4QB" },
            { SCOTLAND_DEV_CASE_TYPE_ID, EDINBURGH_OFFICE, "EH3 7HF" },
            { SCOTLAND_USERS_CASE_TYPE_ID, EDINBURGH_OFFICE, "EH3 7HF" },
            { SCOTLAND_CASE_TYPE_ID, EDINBURGH_OFFICE, "EH3 7HF" },
            { "UNKNOWN_CASE_TYPE_ID", null, "M3 2JA" },
    };

    private String caseTypeId;
    private String managingOffice;
    private String expectedPostcode;

    public TribunalOfficeServiceTest(String caseTypeId, String managingOffice, String expectedPostcode) {
        this.caseTypeId = caseTypeId;
        this.managingOffice = managingOffice;
        this.expectedPostcode = expectedPostcode;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(TEST_CASES);
    }

    @Test
    public void testGetsCorrectTribunalContactDetails() {
        ContactDetails contactDetails = tribunalOfficesService.getTribunalContactDetails(caseTypeId, managingOffice);
        assertEquals(expectedPostcode, contactDetails.getPostcode());
    }
}