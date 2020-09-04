package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.HelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.ArrayList;
import java.util.TreeMap;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleUpdateServiceTest {

    @Mock
    private CreateUpdatesBusSender createUpdatesBusSender;
    @Mock
    private UserService userService;
    @Mock
    private ExcelReadingService excelReadingService;
    @InjectMocks
    private MultipleUpdateService multipleUpdateService;

    private TreeMap<String, Object> multipleObjectsFlags;
    private MultipleDetails multipleDetails;
    private String userToken;

    @Before
    public void setUp() {
        multipleObjectsFlags = MultipleUtil.getMultipleObjectsFlags();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        UserDetails userDetails = HelperTest.getUserDetails();
        when(userService.getUserDetails(anyString())).thenReturn(userDetails);
        userToken = "authString";
    }

    @Test
    public void bulkUpdateLogic() {
        when(excelReadingService.readExcel(anyString(), anyString(), anyList(), any(), any()))
                .thenReturn(multipleObjectsFlags);
        multipleUpdateService.bulkUpdateLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(userService, times(1)).getUserDetails(userToken);
        verifyNoMoreInteractions(userService);
    }

}