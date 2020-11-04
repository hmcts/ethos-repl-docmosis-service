package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeC;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.HelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleHelperServiceTest {

    @Mock
    private SingleCasesReadingService singleCasesReadingService;
    @Mock
    private MultipleCasesReadingService multipleCasesReadingService;
    @Mock
    private ExcelReadingService excelReadingService;
    @Mock
    private ExcelDocManagementService excelDocManagementService;
    @Mock
    private MultipleCasesSendingService multipleCasesSendingService;
    @Mock
    private CreateUpdatesBusSender createUpdatesBusSender;
    @Mock
    private UserService userService;

    @InjectMocks
    private MultipleHelperService multipleHelperService;

    private MultipleDetails multipleDetails;
    private String userToken;
    private List<SubmitEvent> submitEventList;
    private List<SubmitMultipleEvent> submitMultipleEvents;
    private TreeMap<String, Object> multipleObjects;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        multipleDetails.setCaseTypeId("Manchester_Multiple");
        multipleDetails.setCaseId("12121212");
        submitEventList = MultipleUtil.getSubmitEvents();
        UserDetails userDetails = HelperTest.getUserDetails();
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        userToken = "authString";
        ReflectionTestUtils.setField(multipleHelperService, "ccdGatewayBaseUrl", "http://www-demo.ccd/dm-store:8080/v2/case/");
        submitMultipleEvents = MultipleUtil.getSubmitMultipleEvents();
        multipleObjects = MultipleUtil.getMultipleObjectsAll();
    }

    @Test
    public void addLeadMarkUp() {
        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getLeadCase()))
                .thenReturn(submitEventList.get(0));
        multipleHelperService.addLeadMarkUp(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData(),
                multipleDetails.getCaseData().getLeadCase(),
                "");
        assertEquals("<a target=\"_blank\" href=\"http://www-demo.ccd/dm-store:8080/v2/case//v2/case/1232121232\">21006/2020</a>",
                multipleDetails.getCaseData().getLeadCase());
    }

    @Test
    public void addLeadMarkUpWithCaseId() {
        submitEventList.get(0).setCaseId(12345L);
        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getLeadCase()))
                .thenReturn(submitEventList.get(0));
        multipleHelperService.addLeadMarkUp(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData(),
                multipleDetails.getCaseData().getLeadCase(),
                "12345");
        assertEquals("<a target=\"_blank\" href=\"http://www-demo.ccd/dm-store:8080/v2/case//v2/case/12345\">21006/2020</a>",
                multipleDetails.getCaseData().getLeadCase());
    }

    @Test
    public void addLeadMarkUpEmptyCase() {
        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getLeadCase()))
                .thenReturn(null);
        multipleHelperService.addLeadMarkUp(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData(),
                multipleDetails.getCaseData().getLeadCase(),
                "");
        assertEquals("21006/2020", multipleDetails.getCaseData().getLeadCase());
    }

    @Test
    public void multipleValidationLogicMultipleAndSubExist() {

        List<String> errors = new ArrayList<>();

        String multipleReference = "246001";
        String subMultipleName = "SubMultiple";

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleReference)
        ).thenReturn(submitMultipleEvents);

        multipleHelperService.validateExternalMultipleAndSubMultiple(userToken,
                multipleDetails.getCaseTypeId(),
                multipleReference,
                subMultipleName,
                errors);

        assertEquals(0, errors.size());

    }

    @Test
    public void multipleValidationLogicSubMultipleDoesNotExist() {

        List<String> errors = new ArrayList<>();

        String multipleReference = "246001";
        String subMultipleName = "SubMultiple3";

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleReference)
        ).thenReturn(submitMultipleEvents);

        multipleHelperService.validateExternalMultipleAndSubMultiple(userToken,
                multipleDetails.getCaseTypeId(),
                multipleReference,
                subMultipleName,
                errors);

        assertEquals("Sub multiple SubMultiple3 does not exist in 246001", errors.get(0));

    }

    @Test
    public void multipleValidationLogicSubMultipleNull() {

        List<String> errors = new ArrayList<>();

        String multipleReference = "246001";
        String subMultipleName = "SubMultiple3";

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleReference)
        ).thenReturn(submitMultipleEvents);

        submitMultipleEvents.get(0).getCaseData().setSubMultipleCollection(null);

        multipleHelperService.validateExternalMultipleAndSubMultiple(userToken,
                multipleDetails.getCaseTypeId(),
                multipleReference,
                subMultipleName,
                errors);

        assertEquals("Sub multiple SubMultiple3 does not exist in 246001", errors.get(0));

    }

    @Test
    public void multipleValidationLogicMultipleDoesNotExist() {

        List<String> errors = new ArrayList<>();

        String multipleReference = "246002";
        String subMultipleName = "SubMultiple3";

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleReference)
        ).thenReturn(new ArrayList<>());

        multipleHelperService.validateExternalMultipleAndSubMultiple(userToken,
                multipleDetails.getCaseTypeId(),
                multipleReference,
                subMultipleName,
                errors);

        assertEquals("Multiple 246002 does not exist", errors.get(0));

    }

    @Test
    public void moveCasesAndSendUpdateToMultiple() {

        String subMultipleName = "SubMultiple3";

        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);

        multipleHelperService.moveCasesAndSendUpdateToMultiple(userToken,
                subMultipleName,
                multipleDetails.getJurisdiction(),
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseId(),
                multipleDetails.getCaseData(),
                new ArrayList<>(Arrays.asList("245002/2020", "245003/2020")),
                new ArrayList<>());

        verify(excelDocManagementService, times(1)).generateAndUploadExcel(
                anyList(),
                anyString(),
                any());
        verifyNoMoreInteractions(excelDocManagementService);

        verify(multipleCasesSendingService, times(1)).sendUpdateToMultiple(
                userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                multipleDetails.getCaseData(),
                multipleDetails.getCaseId());
        verifyNoMoreInteractions(multipleCasesSendingService);

    }

    @Test
    public void moveCasesAndSendUpdateToMultipleWithoutSubMultiple() {

        String subMultipleName = "";

        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);

        multipleHelperService.moveCasesAndSendUpdateToMultiple(userToken,
                subMultipleName,
                multipleDetails.getJurisdiction(),
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseId(),
                multipleDetails.getCaseData(),
                new ArrayList<>(Arrays.asList("245002/2020", "245003/2020")),
                new ArrayList<>());

        verify(excelDocManagementService, times(1)).generateAndUploadExcel(
                anyList(),
                anyString(),
                any());
        verifyNoMoreInteractions(excelDocManagementService);

        verify(multipleCasesSendingService, times(1)).sendUpdateToMultiple(
                userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                multipleDetails.getCaseData(),
                multipleDetails.getCaseId());
        verifyNoMoreInteractions(multipleCasesSendingService);

    }

    @Test
    public void sendCreationUpdatesToSinglesWithoutConfirmation() {

        multipleHelperService.sendCreationUpdatesToSinglesWithoutConfirmation(
                userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                multipleDetails.getCaseData(),
                new ArrayList<>(),
                new ArrayList<>(),
                ""
        );

        verify(userService).getUserDetails(userToken);
        verifyNoMoreInteractions(userService);

    }

    @Test
    public void sendDetachUpdatesToSinglesWithoutConfirmation() {

        multipleHelperService.sendDetachUpdatesToSinglesWithoutConfirmation(
                userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjects
        );

        verify(userService).getUserDetails(userToken);
        verifyNoMoreInteractions(userService);

    }

    @Test
    public void sendUpdatesToSinglesWithConfirmationNullCaseData() {

        multipleHelperService.sendUpdatesToSinglesWithConfirmation(
                userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjects,
                null
        );

        verify(userService).getUserDetails(userToken);
        verifyNoMoreInteractions(userService);

    }

    @Test
    public void sendUpdatesToSinglesWithConfirmation() {

        RepresentedTypeC representedTypeC = new RepresentedTypeC();
        representedTypeC.setNameOfRepresentative("Rep");
        submitEventList.get(0).getCaseData().setRepresentativeClaimantType(representedTypeC);

        JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJuridictionCodesList("AA");
        jurCodesTypeItem.setValue(jurCodesType);
        submitEventList.get(0).getCaseData().setJurCodesCollection(new ArrayList<>(Collections.singletonList(jurCodesTypeItem)));

        multipleDetails.getCaseData().setBatchUpdateClaimantRep(MultipleUtil.generateDynamicList("Rep"));
        multipleDetails.getCaseData().setBatchUpdateJurisdiction(MultipleUtil.generateDynamicList("AA"));
        multipleDetails.getCaseData().setBatchUpdateRespondent(MultipleUtil.generateDynamicList("Andrew Smith"));

        multipleHelperService.sendUpdatesToSinglesWithConfirmation(
                userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjects,
                submitEventList.get(0).getCaseData()
        );

        verify(userService).getUserDetails(userToken);
        verifyNoMoreInteractions(userService);

    }

    @Test
    public void sendPreAcceptToSinglesWithConfirmation() {

        multipleHelperService.sendPreAcceptToSinglesWithConfirmation(
                userToken,
                multipleDetails,
                new ArrayList<>()
        );

        verify(userService).getUserDetails(userToken);
        verifyNoMoreInteractions(userService);

    }

    @Test
    public void getLeadCaseFromExcel() {

        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjects);

        assertEquals("245000/2020", multipleHelperService.getLeadCaseFromExcel(
                userToken,
                multipleDetails.getCaseData(),
                new ArrayList<>()));

    }

    @Test
    public void getEmptyLeadCaseFromExcel() {

        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(new TreeMap<>());

        assertEquals("", multipleHelperService.getLeadCaseFromExcel(
                userToken,
                multipleDetails.getCaseData(),
                new ArrayList<>()));

    }

}