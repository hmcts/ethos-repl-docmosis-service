package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.helper.CaseEventDetail;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.RefDataFixesCcdDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.ReferenceDataFixesService;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.AdminData;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.AdminDetails;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RANGE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.ReferenceDataFixesService.GENERATE_CORRESPONDENCE;

@RunWith(SpringJUnit4ClassRunner.class)
public class ReferenceDataFixesServiceTest {
    @Mock
    private CcdClient ccdClient;
    private RefDataFixesCcdDataSource dataSource;

    @InjectMocks
    private ReferenceDataFixesService referenceDataFixesService;

    private AdminDetails adminDetails;
    private static final String REQUIRED_CODE_1 = "requiredJudgeCode1";
    private List<SubmitEvent> submitEvents;

    @Before
    public void setUp() throws IOException {

        dataSource = mock(RefDataFixesCcdDataSource.class);
        adminDetails = new AdminDetails();
        AdminData adminData = new AdminData();
        adminDetails.setCaseData(adminData);
        adminDetails.setJurisdiction("EMPLOYMENT");
        adminData.setTribunalOffice(MANCHESTER_CASE_TYPE_ID);
        adminData.setHearingDateType(RANGE_HEARING_DATE_TYPE);
        adminData.setDateFrom("2022-07-01");
        adminData.setDateTo("2022-07-31");
        adminData.setExistingJudgeCode("existingJudgeCode1");
        adminData.setRequiredJudgeCode(REQUIRED_CODE_1);
        HearingTypeItem hearingTypeItem1 = new HearingTypeItem();
        hearingTypeItem1.setId("2222");
        HearingType hearingType1 = new HearingType();
        hearingType1.setJudge("existingJudgeCode1");
        DateListedTypeItem dateListedTypeItem1 = new DateListedTypeItem();
        DateListedType dateListedType1 = new DateListedType();
        dateListedType1.setListedDate("2022-07-01T13:00:00.000");
        dateListedTypeItem1.setValue(dateListedType1);
        hearingType1.setHearingDateCollection(Collections.singletonList(dateListedTypeItem1));
        hearingTypeItem1.setValue(hearingType1);
        HearingTypeItem hearingTypeItem2 = new HearingTypeItem();
        hearingTypeItem2.setId("3333");
        HearingType hearingType2 = new HearingType();
        hearingType2.setJudge("existingJudgeCode2");
        hearingTypeItem2.setValue(hearingType2);
        CaseData caseData1 = new CaseData();
        caseData1.setHearingCollection(Arrays.asList(hearingTypeItem1, hearingTypeItem2));
        SubmitEvent submitEvent1 = new SubmitEvent();
        submitEvent1.setCaseData(caseData1);
        submitEvent1.setCaseId(1);
        submitEvents = new ArrayList<>(List.of(submitEvent1));
        when(dataSource.getDataForJudges(anyString(), anyString(), anyString(), any())).thenReturn(submitEvents);
        when(dataSource.getDataForInsertClaimDate(anyString(), anyString(), anyString(), any())).thenReturn(submitEvents);
        CCDRequest ccdRequest = new CCDRequest();
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(submitEvents.get(0).getCaseData());
        ccdRequest.setCaseDetails(caseDetails);
        doReturn(ccdRequest).when(ccdClient).startEventForCase(anyString(), anyString(), anyString(),
                anyString());

    }

    @Test
    public void judgeCodeReplaceTest() {
        referenceDataFixesService.updateJudgesItcoReferences(
                adminDetails, "authToken", dataSource);
        assertEquals(REQUIRED_CODE_1,
                submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().getJudge());
    }

    @Test
    public void judgeCodeCaseTypeIdTest() {
        adminDetails.getCaseData().setTribunalOffice("Manchester");

        referenceDataFixesService.updateJudgesItcoReferences(
                adminDetails, "authToken", dataSource);
        assertEquals(REQUIRED_CODE_1,
                submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().getJudge());
    }

    @Test
    public void judgeCodeMidlandsWestTest() throws IOException {
        adminDetails.getCaseData().setTribunalOffice("Midlands West");
        referenceDataFixesService.updateJudgesItcoReferences(
                adminDetails, "authToken", dataSource);
        assertEquals(REQUIRED_CODE_1,
                submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().getJudge());
    }

    @Test
    public void judgeCodeMidlandsEastTest() throws IOException {
        adminDetails.getCaseData().setTribunalOffice("Midlands East");
        referenceDataFixesService.updateJudgesItcoReferences(
                adminDetails, "authToken", dataSource);
        assertEquals(REQUIRED_CODE_1,
                submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().getJudge());
    }

