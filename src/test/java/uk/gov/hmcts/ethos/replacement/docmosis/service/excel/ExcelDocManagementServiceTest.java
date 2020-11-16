package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.HelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesScheduleHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.DocumentManagementService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.excel.ExcelDocManagementService.APPLICATION_EXCEL_VALUE;

@RunWith(SpringJUnit4ClassRunner.class)
public class ExcelDocManagementServiceTest {

    @Mock
    private DocumentManagementService documentManagementService;
    @Mock
    private ExcelCreationService excelCreationService;
    @Mock
    private UserService userService;
    @Mock
    private ScheduleCreationService scheduleCreationService;
    @InjectMocks
    private ExcelDocManagementService excelDocManagementService;

    private MultipleDetails multipleDetails;
    private String userToken;
    private byte[] bytes;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        UserDetails userDetails = HelperTest.getUserDetails();
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        userToken = "authString";
        bytes = "Bytes to return".getBytes();
    }

    @Test
    public void uploadExcelDocument() {
        URI uri = URI.create("http://google.com");
        when(documentManagementService.uploadDocument(userToken,
                bytes,
                MultiplesHelper.generateExcelDocumentName(multipleDetails.getCaseData()), APPLICATION_EXCEL_VALUE))
                .thenReturn(uri);
        excelDocManagementService.uploadExcelDocument(userToken,
                multipleDetails.getCaseData(),
                bytes);
        verify(documentManagementService, times(1)).uploadDocument(userToken,
                bytes,
                MultiplesHelper.generateExcelDocumentName(multipleDetails.getCaseData()), APPLICATION_EXCEL_VALUE);
        verifyNoMoreInteractions(documentManagementService);
    }

    @Test
    public void downloadExcelDocument() throws IOException {
        String binaryUrl = "http://127.0.0.1:3453/documents/20d8a494-4232-480a-aac3-23ad0746c07b/binary";
        when(documentManagementService.downloadFile(binaryUrl))
                .thenReturn(MultipleUtil.getUploadedDocument());
        assertNotNull(excelDocManagementService.downloadExcelDocument(userToken, binaryUrl));
    }

    @Test
    public void generateAndUploadExcel() {
        URI uri = URI.create("http://google.com");
        List<String> multipleCollection = new ArrayList<>(Arrays.asList("245000/2020", "245001/2020", "245002/2020"));
        List<String> subMultipleCollection = MultiplesHelper.generateSubMultipleStringCollection(multipleDetails.getCaseData());
        when(documentManagementService.uploadDocument(userToken,
                bytes,
                MultiplesHelper.generateExcelDocumentName(multipleDetails.getCaseData()), APPLICATION_EXCEL_VALUE))
                .thenReturn(uri);
        when(excelCreationService.writeExcel(multipleCollection,
                subMultipleCollection))
                .thenReturn(bytes);
        excelDocManagementService.generateAndUploadExcel(multipleCollection,
                userToken, multipleDetails.getCaseData());
        verify(documentManagementService, times(1)).uploadDocument(userToken,
                bytes,
                MultiplesHelper.generateExcelDocumentName(multipleDetails.getCaseData()), APPLICATION_EXCEL_VALUE);
        verifyNoMoreInteractions(documentManagementService);
        verify(excelCreationService, times(1)).writeExcel(multipleCollection, subMultipleCollection);
        verifyNoMoreInteractions(excelCreationService);
        assertEquals("3", multipleDetails.getCaseData().getCaseCounter());
    }

    @Test
    public void generateAndUploadExcelEmptySubMultipleCollection() {
        URI uri = URI.create("http://google.com");
        List<String> multipleCollection = new ArrayList<>(Arrays.asList("245000/2020", "245001/2020", "245002/2020"));
        multipleDetails.getCaseData().setSubMultipleCollection(null);
        when(documentManagementService.uploadDocument(userToken,
                bytes,
                MultiplesHelper.generateExcelDocumentName(multipleDetails.getCaseData()), APPLICATION_EXCEL_VALUE))
                .thenReturn(uri);
        when(excelCreationService.writeExcel(multipleCollection,
                new ArrayList<>()))
                .thenReturn(bytes);
        excelDocManagementService.generateAndUploadExcel(multipleCollection,
                userToken, multipleDetails.getCaseData());
        verify(documentManagementService, times(1)).uploadDocument(userToken,
                bytes,
                MultiplesHelper.generateExcelDocumentName(multipleDetails.getCaseData()), APPLICATION_EXCEL_VALUE);
        verifyNoMoreInteractions(documentManagementService);
        verify(excelCreationService, times(1)).writeExcel(multipleCollection, new ArrayList<>());
        verifyNoMoreInteractions(excelCreationService);
    }

    @Test
    public void writeAndUploadScheduleDocument() {
        URI uri = URI.create("http://google.com");
        when(scheduleCreationService.writeSchedule(multipleDetails.getCaseData(),
                new ArrayList<>(),
                new TreeMap<>()))
                .thenReturn(bytes);
        when(documentManagementService.uploadDocument(userToken,
                bytes,
                MultiplesScheduleHelper.generateScheduleDocumentName(multipleDetails.getCaseData()), APPLICATION_EXCEL_VALUE))
                .thenReturn(uri);
        excelDocManagementService.writeAndUploadScheduleDocument(userToken, new TreeMap<>(),
                multipleDetails, new ArrayList<>());
        verify(documentManagementService, times(1)).uploadDocument(userToken,
                bytes,
                MultiplesScheduleHelper.generateScheduleDocumentName(multipleDetails.getCaseData()), APPLICATION_EXCEL_VALUE);
        verify(scheduleCreationService, times(1)).writeSchedule(multipleDetails.getCaseData(),
                new ArrayList<>(),
                new TreeMap<>());
        verifyNoMoreInteractions(excelCreationService);
    }

}