package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleHelperServiceTest {

    @Mock
    private SingleCasesReadingService singleCasesReadingService;
    @InjectMocks
    private MultipleHelperService multipleHelperService;

    private MultipleDetails multipleDetails;
    private String userToken;
    private List<SubmitEvent> submitEventList;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        multipleDetails.setCaseTypeId("Manchester_Multiple");
        submitEventList = MultipleUtil.getSubmitEvents();
        userToken = "authString";
        ReflectionTestUtils.setField(multipleHelperService, "ccdGatewayBaseUrl", "http://www-demo.ccd/dm-store:8080/v2/case/");
    }

    @Test
    public void addLeadMarkUp() {
        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getLeadCase()))
                .thenReturn(submitEventList.get(0));
        multipleHelperService.addLeadMarkUp(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData(),
                multipleDetails.getCaseData().getLeadCase());
        assertEquals("<a target=\"_blank\" href=\"http://www-demo.ccd/dm-store:8080/v2/case//v2/case/1232121232\">21006/2020</a>",
                multipleDetails.getCaseData().getLeadCase());
    }


    @Test
    public void addLeadMarkUpEmptyCase() {
        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getLeadCase()))
                .thenReturn(null);
        multipleHelperService.addLeadMarkUp(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData(),
                multipleDetails.getCaseData().getLeadCase());
        assertEquals("21006/2020", multipleDetails.getCaseData().getLeadCase());
    }
}