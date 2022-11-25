package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import java.io.IOException;
import java.util.*;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil.*;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleUploadService.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleUploadServiceTest {

    @Mock
    private ExcelReadingService excelReadingService;
    @Mock
    private MultipleBatchUpdate2Service multipleBatchUpdate2Service;
    @Mock
    private ExcelDocManagementService excelDocManagementService;
    @Mock
    private CcdClient ccdClient;
    @InjectMocks
    private MultipleUploadService multipleUploadService;

    private MultipleDetails multipleDetails;
    private String userToken;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        userToken = "authString";
    }

    @Test
    public void bulkUploadLogic() throws IOException {

        List<String> errors = new ArrayList<>();

        when(excelReadingService.checkExcelErrors(
                userToken,
                MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                new ArrayList<>()))
                .thenReturn(getDataTypeSheet(TESTING_FILE_NAME_WITH_TWO));

        multipleUploadService.bulkUploadLogic(userToken,
                multipleDetails,
                errors);

        assertEquals(0, errors.size());
    }

    @Test
    public void bulkUploadLogicSetSubMultipleInCaseDataTest() throws IOException {
        excelReadingService = new ExcelReadingService(excelDocManagementService, ccdClient);
        List<String> errors = new ArrayList<>();
        SubmitEvent submitEvent = new SubmitEvent();
        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference("1234");
        submitEvent.setCaseData(caseData);
        MultipleObject multipleObject =  MultipleObject.builder()
                .subMultiple("subMultiple")
                .ethosCaseRef("1234")
                .build();
        SortedMap<String, Object> map = new TreeMap<>(Map.of("1234", multipleObject));
        when(excelReadingService.checkExcelErrors(
                userToken,
                MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                new ArrayList<>()))
                .thenReturn(getDataTypeSheet(TESTING_FILE_NAME_WITH_TWO));
        when(ccdClient.retrieveCasesElasticSearch(anyString(),
                anyString(), anyList()))
                .thenReturn(List.of(submitEvent));
        when(excelReadingService.readExcel(anyString(),
                anyString(),
                anyList(),
                any(),
                any()))
                .thenReturn(map);
        multipleUploadService.bulkUploadLogic(userToken,
                multipleDetails,
                errors);

        assertEquals("subMultiple", submitEvent.getCaseData().getSubMultipleName());
    }

    @Test
    public void bulkUploadLogicWrongColumnRow() throws IOException {

        List<String> errors = new ArrayList<>();

        when(excelReadingService.checkExcelErrors(
                userToken,
                MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                new ArrayList<>()))
                .thenReturn(getDataTypeSheet(TESTING_FILE_NAME_WRONG_COLUMN_ROW));

        multipleUploadService.bulkUploadLogic(userToken,
                multipleDetails,
                errors);

        assertEquals(ERROR_SHEET_NUMBER_ROWS + multipleDetails.getCaseData().getCaseIdCollection().size(),
                errors.get(0));
        assertEquals(ERROR_SHEET_NUMBER_COLUMNS + MultiplesHelper.HEADERS.size(),
                errors.get(1));
    }

    @Test
    public void bulkUploadLogicEmptySheet() throws IOException {

        List<String> errors = new ArrayList<>();
        getDocumentCollection(multipleDetails.getCaseData());
        when(excelReadingService.checkExcelErrors(
                userToken,
                MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                new ArrayList<>()))
                .thenReturn(getDataTypeSheet(TESTING_FILE_NAME_EMPTY));

        multipleUploadService.bulkUploadLogic(userToken,
                multipleDetails,
                errors);

        assertEquals(ERROR_SHEET_EMPTY, errors.get(0));
    }

    @Test(expected = Exception.class)
    public void bulkUploadLogicException() throws IOException {

        when(excelReadingService.checkExcelErrors(
                userToken,
                MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                new ArrayList<>()))
                .thenThrow(new IOException());
        multipleUploadService.bulkUploadLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelReadingService, times(1)).checkExcelErrors(
                userToken,
                MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                new ArrayList<>());
        verifyNoMoreInteractions(excelReadingService);
    }

}