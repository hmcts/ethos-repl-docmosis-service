package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleBatchUpdate3ServiceTest {

    @Mock
    private SingleCasesReadingService singleCasesReadingService;
    @Mock
    private MultipleHelperService multipleHelperService;

    @InjectMocks
    private MultipleBatchUpdate3Service multipleBatchUpdate3Service;

    private TreeMap<String, Object> multipleObjectsFlags;
    private MultipleDetails multipleDetails;
    private List<SubmitEvent> submitEvents;
    private String userToken;

    @Before
    public void setUp() {
        multipleObjectsFlags = MultipleUtil.getMultipleObjectsFlags();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        submitEvents = MultipleUtil.getSubmitEvents();
        userToken = "authString";
    }

    @Test
    public void batchUpdate3Logic() {

        multipleDetails.getCaseData().setBatchUpdateClaimantRep(MultipleUtil.generateDynamicList(SELECT_NONE_VALUE));
        multipleDetails.getCaseData().setBatchUpdateJurisdiction(MultipleUtil.generateDynamicList("AA"));
        multipleDetails.getCaseData().setBatchUpdateRespondent(MultipleUtil.generateDynamicList("Andrew Smith"));

        multipleDetails.getCaseData().setBatchUpdateCase("245000/2020");

        assertEquals(3, multipleObjectsFlags.size());

        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getBatchUpdateCase()))
                .thenReturn(submitEvents.get(0));

        multipleBatchUpdate3Service.batchUpdate3Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);

        assertEquals(2, multipleObjectsFlags.size());

        verify(multipleHelperService, times(1))
                .sendUpdatesToSinglesWithConfirmation(userToken, multipleDetails, new ArrayList<>(),
                        multipleObjectsFlags, submitEvents.get(0).getCaseData());
        verifyNoMoreInteractions(multipleHelperService);

    }

    @Test
    public void batchUpdate3LogicNoChanges() {

        multipleDetails.getCaseData().setBatchUpdateClaimantRep(MultipleUtil.generateDynamicList(SELECT_NONE_VALUE));
        multipleDetails.getCaseData().setBatchUpdateJurisdiction(MultipleUtil.generateDynamicList(SELECT_NONE_VALUE));
        multipleDetails.getCaseData().setBatchUpdateRespondent(MultipleUtil.generateDynamicList(SELECT_NONE_VALUE));

        multipleDetails.getCaseData().setBatchUpdateCase("245000/2020");

        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getBatchUpdateCase()))
                .thenReturn(submitEvents.get(0));

        multipleBatchUpdate3Service.batchUpdate3Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);

        verifyNoMoreInteractions(multipleHelperService);

        assertEquals(OPEN_STATE, multipleDetails.getCaseData().getState());

    }

}