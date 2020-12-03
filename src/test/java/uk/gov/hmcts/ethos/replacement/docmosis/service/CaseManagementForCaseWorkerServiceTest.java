package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.Address;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeC;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeR;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ABOUT_TO_SUBMIT_EVENT_CALLBACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MID_EVENT_CALLBACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.REJECTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SUBMITTED_CALLBACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.SetUpUtils.feignError;

@RunWith(SpringJUnit4ClassRunner.class)
public class CaseManagementForCaseWorkerServiceTest {

    @InjectMocks
    private CaseManagementForCaseWorkerService caseManagementForCaseWorkerService;
    private static final String AUTH_TOKEN = "Bearer eyJhbGJbpjciOiJIUzI1NiJ9";
    private CCDRequest scotlandCcdRequest1;
    private CCDRequest scotlandCcdRequest2;
    private CCDRequest scotlandCcdRequest3;
    private CCDRequest ccdRequest10;
    private CCDRequest ccdRequest11;
    private CCDRequest ccdRequest12;
    private CCDRequest ccdRequest13;
    private CCDRequest ccdRequest14;
    private CCDRequest ccdRequest15;
    private CCDRequest manchesterCcdRequest;
    private SubmitEvent submitEvent;
    @MockBean
    private CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService;
    @MockBean
    private CcdClient ccdClient;

    @Before
    public void setUp() throws Exception {
        scotlandCcdRequest1 = new CCDRequest();
        CaseDetails caseDetailsScot1 = generateCaseDetails("caseDetailsScotTest1.json");
        scotlandCcdRequest1.setCaseDetails(caseDetailsScot1);

        scotlandCcdRequest2 = new CCDRequest();
        CaseDetails caseDetailsScot2 = generateCaseDetails("caseDetailsScotTest2.json");
        scotlandCcdRequest2.setCaseDetails(caseDetailsScot2);

        scotlandCcdRequest3 = new CCDRequest();
        CaseDetails caseDetailsScot3 = generateCaseDetails("caseDetailsScotTest3.json");
        scotlandCcdRequest3.setCaseDetails(caseDetailsScot3);

        ccdRequest10 = new CCDRequest();
        CaseDetails caseDetails10 = generateCaseDetails("caseDetailsTest10.json");
        ccdRequest10.setCaseDetails(caseDetails10);

        ccdRequest11 = new CCDRequest();
        CaseDetails caseDetails11 = generateCaseDetails("caseDetailsTest11.json");
        ccdRequest11.setCaseDetails(caseDetails11);

        ccdRequest12 = new CCDRequest();
        CaseDetails caseDetails12 = generateCaseDetails("caseDetailsTest12.json");
        ccdRequest12.setCaseDetails(caseDetails12);

        ccdRequest13 = new CCDRequest();
        CaseDetails caseDetails13 = generateCaseDetails("caseDetailsTest13.json");
        ccdRequest13.setCaseDetails(caseDetails13);

        ccdRequest14 = new CCDRequest();
        CaseDetails caseDetails14 = generateCaseDetails("caseDetailsTest14.json");
        ccdRequest14.setCaseDetails(caseDetails14);

        ccdRequest15 = new CCDRequest();
        CaseDetails caseDetails15 = generateCaseDetails("caseDetailsTest15.json");
        ccdRequest15.setCaseDetails(caseDetails15);

        manchesterCcdRequest = new CCDRequest();
        CaseDetails manchesterCaseDetails = new CaseDetails();
        CaseData caseData = new CaseData();
        CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
        casePreAcceptType.setCaseAccepted(YES);
        caseData.setPreAcceptCase(casePreAcceptType);
        caseData.setCaseRefECC("11111");
        caseData.setRespondentECC(createRespondentECC());
        manchesterCaseDetails.setCaseData(caseData);
        manchesterCaseDetails.setCaseId("123456");
        manchesterCaseDetails.setCaseTypeId(MANCHESTER_DEV_CASE_TYPE_ID);
        manchesterCaseDetails.setJurisdiction("TRIBUNALS");
        manchesterCcdRequest.setCaseDetails(manchesterCaseDetails);

        submitEvent = new SubmitEvent();
        CaseData submitCaseData = new CaseData();
        submitCaseData.setRespondentCollection(createRespondentCollection(true));
        submitCaseData.setClaimantIndType(createClaimantIndType());
        submitCaseData.setRepresentativeClaimantType(createRepresentedTypeC());
        submitCaseData.setRepCollection(createRepCollection(false));
        submitCaseData.setClaimantRepresentedQuestion(YES);
        ClaimantType claimantType = new ClaimantType();
        Address address = new Address();
        address.setAddressLine1("AddressLine1");
        address.setAddressLine2("AddressLine2");
        address.setAddressLine3("AddressLine3");
        address.setPostTown("Manchester");
        address.setCountry("UK");
        address.setPostCode("L1 122");
        claimantType.setClaimantAddressUK(address);
        submitCaseData.setClaimantType(claimantType);
        submitEvent.setState("Accepted");
        submitEvent.setCaseId(123);
        submitEvent.setCaseData(submitCaseData);

        caseManagementForCaseWorkerService = new CaseManagementForCaseWorkerService(caseRetrievalForCaseWorkerService, ccdClient);
    }

