package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.types.MoveCasesType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleMidEventValidationServiceTest {

    @Mock
    private MultipleHelperService multipleHelperService;
    @InjectMocks
    private MultipleMidEventValidationService multipleMidEventValidationService;

    private MultipleDetails multipleDetails;
    private String userToken;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        userToken = "authString";
    }

    @Test
    public void multipleValidationLogicCurrentMultiple() {
        MoveCasesType moveCasesType = new MoveCasesType();
        moveCasesType.setUpdatedMultipleRef("246000");
        moveCasesType.setUpdatedSubMultipleRef("SubMultiple");
        moveCasesType.setConvertToSingle(NO);
        multipleDetails.getCaseData().setMoveCases(moveCasesType);

        List<String> errors = new ArrayList<>();
        multipleMidEventValidationService.multipleValidationLogic(userToken,
                multipleDetails,
                errors);

        verify(multipleHelperService, times(1)).validateSubMultiple(
                "SubMultiple",
                multipleDetails.getCaseData().getSubMultipleCollection(),
                errors,
                "246000");
        verifyNoMoreInteractions(multipleHelperService);

    }

    @Test
    public void multipleValidationLogicExternalMultiple() {

        MoveCasesType moveCasesType = new MoveCasesType();
        moveCasesType.setUpdatedMultipleRef("246001");
        moveCasesType.setUpdatedSubMultipleRef("SubMultiple");
        moveCasesType.setConvertToSingle(NO);
        multipleDetails.getCaseData().setMoveCases(moveCasesType);
        List<String> errors = new ArrayList<>();

        multipleMidEventValidationService.multipleValidationLogic(userToken,
                multipleDetails,
                errors);

        verify(multipleHelperService, times(1)).validateExternalMultipleAndSubMultiple(
                userToken,
                multipleDetails.getCaseTypeId(),
                "246001",
                "SubMultiple",
                errors);
        verifyNoMoreInteractions(multipleHelperService);

    }

    @Test
    public void multipleValidationLogicConvertToSingle() {

        MoveCasesType moveCasesType = new MoveCasesType();
        moveCasesType.setUpdatedMultipleRef("");
        moveCasesType.setUpdatedSubMultipleRef("");
        moveCasesType.setConvertToSingle(YES);
        multipleDetails.getCaseData().setMoveCases(moveCasesType);
        List<String> errors = new ArrayList<>();

        multipleMidEventValidationService.multipleValidationLogic(userToken,
                multipleDetails,
                errors);

        assertEquals(0, errors.size());

    }

}