    @Test
    public void judgeCodeLondonEastTest() throws IOException {
        adminDetails.getCaseData().setTribunalOffice("London East");
        referenceDataFixesService.updateJudgesItcoReferences(
                adminDetails, "authToken", dataSource);
        assertEquals(REQUIRED_CODE_1,
                submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().getJudge());
    }

    @Test
    public void judgeCodeLondonSouthTest() throws IOException {
        adminDetails.getCaseData().setTribunalOffice("London South");
        referenceDataFixesService.updateJudgesItcoReferences(
                adminDetails, "authToken", dataSource);
        assertEquals(REQUIRED_CODE_1,
                submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().getJudge());
    }

    @Test
    public void judgeCodeLondonCentralTest() throws IOException {
        adminDetails.getCaseData().setTribunalOffice("London Central");
        referenceDataFixesService.updateJudgesItcoReferences(
                adminDetails, "authToken", dataSource);
        assertEquals(REQUIRED_CODE_1,
                submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().getJudge());
    }

    @Test
    public void judgeCodeDateTest() throws IOException {
        adminDetails.getCaseData().setDate("2022-07-01");
        adminDetails.getCaseData().setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        referenceDataFixesService.updateJudgesItcoReferences(
                adminDetails, "authToken", dataSource);
        assertEquals(REQUIRED_CODE_1,
                submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().getJudge());
    }

    @Test
    public void wrongJudgeCodeTest() {
        adminDetails.getCaseData().setExistingJudgeCode("WrongJudgeCode");
        referenceDataFixesService.updateJudgesItcoReferences(
                adminDetails, "authToken", dataSource);
        assertNotEquals(REQUIRED_CODE_1,
                submitEvents.get(0).getCaseData().getHearingCollection().get(0).getValue().getJudge());
    }

    @Test
    public void initAdminDataTest() {
        referenceDataFixesService.initAdminData(
                adminDetails.getCaseData());
        assertNull(adminDetails.getCaseData().getDate());
        assertNull(adminDetails.getCaseData().getDateTo());
        assertNull(adminDetails.getCaseData().getDateFrom());
        assertNull(adminDetails.getCaseData().getHearingDateType());
        assertNull(adminDetails.getCaseData().getExistingJudgeCode());
        assertNull(adminDetails.getCaseData().getRequiredJudgeCode());
    }

    @Test
    public void insertClaimServedDateTest() throws IOException {
        CaseEventDetail caseEventDetail = CaseEventDetail.builder().build();
        caseEventDetail.setId(GENERATE_CORRESPONDENCE);
        caseEventDetail.setStateId(ACCEPTED_STATE);
        caseEventDetail.setCreatedDate(LocalDateTime.parse("2022-09-20T00:00:00"));
        CaseEventDetail caseEventDetail2 = CaseEventDetail.builder().build();
        caseEventDetail2.setId(GENERATE_CORRESPONDENCE);
        caseEventDetail2.setCreatedDate(LocalDateTime.parse("2022-09-26T00:00:00"));
        when(ccdClient.retrieveCaseEventDetails(anyString(),
                anyString(), anyString(), anyString()))
                .thenReturn(Arrays.asList(caseEventDetail2, caseEventDetail));
        referenceDataFixesService.insertClaimServedDate(
                adminDetails, "authToken", dataSource, new ArrayList<>());
        assertEquals("2022-09-20",
                submitEvents.get(0).getCaseData().getClaimServedDate());
    }

    @Test
    public void insertClaimServedDateTestNoEvent() throws IOException {
        CaseEventDetail caseEventDetail = CaseEventDetail.builder().build();
        caseEventDetail.setId("blahblah");
        caseEventDetail.setCreatedDate(LocalDateTime.parse("2022-09-20T00:00:00"));
        when(ccdClient.retrieveCaseEventDetails(anyString(),
                anyString(), anyString(), anyString())).thenReturn(Collections.singletonList(caseEventDetail));
        assertNull(submitEvents.get(0).getCaseData().getClaimServedDate());
    }

    @Test
    public void insertClaimServedDateTestAlreadyExists() throws IOException {
        CaseEventDetail caseEventDetail = CaseEventDetail.builder().build();
        caseEventDetail.setId(GENERATE_CORRESPONDENCE);
        caseEventDetail.setCreatedDate(LocalDateTime.parse("2022-09-20T00:00:00"));
        submitEvents.get(0).getCaseData().setClaimServedDate("2022-10-01");
        when(ccdClient.retrieveCaseEventDetails(anyString(),
                anyString(), anyString(), anyString())).thenReturn(Collections.singletonList(caseEventDetail));
        assertEquals("2022-10-01", submitEvents.get(0).getCaseData().getClaimServedDate());
    }
}
