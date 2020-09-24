package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleHelperService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class SingleCaseMultipleMidEventValidationServiceTest {

    @Mock
    private MultipleHelperService multipleHelperService;
    @InjectMocks
    private SingleCaseMultipleMidEventValidationService singleCaseMultipleMidEventValidationService;

    private CaseDetails caseDetails;
    private String userToken;
    private String multipleCaseTypeId;

    @Before
    public void setUp() {
        caseDetails = new CaseDetails();
        caseDetails.setCaseTypeId(MANCHESTER_CASE_TYPE_ID);
        String oldMultipleCaseTypeId = UtilHelper.getBulkCaseTypeId(caseDetails.getCaseTypeId());
        multipleCaseTypeId = oldMultipleCaseTypeId.substring(0, oldMultipleCaseTypeId.length() - 1);
        caseDetails.setCaseData(MultipleUtil.getCaseDataForSinglesToBeMoved());
        userToken = "authString";
    }

    @Test
    public void singleCaseMultipleValidationLogic() {

        List<String> errors = new ArrayList<>();

        singleCaseMultipleMidEventValidationService.singleCaseMultipleValidationLogic(userToken,
                caseDetails,
                errors);

        verify(multipleHelperService, times(1)).validateExternalMultipleAndSubMultiple(
                userToken,
                multipleCaseTypeId,
                caseDetails.getCaseData().getMultipleReference(),
                caseDetails.getCaseData().getSubMultipleName(),
                errors);
        verifyNoMoreInteractions(multipleHelperService);

    }

    @Test
    public void singleCaseMultipleValidationLogicMultipleToSingleError() {

        List<String> errors = new ArrayList<>();

        caseDetails.getCaseData().setCaseType(SINGLE_CASE_TYPE);
        caseDetails.getCaseData().setCheckMultiple(YES);

        singleCaseMultipleMidEventValidationService.singleCaseMultipleValidationLogic(userToken,
                caseDetails,
                errors);

        assertEquals(1, errors.size());
        assertEquals("Case belongs to a multiple. It can not be moved to single", errors.get(0));

    }

    @Test
    public void singleCaseMultipleValidationLogicSingleToSingle() {

        List<String> errors = new ArrayList<>();

        caseDetails.getCaseData().setCaseType(SINGLE_CASE_TYPE);
        caseDetails.getCaseData().setCheckMultiple(NO);

        singleCaseMultipleMidEventValidationService.singleCaseMultipleValidationLogic(userToken,
                caseDetails,
                errors);

        assertEquals(0, errors.size());

    }

}