package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleAmendCaseIdsServiceTest {

    @Mock
    private ExcelReadingService excelReadingService;
    @Mock
    private ExcelDocManagementService excelDocManagementService;
    @Mock
    private MultipleHelperService multipleHelperService;
    @InjectMocks
    private MultipleAmendCaseIdsService multipleAmendCaseIdsService;

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
    public void bulkAmendCaseIdsLogic() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);
        multipleAmendCaseIdsService.bulkAmendCaseIdsLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(getMultipleObjectsList(),
                userToken,
                multipleDetails.getCaseData());
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void bulkAmendCaseIdsLogicNewLeadAsEmptyExcel() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(new TreeMap<>());
        multipleAmendCaseIdsService.bulkAmendCaseIdsLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(getNewMultipleObjectsList(),
                userToken,
                multipleDetails.getCaseData());
        verifyNoMoreInteractions(excelDocManagementService);
    }

    private List<MultipleObject> getMultipleObjectsList() {
        return new ArrayList<>(Arrays.asList(
                MultipleObject.builder()
                        .subMultiple("245000")
                        .ethosCaseRef("245000/2020")
                        .flag1("AA")
                        .flag2("BB")
                        .flag3("")
                        .flag4("")
                        .build(),
                MultipleObject.builder()
                        .subMultiple("")
                        .ethosCaseRef("245001/2020")
                        .flag1("")
                        .flag2("")
                        .flag3("")
                        .flag4("")
                        .build(),
                MultipleObject.builder()
                        .subMultiple("245003")
                        .ethosCaseRef("245003/2020")
                        .flag1("AA")
                        .flag2("EE")
                        .flag3("")
                        .flag4("")
                        .build(),
                MultipleObject.builder()
                        .subMultiple("245002")
                        .ethosCaseRef("245004/2020")
                        .flag1("AA")
                        .flag2("BB")
                        .flag3("")
                        .flag4("")
                        .build(),
                MultipleObject.builder()
                        .subMultiple("SubMultiple")
                        .ethosCaseRef("245005/2020")
                        .flag1("AA")
                        .flag2("BB")
                        .flag3("")
                        .flag4("")
                        .build()));
    }

    private List<MultipleObject> getNewMultipleObjectsList() {
        return new ArrayList<>(Arrays.asList(
                MultipleObject.builder()
                        .subMultiple("")
                        .ethosCaseRef("245000/2020")
                        .flag1("")
                        .flag2("")
                        .flag3("")
                        .flag4("")
                        .build(),
                MultipleObject.builder()
                        .subMultiple("")
                        .ethosCaseRef("245001/2020")
                        .flag1("")
                        .flag2("")
                        .flag3("")
                        .flag4("")
                        .build()));
    }

}