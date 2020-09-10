package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ecm.common.model.multiples.types.MoveCasesType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleMidEventValidationServiceTest {

    @Mock
    private MultipleCasesReadingService multipleCasesReadingService;
    @InjectMocks
    private MultipleMidEventValidationService multipleMidEventValidationService;

    private MultipleDetails multipleDetails;
    private String userToken;
    private List<SubmitMultipleEvent> submitMultipleEvents;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        submitMultipleEvents = MultipleUtil.getSubmitMultipleEvents();
        userToken = "authString";
    }

    @Test
    public void multipleValidationLogicCurrentMultiple() {

        List<String> errors = new ArrayList<>();

        MoveCasesType moveCasesType = new MoveCasesType();
        moveCasesType.setUpdatedMultipleRef("246000");
        moveCasesType.setUpdatedSubMultipleRef("246000/1");
        multipleDetails.getCaseData().setMoveCases(moveCasesType);

        multipleMidEventValidationService.multipleValidationLogic(userToken,
                multipleDetails,
                errors);

        assertEquals(0, errors.size());

    }

    @Test
    public void multipleValidationLogicMultipleAndSubExist() {

        List<String> errors = new ArrayList<>();

        MoveCasesType moveCasesType = new MoveCasesType();
        moveCasesType.setUpdatedMultipleRef("246001");
        moveCasesType.setUpdatedSubMultipleRef("246000/1");
        multipleDetails.getCaseData().setMoveCases(moveCasesType);

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMoveCases().getUpdatedMultipleRef())
        ).thenReturn(submitMultipleEvents);

        multipleMidEventValidationService.multipleValidationLogic(userToken,
                multipleDetails,
                errors);

        assertEquals(0, errors.size());

    }

    @Test
    public void multipleValidationLogicSubMultipleDoesNotExist() {

        List<String> errors = new ArrayList<>();

        MoveCasesType moveCasesType = new MoveCasesType();
        moveCasesType.setUpdatedMultipleRef("246002");
        moveCasesType.setUpdatedSubMultipleRef("246002/1");
        multipleDetails.getCaseData().setMoveCases(moveCasesType);

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMoveCases().getUpdatedMultipleRef())
        ).thenReturn(submitMultipleEvents);

        multipleMidEventValidationService.multipleValidationLogic(userToken,
                multipleDetails,
                errors);

        assertEquals("Sub multiple 246002/1 does not exists in 246002", errors.get(0));

    }

    @Test
    public void multipleValidationLogicSubMultipleNull() {

        List<String> errors = new ArrayList<>();

        MoveCasesType moveCasesType = new MoveCasesType();
        moveCasesType.setUpdatedMultipleRef("246002");
        moveCasesType.setUpdatedSubMultipleRef("246002/1");
        multipleDetails.getCaseData().setMoveCases(moveCasesType);

        submitMultipleEvents.get(0).getCaseData().setSubMultipleCollection(null);

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMoveCases().getUpdatedMultipleRef())
        ).thenReturn(submitMultipleEvents);

        multipleMidEventValidationService.multipleValidationLogic(userToken,
                multipleDetails,
                errors);

        assertEquals("Sub multiple 246002/1 does not exists in 246002", errors.get(0));

    }

    @Test
    public void multipleValidationLogicMultipleDoesNotExist() {

        List<String> errors = new ArrayList<>();

        MoveCasesType moveCasesType = new MoveCasesType();
        moveCasesType.setUpdatedMultipleRef("246002");
        multipleDetails.getCaseData().setMoveCases(moveCasesType);

        when(multipleCasesReadingService.retrieveMultipleCases(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getMoveCases().getUpdatedMultipleRef())
        ).thenReturn(new ArrayList<>());

        multipleMidEventValidationService.multipleValidationLogic(userToken,
                multipleDetails,
                errors);

        assertEquals("Multiple 246002 does not exists", errors.get(0));

    }

}