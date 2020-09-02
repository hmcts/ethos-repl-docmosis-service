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
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ET1_ONLINE_CASE_SOURCE;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleCreationServiceTest {

    @Mock
    private CreateUpdatesBusSender createUpdatesBusSender;
    @Mock
    private ExcelDocManagementService excelDocManagementService;
    @Mock
    private UserService userService;
    @InjectMocks
    private MultipleCreationService multipleCreationService;

    private MultipleDetails multipleDetails;
    private List<String> ethosCaseRefCollection;
    private String userToken;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        ethosCaseRefCollection = MultiplesHelper.getCaseIds(multipleDetails.getCaseData());
        UserDetails userDetails = HelperTest.getUserDetails();
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        userToken = "authString";
    }

    @Test
    public void bulkCreationLogic() {
        multipleCreationService.bulkCreationLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(ethosCaseRefCollection,
                userToken,
                multipleDetails.getCaseData());
        verifyNoMoreInteractions(excelDocManagementService);
        verify(userService).getUserDetails(userToken);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void bulkCreationLogicETOnline() {
        multipleDetails.getCaseData().setMultipleSource(ET1_ONLINE_CASE_SOURCE);
        multipleCreationService.bulkCreationLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(ethosCaseRefCollection,
                userToken,
                multipleDetails.getCaseData());
        verifyNoMoreInteractions(excelDocManagementService);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void bulkCreationLogicEmptyCaseIdCollection() {
        multipleDetails.getCaseData().setCaseIdCollection(new ArrayList<>());
        multipleCreationService.bulkCreationLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(new ArrayList<>(),
                userToken,
                multipleDetails.getCaseData());
        verifyNoMoreInteractions(excelDocManagementService);
        verifyNoMoreInteractions(userService);
    }
}