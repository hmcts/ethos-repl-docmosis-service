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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASE_IS_NOT_IN_MULTIPLE_ERROR;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleAmendLeadCaseServiceTest {

    @Mock
    private ExcelReadingService excelReadingService;
    @Mock
    private ExcelDocManagementService excelDocManagementService;
    @Mock
    private MultipleHelperService multipleHelperService;
    @InjectMocks
    private MultipleAmendLeadCaseService multipleAmendLeadCaseService;

    private TreeMap<String, Object> multipleObjects;
    private MultipleDetails multipleDetails;
    private String userToken;

    @Before
    public void setUp() {
        multipleObjects = MultipleUtil.getMultipleObjectsAll();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        userToken = "authString";
    }

    @Test
    public void bulkAmendLeadCaseLogicDoesNotExist() {
        List<String> errors = new ArrayList<>();
        multipleDetails.getCaseData().setAmendLeadCase("245020/2020");
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);
        multipleAmendLeadCaseService.bulkAmendLeadCaseLogic(userToken,
                multipleDetails,
                errors);
        assertEquals(1, errors.size());
        assertEquals(CASE_IS_NOT_IN_MULTIPLE_ERROR, errors.get(0));
    }

    @Test
    public void bulkAmendLeadCaseLogicDifferentLead() {
        multipleDetails.getCaseData().setAmendLeadCase("245000/2020");
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);
        multipleAmendLeadCaseService.bulkAmendLeadCaseLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(new ArrayList<>(multipleObjects.values()),
                userToken,
                multipleDetails.getCaseData());
        verifyNoMoreInteractions(excelDocManagementService);
    }

}