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

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TYPE_AMENDMENT_ADDITION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TYPE_AMENDMENT_LEAD;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleAmendServiceTest {

    @Mock
    private ExcelReadingService excelReadingService;
    @Mock
    private ExcelDocManagementService excelDocManagementService;
    @Mock
    private MultipleAmendLeadCaseService multipleAmendLeadCaseService;
    @Mock
    private MultipleAmendCaseIdsService multipleAmendCaseIdsService;
    @InjectMocks
    private MultipleAmendService multipleAmendService;

    private TreeMap<String, Object> multipleObjects;
    private MultipleDetails multipleDetails;
    private String userToken;
    private List<String> typeOfAmendmentMSL;

    @Before
    public void setUp() {
        typeOfAmendmentMSL = new ArrayList<>(Arrays.asList(TYPE_AMENDMENT_LEAD, TYPE_AMENDMENT_ADDITION));
        multipleObjects = MultipleUtil.getMultipleObjectsAll();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        userToken = "authString";
    }

    @Test
    public void bulkAmendMultipleLogic() {
        multipleDetails.getCaseData().setTypeOfAmendmentMSL(typeOfAmendmentMSL);
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);
        when(multipleAmendCaseIdsService.bulkAmendCaseIdsLogic(anyString(), any(), anyList(), any()))
                .thenReturn((getMultipleObjectsList()));
        multipleAmendService.bulkAmendMultipleLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(
                getMultipleObjectsList(),
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
                        .ethosCaseRef("245005/2020")
                        .subMultiple("SubMultiple")
                        .flag1("AA")
                        .flag2("BB")
                        .flag3("")
                        .flag4("")
                        .build()));
    }

}