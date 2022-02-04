package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleCasesReadingService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;

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
        submitMultipleEvents.get(0).getCaseData().setMultipleReference(multipleReference);
        submitMultipleEvents.get(0).setCaseId(ccdReference);

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                MANCHESTER_BULK_CASE_TYPE_ID,
                multipleReference)
        ).thenReturn(submitMultipleEvents);

        urlLinkMarkUp = MultiplesHelper.generateMarkUp(null,
                String.valueOf(ccdReference), caseDetails.getCaseData().getMultipleReference());
    }

    @Test
    public void checkUpdateMultipleReference_LinkMarkUp_Normal() {
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
}