    @Test
    public void caseDataDefaultsClaimantIndividual() {
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        assertEquals("Anton Juliet Rodriguez", caseData.getClaimant());
    }

    @Test
    public void caseDataDefaultsClaimantCompany() {
        CaseData caseData = scotlandCcdRequest2.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        assertEquals("Orlando LTD", caseData.getClaimant());
    }

    @Test
    public void caseDataDefaultsClaimantMissing() {
        CaseData caseData = scotlandCcdRequest2.getCaseDetails().getCaseData();
        caseData.setClaimantTypeOfClaimant(null);
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        assertEquals("Missing claimant", caseData.getClaimant());
    }

    @Test
    public void caseDataDefaultsRespondentAvailable() {
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        assertEquals("Antonio Vazquez", caseData.getRespondent());
    }

    @Test
    public void caseDataDefaultsRespondentMissing() {
        CaseData caseData = scotlandCcdRequest2.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        assertEquals("Missing respondent", caseData.getRespondent());
    }

    @Test
    public void caseDataDefaultsStruckOutYESandNulltoNO() {
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();

        caseManagementForCaseWorkerService.caseDataDefaults(caseData);

        assertEquals(3, caseData.getRespondentCollection().size());

        assertEquals("Antonio Vazquez", caseData.getRespondentCollection().get(0).getValue().getRespondentName());
        assertEquals(NO, caseData.getRespondentCollection().get(0).getValue().getResponseStruckOut());
        assertEquals("Juan Garcia", caseData.getRespondentCollection().get(1).getValue().getRespondentName());
        assertEquals(NO, caseData.getRespondentCollection().get(1).getValue().getResponseStruckOut());
        assertEquals("Roberto Dondini", caseData.getRespondentCollection().get(2).getValue().getRespondentName());
        assertEquals(NO, caseData.getRespondentCollection().get(2).getValue().getResponseStruckOut());
    }

    @Test
    public void caseDataDefaultsStruckOutUnchanged() {
        CaseData caseData = scotlandCcdRequest3.getCaseDetails().getCaseData();

        caseManagementForCaseWorkerService.caseDataDefaults(caseData);

        assertEquals(1, caseData.getRespondentCollection().size());

        assertEquals("Antonio Vazquez", caseData.getRespondentCollection().get(0).getValue().getRespondentName());
        assertEquals(NO, caseData.getRespondentCollection().get(0).getValue().getResponseStruckOut());
    }

