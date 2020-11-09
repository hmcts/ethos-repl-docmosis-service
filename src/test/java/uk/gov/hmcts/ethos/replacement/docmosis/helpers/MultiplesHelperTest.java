package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.types.CaseType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MultiplesHelperTest {

    private MultipleData multipleData;

    @Before
    public void setUp()  {
        multipleData = MultipleUtil.getMultipleData();
    }

    @Test
    public void addLeadToCaseIdsWhenEmptyCollection() {

        multipleData.setCaseIdCollection(null);
        MultiplesHelper.addLeadToCaseIds(multipleData, "245003/2020");
        assertEquals(1, multipleData.getCaseIdCollection().size());
        assertEquals("245003/2020", multipleData.getCaseIdCollection().get(0).getValue().getEthosCaseReference());

    }

    @Test
    public void filterDuplicatedAndEmptyCaseIds() {

        multipleData.getCaseIdCollection().add(createCaseIdTypeItem("3", "245000/2020"));
        multipleData.getCaseIdCollection().add(createCaseIdTypeItem("4", null));
        multipleData.getCaseIdCollection().add(createCaseIdTypeItem("5", "245000/2020"));
        multipleData.getCaseIdCollection().add(createCaseIdTypeItem("6", "245000/2020"));
        multipleData.getCaseIdCollection().add(createCaseIdTypeItem("7", ""));
        assertEquals(7, multipleData.getCaseIdCollection().size());
        List<CaseIdTypeItem> list = MultiplesHelper.filterDuplicatedAndEmptyCaseIds(multipleData);
        assertEquals(2, list.size());

    }

    @Test
    public void getCurrentLead() {

        String leadLink = "<a target=\"_blank\" href=\"https://www-ccd.perftest.platform.hmcts.net/v2/case/1604313560561842\">1852013/2020</a>";
        assertEquals("1852013/2020", MultiplesHelper.getCurrentLead(leadLink));

    }

    private CaseIdTypeItem createCaseIdTypeItem(String id, String value) {

        CaseType caseType = new CaseType();
        caseType.setEthosCaseReference(value);
        CaseIdTypeItem caseIdTypeItem = new CaseIdTypeItem();
        caseIdTypeItem.setId(id);
        caseIdTypeItem.setValue(caseType);
        return caseIdTypeItem;

    }

}
