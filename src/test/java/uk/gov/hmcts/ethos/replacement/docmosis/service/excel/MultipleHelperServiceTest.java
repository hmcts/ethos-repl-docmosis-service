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
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgementType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeC;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.HelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TRANSFERRED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

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
    private String gatewayURL;

    @Before
    public void setUp() {
        gatewayURL = "https://manage-case.test.platform.hmcts.net";
        ReflectionTestUtils.setField(multipleHelperService, "ccdGatewayBaseUrl", gatewayURL);
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        multipleDetails.setCaseTypeId("Manchester_Multiple");
        multipleDetails.setCaseId("12121212");
        submitEventList = MultipleUtil.getSubmitEvents();
        UserDetails userDetails = HelperTest.getUserDetails();
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        userToken = "authString";
        submitMultipleEvents = MultipleUtil.getSubmitMultipleEvents();
        multipleObjects = MultipleUtil.getMultipleObjectsAll();
    }

    @Test
    public void addLeadMarkUp() {
        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getLeadCase(),
                multipleDetails.getCaseData().getMultipleSource()))
                .thenReturn(submitEventList.getFirst());
        multipleHelperService.addLeadMarkUp(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData(),
                multipleDetails.getCaseData().getLeadCase(),
                "");
        assertEquals("<a target=\"_blank\" href=\"" + gatewayURL + "/cases/case-details/1232121232\">21006/2020</a>",
                multipleDetails.getCaseData().getLeadCase());
    }

    @Test
    public void addLeadMarkUpWithCaseId() {
        submitEventList.getFirst().setCaseId(12345L);
        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getLeadCase(),
                multipleDetails.getCaseData().getMultipleSource()))
                .thenReturn(submitEventList.getFirst());
        multipleHelperService.addLeadMarkUp(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData(),
                multipleDetails.getCaseData().getLeadCase(),
                "12345");
        assertEquals("<a target=\"_blank\" href=\"" + gatewayURL + "/cases/case-details/12345\">21006/2020</a>",
                multipleDetails.getCaseData().getLeadCase());
    }

    @Test
    public void addLeadMarkUpEmptyCase() {

        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getLeadCase(),
                multipleDetails.getCaseData().getMultipleSource()))
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

        assertEquals("Sub multiple SubMultiple3 does not exist in 246001", errors.getFirst());

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

        submitMultipleEvents.getFirst().getCaseData().setSubMultipleCollection(null);

        multipleHelperService.validateExternalMultipleAndSubMultiple(userToken,
                multipleDetails.getCaseTypeId(),
                multipleReference,
                subMultipleName,
                errors);

        assertEquals("Sub multiple SubMultiple3 does not exist in 246001", errors.getFirst());

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

        assertEquals("Multiple 246002 does not exist", errors.getFirst());

    }

    @Test
    public void multipleValidationLogicMultipleTransferred() {

        List<String> errors = new ArrayList<>();

        String multipleReference = "246001";
        String subMultipleName = "SubMultiple";

        submitMultipleEvents.getFirst().setState(TRANSFERRED_STATE);
        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleReference)
        ).thenReturn(submitMultipleEvents);

        multipleHelperService.validateExternalMultipleAndSubMultiple(userToken,
                multipleDetails.getCaseTypeId(),
                multipleReference,
                subMultipleName,
                errors);

        assertEquals(1, errors.size());
        assertEquals("Multiple 246001 has been transferred. "
                + "The case cannot be moved to this multiple", errors.getFirst());

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
                "",
                multipleDetails.getCaseId()
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
    public void sendResetMultipleStateWithoutConfirmation() {

        multipleHelperService.sendResetMultipleStateWithoutConfirmation(
                userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getJurisdiction(),
                multipleDetails.getCaseData(),
                new ArrayList<>(),
                multipleDetails.getCaseId()
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
        submitEventList.getFirst().getCaseData().setRepresentativeClaimantType(representedTypeC);

        JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJuridictionCodesList("AA");
        jurCodesTypeItem.setValue(jurCodesType);
        submitEventList.getFirst().getCaseData().setJurCodesCollection(
            new ArrayList<>(Collections.singletonList(jurCodesTypeItem)));

        JudgementTypeItem judgementTypeItem = new JudgementTypeItem();
        JudgementType judgementType = new JudgementType();
        judgementType.setJudgementType("JudgementType");
        judgementType.setDateJudgmentMade("25/01/2021");
        judgementTypeItem.setValue(judgementType);
        judgementTypeItem.setId("JD");
        submitEventList.getFirst().getCaseData().setJudgementCollection(
            new ArrayList<>(Collections.singletonList(judgementTypeItem)));

        multipleDetails.getCaseData().setBatchUpdateClaimantRep(MultipleUtil.generateDynamicList("Rep"));
        multipleDetails.getCaseData().setBatchUpdateJurisdiction(MultipleUtil.generateDynamicList("AA"));
        multipleDetails.getCaseData().setBatchUpdateRespondent(MultipleUtil.generateDynamicList("Andrew Smith"));
        multipleDetails.getCaseData().setBatchUpdateJudgment(MultipleUtil.generateDynamicList("JD"));
        multipleDetails.getCaseData().setBatchUpdateRespondentRep(MultipleUtil.generateDynamicList("1"));

        multipleHelperService.sendUpdatesToSinglesWithConfirmation(
                userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjects,
                submitEventList.getFirst().getCaseData()
        );

        verify(userService).getUserDetails(userToken);
        verifyNoMoreInteractions(userService);

    }

    @Test
    public void sendPreAcceptToSinglesWithConfirmation() {

        CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
        casePreAcceptType.setCaseAccepted(YES);
        casePreAcceptType.setDateAccepted("2021-02-23");
        multipleDetails.getCaseData().setPreAcceptCase(casePreAcceptType);

        multipleHelperService.sendPreAcceptToSinglesWithConfirmation(
                userToken,
                multipleDetails,
                new ArrayList<>()
        );

        verify(userService).getUserDetails(userToken);
        verifyNoMoreInteractions(userService);

    }

    @Test
    public void sendPreAcceptRejectedToSinglesWithConfirmation() {

        CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
        casePreAcceptType.setCaseAccepted(NO);
        casePreAcceptType.setDateRejected("2021-02-23");
        casePreAcceptType.setRejectReason(new ArrayList<>());
        multipleDetails.getCaseData().setPreAcceptCase(casePreAcceptType);

        multipleHelperService.sendRejectToSinglesWithConfirmation(
                userToken,
                multipleDetails,
                new ArrayList<>()
        );

        verify(userService).getUserDetails(userToken);
        verifyNoMoreInteractions(userService);

    }

    @Test
    public void sendCloseToSinglesWithConfirmation() {

        multipleDetails.getCaseData().setClerkResponsible("Clerk");
        multipleDetails.getCaseData().setFileLocation("FileLocation");
        multipleDetails.getCaseData().setNotes("Notes");

        multipleHelperService.sendCloseToSinglesWithoutConfirmation(
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

    @Test
    public void sendUpdatesToSinglesLogicCheckingLead() {

        String leadLink = "<a target=\"_blank\" href=\"https://www-ccd.perftest.platform.hmcts.net/v2/case/1604313560561842\">245007/2020</a>";
        multipleDetails.getCaseData().setLeadCase(leadLink);
        String newLeadCase = "245000/2020";
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setCaseId(10561843);
        when(singleCasesReadingService.retrieveSingleCase(userToken, multipleDetails.getCaseTypeId(),
                newLeadCase, multipleDetails.getCaseData().getMultipleSource()))
                .thenReturn(submitEvent);

        multipleHelperService.sendUpdatesToSinglesLogic(
                userToken,
                multipleDetails,
                new ArrayList<>(),
                newLeadCase,
                multipleObjects,
                new ArrayList<>(Arrays.asList("245008/2020", "245009/2020")));

        assertEquals("<a target=\"_blank\" href=\"" + gatewayURL + "/cases/case-details/10561843\">245000/2020</a>",
                multipleDetails.getCaseData().getLeadCase());

    }

    @Test
    public void sendUpdatesToSinglesLogicCheckingSameLead() {

        String leadLink = "<a target=\"_blank\" href=\"https://www-ccd.perftest.platform.hmcts.net/v2/case/1604313560561842\">245007/2020</a>";
        multipleDetails.getCaseData().setLeadCase(leadLink);
        String newLeadCase = "245007/2020";

        multipleHelperService.sendUpdatesToSinglesLogic(
                userToken,
                multipleDetails,
                new ArrayList<>(),
                newLeadCase,
                multipleObjects,
                new ArrayList<>());

        assertEquals(leadLink, multipleDetails.getCaseData().getLeadCase());

    }

}