package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.AMEND_ACTION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DELETE_ACTION;

@RunWith(SpringJUnit4ClassRunner.class)
public class SubMultipleMidEventValidationServiceTest {

    @InjectMocks
    private SubMultipleMidEventValidationService subMultipleMidEventValidationService;

    private MultipleDetails multipleDetails;
    private List<String> errors;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        errors = new ArrayList<>();
    }

    @Test
    public void subMultipleMidEventValidationServiceCreate() {

        multipleDetails.getCaseData().getSubMultipleAction().setCreateSubMultipleName("SubMultiple");

        subMultipleMidEventValidationService.subMultipleValidationLogic(
                multipleDetails,
                errors);

        assertEquals(1, errors.size());
        assertEquals("Sub Multiple SubMultiple already exists", errors.getFirst());

    }

    @Test
    public void subMultipleMidEventValidationServiceAmend() {

        multipleDetails.getCaseData().getSubMultipleAction().setActionType(AMEND_ACTION);
        multipleDetails.getCaseData().getSubMultipleAction().setAmendSubMultipleNameExisting("SubMultipleDoesNotExist");
        multipleDetails.getCaseData().getSubMultipleAction().setAmendSubMultipleNameNew("SubMultiple");

        subMultipleMidEventValidationService.subMultipleValidationLogic(
                multipleDetails,
                errors);

        assertEquals(2, errors.size());
        assertEquals("Sub Multiple SubMultipleDoesNotExist does not exist", errors.getFirst());
        assertEquals("Sub Multiple SubMultiple already exists", errors.get(1));

    }

    @Test
    public void subMultipleMidEventValidationServiceAmendEmptySubCollection() {

        multipleDetails.getCaseData().getSubMultipleAction().setActionType(AMEND_ACTION);
        multipleDetails.getCaseData().getSubMultipleAction().setAmendSubMultipleNameExisting("SubMultipleDoesNotExist");
        multipleDetails.getCaseData().getSubMultipleAction().setAmendSubMultipleNameNew("SubMultiple");
        multipleDetails.getCaseData().setSubMultipleCollection(null);

        subMultipleMidEventValidationService.subMultipleValidationLogic(
                multipleDetails,
                errors);

        assertEquals(1, errors.size());
        assertEquals("Sub Multiple SubMultipleDoesNotExist does not exist", errors.getFirst());

    }

    @Test
    public void subMultipleMidEventValidationServiceDelete() {

        multipleDetails.getCaseData().getSubMultipleAction().setActionType(DELETE_ACTION);
        multipleDetails.getCaseData().getSubMultipleAction().setDeleteSubMultipleName("SubMultipleDoesNotExist");

        subMultipleMidEventValidationService.subMultipleValidationLogic(
                multipleDetails,
                errors);

        assertEquals(1, errors.size());
        assertEquals("Sub Multiple SubMultipleDoesNotExist does not exist", errors.getFirst());

    }

}