package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.ArrayList;
import java.util.TreeMap;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleBatchUpdate3ServiceTest {

    @Mock
    private CreateUpdatesBusSender createUpdatesBusSender;
    @Mock
    private UserService userService;


    @InjectMocks
    private MultipleBatchUpdate3Service multipleBatchUpdate3Service;

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
    public void batchUpdate3Logic() {
        multipleBatchUpdate3Service.batchUpdate3Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);
    }

}