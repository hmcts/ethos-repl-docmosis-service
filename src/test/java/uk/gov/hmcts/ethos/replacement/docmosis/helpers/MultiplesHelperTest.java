package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.types.CaseType;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.types.UploadedDocumentType;
import uk.gov.hmcts.ecm.common.model.helper.SchedulePayload;
import uk.gov.hmcts.ecm.common.model.multiples.CaseImporterFile;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultiplesHelperTest {

    private MultipleData multipleData;

    @Mock
    private CcdClient ccdClient;

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
        var refList = Arrays.asList("1800074/2020", "1800074/2021", "1800075/2020", "1800075/2021");
        var expectedResult = new TreeMap<>(Map.of(
                "2020", new TreeMap<>(Map.of("1800074", "1800074/2020", "1800075", "1800075/2020")),
                "2021", new TreeMap<>(Map.of("1800074", "1800074/2021", "1800075", "1800075/2021")))
        );

        assertEquals(expectedResult, MultiplesHelper.createCollectionOrderedByCaseRef(refList));
    }

    @Test
    public void orderMultipleObjects() {
        var refList = Arrays.asList(
                MultiplesHelper.createMultipleObject("1800074/2020", ""),
                MultiplesHelper.createMultipleObject("1800074/2021", ""),
                MultiplesHelper.createMultipleObject("1800075/2020", ""),
                MultiplesHelper.createMultipleObject("1800075/2021", "")
        );
        var expectedResult = new TreeMap<>(Map.of(
                "2020",
                new TreeMap<>(Map.of(
                        "1800074", MultiplesHelper.createMultipleObject("1800074/2020", ""),
                        "1800075", MultiplesHelper.createMultipleObject("1800075/2020", "")
                )),
                "2021",
                new TreeMap<>(Map.of(
                        "1800074", MultiplesHelper.createMultipleObject("1800074/2021", ""),
                        "1800075", MultiplesHelper.createMultipleObject("1800075/2021", "")
                ))
        ));

        assertEquals(expectedResult, MultiplesHelper.createCollectionOrderedByCaseRef(refList));
    }

    @Test
    public void orderSchedulePayloads() {
        var refList = Arrays.asList(
                SchedulePayload.builder().ethosCaseRef("1800074/2020").build(),
                SchedulePayload.builder().ethosCaseRef("1800074/2021").build(),
                SchedulePayload.builder().ethosCaseRef("1800075/2020").build(),
                SchedulePayload.builder().ethosCaseRef("1800075/2021").build()
        );

        var expectedResult = new TreeMap<>(Map.of(
                "2020",
                new TreeMap<>(Map.of(
                        "1800074", SchedulePayload.builder().ethosCaseRef("1800074/2020").build(),
                        "1800075", SchedulePayload.builder().ethosCaseRef("1800075/2020").build()
                )),
                "2021",
                new TreeMap<>(Map.of(
                        "1800074", SchedulePayload.builder().ethosCaseRef("1800074/2021").build(),
                        "1800075", SchedulePayload.builder().ethosCaseRef("1800075/2021").build()
                ))));

        assertEquals(expectedResult, MultiplesHelper.createCollectionOrderedByCaseRef(refList));
    }

    @Test
    public void orderMultiplesObjectTypNotRecognised() {
        List<Object> refList = Arrays.asList(5, 6, 4, 5);
        var expectedResult = new TreeMap<>();
        assertEquals(MultiplesHelper.createCollectionOrderedByCaseRef(refList), expectedResult);
    }

    @Test
    public void setSubMultipleFieldInSingleCaseDataTest() throws IOException {
        MultipleDetails multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        String userToken = "authString";
        SubmitEvent submitEvent = new SubmitEvent();
        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference("1234");
        submitEvent.setCaseData(caseData);
        multipleDetails.setJurisdiction("EMPLOYMENT");
        multipleDetails.setCaseTypeId("Leeds_Multiple");
        MultipleData multipleData = new MultipleData();
        multipleData.setCaseCounter("1");
        CaseImporterFile caseImporterFile = new CaseImporterFile();
        UploadedDocumentType uploadedDocumentType = new UploadedDocumentType();
        uploadedDocumentType.setDocumentBinaryUrl("url");
        caseImporterFile.setUploadedDocument(uploadedDocumentType);
        multipleData.setCaseImporterFile(caseImporterFile);
        multipleDetails.setCaseData(multipleData);

        when(ccdClient.retrieveCasesElasticSearch(anyString(),
                anyString(), anyList()))
                .thenReturn(List.of(submitEvent));
        CCDRequest ccdRequest = new CCDRequest();
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(submitEvent.getCaseData());
        ccdRequest.setCaseDetails(caseDetails);
        when(ccdClient.startEventForCase(anyString(), anyString(), anyString(),
                        anyString()))
                .thenReturn(ccdRequest);
        MultiplesHelper.setSubMultipleFieldInSingleCaseData(userToken,
                multipleDetails,
                "1234",
                "subMultiple",
                ccdClient);

        assertEquals("subMultiple", caseData.getSubMultipleName());
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
