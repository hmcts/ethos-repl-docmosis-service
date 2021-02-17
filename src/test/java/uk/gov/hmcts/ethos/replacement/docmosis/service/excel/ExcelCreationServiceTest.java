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

    String leadLink = "<a target=\"_blank\" href=\"https://www-ccd.perftest.platform.hmcts.net/v2/case/1604313560561842\">245000/2020</a>";

    private TreeMap<String, Object> multipleObjects;

    @Before
    public void setUp() {
        multipleObjects = MultipleUtil.getMultipleObjectsAll();
    }

    @Test
    public void writeExcelObjects() {
        assertNotNull(excelCreationService.writeExcel(
                new ArrayList<>(multipleObjects.values()),
                new ArrayList<>(Arrays.asList("245000/1", "245000/1")),
                leadLink));
    }

    @Test
    public void writeExcelObjectsEmptySubMultiples() {
        assertNotNull(excelCreationService.writeExcel(
                new ArrayList<>(multipleObjects.values()),
                new ArrayList<>(),
                leadLink));
    }

    @Test
    public void writeExcelString() {
        assertNotNull(excelCreationService.writeExcel(
                new ArrayList<>(Arrays.asList("245000/2020", "245001/2020", "245002/2020")),
                new ArrayList<>(),
                leadLink));
    }

    @Test
    public void writeExcelStringEmpty() {
        assertNotNull(excelCreationService.writeExcel(
                new ArrayList<>(),
                new ArrayList<>(),
                leadLink));
    }
}