package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleCasesReadingService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;

@RunWith(SpringJUnit4ClassRunner.class)
public class FixCaseApiServiceTest {

    @Mock
    private MultipleCasesReadingService multipleCasesReadingService;
    @InjectMocks
    private FixCaseApiService fixCaseApiService;

    private CaseDetails caseDetails;
    private String userToken;
    private String urlLinkMarkUp;
    private long ccdReference;

    @Before
    public void setUp() {
        userToken = "authToken";
        ccdReference = 1643639063185009L;
        String multipleReference = "246001";

        caseDetails = new CaseDetails();
        caseDetails.setCaseData(MultipleUtil.getCaseDataForSinglesToBeMoved());
        caseDetails.getCaseData().setMultipleReference(multipleReference);
        caseDetails.setCaseTypeId(MANCHESTER_CASE_TYPE_ID);
        caseDetails.setCaseId(String.valueOf(ccdReference));

        List<SubmitMultipleEvent> submitMultipleEvents = MultipleUtil.getSubmitMultipleEvents();
        submitMultipleEvents.getFirst().getCaseData().setMultipleReference(multipleReference);
        submitMultipleEvents.getFirst().setCaseId(ccdReference);

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                MANCHESTER_BULK_CASE_TYPE_ID,
                caseDetails.getCaseData().getMultipleReference())
        ).thenReturn(submitMultipleEvents);

        urlLinkMarkUp = MultiplesHelper.generateMarkUp(null,
                String.valueOf(ccdReference), caseDetails.getCaseData().getMultipleReference());
    }

    @Test
    public void checkUpdateMultipleReference_LinkMarkUp_Normal() {
        caseDetails.getCaseData().setMultipleReferenceLinkMarkUp(urlLinkMarkUp);
        fixCaseApiService.checkUpdateMultipleReference(caseDetails, userToken);
        assertEquals(urlLinkMarkUp, caseDetails.getCaseData().getMultipleReferenceLinkMarkUp());
    }

    @Test
    public void checkUpdateMultipleReference_LinkMarkUp_Null() {
        caseDetails.getCaseData().setMultipleReferenceLinkMarkUp(null);
        fixCaseApiService.checkUpdateMultipleReference(caseDetails, userToken);
        assertEquals(urlLinkMarkUp, caseDetails.getCaseData().getMultipleReferenceLinkMarkUp());
    }

    @Test
    public void checkUpdateMultipleReference_LinkMarkUp_CcdReference() {
        caseDetails.getCaseData().setMultipleReferenceLinkMarkUp(String.valueOf(ccdReference));
        fixCaseApiService.checkUpdateMultipleReference(caseDetails, userToken);
        assertEquals(urlLinkMarkUp, caseDetails.getCaseData().getMultipleReferenceLinkMarkUp());
    }

    @Test
    public void checkUpdateMultipleReference_EcmCaseType_getCaseData() {
        CaseData caseData = MultipleUtil.getCaseData("245000/2021");
        caseDetails.setCaseData(caseData);
        fixCaseApiService.checkUpdateMultipleReference(caseDetails, userToken);
        assertNull(caseDetails.getCaseData().getMultipleReferenceLinkMarkUp());
    }

    @Test
    public void checkUpdateMultipleReference_EcmCaseType_Null() {
        caseDetails.getCaseData().setEcmCaseType(null);
        fixCaseApiService.checkUpdateMultipleReference(caseDetails, userToken);
        assertNull(caseDetails.getCaseData().getMultipleReferenceLinkMarkUp());
    }

    @Test
    public void checkUpdateMultipleReference_EcmCaseType_Single() {
        caseDetails.getCaseData().setEcmCaseType(SINGLE_CASE_TYPE);
        fixCaseApiService.checkUpdateMultipleReference(caseDetails, userToken);
        assertNull(caseDetails.getCaseData().getMultipleReferenceLinkMarkUp());
    }

    @Test
    public void checkUpdateMultipleReference_submitMultipleEvents_Null() {
        List<SubmitMultipleEvent> newSubmitMultipleEvents = new ArrayList<>();
        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                MANCHESTER_BULK_CASE_TYPE_ID,
                caseDetails.getCaseData().getMultipleReference())
        ).thenReturn(newSubmitMultipleEvents);
        fixCaseApiService.checkUpdateMultipleReference(caseDetails, userToken);
        assertNull(caseDetails.getCaseData().getMultipleReferenceLinkMarkUp());
    }

    @Test
    public void checkUpdateParentMultipleIdSetToNull() {
        caseDetails.getCaseData().setParentMultipleCaseId("blah");
        fixCaseApiService.checkUpdateMultipleReference(caseDetails, userToken);
        assertNull(caseDetails.getCaseData().getParentMultipleCaseId());
    }

}