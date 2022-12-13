package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.types.UploadedDocumentType;
import uk.gov.hmcts.ecm.common.model.multiples.CaseImporterFile;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil.TESTING_FILE_NAME_EMPTY;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil.TESTING_FILE_NAME_WITH_TWO;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil.TESTING_FILE_NAME_WRONG_COLUMN_ROW;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil.getDataTypeSheet;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil.getDocumentCollection;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleUploadService.ERROR_SHEET_EMPTY;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleUploadService.ERROR_SHEET_NUMBER_COLUMNS;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleUploadService.ERROR_SHEET_NUMBER_ROWS;

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
    @Mock
    private InputStream inputStream;
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
    public void bulkUploadLogicSetSubMultipleTest() throws IOException {
        List<String> errors = new ArrayList<>();
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
        when(excelReadingService.checkExcelErrors(
                userToken,
                "url",
                new ArrayList<>()))
                .thenReturn(getDataTypeSheet(TESTING_FILE_NAME_WITH_TWO));
        MultipleObject multipleObject = MultipleObject.builder()
                .subMultiple("subMultiple")
                .ethosCaseRef("1234")
                .flag1("1")
                .flag2("2")
                .flag3("3")
                .flag4("4")
                .build();
        SortedMap<String, Object> multipleObjects = new TreeMap<>(Map.of("1234", multipleObject));

       when(excelReadingService.readExcel(eq(userToken),eq("url"), eq(errors), eq(multipleData),
                any()))
               .thenReturn(multipleObjects);
        when(ccdClient.retrieveCasesElasticSearch(anyString(),
                anyString(), anyList()))
                .thenReturn(List.of(submitEvent));
        multipleUploadService.bulkUploadLogic(userToken,
                multipleDetails,
                errors);

        assertEquals("subMultiple", caseData.getSubMultipleName());
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