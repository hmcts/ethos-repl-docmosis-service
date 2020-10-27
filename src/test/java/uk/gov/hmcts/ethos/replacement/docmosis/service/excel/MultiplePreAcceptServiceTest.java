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

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultiplePreAcceptServiceTest {

    @Mock
    private MultipleHelperService multipleHelperService;
    @InjectMocks
    private MultiplePreAcceptService multiplePreAcceptService;

    private MultipleDetails multipleDetails;
    private String userToken;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        userToken = "authString";
    }

    @Test
    public void bulkPreAcceptLogic() {
        multiplePreAcceptService.bulkPreAcceptLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(multipleHelperService, times(1))
                .sendPreAcceptToSinglesWithConfirmation(userToken, multipleDetails, new ArrayList<>());
        verifyNoMoreInteractions(multipleHelperService);
    }

}