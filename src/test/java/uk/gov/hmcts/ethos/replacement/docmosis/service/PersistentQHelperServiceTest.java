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
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.HelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;

@RunWith(SpringJUnit4ClassRunner.class)
public class PersistentQHelperServiceTest {

    @InjectMocks
    private PersistentQHelperService persistentQHelperService;
    @Mock
    private CreateUpdatesBusSender createUpdatesBusSender;
    @Mock
    private UserService userService;

    private CCDRequest ccdRequest;
    private SubmitEvent submitEvent;
    private String userToken;

    @Before
    public void setUp() {
        ccdRequest = new CCDRequest();
        CaseDetails caseDetails = new CaseDetails();
        CaseData caseData = MultipleUtil.getCaseData("2123456/2020");
        caseData.setCaseRefNumberCount("2");
        caseData.setPositionTypeCT("PositionTypeCT");
        DynamicFixedListType officeCT = new DynamicFixedListType();
        DynamicValueType valueType = new DynamicValueType();
        valueType.setCode(LEEDS_CASE_TYPE_ID);
        officeCT.setValue(valueType);
        caseData.setOfficeCT(officeCT);
        caseDetails.setCaseData(caseData);
        caseDetails.setCaseTypeId("Manchester");
        caseDetails.setJurisdiction("Employment");
        caseDetails.setState(ACCEPTED_STATE);
        ccdRequest.setCaseDetails(caseDetails);
        submitEvent = new SubmitEvent();
        userToken = "authToken";
    }

    @Test
    public void sendCreationEventToSinglesWithoutConfirmation() {
        when(userService.getUserDetails("authToken")).thenReturn(HelperTest.getUserDetails());
        persistentQHelperService.sendCreationEventToSinglesWithoutConfirmation(userToken,
                ccdRequest.getCaseDetails().getCaseTypeId(), ccdRequest.getCaseDetails().getJurisdiction(),
                new ArrayList<>(), "ethosCaseReference", LEEDS_CASE_TYPE_ID,
                "positionTypeCT", "ccdGatewayBaseUrl");
        verify(userService).getUserDetails(userToken);
        verifyNoMoreInteractions(userService);
    }

}