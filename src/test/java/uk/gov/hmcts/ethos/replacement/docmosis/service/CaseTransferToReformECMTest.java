package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.REJECTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SUBMITTED_STATE;

@RunWith(SpringJUnit4ClassRunner.class)
public class CaseTransferToReformECMTest {

    @InjectMocks
    private CaseTransferToReformECM caseTransferToReformECM;
    @Mock
    private CcdClient ccdClient;
    private CCDRequest ccdRequest;
    private SubmitEvent submitEvent;
    private String authToken;

    @Mock
    private PersistentQHelperService persistentQHelperService;

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
        submitEvent.setCaseData(caseData);
        submitEvent.setCaseId(12345);
        authToken = "authToken";
    }
/*
    @Test
    public void createCaseTransferToReformECM() {
        var errors = caseTransferToReformECM.createCaseTransferToReformECM(ccdRequest.getCaseDetails(), authToken);
        assertTrue(errors.isEmpty());
        assertEquals("PositionTypeCT", ccdRequest.getCaseDetails().getCaseData().getPositionType());
        assertEquals("Transferred to " + LEEDS_CASE_TYPE_ID,
                ccdRequest.getCaseDetails().getCaseData().getLinkedCaseCT());
    }

    @Test
    public void createCaseTransferCaseWithInValidState() {
        ccdRequest.getCaseDetails().setState(REJECTED_STATE);

        var errors = caseTransferToReformECM.createCaseTransferToReformECM(ccdRequest.getCaseDetails(), authToken);

        var expectedError = String.format(CaseTransferToReformECM.CASE_STATE_ERROR_MSG,
                ccdRequest.getCaseDetails().getCaseData().getEthosCaseReference());
        assertEquals(expectedError, errors.get(0));
    }
*/
    @Test
    public void createCaseTransferCaseWithValidState() {
        ccdRequest.getCaseDetails().setState(SUBMITTED_STATE);
        var errors = caseTransferToReformECM.createCaseTransferToReformECM(ccdRequest.getCaseDetails(), authToken);
        assertEquals(0, errors.size());
    }
}
