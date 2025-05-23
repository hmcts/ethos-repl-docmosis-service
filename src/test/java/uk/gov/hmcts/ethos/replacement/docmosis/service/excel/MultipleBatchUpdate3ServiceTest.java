package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeC;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeR;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OPEN_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SELECT_NONE_VALUE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleBatchUpdate3ServiceTest {

    @Mock
    private SingleCasesReadingService singleCasesReadingService;
    @Mock
    private MultipleHelperService multipleHelperService;
    @Mock
    private CcdClient ccdClient;

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
        multipleDetails.getCaseData().setBatchUpdateJudgment(MultipleUtil.generateDynamicList("JD"));
        multipleDetails.getCaseData().setBatchUpdateRespondentRep(MultipleUtil
                .generateDynamicList("Respondent Rep"));

        multipleDetails.getCaseData().setBatchUpdateCase("245000/2020");

        assertEquals(3, multipleObjectsFlags.size());

        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getBatchUpdateCase(),
                multipleDetails.getCaseData().getMultipleSource()))
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
    public void batchUpdate3LogicClaimantRepRemoval() throws IOException {

        multipleDetails.getCaseData().setBatchUpdateClaimantRep(MultipleUtil.generateDynamicList(SELECT_NONE_VALUE));
        multipleDetails.getCaseData().setBatchUpdateJurisdiction(MultipleUtil.generateDynamicList("AA"));
        multipleDetails.getCaseData().setBatchUpdateRespondent(MultipleUtil.generateDynamicList("Andrew Smith"));
        multipleDetails.getCaseData().setBatchUpdateJudgment(MultipleUtil.generateDynamicList("JD"));
        multipleDetails.getCaseData().setBatchUpdateRespondentRep(MultipleUtil
                .generateDynamicList("Respondent Rep"));
        multipleDetails.setJurisdiction("Employment");
        multipleDetails.setCaseTypeId("Leeds");
        multipleDetails.getCaseData().setBatchRemoveClaimantRep(YES);
        multipleDetails.getCaseData().setBatchUpdateCase("245000/2020");
        RepresentedTypeC representedTypeC = new RepresentedTypeC();
        representedTypeC.setNameOfRepresentative("ABC");
        representedTypeC.setNameOfOrganisation("FDS");
        submitEvents.get(0).getCaseData().setRepresentativeClaimantType(representedTypeC);
        assertEquals(3, multipleObjectsFlags.size());

        CCDRequest ccdRequest = new CCDRequest();
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(submitEvents.get(0).getCaseData());
        ccdRequest.setCaseDetails(caseDetails);
        doReturn(ccdRequest).when(ccdClient).startEventForCase(anyString(), anyString(), anyString(),
                anyString());
        doReturn(submitEvents.get(0)).when(singleCasesReadingService).retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getBatchUpdateCase(),
                multipleDetails.getCaseData().getMultipleSource());
        multipleBatchUpdate3Service.batchUpdate3Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);

        assertEquals(2, multipleObjectsFlags.size());
        assertEquals(submitEvents.get(0).getCaseData().getRepresentativeClaimantType(), new RepresentedTypeC());
        verify(multipleHelperService, times(1))
                .sendUpdatesToSinglesWithConfirmation(userToken, multipleDetails, new ArrayList<>(),
                        multipleObjectsFlags, submitEvents.get(0).getCaseData());
        verifyNoMoreInteractions(multipleHelperService);

    }

    @Test
    public void batchUpdate3LogicRespondentRepRemoval() throws IOException {

        multipleDetails.getCaseData().setBatchUpdateClaimantRep(MultipleUtil.generateDynamicList(SELECT_NONE_VALUE));
        multipleDetails.getCaseData().setBatchUpdateJurisdiction(MultipleUtil.generateDynamicList("AA"));
        multipleDetails.getCaseData().setBatchUpdateRespondent(MultipleUtil.generateDynamicList("Andrew Smith"));
        multipleDetails.getCaseData().setBatchUpdateJudgment(MultipleUtil.generateDynamicList("JD"));
        multipleDetails.getCaseData().setBatchUpdateRespondentRep(MultipleUtil
                .generateDynamicList("Respondent Rep"));
        multipleDetails.setJurisdiction("Employment");
        multipleDetails.setCaseTypeId("Leeds");
        multipleDetails.getCaseData().setBatchRemoveRespondentRep(YES);
        multipleDetails.getCaseData().setBatchUpdateCase("245000/2020");
        var representedTypeR = new RepresentedTypeR();
        representedTypeR.setRespRepName("Andrew Smith");
        representedTypeR.setNameOfRepresentative("Respondent Rep");
        var representedTypeRItem = new RepresentedTypeRItem();
        representedTypeRItem.setId("Respondent Rep");
        representedTypeRItem.setValue(representedTypeR);
        submitEvents.get(0).getCaseData().setRepCollection
                (new ArrayList<>(Collections.singletonList(representedTypeRItem)));
        assertEquals(3, multipleObjectsFlags.size());

        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getBatchUpdateCase(),
                multipleDetails.getCaseData().getMultipleSource()))
                .thenReturn(submitEvents.get(0));
        CCDRequest ccdRequest = new CCDRequest();
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(submitEvents.get(0).getCaseData());
        ccdRequest.setCaseDetails(caseDetails);
        doReturn(ccdRequest).when(ccdClient).startEventForCase(anyString(), anyString(), anyString(),
                anyString());
        multipleBatchUpdate3Service.batchUpdate3Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);

        assertEquals(2, multipleObjectsFlags.size());
        assertNull( submitEvents.get(0).getCaseData().getRepCollection().get(0).getValue());
    }

    @Test
    public void batchUpdate3LogicNoChanges() {

        multipleDetails.getCaseData().setBatchUpdateClaimantRep(MultipleUtil.generateDynamicList(SELECT_NONE_VALUE));
        multipleDetails.getCaseData().setBatchUpdateJurisdiction(MultipleUtil.generateDynamicList(SELECT_NONE_VALUE));
        multipleDetails.getCaseData().setBatchUpdateRespondent(MultipleUtil.generateDynamicList(SELECT_NONE_VALUE));
        multipleDetails.getCaseData().setBatchUpdateJudgment(MultipleUtil.generateDynamicList(SELECT_NONE_VALUE));
        multipleDetails.getCaseData().setBatchUpdateRespondentRep(MultipleUtil.generateDynamicList(SELECT_NONE_VALUE));

        multipleDetails.getCaseData().setBatchUpdateCase("245000/2020");

        when(singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(),
                multipleDetails.getCaseData().getBatchUpdateCase(),
                multipleDetails.getCaseData().getMultipleSource()))
                .thenReturn(submitEvents.get(0));

        multipleBatchUpdate3Service.batchUpdate3Logic(userToken,
                multipleDetails,
                new ArrayList<>(),
                multipleObjectsFlags);

        verifyNoMoreInteractions(multipleHelperService);

        assertEquals(OPEN_STATE, multipleDetails.getCaseData().getState());

    }

}