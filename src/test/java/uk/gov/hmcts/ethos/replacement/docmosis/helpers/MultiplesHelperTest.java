package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.types.CaseType;
import uk.gov.hmcts.ecm.common.model.helper.SchedulePayload;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    @Test
    public void orderMultiplesStringRef() {
        List<String> refList = Arrays.asList("1800074/2020", "1800074/2021", "1800075/2020", "1800075/2021");;
        var yearListA = new TreeMap<>(Map.of("1800074", "1800074/2020", "1800075", "1800075/2020"));
        var yearListB = new TreeMap<>(Map.of("1800074", "1800074/2021", "1800075", "1800075/2021"));
        var expectedResult = new TreeMap<>(Map.of("2020", yearListA, "2021", yearListB));

        assertEquals(expectedResult, MultiplesHelper.createOrderedCaseList(refList));
    }

    @Test
    public void orderMultipleObjects() {
        List<MultipleObject> refList = Arrays.asList(
                MultiplesHelper.createMultipleObject("1800074/2020", ""),
                MultiplesHelper.createMultipleObject("1800074/2021", ""),
                MultiplesHelper.createMultipleObject("1800075/2020", ""),
                MultiplesHelper.createMultipleObject("1800075/2021", "")
        );
        var yearListA = new TreeMap<>(Map.of(
                        "1800074", MultiplesHelper.createMultipleObject("1800074/2020", ""),
                        "1800075", MultiplesHelper.createMultipleObject("1800075/2020", "")
                        ));
        var yearListB = new TreeMap<>(Map.of(
                        "1800074", MultiplesHelper.createMultipleObject("1800074/2021", ""),
                        "1800075", MultiplesHelper.createMultipleObject("1800075/2021", "")
                        ));
        var expectedResult = new TreeMap<>(Map.of("2020", yearListA, "2021", yearListB));

        assertEquals(expectedResult, MultiplesHelper.createOrderedCaseList(refList));
    }

    @Test
    public void orderSchedulePayloads() {
        List<SchedulePayload> refList = Arrays.asList(
                SchedulePayload.builder().ethosCaseRef("1800074/2020").build(),
                SchedulePayload.builder().ethosCaseRef("1800074/2021").build(),
                SchedulePayload.builder().ethosCaseRef("1800075/2020").build(),
                SchedulePayload.builder().ethosCaseRef("1800075/2021").build()
        );
        var yearListA = new TreeMap<>(Map.of(
                "1800074", SchedulePayload.builder().ethosCaseRef("1800074/2020").build(),
                "1800075", SchedulePayload.builder().ethosCaseRef("1800075/2020").build()
        ));
        var yearListB = new TreeMap<>(Map.of(
                "1800074", SchedulePayload.builder().ethosCaseRef("1800074/2021").build(),
                "1800075", SchedulePayload.builder().ethosCaseRef("1800075/2021").build()
        ));
        var expectedResult = new TreeMap<>(Map.of("2020", yearListA, "2021", yearListB));

        assertEquals(expectedResult, MultiplesHelper.createOrderedCaseList(refList));
    }

    @Test
    public void orderMultiplesObjectTypNotRecognised() {
        List<Object> refList = Arrays.asList(5, 6, 4, 5);
        var expectedResult = new TreeMap<>();
        assertEquals(MultiplesHelper.createOrderedCaseList(refList), expectedResult);
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
