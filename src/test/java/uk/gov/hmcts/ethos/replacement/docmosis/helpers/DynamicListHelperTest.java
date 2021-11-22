package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicDepositOrder;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicRespondentRepresentative;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.dynamiclists.DynamicRestrictedReporting;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DynamicListHelperTest {

    private CaseDetails caseDetails1;
    private CaseDetails caseDetails4;
    private CaseDetails caseDetails6;
    private DynamicValueType dynamicValueType;

    @Before
    public void setUp() throws Exception {
        caseDetails1 = generateCaseDetails("caseDetailsTest1.json");
        caseDetails4 = generateCaseDetails("caseDetailsTest4.json");
        caseDetails6 = generateCaseDetails("caseDetailsTest6.json");
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
        DynamicRespondentRepresentative.dynamicRespondentRepresentativeNames(caseDetails1.getCaseData());
        assertNotNull(caseDetails1.getCaseData().getRepCollection());
        var dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getRepCollection().get(0)
                .getValue().getDynamicRespRepName().getListItems().get(0));
    }

    @Test
    public void populateDynamicRespondentRepList() {
        DynamicRespondentRepresentative.dynamicRespondentRepresentativeNames(caseDetails6.getCaseData());
        assertNotNull(caseDetails6.getCaseData().getRepCollection().get(0).getValue().getDynamicRespRepName());
        var dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails6.getCaseData().getRepCollection().get(0)
                .getValue().getDynamicRespRepName().getListItems().get(0));
    }

    @Test
    public void createDynamicListForRestrictedReporting() {
        DynamicRestrictedReporting.dynamicRestrictedReporting(caseDetails1.getCaseData());
        assertNotNull(caseDetails1.getCaseData().getRestrictedReporting());
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getRestrictedReporting()
                .getDynamicRequestedBy().getListItems().get(0));
    }

    @Test
    public void populateDynamicListForRestrictedReporting() {
        DynamicRestrictedReporting.dynamicRestrictedReporting(caseDetails4.getCaseData());
        assertNotNull(caseDetails4.getCaseData().getRestrictedReporting().getDynamicRequestedBy());
        dynamicValueType.setCode("Judge");
        dynamicValueType.setLabel("Judge");
        assertEquals(dynamicValueType, caseDetails4.getCaseData().getRestrictedReporting()
                .getDynamicRequestedBy().getValue());
    }

    @Test
    public void dynamicValueTypeRespondent() {
        var dynamicValueRespondent = new DynamicValueType();
        dynamicValueRespondent.setCode("R: Antonio Vazquez");
        dynamicValueRespondent.setLabel("Antonio Vazquez");
        List<DynamicValueType> listItems = DynamicListHelper.createDynamicRespondentName(caseDetails1.getCaseData().getRespondentCollection());
        listItems.add(DynamicListHelper.getDynamicCodeLabel("C: " + caseDetails1.getCaseData().getClaimant(), caseDetails1.getCaseData().getClaimant()));
        var dynamicValue = DynamicListHelper.getDynamicValueType(caseDetails1.getCaseData(), listItems, "Respondent");
        assertEquals(dynamicValue, dynamicValueRespondent);
    }

    @Test
    public void dynamicDepositOrder() {
        DynamicDepositOrder.dynamicDepositOrder(caseDetails1.getCaseData());
        assertNotNull(caseDetails1.getCaseData().getDepositCollection());
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getDepositCollection().get(0)
                .getValue().getDynamicDepositOrderAgainst().getValue());
        dynamicValueType.setCode("Tribunal");
        dynamicValueType.setLabel("Tribunal");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getDepositCollection().get(0)
                .getValue().getDynamicDepositRequestedBy().getValue());
    }

    @Test
    public void dynamicDepositRefund() {
        caseDetails1.getCaseData().getDepositCollection().get(0).getValue().setDepositRefund("Yes");
        DynamicDepositOrder.dynamicDepositOrder(caseDetails1.getCaseData());
        dynamicValueType.setCode("R: Antonio Vazquez");
        dynamicValueType.setLabel("Antonio Vazquez");
        assertEquals(dynamicValueType, caseDetails1.getCaseData().getDepositCollection().get(0)
                .getValue().getDynamicDepositRefundedTo().getValue());
    }

}
