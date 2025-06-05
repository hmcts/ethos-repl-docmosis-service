package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleDocGenerationServiceTest {

    @Mock
    private MultipleLetterService multipleLetterService;

    @InjectMocks
    private MultipleDocGenerationService multipleDocGenerationService;

    private MultipleDetails multipleDetails;
    private String userToken;
    private List<String> errors;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        multipleDetails.setCaseTypeId("Leeds_Multiple");
        userToken = "authString";
        errors = new ArrayList<>();
    }

    @Test
    public void midSelectedAddressLabelsMultiple() {
        multipleDetails.getCaseData().setAddressLabelCollection(MultipleUtil.getAddressLabelTypeItemList());
        multipleDocGenerationService.midSelectedAddressLabelsMultiple(userToken, multipleDetails, errors);
        verify(multipleLetterService, times(1)).bulkLetterLogic(
                userToken,
                multipleDetails,
                errors,
                true);
        verifyNoMoreInteractions(multipleLetterService);
        assertNull(multipleDetails.getCaseData().getAddressLabelCollection());
    }

}