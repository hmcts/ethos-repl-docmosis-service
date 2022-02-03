package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
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

    private CCDRequest ccdRequest;
    private String userToken;
    private String multipleReference;
    private String urlLinkMarkUp;
    private long ccdReference;

    @Before
    public void setUp() {
        userToken = "authToken";
        multipleReference = "246001";
        ccdReference = 1643639063185009L;

        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseTypeId(MANCHESTER_CASE_TYPE_ID);
        caseDetails.setCaseId(String.valueOf(ccdReference));
        caseDetails.setCaseData(MultipleUtil.getCaseDataForSinglesToBeMoved());
        caseDetails.getCaseData().setMultipleReference(multipleReference);

        ccdRequest = new CCDRequest();
        ccdRequest.setCaseDetails(caseDetails);

        List<SubmitMultipleEvent> submitMultipleEvents = MultipleUtil.getSubmitMultipleEvents();
        submitMultipleEvents.get(0).setCaseId(ccdReference);
        submitMultipleEvents.get(0).getCaseData().setMultipleReference(multipleReference);

        urlLinkMarkUp = MultiplesHelper.generateMarkUp(null,
                String.valueOf(ccdReference), caseDetails.getCaseData().getMultipleReference());

        when(multipleCasesReadingService.retrieveMultipleCasesCcdReference(userToken,
                MANCHESTER_BULK_CASE_TYPE_ID,
                String.valueOf(ccdReference))
        ).thenReturn(submitMultipleEvents);

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                MANCHESTER_BULK_CASE_TYPE_ID,
                multipleReference)
        ).thenReturn(submitMultipleEvents);
    }

    @Test
    public void checkUpdateMultipleReference_CcdReference() {
        ccdRequest.getCaseDetails().getCaseData().setMultipleReference(String.valueOf(ccdReference));
        ccdRequest.getCaseDetails().getCaseData().setMultipleReferenceLinkMarkUp(null);

        fixCaseApiService.checkUpdateMultipleReference(ccdRequest, userToken);

        assertEquals(multipleReference,
                ccdRequest.getCaseDetails().getCaseData().getMultipleReference());
        assertEquals(urlLinkMarkUp,
                ccdRequest.getCaseDetails().getCaseData().getMultipleReferenceLinkMarkUp());
    }

    @Test
    public void checkUpdateMultipleReference_MultipleReference() {
        ccdRequest.getCaseDetails().getCaseData().setMultipleReferenceLinkMarkUp(null);

        fixCaseApiService.checkUpdateMultipleReference(ccdRequest, userToken);

        assertEquals(multipleReference,
                ccdRequest.getCaseDetails().getCaseData().getMultipleReference());
        assertEquals(urlLinkMarkUp,
                ccdRequest.getCaseDetails().getCaseData().getMultipleReferenceLinkMarkUp());
    }
}