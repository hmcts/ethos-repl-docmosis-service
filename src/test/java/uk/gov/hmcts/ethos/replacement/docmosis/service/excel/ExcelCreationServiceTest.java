package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class ExcelCreationServiceTest {

    @InjectMocks
    private ExcelCreationService excelCreationService;

    private TreeMap<String, Object> multipleObjects;

    @Before
    public void setUp() {
        multipleObjects = MultipleUtil.getMultipleObjectsAll();
    }

    @Test
    public void writeExcelObjects() {
        assertNotNull(excelCreationService.writeExcel(new ArrayList<>(multipleObjects.values())));
    }

    @Test
    public void writeExcelString() {
        assertNotNull(excelCreationService.writeExcel(new ArrayList<>(Arrays.asList("245000/2020", "245001/2020", "245002/2020"))));
    }

    @Test
    public void writeExcelStringEmpty() {
        assertNotNull(excelCreationService.writeExcel(new ArrayList<>()));
    }
}