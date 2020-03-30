package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.CasePreAcceptType;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class CaseManagementForCaseWorkerServiceTest {

    @InjectMocks
    private CaseManagementForCaseWorkerService caseManagementForCaseWorkerService;
    private CCDRequest manchesterCcdRequest;

    @Before
    public void setUp() {
        manchesterCcdRequest = new CCDRequest();
        CaseDetails manchesterCaseDetails = new CaseDetails();
        CaseData caseData = new CaseData();
        CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
        casePreAcceptType.setCaseAccepted("Yes");
        caseData.setPreAcceptCase(casePreAcceptType);
        manchesterCaseDetails.setCaseData(caseData);
        manchesterCaseDetails.setCaseId("123456");
        manchesterCaseDetails.setCaseTypeId(MANCHESTER_DEV_CASE_TYPE_ID);
        manchesterCaseDetails.setJurisdiction("TRIBUNALS");
        manchesterCcdRequest.setCaseDetails(manchesterCaseDetails);
    }

    @Test
    public void preAcceptCaseAccepted() {
        assertEquals(ACCEPTED_STATE, caseManagementForCaseWorkerService.preAcceptCase(manchesterCcdRequest).getState());
    }

    @Test
    public void preAcceptCaseRejected() {
        manchesterCcdRequest.getCaseDetails().getCaseData().getPreAcceptCase().setCaseAccepted("No");
        assertEquals(REJECTED_STATE, caseManagementForCaseWorkerService.preAcceptCase(manchesterCcdRequest).getState());
    }

}