    @Test
    public void caseDataDefaultsFlagsImageFileNameNull() {
        CaseData caseData = manchesterCcdRequest.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        assertNull(caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-0000000.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    public void caseDataDefaultsFlagsImageFileNameEmpty() {
        CaseData caseData = ccdRequest10.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        assertNull(caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-0000000.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    public void preAcceptCaseAccepted() {
        assertEquals(ACCEPTED_STATE, caseManagementForCaseWorkerService.preAcceptCase(manchesterCcdRequest).getState());
    }

    @Test
    public void preAcceptCaseRejected() {
        manchesterCcdRequest.getCaseDetails().getCaseData().getPreAcceptCase().setCaseAccepted(NO);
        assertEquals(REJECTED_STATE, caseManagementForCaseWorkerService.preAcceptCase(manchesterCcdRequest).getState());
    }

    @Test
    public void dateToCurrentPositionChanged() {
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.dateToCurrentPosition(caseData);
        assertEquals(caseData.getCurrentPosition(), caseData.getPositionType());
        assertEquals(caseData.getDateToPosition(), LocalDate.now().toString());
    }

    @Test
    public void dateToCurrentPositionUnChanged() {
        CaseData caseData = scotlandCcdRequest2.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.dateToCurrentPosition(caseData);
        assertEquals(caseData.getCurrentPosition(), caseData.getPositionType());
        assertEquals("2019-11-15", caseData.getDateToPosition());
    }

    @Test
    public void dateToCurrentPositionNullPositionType() {
        CaseData caseData = scotlandCcdRequest3.getCaseDetails().getCaseData();
        caseData.setPositionType(null);
        caseManagementForCaseWorkerService.dateToCurrentPosition(caseData);
        assertNull(caseData.getPositionType());
        assertNull(caseData.getDateToPosition());
    }

    @Test
    public void dateToCurrentPositionNullCurrentPosition() {
        CaseData caseData = scotlandCcdRequest3.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.dateToCurrentPosition(caseData);
        assertEquals(caseData.getCurrentPosition(), caseData.getPositionType());
        assertEquals(caseData.getDateToPosition(), LocalDate.now().toString());
    }

    @Test
    public void struckOutRespondentFirstToLast() {
        CaseData caseData = caseManagementForCaseWorkerService.struckOutRespondents(scotlandCcdRequest1);

        assertEquals(3, caseData.getRespondentCollection().size());

        assertEquals("Antonio Vazquez", caseData.getRespondentCollection().get(0).getValue().getRespondentName());
        assertEquals(NO, caseData.getRespondentCollection().get(0).getValue().getResponseStruckOut());
        assertEquals("Roberto Dondini", caseData.getRespondentCollection().get(1).getValue().getRespondentName());
        assertEquals(NO, caseData.getRespondentCollection().get(1).getValue().getResponseStruckOut());
        assertEquals("Juan Garcia", caseData.getRespondentCollection().get(2).getValue().getRespondentName());
        assertEquals(YES, caseData.getRespondentCollection().get(2).getValue().getResponseStruckOut());
    }

    @Test
    public void struckOutRespondentUnchanged() {
        CaseData caseData = caseManagementForCaseWorkerService.struckOutRespondents(scotlandCcdRequest3);

        assertEquals(1, caseData.getRespondentCollection().size());

        assertEquals("Antonio Vazquez", caseData.getRespondentCollection().get(0).getValue().getRespondentName());
    }

    @Test
    public void buildFlagsImageFileNameForNullFlagsTypes() {
        CaseData caseData = ccdRequest11.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.buildFlagsImageFileName(caseData);
        assertEquals("", caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-0000000.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    public void buildFlagsImageFileNameForNullFlagsFields() {
        CaseData caseData = ccdRequest12.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.buildFlagsImageFileName(caseData);
        assertEquals("", caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-0000000.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    public void buildFlagsImageFileNameForEmptyFlagsFields() {
        CaseData caseData = ccdRequest13.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.buildFlagsImageFileName(caseData);
        assertEquals("", caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-0000000.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    public void buildFlagsImageFileNameForFalseFlagsFields() {
        CaseData caseData = ccdRequest14.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.buildFlagsImageFileName(caseData);
        assertEquals("", caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-0000000.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    public void buildFlagsImageFileNameForTrueFlagsFields() {
        CaseData caseData = ccdRequest15.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.buildFlagsImageFileName(caseData);
        String expected = "" +
                "<font color='Black'>DO NOT POSTPONE</font> - " +
                "<font color='Green'>LIVE APPEAL</font> - " +
                "<font color='Red'>RULE 50(3)b</font> - " +
                "<font color='Turquoise'>REPORTING</font> - " +
                "<font color='Orange'>SENSITIVE</font> - " +
                "<font color='Purple'>RESERVED</font> - " +
                "<font color='Blue'>ECC</font>";
        assertEquals(expected, caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-1111111.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    public void addNewHearingItemForExistingHearingCollection() {
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();

        assertEquals(1, caseData.getHearingCollection().size());
        assertEquals(1, caseData.getHearingCollection().get(0).getValue().getHearingDateCollection().size());
        assertEquals("1", caseData.getHearingSelection().getValue().getLabel());

        assertEquals("2", caseData.getHearingNumbers());
        assertEquals("Preliminary Hearing", caseData.getHearingTypes());
        assertEquals("Public", caseData.getHearingPublicPrivate());
        assertEquals("Crown Court", caseData.getHearingVenue().getValue().getLabel());
        assertEquals("1", caseData.getHearingEstLengthNum());
        assertEquals("Days", caseData.getHearingEstLengthNumType());
        assertEquals("Full Panel", caseData.getHearingSitAlone());
        assertEquals("Stage 1", caseData.getHearingStage());
        assertEquals("2019-11-25T12:11:00.000", caseData.getListedDate());
        assertEquals("Test hearing 2", caseData.getHearingNotes());

        caseData = caseManagementForCaseWorkerService.addNewHearingItem(scotlandCcdRequest1);

        assertEquals(2, caseData.getHearingCollection().size());
        assertEquals(1, caseData.getHearingCollection().get(1).getValue().getHearingDateCollection().size());
        assertEquals(2, caseData.getHearingSelection().getListItems().size());

        assertNull(caseData.getHearingNumbers());
        assertNull(caseData.getHearingTypes());
        assertNull(caseData.getHearingPublicPrivate());
        assertNull(caseData.getHearingVenue());
        assertNull(caseData.getHearingEstLengthNum());
        assertNull(caseData.getHearingEstLengthNumType());
        assertNull(caseData.getHearingSitAlone());
        assertNull(caseData.getHearingStage());
        assertNull(caseData.getListedDate());
        assertNull(caseData.getHearingNotes());
    }

    @Test
    public void addNewHearingItemForEmptyHearingCollection() {
        CaseData caseData = scotlandCcdRequest3.getCaseDetails().getCaseData();

        assertNull(caseData.getHearingCollection());
        assertNull(caseData.getHearingSelection());

        assertEquals("1", caseData.getHearingNumbers());
        assertEquals("Preliminary Hearing", caseData.getHearingTypes());
        assertEquals("Public", caseData.getHearingPublicPrivate());
        assertEquals("Crown Court", caseData.getHearingVenue().getValue().getLabel());
        assertEquals("1", caseData.getHearingEstLengthNum());
        assertEquals("Days", caseData.getHearingEstLengthNumType());
        assertEquals("Sit Alone", caseData.getHearingSitAlone());
        assertEquals("Stage 1", caseData.getHearingStage());
        assertEquals("2019-11-25T12:11:00.000", caseData.getListedDate());
        assertEquals("Test hearing 1", caseData.getHearingNotes());

        caseData = caseManagementForCaseWorkerService.addNewHearingItem(scotlandCcdRequest3);

        assertEquals(1, caseData.getHearingCollection().size());
        assertEquals(1, caseData.getHearingCollection().get(0).getValue().getHearingDateCollection().size());
        assertEquals(1, caseData.getHearingSelection().getListItems().size());

        assertNull(caseData.getHearingNumbers());
        assertNull(caseData.getHearingTypes());
        assertNull(caseData.getHearingPublicPrivate());
        assertNull(caseData.getHearingVenue());
        assertNull(caseData.getHearingEstLengthNum());
        assertNull(caseData.getHearingEstLengthNumType());
        assertNull(caseData.getHearingSitAlone());
        assertNull(caseData.getHearingStage());
        assertNull(caseData.getListedDate());
        assertNull(caseData.getHearingNotes());
    }

    @Test
    public void fetchHearingItemData() {
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();

        caseData.setHearingTypes(null);
        caseData.setHearingSitAlone(null);
        caseData.setHearingERMember(null);
        caseData.setHearingEEMember(null);

        assertEquals("1", caseData.getHearingSelection().getValue().getCode());
        assertEquals("Edit hearing", caseData.getHearingActions());

        assertNull(caseData.getHearingTypes());
        assertNull(caseData.getHearingSitAlone());
        assertNull(caseData.getHearingERMember());
        assertNull(caseData.getHearingEEMember());

        assertNull(caseData.getHearingDatesRequireAmending());
        assertNull(caseData.getHearingDateSelection());

        caseData = caseManagementForCaseWorkerService.fetchHearingItemData(scotlandCcdRequest1);

        assertEquals("1", caseData.getHearingSelection().getValue().getCode());
        assertEquals("Edit hearing", caseData.getHearingActions());

        assertEquals("Single", caseData.getHearingTypes());
        assertEquals("Full Panel", caseData.getHearingSitAlone());
        assertEquals("J Lee", caseData.getHearingERMember());
        assertEquals("L Hill", caseData.getHearingEEMember());

        assertNull(caseData.getHearingDatesRequireAmending());
        assertEquals(2, caseData.getHearingDateSelection().getListItems().size());
    }

    @Test
    public void amendHearingItemDetailsWithEditHearing() {
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();

        List<DynamicValueType> listItems = new ArrayList<>();
        DynamicValueType dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode("1");
        dynamicValueType.setLabel("1");
        listItems.add(dynamicValueType);

        caseData.getHearingSelection().setListItems(listItems);

        assertEquals("1", caseData.getHearingSelection().getValue().getCode());
        assertEquals("Edit hearing", caseData.getHearingActions());

        assertEquals("Full Panel", caseData.getHearingCollection().get(0).getValue().getHearingSitAlone());
        assertEquals("J Lee", caseData.getHearingCollection().get(0).getValue().getHearingERMember());
        assertEquals("L Hill", caseData.getHearingCollection().get(0).getValue().getHearingEEMember());

        caseData = caseManagementForCaseWorkerService.amendHearingItemDetails(scotlandCcdRequest1);

        assertEquals("Full Panel", caseData.getHearingCollection().get(0).getValue().getHearingSitAlone());
        assertEquals("A Morgan", caseData.getHearingCollection().get(0).getValue().getHearingERMember());
        assertEquals("S Jones", caseData.getHearingCollection().get(0).getValue().getHearingEEMember());

        assertEquals("1", caseData.getHearingSelection().getValue().getCode());
        assertNull(caseData.getHearingActions());

        assertNull(caseData.getHearingTypes());
        assertNull(caseData.getHearingSitAlone());
        assertNull(caseData.getHearingERMember());
        assertNull(caseData.getHearingEEMember());
        assertNull(caseData.getHearingDatesRequireAmending());
        assertNull(caseData.getHearingDateSelection());

        assertNull(caseData.getHearingDateActions());
    }

    @Test
    public void amendHearingItemDetailsWithDeleteHearing() {
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();

        caseData.setHearingActions("Delete hearing");

        assertEquals(1, caseData.getHearingCollection().size());
        assertEquals(1, caseData.getHearingCollection().get(0).getValue().getHearingDateCollection().size());
        assertEquals("1", caseData.getHearingSelection().getValue().getLabel());
        assertEquals("Delete hearing", caseData.getHearingActions());

        caseData = caseManagementForCaseWorkerService.amendHearingItemDetails(scotlandCcdRequest1);

        assertEquals(0, caseData.getHearingCollection().size());

        assertNull(caseData.getHearingActions());
    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }

    @Test
    public void midRespondentECC() {
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN), isA(String.class), isA(List.class)))
                .thenReturn(new ArrayList(Collections.singleton(submitEvent)));
        assertEquals(1, caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(), AUTH_TOKEN,
                new ArrayList<>(), MID_EVENT_CALLBACK).getRespondentECC().getListItems().size());
    }

    @Test
    public void midRespondentECCWithStruckOut() {
        CaseData caseData = new CaseData();
        caseData.setRespondentCollection(createRespondentCollection(false));
        submitEvent.setCaseData(caseData);
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN), isA(String.class), isA(List.class)))
                .thenReturn(new ArrayList(Collections.singleton(submitEvent)));
        assertEquals(2, caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(), AUTH_TOKEN,
                new ArrayList<>(), MID_EVENT_CALLBACK).getRespondentECC().getListItems().size());
    }

    @Test
    public void midRespondentECCEmpty() {
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN), isA(String.class), isA(List.class)))
                .thenReturn(null);
        List<String> errors = new ArrayList<>();
        caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(), AUTH_TOKEN, errors, MID_EVENT_CALLBACK);
        assertEquals("[Case Reference Number not found.]", errors.toString());
    }

    @Test
    public void midRespondentECCWithNoRespondentECC() {
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN), isA(String.class), isA(List.class)))
                .thenReturn(new ArrayList(Collections.singleton(submitEvent)));
        manchesterCcdRequest.getCaseDetails().getCaseData().setRespondentECC(null);
        assertEquals(1, caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(), AUTH_TOKEN,
                new ArrayList<>(), MID_EVENT_CALLBACK).getRespondentECC().getListItems().size());
    }

    @Test
    public void createECC() {
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN), isA(String.class), isA(List.class)))
                .thenReturn(new ArrayList(Collections.singleton(submitEvent)));
        assertEquals("123", caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(), AUTH_TOKEN,
                new ArrayList<>(), ABOUT_TO_SUBMIT_EVENT_CALLBACK).getCcdID());
    }

    @Test
    public void linkOriginalCaseECC() {
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN), isA(String.class), isA(List.class)))
                .thenReturn(new ArrayList(Collections.singleton(submitEvent)));
        assertEquals("11111", caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(), AUTH_TOKEN,
                new ArrayList<>(), SUBMITTED_CALLBACK).getCaseRefECC());
    }

    @Test(expected = Exception.class)
    public void linkOriginalCaseECCException() throws IOException {
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN), isA(String.class), isA(List.class)))
                .thenReturn(new ArrayList(Collections.singleton(submitEvent)));
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString())).thenThrow(feignError());
        caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(), AUTH_TOKEN,
                new ArrayList<>(), SUBMITTED_CALLBACK);
    }

    @Test
    public void createECCFromClosedCaseWithoutET3() {
        submitEvent.setState("Closed");
        submitEvent.getCaseData().getRespondentCollection().get(0).getValue().setResponseReceived(NO);
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN), isA(String.class), isA(List.class)))
                .thenReturn(new ArrayList(Collections.singleton(submitEvent)));
        List<String> errors = new ArrayList<>();
        CaseData caseData = caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(), AUTH_TOKEN, errors, MID_EVENT_CALLBACK);
        assertNull(caseData.getRespondentECC().getListItems());
        assertEquals(2, errors.size());
        submitEvent.setState("Accepted");
        submitEvent.getCaseData().getRespondentCollection().get(0).getValue().setResponseReceived(YES);
    }

    private List<RespondentSumTypeItem> createRespondentCollection(boolean single) {
        RespondentSumTypeItem respondentSumTypeItem1 = createRespondentSumType("RespondentName1", false);
        RespondentSumTypeItem respondentSumTypeItem2 = createRespondentSumType("RespondentName2", false);
        RespondentSumTypeItem respondentSumTypeItem3 = createRespondentSumType("RespondentName3", true);
        if (single) {
            return new ArrayList<>(Collections.singletonList(respondentSumTypeItem1));
        } else {
            return new ArrayList<>(Arrays.asList(respondentSumTypeItem1, respondentSumTypeItem2, respondentSumTypeItem3));
        }
    }

    private RespondentSumTypeItem createRespondentSumType(String respondentName, boolean struckOut) {
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName(respondentName);
        if (struckOut) {
            respondentSumType.setResponseStruckOut(YES);
        }
        respondentSumType.setResponseReceived(YES);
        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setId("111");
        respondentSumTypeItem.setValue(respondentSumType);
        return respondentSumTypeItem;
    }

    private ClaimantIndType createClaimantIndType() {
        ClaimantIndType claimantIndType = new ClaimantIndType();
        claimantIndType.setClaimantLastName("ClaimantSurname");
        claimantIndType.setClaimantFirstNames("ClaimantName");
        claimantIndType.setClaimantTitle("Mr");
        return claimantIndType;
    }

    private RepresentedTypeC createRepresentedTypeC() {
        RepresentedTypeC representativeClaimantType = new RepresentedTypeC();
        representativeClaimantType.setNameOfRepresentative("Claimant Rep Name");
        representativeClaimantType.setNameOfOrganisation("Claimant Rep Org");
        representativeClaimantType.setRepresentativeReference("Claimant Rep Ref");
        return representativeClaimantType;
    }

    private List<RepresentedTypeRItem> createRepCollection(boolean single) {
        RepresentedTypeRItem representedTypeRItem1 = createRepresentedTypeR("", "RepresentativeNameAAA");
        RepresentedTypeRItem representedTypeRItem2 = createRepresentedTypeR("dummy", "RepresentativeNameBBB");
        RepresentedTypeRItem representedTypeRItem3 = createRepresentedTypeR("RespondentName1", "RepresentativeNameCCC");
        if (single) {
            return new ArrayList<>(Collections.singletonList(representedTypeRItem1));
        } else {
            return new ArrayList<>(Arrays.asList(representedTypeRItem1, representedTypeRItem2, representedTypeRItem3));
        }
    }

    private RepresentedTypeRItem createRepresentedTypeR(String respondentName, String representativeName) {
        RepresentedTypeR representedTypeR = new RepresentedTypeR();
        representedTypeR.setRespRepName(respondentName);
        representedTypeR.setNameOfRepresentative(representativeName);
        RepresentedTypeRItem representedTypeRItem = new RepresentedTypeRItem();
        representedTypeRItem.setId("111");
        representedTypeRItem.setValue(representedTypeR);
        return representedTypeRItem;
    }

    private DynamicFixedListType createRespondentECC() {
        DynamicFixedListType respondentECC = new DynamicFixedListType();
        DynamicValueType dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode("RespondentName1");
        dynamicValueType.setLabel("RespondentName1");
        respondentECC.setValue(dynamicValueType);
        return respondentECC;
    }

}