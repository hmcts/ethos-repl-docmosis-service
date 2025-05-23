package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper.SELECT_ALL;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleDynamicListFlagsServiceTest {

    @Mock
    private ExcelReadingService excelReadingService;
    @InjectMocks
    private MultipleDynamicListFlagsService multipleDynamicListFlagsService;

    private MultipleDetails multipleDetails;
    private TreeMap<String, Object> multipleObjectsDLFlags;
    private String userToken;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleObjectsDLFlags = MultipleUtil.getMultipleObjectsDLFlags();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        userToken = "authString";
    }

    @Test
    public void populateDynamicListFlagsLogic() {

        List<String> errors = new ArrayList<>();

        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsDLFlags);

        multipleDynamicListFlagsService.populateDynamicListFlagsLogic(userToken,
                multipleDetails,
                errors);

        assertEquals(2, multipleDetails.getCaseData().getFlag1().getListItems().size());
        assertEquals("AA", multipleDetails.getCaseData().getFlag1().getListItems().get(1).getCode());
        assertEquals(SELECT_ALL, multipleDetails.getCaseData().getFlag1().getValue().getCode());
        assertEquals(3, multipleDetails.getCaseData().getFlag2().getListItems().size());
        assertEquals("BB", multipleDetails.getCaseData().getFlag2().getListItems().get(1).getCode());
        assertEquals(SELECT_ALL, multipleDetails.getCaseData().getFlag3().getValue().getCode());
        assertEquals(SELECT_ALL, multipleDetails.getCaseData().getFlag4().getListItems().get(0).getCode());
    }

}