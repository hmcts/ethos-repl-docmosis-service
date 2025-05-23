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
import java.util.TreeMap;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleBatchUpdate1ServiceTest {

    @Mock
    private MultipleHelperService multipleHelperService;

    @InjectMocks
    private MultipleBatchUpdate1Service multipleBatchUpdate1Service;

    private TreeMap<String, Object> multipleObjectsFlags;
    private MultipleDetails multipleDetails;
    private String userToken;

    @Before
    public void setUp() {
        multipleObjectsFlags = MultipleUtil.getMultipleObjectsFlags();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        userToken = "authString";
    }

    @Test
    public void batchUpdate1Logic() {

        multipleBatchUpdate1Service.batchUpdate1Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);

        verify(multipleHelperService, times(1))
                .sendUpdatesToSinglesWithConfirmation(userToken, multipleDetails, new ArrayList<>(),
                        multipleObjectsFlags, null);
        verifyNoMoreInteractions(multipleHelperService);

    }

}