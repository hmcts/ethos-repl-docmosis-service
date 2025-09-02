package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.HelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;

@RunWith(SpringJUnit4ClassRunner.class)
public class PersistentQHelperServiceTest {

    @InjectMocks
    private PersistentQHelperService persistentQHelperService;
    @Mock
    private CreateUpdatesBusSender createUpdatesBusSender;
    @Mock
    private UserService userService;

    private CCDRequest ccdRequest;
    private String userToken;

    @Before
    public void setUp() {
        ccdRequest = new CCDRequest();
        CaseData caseData = MultipleUtil.getCaseData("2123456/2020");
        caseData.setCaseRefNumberCount("2");
        caseData.setPositionTypeCT("PositionTypeCT");
        DynamicFixedListType officeCT = new DynamicFixedListType();
        DynamicValueType valueType = new DynamicValueType();
        valueType.setCode(LEEDS_CASE_TYPE_ID);
        officeCT.setValue(valueType);
        caseData.setOfficeCT(officeCT);
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(caseData);
        caseDetails.setCaseTypeId("Manchester");
        caseDetails.setJurisdiction("Employment");
        caseDetails.setState(ACCEPTED_STATE);
        ccdRequest.setCaseDetails(caseDetails);
        userToken = "authToken";
    }

    @Test
    public void sendCreationEventToSinglesWithoutConfirmation() {

        when(userService.getUserDetails("authToken")).thenReturn(HelperTest.getUserDetails());

        persistentQHelperService.sendCreationEventToSingles(userToken,
                ccdRequest.getCaseDetails().getCaseTypeId(), ccdRequest.getCaseDetails().getJurisdiction(),
                new ArrayList<>(), new ArrayList<>(Collections.singletonList("ethosCaseReference")),
                LEEDS_CASE_TYPE_ID,
                "positionTypeCT", "ccdGatewayBaseUrl", "",
                SINGLE_CASE_TYPE, NO,
                MultiplesHelper.generateMarkUp("ccdGatewayBaseUrl",
                        ccdRequest.getCaseDetails().getCaseId(),
                        ccdRequest.getCaseDetails().getCaseData().getMultipleRefNumber()));

        verify(userService).getUserDetails(userToken);
        verifyNoMoreInteractions(userService);

    }

    @Test
    public void sendCreateEventToReformECMWithoutConfirmation() {

        when(userService.getUserDetails("authToken")).thenReturn(HelperTest.getUserDetails());

        persistentQHelperService.sendCreationEventToSinglesReformECM(userToken,
                ccdRequest.getCaseDetails().getCaseTypeId(), ccdRequest.getCaseDetails().getJurisdiction(),
                new ArrayList<>(), new ArrayList<>(Collections.singletonList("ethosCaseReference")),
                LEEDS_CASE_TYPE_ID,
                "positionTypeCT", "ccdGatewayBaseUrl", "",
                SINGLE_CASE_TYPE, NO,
                MultiplesHelper.generateMarkUp("ccdGatewayBaseUrl",
                        ccdRequest.getCaseDetails().getCaseId(),
                        ccdRequest.getCaseDetails().getCaseData().getMultipleRefNumber())
        );

        verify(userService).getUserDetails(userToken);
        verifyNoMoreInteractions(userService);

    }

}