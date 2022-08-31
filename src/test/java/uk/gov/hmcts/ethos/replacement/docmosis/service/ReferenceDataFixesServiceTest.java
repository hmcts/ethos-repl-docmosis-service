package uk.gov.hmcts.ethos.replacement.docmosis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RANGE_HEARING_DATE_TYPE;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.RefDataFixesCcdDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.ReferenceDataFixesService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.RefDataFixesData;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.RefDataFixesDetails;

@RunWith(SpringJUnit4ClassRunner.class)
public class ReferenceDataFixesServiceTest {
    @Mock
    private CcdClient ccdClient;
    private RefDataFixesCcdDataSource dataSource;

    @InjectMocks
    private ReferenceDataFixesService referenceDataFixesService;

    private RefDataFixesDetails refDataFixesDetails;
    private RefDataFixesData refDataFixesData;
    private final String REQUIRED_CODE_1 = "requiredJudgeCode1";
    private List<SubmitEvent> submitEvents;
    private CcdClient client;

    @Before
    public void setUp() {

        dataSource = mock(RefDataFixesCcdDataSource.class);
        refDataFixesDetails = new RefDataFixesDetails();
        refDataFixesData = new RefDataFixesData();
        refDataFixesDetails.setCaseData(refDataFixesData);
        refDataFixesDetails.setJurisdiction("EMPLOYMENT");
        refDataFixesDetails.setCaseTypeId(MANCHESTER_CASE_TYPE_ID);
        refDataFixesData.setHearingDateType(RANGE_HEARING_DATE_TYPE);
        refDataFixesData.setDateFrom("2022-07-01");
        refDataFixesData.setDateTo("2022-07-31");
        refDataFixesData.setExistingJudgeCode("existingJudgeCode1");
        refDataFixesData.setRequiredJudgeCode(REQUIRED_CODE_1);
        SubmitEvent submitEvent1 = new SubmitEvent();
        CaseData caseData1 = new CaseData();
        HearingTypeItem hearingTypeItem1 = new HearingTypeItem();
        hearingTypeItem1.setId("2222");
        HearingType hearingType1 = new HearingType();
        hearingType1.setJudge("existingJudgeCode1");
        hearingTypeItem1.setValue(hearingType1);
        HearingTypeItem hearingTypeItem2 = new HearingTypeItem();
        hearingTypeItem2.setId("3333");
        HearingType hearingType2 = new HearingType();
        hearingType2.setJudge("existingJudgeCode2");
        hearingTypeItem2.setValue(hearingType2);
        caseData1.setHearingCollection(Arrays.asList(hearingTypeItem1, hearingTypeItem2));
        submitEvent1.setCaseData(caseData1);
        submitEvent1.setCaseId(1);
        submitEvents = new ArrayList<>(List.of(submitEvent1));
        when(dataSource.getData(anyString(), anyString(), anyString(), any())).thenReturn(submitEvents);
    }

    @Test
    public void judgeCodeReplaceTest() {
        RefDataFixesData caseDataResult = referenceDataFixesService.updateJudgesItcoReferences(
                refDataFixesDetails, "authToken", dataSource);
        assertEquals(submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().getJudge(),
                REQUIRED_CODE_1);
    }
}