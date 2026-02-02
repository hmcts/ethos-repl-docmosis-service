package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.service.SubMultipleReferenceService;

import java.util.ArrayList;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.AMEND_ACTION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DELETE_ACTION;

@RunWith(SpringJUnit4ClassRunner.class)
public class SubMultipleUpdateServiceTest {

    @Mock
    private ExcelReadingService excelReadingService;
    @Mock
    private SubMultipleReferenceService subMultipleReferenceService;
    @Mock
    private ExcelDocManagementService excelDocManagementService;

    @InjectMocks
    private SubMultipleUpdateService subMultipleUpdateService;

    private TreeMap<String, Object> multipleObjectsAll;
    private MultipleDetails multipleDetails;
    private String userToken;

    @Before
    public void setUp() {
        multipleObjectsAll = MultipleUtil.getMultipleObjectsAll();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        userToken = "authString";
    }

    @Test
    public void subMultipleUpdateLogicCreate() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsAll);

        assertEquals(2, multipleDetails.getCaseData().getSubMultipleCollection().size());

        subMultipleUpdateService.subMultipleUpdateLogic(userToken,
                multipleDetails,
                new ArrayList<>());

        assertEquals(3, multipleDetails.getCaseData().getSubMultipleCollection().size());
        assertEquals("NewSubMultiple",
                multipleDetails.getCaseData().getSubMultipleCollection().get(2).getValue().getSubMultipleName());

        verify(subMultipleReferenceService, times(1)).createReference(
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMultipleReference(),
                1);
        verifyNoMoreInteractions(subMultipleReferenceService);
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(
                anyList(),
                anyString(),
                any());
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void subMultipleUpdateLogicCreateEmptySubMultiples() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsAll);

        multipleDetails.getCaseData().setSubMultipleCollection(null);

        subMultipleUpdateService.subMultipleUpdateLogic(userToken,
                multipleDetails,
                new ArrayList<>());

        assertEquals(1, multipleDetails.getCaseData().getSubMultipleCollection().size());
        assertEquals("NewSubMultiple",
                multipleDetails.getCaseData().getSubMultipleCollection().getFirst().getValue().getSubMultipleName());

        verify(subMultipleReferenceService, times(1)).createReference(
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMultipleReference(),
                1);
        verifyNoMoreInteractions(subMultipleReferenceService);
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(
                anyList(),
                anyString(),
                any());
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void subMultipleUpdateLogicAmend() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsAll);

        multipleDetails.getCaseData().getSubMultipleAction().setActionType(AMEND_ACTION);

        assertEquals(2, multipleDetails.getCaseData().getSubMultipleCollection().size());
        assertEquals(multipleDetails.getCaseData().getSubMultipleAction().getAmendSubMultipleNameExisting(),
                multipleDetails.getCaseData().getSubMultipleCollection().getFirst().getValue().getSubMultipleName());

        subMultipleUpdateService.subMultipleUpdateLogic(userToken,
                multipleDetails,
                new ArrayList<>());

        assertEquals(2, multipleDetails.getCaseData().getSubMultipleCollection().size());
        assertEquals("SubMultipleAmended",
                multipleDetails.getCaseData().getSubMultipleCollection().getFirst().getValue().getSubMultipleName());

        verify(excelDocManagementService, times(1)).generateAndUploadExcel(
                anyList(),
                anyString(),
                any());
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void subMultipleUpdateLogicDelete() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsAll);

        multipleDetails.getCaseData().getSubMultipleAction().setActionType(DELETE_ACTION);

        assertEquals(2, multipleDetails.getCaseData().getSubMultipleCollection().size());

        subMultipleUpdateService.subMultipleUpdateLogic(userToken,
                multipleDetails,
                new ArrayList<>());

        assertEquals(1, multipleDetails.getCaseData().getSubMultipleCollection().size());

        verify(excelDocManagementService, times(1)).generateAndUploadExcel(
                anyList(),
                anyString(),
                any());
        verifyNoMoreInteractions(excelDocManagementService);
    }

}