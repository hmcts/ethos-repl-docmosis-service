package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleHelperService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;

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
        caseDetails.setCaseData(new CaseData());
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
                "",
                "",
                errors);
        verifyNoMoreInteractions(multipleHelperService);

    }

}