package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.types.CaseType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MultiplesHelperTest {

    private MultipleData multipleData;

    @Before
    public void setUp()  {
        multipleData = MultipleUtil.getMultipleData();
    }

    @Test
    public void removeCaseIds() {

        assertEquals(2, multipleData.getCaseIdCollection().size());
        MultiplesHelper.removeCaseIds(multipleData, new ArrayList<>(Collections.singletonList("245000/2020")));
        assertEquals(1, multipleData.getCaseIdCollection().size());
        assertEquals("245001/2020", multipleData.getCaseIdCollection().get(0).getValue().getEthosCaseReference());
        MultiplesHelper.removeCaseIds(multipleData, new ArrayList<>(Collections.singletonList("245001/2020")));
        assertEquals(0, multipleData.getCaseIdCollection().size());

    }

    @Test
    public void addCaseIds() {

        assertEquals(2, multipleData.getCaseIdCollection().size());
        MultiplesHelper.addCaseIds(multipleData, new ArrayList<>(Arrays.asList("245003/2020", "245004/2020")));
        assertEquals(4, multipleData.getCaseIdCollection().size());
        assertEquals("245000/2020", multipleData.getCaseIdCollection().get(0).getValue().getEthosCaseReference());

    }

    @Test
    public void addCaseIdsWhenEmptyCollection() {

        multipleData.setCaseIdCollection(null);
        MultiplesHelper.addCaseIds(multipleData, new ArrayList<>(Arrays.asList("245003/2020", "245004/2020")));
        assertEquals(2, multipleData.getCaseIdCollection().size());
        assertEquals("245003/2020", multipleData.getCaseIdCollection().get(0).getValue().getEthosCaseReference());

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

    private CaseIdTypeItem createCaseIdTypeItem(String id, String value) {

        CaseType caseType = new CaseType();
        caseType.setEthosCaseReference(value);
        CaseIdTypeItem caseIdTypeItem = new CaseIdTypeItem();
        caseIdTypeItem.setId(id);
        caseIdTypeItem.setValue(caseType);
        return caseIdTypeItem;

    }

}
