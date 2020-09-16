package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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

}
