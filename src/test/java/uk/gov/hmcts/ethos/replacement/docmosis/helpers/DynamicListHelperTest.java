package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclist.DepositOrderDynamicList;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclist.JudgementDynamicList;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclist.RespondentRepresentativeDynamicList;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclist.RestrictedReportingDynamicList;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DynamicListHelperTest {

    private CaseDetails caseDetails1;
    private CaseDetails caseDetails4;
    private CaseDetails caseDetails6;
    private CaseDetails caseDetails14;
    private DynamicValueType dynamicValueType;



    @Before
    public void setUp() throws Exception {
        caseDetails1 = generateCaseDetails("caseDetailsTest1.json");
        caseDetails4 = generateCaseDetails("caseDetailsTest4.json");
        caseDetails6 = generateCaseDetails("caseDetailsTest6.json");
        caseDetails14 = generateCaseDetails("caseDetailsTest14.json");
        dynamicValueType = new DynamicValueType();
    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }


    @Test
    public void createDynamicListForRespondentRepresentative() {
        RespondentRepresentativeDynamicList.dynamicRespondentRepresentativeNames(caseDetails1.getCaseData());
        assertNotNull(caseDetails1.getCaseData().getRepCollection());
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getRepCollection().get(0)
                .getValue().getDynamicRespRepName().getListItems().get(0));
    }

    @Test
    public void populateDynamicRespondentRepList() {
        RespondentRepresentativeDynamicList.dynamicRespondentRepresentativeNames(caseDetails6.getCaseData());
        assertNotNull(caseDetails6.getCaseData().getRepCollection().get(0).getValue().getDynamicRespRepName());
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails6.getCaseData().getRepCollection().get(0)
                .getValue().getDynamicRespRepName().getListItems().get(0));
    }

    @Test
    public void createDynamicListForRestrictedReporting() {
        RestrictedReportingDynamicList.dynamicRestrictedReporting(caseDetails1.getCaseData());
        assertNotNull(caseDetails1.getCaseData().getRestrictedReporting());
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getRestrictedReporting()
                .getDynamicRequestedBy().getListItems().get(0));
    }

    @Test
    public void populateDynamicListForRestrictedReporting() {
        RestrictedReportingDynamicList.dynamicRestrictedReporting(caseDetails4.getCaseData());
        assertNotNull(caseDetails4.getCaseData().getRestrictedReporting().getDynamicRequestedBy());
        dynamicValueType.setCode("Judge");
        dynamicValueType.setLabel("Judge");
        assertEquals(dynamicValueType, caseDetails4.getCaseData().getRestrictedReporting()
                .getDynamicRequestedBy().getValue());
    }

    @Test
    public void createDynamicDepositOrderList() {
        DepositOrderDynamicList.dynamicDepositOrder(caseDetails1.getCaseData());
        assertNotNull(caseDetails1.getCaseData().getDepositCollection());
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getDepositCollection().get(0)
            .getValue().getDynamicDepositOrderAgainst().getValue());
    }

    @Test
    public void createDynamicListForJudgements() {
        JudgementDynamicList.createDynamicJudgementList(caseDetails14.getCaseData());
        assertNotNull(caseDetails14.getCaseData().getJudgementCollection());
        dynamicValueType.setCode("1");
        dynamicValueType.setLabel("1 : Single - Manchester - 01 Nov 2019");
        assertEquals(dynamicValueType, caseDetails14.getCaseData().getJudgementCollection()
            .get(0).getValue().getDynamicJudgementHearing().getListItems().get(0));

    }
}
