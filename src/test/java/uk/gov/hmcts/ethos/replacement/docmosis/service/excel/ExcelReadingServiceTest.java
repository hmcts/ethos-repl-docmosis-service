package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil.TESTING_FILE_NAME;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil.TESTING_FILE_NAME_ERROR;

@RunWith(SpringJUnit4ClassRunner.class)
public class ExcelReadingServiceTest {

    @Mock
    private ExcelDocManagementService excelDocManagementService;
    @InjectMocks
    private ExcelReadingService excelReadingService;

    private String documentBinaryUrl;
    private Resource body;
    private List<String> errors;
    private MultipleData multipleData;
    private String userToken;

    @Before
    public void setUp() {
        documentBinaryUrl = "http://127.0.0.1:3453/documents/20d8a494-4232-480a-aac3-23ad0746c07b/binary";
        errors = new ArrayList<>();
        multipleData = MultipleUtil.getMultipleData();
        userToken = "authString";
    }

    @Test
    public void readExcelAll() throws IOException {

        body = new ClassPathResource(TESTING_FILE_NAME);
        when(excelDocManagementService.downloadExcelDocument(userToken, documentBinaryUrl))
                .thenReturn(body.getInputStream());
        TreeMap<String, Object> multipleObjects = excelReadingService.readExcel(userToken, documentBinaryUrl,
                errors, multipleData, FilterExcelType.ALL);
        assertEquals(6, multipleObjects.values().size());
        assertEquals("2", ((MultipleObject)multipleObjects.get("1820001/2019")).getFlag2());
        assertEquals("AA", ((MultipleObject)multipleObjects.get("1820002/2019")).getFlag1());
        assertEquals("", ((MultipleObject)multipleObjects.get("1820005/2019")).getFlag2());
        assertEquals("", ((MultipleObject)multipleObjects.get("1820005/2019")).getEQP());
        assertEquals(0, errors.size());
    }

    @Test
    public void readExcelFlags() throws IOException {

        body = new ClassPathResource(TESTING_FILE_NAME);
        when(excelDocManagementService.downloadExcelDocument(userToken, documentBinaryUrl))
                .thenReturn(body.getInputStream());
        TreeMap<String, Object> multipleObjects = excelReadingService.readExcel(userToken, documentBinaryUrl,
                errors, multipleData, FilterExcelType.FLAGS);
        assertEquals(3, multipleObjects.values().size());
        assertNull(multipleObjects.get("1820001/2019"));
        assertEquals("1820002/2019", multipleObjects.get("1820002/2019"));
        assertEquals(0, errors.size());
    }

    @Test
    public void readExcelSubMultiple() throws IOException {

        body = new ClassPathResource(TESTING_FILE_NAME);
        when(excelDocManagementService.downloadExcelDocument(userToken, documentBinaryUrl))
                .thenReturn(body.getInputStream());
        TreeMap<String, Object> multipleObjects = excelReadingService.readExcel(userToken, documentBinaryUrl,
                errors, multipleData, FilterExcelType.SUB_MULTIPLE);

        List<String> listSub = (List<String>) multipleObjects.get("Sub");
        assertEquals(2, listSub.size());

        List<String> listSub1 = (List<String>) multipleObjects.get("Sub1");
        assertEquals(1, listSub1.size());

        assertEquals("1820004/2019", listSub.get(0));
        assertEquals("1820005/2019", listSub.get(1));
        assertEquals(0, errors.size());
    }

    @Test
    public void readExcelError() throws IOException {

        body = new ClassPathResource(TESTING_FILE_NAME_ERROR);
        when(excelDocManagementService.downloadExcelDocument(userToken, documentBinaryUrl))
                .thenReturn(body.getInputStream());
        excelReadingService.readExcel(userToken, documentBinaryUrl, errors, multipleData, FilterExcelType.ALL);
        assertEquals(1, errors.size());
    }

    @Test
    public void readExcelException() throws IOException {

        body = new ClassPathResource(TESTING_FILE_NAME_ERROR);
        when(excelDocManagementService.downloadExcelDocument(userToken, documentBinaryUrl))
                .thenThrow(new IOException());
        TreeMap<String, Object> multipleObjects = excelReadingService.readExcel(userToken, documentBinaryUrl,
                errors, multipleData, FilterExcelType.ALL);
        assertEquals("{}", multipleObjects.toString());
    }

}