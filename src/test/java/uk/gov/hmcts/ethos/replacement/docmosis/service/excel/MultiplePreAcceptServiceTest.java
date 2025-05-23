package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ET1_ONLINE_CASE_SOURCE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANUALLY_CREATED_POSITION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultiplePreAcceptServiceTest {

    @Mock
    private MultipleHelperService multipleHelperService;
    @InjectMocks
    private MultiplePreAcceptService multiplePreAcceptService;

    private MultipleDetails multipleDetails;
    private String userToken;
    private List<String> errors;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        userToken = "authString";
        errors = new ArrayList<>();
        CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
        casePreAcceptType.setCaseAccepted(YES);
        casePreAcceptType.setDateAccepted("2021-02-23");
        multipleDetails.getCaseData().setPreAcceptCase(casePreAcceptType);
    }

    @Test
    public void bulkPreAcceptLogicETOnline() {
        multipleDetails.getCaseData().setMultipleSource(ET1_ONLINE_CASE_SOURCE);
        multiplePreAcceptService.bulkPreAcceptLogic(userToken,
                multipleDetails,
                errors);
        verify(multipleHelperService, times(1))
                .sendPreAcceptToSinglesWithConfirmation(userToken, multipleDetails, errors);
        verifyNoMoreInteractions(multipleHelperService);
        assertEquals(0, errors.size());
    }

    @Test
    public void bulkPreAcceptLogicAllAccepted() {
        multipleDetails.getCaseData().setMultipleSource(MANUALLY_CREATED_POSITION);
        multiplePreAcceptService.bulkPreAcceptLogic(userToken,
                multipleDetails,
                errors);
        verifyNoMoreInteractions(multipleHelperService);
        assertEquals(1, errors.size());
    }

    @Test
    public void bulkPreAcceptLogicRejected() {
        multipleDetails.getCaseData().getPreAcceptCase().setCaseAccepted(NO);
        multipleDetails.getCaseData().setMultipleSource(ET1_ONLINE_CASE_SOURCE);
        multiplePreAcceptService.bulkPreAcceptLogic(userToken,
                multipleDetails,
                errors);
        verify(multipleHelperService, times(1))
                .sendRejectToSinglesWithConfirmation(userToken, multipleDetails, errors);
        verifyNoMoreInteractions(multipleHelperService);
        assertEquals(0, errors.size());
    }

}