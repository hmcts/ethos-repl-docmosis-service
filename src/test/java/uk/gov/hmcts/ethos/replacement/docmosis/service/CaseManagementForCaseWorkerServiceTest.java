package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.Address;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.Document;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.EccCounterClaimTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.CasePreAcceptType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantType;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.EccCounterClaimType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingListingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeC;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeR;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FlagsImageHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ABERDEEN_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ABOUT_TO_SUBMIT_EVENT_CALLBACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DUNDEE_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.EDINBURGH_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_ECC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.GLASGOW_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MID_EVENT_CALLBACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SUBMITTED_CALLBACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.CaseManagementForCaseWorkerService.LISTED_DATE_ON_WEEKEND_MESSAGE;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException.ERROR_MESSAGE;

@ExtendWith(SpringExtension.class)
class CaseManagementForCaseWorkerServiceTest {
    @InjectMocks
    private CaseManagementForCaseWorkerService caseManagementForCaseWorkerService;
    private static final String AUTH_TOKEN = "Bearer eyJhbGJbpjciOiJIUzI1NiJ9";
    private static final String EMPLOYMENT_JURISDICTION = "EMPLOYMENT";
    private static final String EMPTY_STRING = "";
    private CCDRequest scotlandCcdRequest1;
    private CCDRequest scotlandCcdRequest2;
    private CCDRequest scotlandCcdRequest3;
    private CCDRequest ccdRequest10;
    private CCDRequest ccdRequest11;
    private CCDRequest ccdRequest12;
    private CCDRequest ccdRequest13;
    private CCDRequest ccdRequest14;
    private CCDRequest ccdRequest15;
    private CCDRequest ccdRequest16;
    private CCDRequest manchesterCcdRequest;
    private SubmitEvent submitEvent;

    @MockBean
    private CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService;
    @MockBean
    private CcdClient ccdClient;
    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    @BeforeEach
    void setUp() throws Exception {
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

        ccdRequest16 = new CCDRequest();
        CaseDetails caseDetails16 = generateCaseDetails("caseDetailsScotTestHearingUpdates.json");
        ccdRequest16.setCaseDetails(caseDetails16);

        manchesterCcdRequest = new CCDRequest();
        CaseData caseData = new CaseData();
        CasePreAcceptType casePreAcceptType = new CasePreAcceptType();
        casePreAcceptType.setCaseAccepted(YES);
        caseData.setPreAcceptCase(casePreAcceptType);
        caseData.setCaseRefECC("11111");

        EccCounterClaimTypeItem eccCounterClaimTypeItem = new EccCounterClaimTypeItem();
        EccCounterClaimType counterClaimType = new EccCounterClaimType();
        counterClaimType.setCounterClaim("72632632");
        eccCounterClaimTypeItem.setId(UUID.randomUUID().toString());
        eccCounterClaimTypeItem.setValue(counterClaimType);

        caseData.setEccCases(List.of(eccCounterClaimTypeItem));
        caseData.setRespondentECC(createRespondentECC());
        CaseDetails manchesterCaseDetails = new CaseDetails();
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
        Address address = new Address();
        address.setAddressLine1("AddressLine1");
        address.setAddressLine2("AddressLine2");
        address.setAddressLine3("AddressLine3");
        address.setPostTown("Manchester");
        address.setCountry("UK");
        address.setPostCode("L1 122");
        ClaimantType claimantType = new ClaimantType();
        claimantType.setClaimantAddressUK(address);
        submitCaseData.setClaimantType(claimantType);
        submitEvent.setState("Accepted");
        submitEvent.setCaseId(123);
        submitEvent.setCaseData(submitCaseData);

        caseManagementForCaseWorkerService = new CaseManagementForCaseWorkerService(caseRetrievalForCaseWorkerService,
                ccdClient, ccdGatewayBaseUrl);
    }

    @Test
    void caseDataDefaultsClaimantIndividual() {
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        assertEquals("Anton Juliet Rodriguez", caseData.getClaimant());
    }

    @Test
    void caseDataDefaultsResponseReceived() {
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        for (RespondentSumTypeItem respondentSumTypeItem : caseData.getRespondentCollection()) {
            assertEquals(NO, respondentSumTypeItem.getValue().getResponseReceived());
        }
    }

    @Test
    void caseDataDefaultsResetResponseRespondentAddress() {
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();
        for (RespondentSumTypeItem respondentSumTypeItem : caseData.getRespondentCollection()) {
            respondentSumTypeItem.getValue().setResponseReceived(null);
            respondentSumTypeItem.getValue().setResponseRespondentAddress(new Address());
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setAddressLine1("Address1");
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setAddressLine2("Address2");
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setAddressLine3("Address3");
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setCounty("County");
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setPostTown("PostTown");
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setCountry("Country");
            respondentSumTypeItem.getValue().getResponseRespondentAddress().setPostCode("PostCode");

        }
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        for (RespondentSumTypeItem respondentSumTypeItem : caseData.getRespondentCollection()) {
            assertEquals("", respondentSumTypeItem.getValue().getResponseRespondentAddress().getAddressLine1());
            assertEquals("", respondentSumTypeItem.getValue().getResponseRespondentAddress().getAddressLine2());
            assertEquals("", respondentSumTypeItem.getValue().getResponseRespondentAddress().getAddressLine3());
            assertEquals("", respondentSumTypeItem.getValue().getResponseRespondentAddress().getCountry());
            assertEquals("", respondentSumTypeItem.getValue().getResponseRespondentAddress().getCounty());
            assertEquals("", respondentSumTypeItem.getValue().getResponseRespondentAddress().getPostCode());
            assertEquals("", respondentSumTypeItem.getValue().getResponseRespondentAddress().getPostTown());
        }
    }

    @Test
    void caseDataDefaultsResponseReceivedDoesNotChange() {
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();
        caseData.getRespondentCollection().get(0).getValue().setResponseReceived(YES);
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        assertEquals(YES, caseData.getRespondentCollection().get(0).getValue().getResponseReceived());
        for (RespondentSumTypeItem respondentSumTypeItem : caseData.getRespondentCollection()) {
            if (respondentSumTypeItem != caseData.getRespondentCollection().get(0)) {
                assertEquals(NO, respondentSumTypeItem.getValue().getResponseReceived());
            }
        }
    }

    @Test
    void caseDataDefaultsClaimantCompany() {
        CaseData caseData = scotlandCcdRequest2.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        assertEquals("Orlando LTD", caseData.getClaimant());
    }

    @Test
    void caseDataDefaultsClaimantMissing() {
        CaseData caseData = scotlandCcdRequest2.getCaseDetails().getCaseData();
        caseData.setClaimantTypeOfClaimant(null);
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        assertEquals("Missing claimant", caseData.getClaimant());
    }

    @Test
    void caseDataDefaultsRespondentAvailable() {
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        assertEquals("Antonio Vazquez", caseData.getRespondent());
    }

    @Test
    void caseDataDefaultsRespondentMissing() {
        CaseData caseData = scotlandCcdRequest2.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        assertEquals("Missing respondent", caseData.getRespondent());
    }

    @Test
    void caseDataDefaultsStruckOutYESandNulltoNO() {
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);

        assertEquals(3, caseData.getRespondentCollection().size());
        assertEquals("Antonio Vazquez", caseData.getRespondentCollection().get(0).getValue().getRespondentName());
        assertEquals(NO, caseData.getRespondentCollection().get(0).getValue().getResponseStruckOut());
        assertEquals("Juan Garcia", caseData.getRespondentCollection().get(1).getValue().getRespondentName());
        assertEquals(YES, caseData.getRespondentCollection().get(1).getValue().getResponseStruckOut());
        assertEquals("Roberto Dondini", caseData.getRespondentCollection().get(2).getValue().getRespondentName());
        assertEquals(NO, caseData.getRespondentCollection().get(2).getValue().getResponseStruckOut());
    }

    @Test
    void caseDataDefaultsStruckOutUnchanged() {
        CaseData caseData = scotlandCcdRequest3.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);

        assertEquals(1, caseData.getRespondentCollection().size());
        assertEquals("Antonio Vazquez", caseData.getRespondentCollection().get(0).getValue().getRespondentName());
        assertEquals(NO, caseData.getRespondentCollection().get(0).getValue().getResponseStruckOut());
    }

    @Test
    void caseDataDefaultsFlagsImageFileNameNull() {
        CaseData caseData = manchesterCcdRequest.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        assertNull(caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-0000000.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    void caseDataDefaultsFlagsImageFileNameEmpty() {
        CaseData caseData = ccdRequest10.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.caseDataDefaults(caseData);
        assertNull(caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-0000000.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    void dateToCurrentPositionChanged() {
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.dateToCurrentPosition(caseData);
        assertEquals(caseData.getCurrentPosition(), caseData.getPositionType());
        assertEquals(caseData.getDateToPosition(), LocalDate.now().toString());
    }

    @Test
    void amendRespondentRepNames() {
        RepresentedTypeRItem representedTypeRItem = new RepresentedTypeRItem();
        representedTypeRItem.setId(UUID.randomUUID().toString());
        RepresentedTypeR representedTypeR = new RepresentedTypeR();
        representedTypeR.setNameOfRepresentative("rep1");
        representedTypeR.setRespondentId("respId1");
        representedTypeR.setRespRepName("resp1");
        representedTypeRItem.setValue(representedTypeR);
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();
        caseData.setRepCollection(List.of(representedTypeRItem));
        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setId("respId1");
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName("resp2");
        respondentSumTypeItem.setValue(respondentSumType);
        caseData.setRespondentCollection(List.of(respondentSumTypeItem));
        caseManagementForCaseWorkerService.amendRespondentNameRepresentativeNames(caseData);
        assertEquals("resp2", caseData.getRepCollection().get(0).getValue().getRespRepName());
    }

    @Test
    void dateToCurrentPositionUnChanged() {
        CaseData caseData = scotlandCcdRequest2.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.dateToCurrentPosition(caseData);
        assertEquals(caseData.getCurrentPosition(), caseData.getPositionType());
        assertEquals("2019-11-15", caseData.getDateToPosition());
    }

    @Test
    void updateWithRespondentIds() {
        RepresentedTypeRItem representedTypeRItem = new RepresentedTypeRItem();
        representedTypeRItem.setId(UUID.randomUUID().toString());
        RepresentedTypeR representedTypeR = new RepresentedTypeR();
        representedTypeR.setNameOfRepresentative("rep1");
        representedTypeR.setRespRepName("resp1");
        representedTypeRItem.setValue(representedTypeR);
        CaseData caseData = scotlandCcdRequest2.getCaseDetails().getCaseData();
        caseData.setRepCollection(List.of(representedTypeRItem));
        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setId("respId1");
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName("resp1");
        respondentSumTypeItem.setValue(respondentSumType);
        caseData.setRespondentCollection(List.of(respondentSumTypeItem));
        caseManagementForCaseWorkerService.updateWithRespondentIds(caseData);
        assertEquals("respId1", caseData.getRepCollection().get(0).getValue().getRespondentId());
    }

    @Test
    void setNextListedDate() {
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        dateListedTypeItem.setId(UUID.randomUUID().toString());
        DateListedType dateListedType = new DateListedType();
        dateListedType.setListedDate(LocalDateTime.now().plusDays(2).toString());
        dateListedType.setHearingStatus(HEARING_STATUS_LISTED);
        dateListedTypeItem.setValue(dateListedType);
        CaseData caseData = scotlandCcdRequest1.getCaseDetails().getCaseData();
        List<DateListedTypeItem> dateListedTypeItems =
                caseData.getHearingCollection().get(0).getValue().getHearingDateCollection();
        dateListedTypeItems.add(dateListedTypeItem);
        caseData.getHearingCollection().get(0).getValue().setHearingDateCollection(dateListedTypeItems);
        String expectedNextListedDate = LocalDate.now().plusDays(2).toString();
        caseManagementForCaseWorkerService.setNextListedDate(caseData);
        assertEquals(expectedNextListedDate, caseData.getNextListedDate());
    }

    @Test
    void dateToCurrentPositionNullPositionType() {
        CaseData caseData = scotlandCcdRequest3.getCaseDetails().getCaseData();
        caseData.setPositionType(null);
        caseManagementForCaseWorkerService.dateToCurrentPosition(caseData);
        assertNull(caseData.getPositionType());
        assertNull(caseData.getDateToPosition());
    }

    @Test
    void dateToCurrentPositionNullCurrentPosition() {
        CaseData caseData = scotlandCcdRequest3.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.dateToCurrentPosition(caseData);
        assertEquals(caseData.getCurrentPosition(), caseData.getPositionType());
        assertEquals(caseData.getDateToPosition(), LocalDate.now().toString());
    }

    @Test
    void struckOutRespondentFirstToLast() {
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
    void struckOutRespondentUnchanged() {
        CaseData caseData = caseManagementForCaseWorkerService.struckOutRespondents(scotlandCcdRequest3);

        assertEquals(1, caseData.getRespondentCollection().size());

        assertEquals("Antonio Vazquez", caseData.getRespondentCollection().get(0).getValue().getRespondentName());
    }

    @Test
    void continuingRespondentFirstToLast() {
        CaseData caseData = caseManagementForCaseWorkerService.continuingRespondent(scotlandCcdRequest1);

        assertEquals(3, caseData.getRespondentCollection().size());

        assertEquals("Antonio Vazquez", caseData.getRespondentCollection().get(0).getValue().getRespondentName());
        assertEquals(YES, caseData.getRespondentCollection().get(0).getValue().getResponseContinue());
        assertEquals("Juan Garcia", caseData.getRespondentCollection().get(1).getValue().getRespondentName());
        assertEquals(YES, caseData.getRespondentCollection().get(1).getValue().getResponseContinue());
        assertEquals("Roberto Dondini", caseData.getRespondentCollection().get(2).getValue().getRespondentName());
        assertEquals(NO, caseData.getRespondentCollection().get(2).getValue().getResponseContinue());
    }

    @Test
    void continuingRespondentNull() {
        CaseData caseData = caseManagementForCaseWorkerService.continuingRespondent(scotlandCcdRequest3);
        assertEquals(1, caseData.getRespondentCollection().size());
        assertEquals(YES, caseData.getRespondentCollection().get(0).getValue().getResponseContinue());
    }

    @Test
    void buildFlagsImageFileNameForNullFlagsTypes() {
        CaseData caseData = ccdRequest11.getCaseDetails().getCaseData();
        FlagsImageHelper.buildFlagsImageFileName(caseData);
        assertEquals("", caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-0000000000.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    void buildFlagsImageFileNameForNullFlagsFields() {
        CaseData caseData = ccdRequest12.getCaseDetails().getCaseData();
        FlagsImageHelper.buildFlagsImageFileName(caseData);
        assertEquals("", caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-0000000000.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    void buildFlagsImageFileNameForEmptyFlagsFields() {
        CaseData caseData = ccdRequest13.getCaseDetails().getCaseData();
        FlagsImageHelper.buildFlagsImageFileName(caseData);
        assertEquals("", caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-0000000000.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    void buildFlagsImageFileNameForFalseFlagsFields() {
        CaseData caseData = ccdRequest14.getCaseDetails().getCaseData();
        FlagsImageHelper.buildFlagsImageFileName(caseData);
        assertEquals("", caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-0000000000.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    void buildFlagsImageFileNameForTrueFlagsFields() {
        CaseData caseData = ccdRequest15.getCaseDetails().getCaseData();
        FlagsImageHelper.buildFlagsImageFileName(caseData);
        String expected = "<font color='DarkRed' size='5'> DO NOT POSTPONE </font>"
            + "<font size='5'> - </font>"
            + "<font color='Green' size='5'> LIVE APPEAL </font>"
            + "<font size='5'> - </font>"
            + "<font color='Red' size='5'> RULE 50(3)b </font>"
            + "<font size='5'> - </font>"
            + "<font color='LightBlack' size='5'> REPORTING </font>"
            + "<font size='5'> - </font>"
            + "<font color='Orange' size='5'> SENSITIVE </font>"
            + "<font size='5'> - </font>"
            + "<font color='Purple' size='5'> RESERVED </font>"
            + "<font size='5'> - </font>"
            + "<font color='Olive' size='5'> ECC </font>"
            + "<font size='5'> - </font>"
            + "<font color='SlateGray' size='5'> DIGITAL FILE </font>"
            + "<font size='5'> - </font>"
            + "<font color='DarkSlateBlue' size='5'> REASONABLE ADJUSTMENT </font>";
        assertEquals(expected, caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-0111111111.jpg", caseData.getFlagsImageFileName());
    }

    @Test
    void buildFlagsImageFileNameForTrueFlagsFieldsScotland() {
        CaseData caseData = scotlandCcdRequest3.getCaseDetails().getCaseData();
        FlagsImageHelper.buildFlagsImageFileName(caseData);
        String expected = "<font color='DeepPink' size='5'> WITH OUTSTATION </font>";
        assertEquals(expected, caseData.getFlagsImageAltText());
        assertEquals("EMP-TRIB-1000000000.jpg", caseData.getFlagsImageFileName());
    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }

    @Test
    void amendHearingNonScotland() {
        CaseData caseData = ccdRequest13.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.amendHearing(caseData, MANCHESTER_CASE_TYPE_ID);
        assertEquals(HEARING_STATUS_LISTED, caseData.getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().getHearingStatus());
        assertEquals(HEARING_STATUS_LISTED, caseData.getHearingCollection().get(1).getValue()
                .getHearingDateCollection().get(0).getValue().getHearingStatus());
        assertEquals(HEARING_STATUS_LISTED, caseData.getHearingCollection().get(2).getValue()
                .getHearingDateCollection().get(0).getValue().getHearingStatus());
        assertEquals(HEARING_STATUS_LISTED, caseData.getHearingCollection().get(2).getValue()
                .getHearingDateCollection().get(0).getValue().getHearingStatus());
        assertEquals("Manchester", caseData.getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().getHearingVenueDay());
        assertEquals("2019-11-01T12:11:00.000", caseData.getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().getHearingTimingStart());
        assertEquals("2019-11-01T12:11:00.000", caseData.getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().getHearingTimingFinish());
    }

    @ParameterizedTest
    @CsvSource({"Hearing Number, 1", "Hearing Number, 3"})
    void processHearingsForUpdateRequestByHearingNumber(String hearingsFilterType, String hearingNumber) {
        CaseDetails caseDetails = ccdRequest16.getCaseDetails();
        DynamicFixedListType updateFilterTypeFL = getDynamicFixedListType(hearingNumber);
        caseDetails.getCaseData().setSelectedHearingNumberForUpdate(updateFilterTypeFL);
        caseDetails.getCaseData().setHearingUpdateFilterType(hearingsFilterType);

        caseManagementForCaseWorkerService.processHearingsForUpdateRequest(caseDetails);

        assertNotNull(caseDetails.getCaseData().getHearingsCollectionForUpdate());
        assertEquals(1, caseDetails.getCaseData().getHearingsCollectionForUpdate().size());
        assertEquals(YES, caseDetails.getCaseData().getHearingsCollectionForUpdate().get(0)
                .getValue().getDoesHearingNotesDocExist());
    }

    @ParameterizedTest
    @CsvSource({",", ","})
    void processHearingsForUpdateRequestByHearingNumberWithNullHearingNumber(String hearingsFilterType,
                                                                             String hearingNumber) {
        CaseDetails caseDetails = ccdRequest16.getCaseDetails();
        DynamicFixedListType updateFilterTypeFL = getDynamicFixedListType(hearingNumber);
        caseDetails.getCaseData().setSelectedHearingNumberForUpdate(updateFilterTypeFL);
        caseDetails.getCaseData().setHearingUpdateFilterType("Hearing Number");

        caseManagementForCaseWorkerService.processHearingsForUpdateRequest(caseDetails);

        assertEquals(0, caseDetails.getCaseData().getHearingsCollectionForUpdate().size());
    }

    private DynamicFixedListType getDynamicFixedListType(String hearingNumber) {
        if (hearingNumber == null) {
            return null;
        }

        DynamicFixedListType updateFilterTypeFL = new DynamicFixedListType();
        DynamicValueType dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode(hearingNumber);
        dynamicValueType.setLabel(hearingNumber);
        updateFilterTypeFL.setValue(dynamicValueType);
        updateFilterTypeFL.setListItems(List.of(dynamicValueType));
        return updateFilterTypeFL;

    }

    @ParameterizedTest
    @CsvSource({"Custom filter, Single"})
    void processHearingsForUpdateRequestWithCustomFilterSingleDate(String hearingsFilterType,
                                                                          String hearingDateType) {
        CaseDetails caseDetails = ccdRequest16.getCaseDetails();
        caseDetails.getCaseData().setHearingUpdateFilterType(hearingsFilterType);
        HearingListingType hearingListingType = new HearingListingType();
        hearingListingType.setHearingDate("2019-11-01");
        hearingListingType.setHearingDateType(hearingDateType);
        caseDetails.getCaseData().setUpdateHearingDetails(hearingListingType);

        caseManagementForCaseWorkerService.processHearingsForUpdateRequest(caseDetails);

        assertNotNull(caseDetails.getCaseData().getHearingsCollectionForUpdate());
        assertEquals(2, caseDetails.getCaseData().getHearingsCollectionForUpdate().size());
    }

    @ParameterizedTest
    @CsvSource({"Custom filter, Range, 2019-10-01, 2019-11-01", "Custom filter, Range, 2019-10-01, 2019-11-02"})
    void processHearingsForUpdateRequestWithCustomFilterDateRange(String hearingsFilterType,
                                                                         String hearingDateType,
                                                                         String from, String to) {
        CaseDetails caseDetails = ccdRequest16.getCaseDetails();
        caseDetails.getCaseData().setHearingUpdateFilterType(hearingsFilterType);
        HearingListingType hearingListingType = new HearingListingType();
        hearingListingType.setHearingDateFrom(from);
        hearingListingType.setHearingDateTo(to);
        hearingListingType.setHearingDateType(hearingDateType);
        caseDetails.getCaseData().setUpdateHearingDetails(hearingListingType);

        caseManagementForCaseWorkerService.processHearingsForUpdateRequest(caseDetails);
        assertNotNull(caseDetails.getCaseData().getHearingsCollectionForUpdate());
        assertEquals(4, caseDetails.getCaseData().getHearingsCollectionForUpdate().size());
    }

    @ParameterizedTest
    @CsvSource({"Hearing number, 1, 0", "Hearing number, 2, 1"})
    void updateSelectedHearingMatchingHearingFound(String hearingsFilterType,
                                                          String hearingNumber,
                                                          int index) {
        CaseDetails caseDetails = ccdRequest16.getCaseDetails();
        DynamicFixedListType updateFilterTypeFL = getDynamicFixedListType(hearingNumber);
        caseDetails.getCaseData().setSelectedHearingNumberForUpdate(updateFilterTypeFL);
        caseDetails.getCaseData().setHearingUpdateFilterType(hearingsFilterType);
        var updatedHearing = caseDetails.getCaseData().getHearingCollection().get(index);
        updatedHearing.getValue().setHearingSitAlone("Sit Alone");
        caseDetails.getCaseData().getHearingsCollectionForUpdate()
                .add(updatedHearing);

        caseManagementForCaseWorkerService.updateSelectedHearing(caseDetails.getCaseData());

        assertNotNull(caseDetails.getCaseData().getHearingsCollectionForUpdate());
        assertEquals(0, caseDetails.getCaseData().getHearingsCollectionForUpdate().size());
        assertNull(caseDetails.getCaseData().getHearingUpdateFilterType());
        assertEquals(updatedHearing.getValue().getHearingSitAlone(),
                caseDetails.getCaseData().getHearingCollection().get(index).getValue().getHearingSitAlone());
    }

    @Test
    void removeHearingNotesDocument() {
        CaseDetails caseDetails = ccdRequest16.getCaseDetails();
        DynamicFixedListType updateFilterTypeFL = getDynamicFixedListType(String.valueOf(1));
        caseDetails.getCaseData().setSelectedHearingNumberForUpdate(updateFilterTypeFL);
        caseDetails.getCaseData().setHearingUpdateFilterType("Hearing number");
        HearingTypeItem updatedHearing = caseDetails.getCaseData().getHearingCollection().get(0);
        updatedHearing.getValue().setRemoveHearingNotesDocument(List.of("Remove"));
        caseDetails.getCaseData().getHearingsCollectionForUpdate().add(updatedHearing);

        caseManagementForCaseWorkerService.updateSelectedHearing(caseDetails.getCaseData());
        assertNull(caseDetails.getCaseData().getHearingCollection().get(0).getValue().getHearingNotesDocument());
    }

    @Test
    void addHearingNotesDocument() {
        CaseDetails caseDetails = ccdRequest16.getCaseDetails();
        DynamicFixedListType updateFilterTypeFL = getDynamicFixedListType(String.valueOf(1));
        caseDetails.getCaseData().setSelectedHearingNumberForUpdate(updateFilterTypeFL);
        caseDetails.getCaseData().setHearingUpdateFilterType("Hearing number");
        Document document = new Document();
        document.setFileName("Test Hearing Notes.doc");
        document.setUrl("http://localhost:8080/documents/123456");
        document.setBinaryUrl("http://localhost:8080/documents/123456/binary");
        HearingTypeItem updatedHearing = caseDetails.getCaseData().getHearingCollection().get(0);
        updatedHearing.getValue().setHearingNotesDocument(document);
        caseDetails.getCaseData().getHearingsCollectionForUpdate().add(updatedHearing);

        caseManagementForCaseWorkerService.updateSelectedHearing(caseDetails.getCaseData());
        assertEquals(document, caseDetails.getCaseData().getHearingCollection().get(0)
                .getValue().getHearingNotesDocument());
    }

    @ParameterizedTest
    @CsvSource({"Hearing number, 1, 0", "Hearing number, 2, 1"})
    void updateSelectedHearingWithEmptyHearingsCollectionForUpdate(String hearingsFilterType,
                                                          String hearingNumber, int index) {
        CaseDetails caseDetails = ccdRequest16.getCaseDetails();
        DynamicFixedListType updateFilterTypeFL = getDynamicFixedListType(hearingNumber);
        caseDetails.getCaseData().setSelectedHearingNumberForUpdate(updateFilterTypeFL);
        caseDetails.getCaseData().setHearingUpdateFilterType(hearingsFilterType);
        var updatedHearing = caseDetails.getCaseData().getHearingCollection().get(index);
        updatedHearing.getValue().setHearingSitAlone("Sit Alone");
        caseDetails.getCaseData().setHearingsCollectionForUpdate(null);
        
        caseManagementForCaseWorkerService.updateSelectedHearing(caseDetails.getCaseData());

        assertNull(caseDetails.getCaseData().getHearingsCollectionForUpdate());
        assertEquals(caseDetails.getCaseData().getHearingUpdateFilterType(), hearingsFilterType);
    }

    @ParameterizedTest
    @CsvSource({"Hearing number, 1, Full Panel, EEMember, , 0",
        "Hearing number, 3, Full Panel, EEMember, ERMember, 2",
        "Hearing number, 4, Sit Alone, , , 3"})
    void updateSelectedHearingMatchingHearingByPanelType(String hearingsFilterType, String hearingNumber,
                                                                  String panelType, String eeMember,
                                                                  String erMember, int index) {
        CaseDetails caseDetails = ccdRequest16.getCaseDetails();
        DynamicFixedListType updateFilterTypeFL = getDynamicFixedListType(hearingNumber);
        caseDetails.getCaseData().setSelectedHearingNumberForUpdate(updateFilterTypeFL);
        caseDetails.getCaseData().setHearingUpdateFilterType(hearingsFilterType);
        HearingTypeItem selectedHearing = caseDetails.getCaseData().getHearingCollection().get(index);
        selectedHearing.getValue().setHearingSitAlone(panelType);
        selectedHearing.getValue().setHearingEEMember(eeMember);
        selectedHearing.getValue().setHearingERMember(erMember);
        caseDetails.getCaseData().getHearingsCollectionForUpdate().add(selectedHearing);

        caseManagementForCaseWorkerService.updateSelectedHearing(caseDetails.getCaseData());

        assertNotNull(caseDetails.getCaseData().getHearingsCollectionForUpdate());
        assertEquals(0, caseDetails.getCaseData().getHearingsCollectionForUpdate().size());
        assertEquals(caseDetails.getCaseData().getHearingCollection().get(index)
                        .getValue().getHearingSitAlone(), panelType);
        assertEquals(caseDetails.getCaseData().getHearingCollection().get(index)
                .getValue().getHearingEEMember(), eeMember);
        assertEquals(caseDetails.getCaseData().getHearingCollection().get(index)
                .getValue().getHearingERMember(), erMember);
    }

    @Test
    void midEventAmendHearingDateOnWeekend() {
        CaseData caseData = ccdRequest13.getCaseDetails().getCaseData();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        caseData.getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().setListedDate("2022-03-19T12:11:00.000");
        String hearingNumber = caseData.getHearingCollection().get(0).getValue().getHearingNumber();
        caseManagementForCaseWorkerService.midEventAmendHearing(caseData, errors, warnings);
        assertFalse(errors.isEmpty());
        assertEquals(LISTED_DATE_ON_WEEKEND_MESSAGE + hearingNumber, errors.get(0));
    }

    @Test
    void amendMidEventHearingDateFridayNight() {
        CaseData caseData = createCaseWithHearingDate("2022-03-18T23:59:00.000");
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        caseManagementForCaseWorkerService.midEventAmendHearing(caseData, errors, warnings);
        assertTrue(errors.isEmpty());
    }

    @Test
    void amendMidEventHearingDateMondayMorning() {
        CaseData caseData = createCaseWithHearingDate("2022-03-21T00:00:00.000");
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        caseManagementForCaseWorkerService.midEventAmendHearing(caseData, errors, warnings);
        assertTrue(errors.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({"Listed, 1", " , 1", "Heard, 0"})
    void midEventAmendHearingDateInPast(String hearingStatus, int warning) {
        CaseData caseData = createCaseWithHearingDate("2022-03-18T23:59:00.000");
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        DateListedType dateListedType = caseData.getHearingCollection().get(0)
                .getValue().getHearingDateCollection()
                .get(0).getValue();
        dateListedType.setListedDate("2022-03-19T12:11:00.000");
        dateListedType.setHearingStatus(hearingStatus);
        caseManagementForCaseWorkerService.midEventAmendHearing(caseData, errors, warnings);
        assertEquals(warning, warnings.size());
    }

    private CaseData createCaseWithHearingDate(String date) {
        HearingTypeItem hearing = new HearingTypeItem();
        hearing.setId(UUID.randomUUID().toString());
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        DateListedType dateListedType = new DateListedType();
        dateListedType.setListedDate(date);
        dateListedTypeItem.setId(UUID.randomUUID().toString());
        dateListedTypeItem.setValue(dateListedType);
        HearingType hearingType = new HearingType();
        hearingType.setHearingDateCollection(Collections.singletonList(dateListedTypeItem));
        hearing.setValue(hearingType);
        List<HearingTypeItem> hearings = new ArrayList<>();
        hearings.add(hearing);
        CaseData caseData = new CaseData();
        caseData.setHearingCollection(hearings);
        return caseData;
    }

    @Test
    void amendHearingScotland() {
        CaseData caseData = scotlandCcdRequest3.getCaseDetails().getCaseData();
        caseManagementForCaseWorkerService.amendHearing(caseData, SCOTLAND_CASE_TYPE_ID);
        assertEquals(HEARING_STATUS_LISTED, caseData.getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().getHearingStatus());
        assertEquals(ABERDEEN_OFFICE, caseData.getHearingCollection().get(0).getValue()
                .getHearingDateCollection().get(0).getValue().getHearingAberdeen());
        assertNull(caseData.getHearingCollection().get(0).getValue().getHearingDateCollection()
                .get(0).getValue().getHearingGlasgow());
        assertEquals(GLASGOW_OFFICE, caseData.getHearingCollection().get(1).getValue()
                .getHearingDateCollection().get(0).getValue().getHearingGlasgow());
        assertNull(caseData.getHearingCollection().get(1).getValue().getHearingDateCollection()
                .get(0).getValue().getHearingAberdeen());
        assertEquals(EDINBURGH_OFFICE, caseData.getHearingCollection().get(2).getValue()
                .getHearingDateCollection().get(0).getValue().getHearingEdinburgh());
        assertNull(caseData.getHearingCollection().get(0).getValue().getHearingDateCollection()
                .get(0).getValue().getHearingGlasgow());
        assertEquals(DUNDEE_OFFICE, caseData.getHearingCollection().get(3).getValue()
                .getHearingDateCollection().get(0).getValue().getHearingDundee());
        assertEquals(DUNDEE_OFFICE, caseData.getHearingCollection().get(3).getValue()
                .getHearingDateCollection().get(0).getValue().getHearingVenueDay());
    }

    @Test
    void amendHearingsNewcastleCFT() {
        String expectedHearingVenue = "Newcastle CFT";
        HearingType hearingTypeOne = new HearingType();
        hearingTypeOne.setHearingVenue(expectedHearingVenue);
        hearingTypeOne.setHearingType("Hearing");
        hearingTypeOne.setHearingFormat(List.of("In person"));
        hearingTypeOne.setHearingNumber("1223");
        hearingTypeOne.setHearingSitAlone("Full Panel");
        hearingTypeOne.setHearingEstLengthNum("2");
        hearingTypeOne.setHearingEstLengthNumType("Hours");
        DateListedTypeItem dateListedTypeItemOne = new DateListedTypeItem();
        dateListedTypeItemOne.setId("c409336e-8bf3-405f-b29d-074c59196c8d");

        DateListedType dateListedTypeOne = new DateListedType();
        dateListedTypeOne.setListedDate("2022-12-15T11:00:00.000");
        dateListedTypeOne.setHearingStatus("Listed");
        dateListedTypeOne.setHearingVenueDay("Newcastle CFT");
        dateListedTypeOne.setHearingVenueNameForNewcastleCFT("Newcastle CFCTC");
        dateListedTypeOne.setHearingTimingStart("2022-12-15T11:00:00.000");
        dateListedTypeOne.setHearingTimingFinish("2022-12-15T11:00:00.000");
        dateListedTypeItemOne.setValue(dateListedTypeOne);

        hearingTypeOne.setHearingDateCollection(List.of(dateListedTypeItemOne));

        HearingTypeItem hearingTypeItemOne = new HearingTypeItem();
        hearingTypeItemOne.setId("3912807b-e862-43f2-8436-0eeeccb74220");
        hearingTypeItemOne.setValue(hearingTypeOne);
        List<HearingTypeItem> hearingTypeItems = new ArrayList<>();
        hearingTypeItems.add(hearingTypeItemOne);
        CaseData caseData = ccdRequest10.getCaseDetails().getCaseData();
        caseData.setHearingCollection(hearingTypeItems);

        caseManagementForCaseWorkerService.amendHearing(caseData, NEWCASTLE_CASE_TYPE_ID);

        String actualHearingVenueNameForNewcastleCFT = caseData.getHearingCollection()
            .get(0).getValue().getHearingDateCollection().get(0).getValue().getHearingVenueNameForNewcastleCFT();
        assertNotNull(actualHearingVenueNameForNewcastleCFT);
        assertEquals("Newcastle CFCTC", actualHearingVenueNameForNewcastleCFT);
    }

    @Test
    void amendHearingsTeessideMags() {
        String expectedHearingVenue = "Teesside Mags";
        HearingType hearingTypeOne = new HearingType();
        hearingTypeOne.setHearingVenue(expectedHearingVenue);
        hearingTypeOne.setHearingType("Hearing");
        hearingTypeOne.setHearingFormat(List.of("In person"));
        hearingTypeOne.setHearingNumber("1223");
        hearingTypeOne.setHearingSitAlone("Full Panel");
        hearingTypeOne.setHearingEstLengthNum("5");
        hearingTypeOne.setHearingEstLengthNumType("Hours");
        DateListedTypeItem dateListedTypeItemOne = new DateListedTypeItem();
        dateListedTypeItemOne.setId("c409336e-8bf3-405f-b29d-074c59196c8d");

        DateListedType dateListedTypeOne = new DateListedType();
        dateListedTypeOne.setListedDate("2022-12-15T11:00:00.000");
        dateListedTypeOne.setHearingStatus("Listed");
        dateListedTypeOne.setHearingVenueDay(expectedHearingVenue);
        dateListedTypeOne.setHearingVenueNameForTeessideMags("Teesside Justice Centre");
        dateListedTypeOne.setHearingTimingStart("2022-12-15T11:00:00.000");
        dateListedTypeOne.setHearingTimingFinish("2022-12-15T11:00:00.000");
        dateListedTypeItemOne.setValue(dateListedTypeOne);
        hearingTypeOne.setHearingDateCollection(List.of(dateListedTypeItemOne));

        HearingTypeItem hearingTypeItemOne = new HearingTypeItem();
        hearingTypeItemOne.setId("3912807b-e862-43f2-8436-0eeeccb74220");
        hearingTypeItemOne.setValue(hearingTypeOne);
        List<HearingTypeItem> hearingTypeItems = new ArrayList<>();
        hearingTypeItems.add(hearingTypeItemOne);
        CaseData caseData = ccdRequest10.getCaseDetails().getCaseData();
        caseData.setHearingCollection(hearingTypeItems);

        caseManagementForCaseWorkerService.amendHearing(caseData, NEWCASTLE_CASE_TYPE_ID);

        String actualHearingVenueNameForTeessideMags = caseData.getHearingCollection()
            .get(0).getValue().getHearingDateCollection().get(0).getValue().getHearingVenueNameForTeessideMags();
        assertNotNull(actualHearingVenueNameForTeessideMags);
        assertEquals("Teesside Justice Centre", actualHearingVenueNameForTeessideMags);
    }

    @Test
    void midRespondentECC() {
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN),
                isA(String.class), isA(List.class)))
                .thenReturn(new ArrayList(Collections.singleton(submitEvent)));
        assertEquals(1, caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(), AUTH_TOKEN,
                new ArrayList<>(), MID_EVENT_CALLBACK).getRespondentECC().getListItems().size());
    }

    @Test
    void midRespondentECCWithStruckOut() {
        CaseData caseData = new CaseData();
        caseData.setRespondentCollection(createRespondentCollection(false));
        submitEvent.setCaseData(caseData);
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN),
                isA(String.class), isA(List.class)))
                .thenReturn(new ArrayList(Collections.singleton(submitEvent)));
        assertEquals(2, caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(), AUTH_TOKEN,
                new ArrayList<>(), MID_EVENT_CALLBACK).getRespondentECC().getListItems().size());
    }

    @Test
    void midRespondentECCEmpty() {
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN),
                isA(String.class), isA(List.class)))
                .thenReturn(null);
        List<String> errors = new ArrayList<>();
        caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(), AUTH_TOKEN,
                errors, MID_EVENT_CALLBACK);
        assertEquals("[Case Reference Number not found.]", errors.toString());
    }

    @Test
    void midRespondentECCWithNoRespondentECC() {
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN),
                isA(String.class), isA(List.class)))
                .thenReturn(new ArrayList(Collections.singleton(submitEvent)));
        manchesterCcdRequest.getCaseDetails().getCaseData().setRespondentECC(null);
        assertEquals(1, caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(), AUTH_TOKEN,
                new ArrayList<>(), MID_EVENT_CALLBACK).getRespondentECC().getListItems().size());
    }

    @Test
    void createECC() {
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN),
                isA(String.class), isA(List.class)))
                .thenReturn(new ArrayList(Collections.singleton(submitEvent)));
        var casedata = caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(), AUTH_TOKEN,
                new ArrayList<>(), ABOUT_TO_SUBMIT_EVENT_CALLBACK);
        assertEquals("11111", casedata.getCaseRefECC());
        assertEquals(FLAG_ECC, casedata.getCaseSource());
        assertTrue(casedata.getJurCodesCollection().get(0).getId().matches(
                "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"));
    }

    @Test
    void linkOriginalCaseECC() throws IOException {
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN),
                isA(String.class), isA(List.class)))
                .thenReturn(new ArrayList(Collections.singleton(submitEvent)));
        when(ccdClient.startEventForCase(anyString(), anyString(), anyString(), anyString())).thenReturn(ccdRequest10);
        assertEquals("11111", caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(),
                AUTH_TOKEN, new ArrayList<>(), SUBMITTED_CALLBACK).getCaseRefECC());
    }

    @Test
    void linkOriginalCaseECCCounterClaims() throws IOException {
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN),
                isA(String.class), isA(List.class)))
                .thenReturn(new ArrayList(Collections.singleton(submitEvent)));
        when(ccdClient.startEventForCase(anyString(), anyString(), anyString(), anyString())).thenReturn(ccdRequest10);
        assertEquals("72632632", caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(),
                AUTH_TOKEN, new ArrayList<>(), SUBMITTED_CALLBACK).getEccCases().get(0).getValue().getCounterClaim());
        EccCounterClaimType counterClaimType1 = new EccCounterClaimType();
        EccCounterClaimType counterClaimType2 = new EccCounterClaimType();
        counterClaimType1.setCounterClaim("72632632");
        counterClaimType2.setCounterClaim("63467343");
        EccCounterClaimTypeItem c1 = new EccCounterClaimTypeItem();
        c1.setId(UUID.randomUUID().toString());
        c1.setValue(counterClaimType1);
        EccCounterClaimTypeItem c2 = new EccCounterClaimTypeItem();
        c2.setId(UUID.randomUUID().toString());
        c2.setValue(counterClaimType2);
        manchesterCcdRequest.getCaseDetails().getCaseData().setEccCases(Arrays.asList(c1, c2));
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN),
                isA(String.class), isA(List.class)))
                .thenReturn(new ArrayList(Collections.singleton(submitEvent)));
        assertEquals(c1.getValue().getCounterClaim(),
                caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(), AUTH_TOKEN,
                    new ArrayList<>(), SUBMITTED_CALLBACK).getEccCases().get(0).getValue().getCounterClaim());
        assertEquals(c2.getValue().getCounterClaim(),
                caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(), AUTH_TOKEN,
                    new ArrayList<>(), SUBMITTED_CALLBACK).getEccCases().get(1).getValue().getCounterClaim());
    }

    @Test
    void linkOriginalCaseECCException() throws IOException {
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN),
                isA(String.class), isA(List.class)))
                .thenReturn(new ArrayList(Collections.singleton(submitEvent)));
        when(ccdClient.submitEventForCase(anyString(), any(), anyString(), anyString(), any(), anyString()))
                .thenThrow(new InternalException(ERROR_MESSAGE));

        assertThrows(Exception.class, () -> {
            caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(), AUTH_TOKEN,
                    new ArrayList<>(), SUBMITTED_CALLBACK);
        });
    }

    @Test
    void createECCFromClosedCaseWithoutET3() {
        submitEvent.setState("Closed");
        submitEvent.getCaseData().getRespondentCollection().get(0).getValue().setResponseReceived(NO);
        when(caseRetrievalForCaseWorkerService.casesRetrievalESRequest(isA(String.class), eq(AUTH_TOKEN),
                isA(String.class), isA(List.class)))
                .thenReturn(new ArrayList(Collections.singleton(submitEvent)));
        List<String> errors = new ArrayList<>();
        CaseData caseData = caseManagementForCaseWorkerService.createECC(manchesterCcdRequest.getCaseDetails(),
                AUTH_TOKEN, errors, MID_EVENT_CALLBACK);
        assertNull(caseData.getRespondentECC().getListItems());
        assertEquals(2, errors.size());
        submitEvent.setState("Accepted");
        submitEvent.getCaseData().getRespondentCollection().get(0).getValue().setResponseReceived(YES);
    }

    @Test
    void testSetMigratedCaseLinkDetails_Success() {
        CaseDetails caseDetails = getCaseDetails();
        SubmitEvent submitEventFullSourceCase = getSubmitEvent();
        caseDetails.getCaseData().setTransferredCaseLinkSourceCaseTypeId("Leeds");
        caseDetails.getCaseData().setTransferredCaseLinkSourceCaseId(
                String.valueOf(submitEventFullSourceCase.getCaseId()));
        when(caseRetrievalForCaseWorkerService.caseRefRetrievalRequest(any(), any(), any(), any()))
                .thenReturn(submitEventFullSourceCase.getCaseData().getEthosCaseReference());

        caseManagementForCaseWorkerService.setMigratedCaseLinkDetails(AUTH_TOKEN, caseDetails);
        assertEquals("<a target=\"_blank\" href=\"" + ccdGatewayBaseUrl + "/cases/case-details/"
                + submitEventFullSourceCase.getCaseId() + "\">"
                        + submitEventFullSourceCase.getCaseData().getEthosCaseReference() + "</a>",
                caseDetails.getCaseData().getTransferredCaseLink());
        assertEquals("EthosCaseRef", caseDetails.getCaseData().getEthosCaseReference());
        assertEquals("testClaimant", caseDetails.getCaseData().getClaimant());
        assertEquals("testRespondent", caseDetails.getCaseData().getRespondent());
        assertEquals("testFeeGroupReference", caseDetails.getCaseData().getFeeGroupReference());
        assertEquals("2024-03-12", caseDetails.getCaseData().getReceiptDate());
        assertTrue(caseDetails.getCaseData().getTransferredCaseLink()
                .contains(submitEventFullSourceCase.getCaseData().getEthosCaseReference()));
        assertTrue(caseDetails.getCaseData().getTransferredCaseLink()
                .contains(String.valueOf(submitEventFullSourceCase.getCaseId())));
        verify(caseRetrievalForCaseWorkerService, times(1)).caseRefRetrievalRequest(
                anyString(), anyString(), anyString(), anyString());
    }

    private static @NotNull CaseDetails getCaseDetails() {
        String caseId = "caseId";
        String caseDetailsId = "123";
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseId(caseDetailsId);
        caseDetails.setCaseTypeId("Manchester");
        CaseData caseData = new CaseData();
        caseData.setCcdID(caseId);
        caseData.setEthosCaseReference("EthosCaseRef");
        caseData.setClaimant("testClaimant");
        caseData.setRespondent("testRespondent");
        caseData.setFeeGroupReference("testFeeGroupReference");
        caseData.setReceiptDate("2024-03-12");
        caseDetails.setCaseData(caseData);
        return caseDetails;
    }

    private static @NotNull SubmitEvent getSubmitEvent() {
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setCaseId(12345);
        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference("EthosCaseTestRef");
        caseData.setClaimant("testClaimant");
        caseData.setRespondent("testRespondent");
        caseData.setFeeGroupReference("testFeeGroupReference");
        caseData.setReceiptDate("2024-03-12");
        submitEvent.setCaseData(caseData);
        submitEvent.setState("Accepted");
        return submitEvent;
    }

    @Test
    void testSetMigratedCaseLinkDetails_When_CaseRefAndCaseDataPairIsNull() {
        String caseId = "caseId";
        CaseDetails caseDetails = new CaseDetails();
        CaseData caseData = new CaseData();
        caseData.setCcdID(caseId);
        caseDetails.setCaseData(caseData);
        String authToken = "authToken";

        when(caseRetrievalForCaseWorkerService.transferSourceCaseRetrievalESRequest(
                anyString(), anyString(), anyString(), anyList())).thenReturn(null);
        caseManagementForCaseWorkerService.setMigratedCaseLinkDetails(authToken, caseDetails);
        assertNull(caseDetails.getCaseData().getTransferredCaseLink());
        verify(caseRetrievalForCaseWorkerService, times(0)).caseRefRetrievalRequest(
                anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testSetMigratedCaseLinkDetails_When_CaseRefAndCaseDataPairList_Size_NotEqualTo_One() {
        List<Pair<String, List<SubmitEvent>>> listOfCaseTypeIdAndCaseDataPair = new ArrayList<>();
        listOfCaseTypeIdAndCaseDataPair.add(Pair.of("Leeds", List.of(submitEvent)));

        SubmitEvent newcastleSubmitEvent = new SubmitEvent();
        CaseData newcastlCaseCaseData = new CaseData();
        newcastlCaseCaseData.setEthosCaseReference("EthosCaseRef");
        newcastleSubmitEvent.setCaseData(newcastlCaseCaseData);
        listOfCaseTypeIdAndCaseDataPair.add(Pair.of("Newcastle", List.of(newcastleSubmitEvent)));

        String authToken = "authToken";
        String caseId = "caseId";
        String caseTypeId = "Leeds";
        CaseDetails caseDetails = new CaseDetails();
        CaseData caseData = new CaseData();
        caseData.setCcdID(caseId);
        caseDetails.setCaseData(caseData);
        when(caseRetrievalForCaseWorkerService.transferSourceCaseRetrievalESRequest(
                caseId, caseTypeId, authToken, List.of("Leeds"))).thenReturn(listOfCaseTypeIdAndCaseDataPair);

        caseManagementForCaseWorkerService.setMigratedCaseLinkDetails(authToken, caseDetails);
        assertNull(caseDetails.getCaseData().getTransferredCaseLink());
        verify(caseRetrievalForCaseWorkerService, times(0)).caseRefRetrievalRequest(
                anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testSetMigratedCaseLinkDetails_When_SubmitEventList_IsEmpty() {
        String authToken = "authToken";
        String caseId = "caseId";
        String caseTypeId = "Leeds";
        CaseDetails caseDetails = new CaseDetails();
        CaseData caseData = new CaseData();
        caseData.setCcdID(caseId);
        caseDetails.setCaseData(caseData);

        when(caseRetrievalForCaseWorkerService.transferSourceCaseRetrievalESRequest(
                caseId, caseTypeId, authToken, List.of("Leeds")))
                .thenReturn(List.of(Pair.of(EMPTY_STRING, new ArrayList<>())));
        caseManagementForCaseWorkerService.setMigratedCaseLinkDetails(authToken, caseDetails);
        assertNull(caseDetails.getCaseData().getTransferredCaseLink());
    }

    @Test
    void testSetMigratedCaseLinkDetails_EmptySourceCaseTypeId() {
        when(caseRetrievalForCaseWorkerService.transferSourceCaseRetrievalESRequest(anyString(), anyString(),
                anyString(), anyList())).thenReturn(List.of(Pair.of("testSourceCaseTypeId", new ArrayList<>())));

        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseId("14423");
        CaseData caseData = new CaseData();
        caseData.setCcdID("89987");
        caseDetails.setCaseData(caseData);
        caseManagementForCaseWorkerService.setMigratedCaseLinkDetails("authToken", caseDetails);
        assertNull(caseDetails.getCaseData().getTransferredCaseLink());
    }

    @Test
    void testSetMigratedCaseLinkDetails_NullSourceCaseDataLinkDetails() {
        SubmitEvent submitEventFour = getSubmitEvent();
        CaseData caseDataOne = new CaseData();
        caseDataOne.setTransferredCaseLinkSourceCaseTypeId(null);
        caseDataOne.setTransferredCaseLinkSourceCaseId(null);
        submitEventFour.setCaseData(caseDataOne);

        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseId("456");
        CaseData caseData = new CaseData();
        caseData.setCcdID("2277");
        caseData.setEthosCaseReference("EthosCaseRef");
        caseDetails.setCaseData(caseData);
        caseDetails.setCaseTypeId("testSourceCaseType");
        caseManagementForCaseWorkerService.setMigratedCaseLinkDetails("authToken", caseDetails);
        assertNull(caseDetails.getCaseData().getTransferredCaseLink());
        verify(caseRetrievalForCaseWorkerService, times(0)).caseRefRetrievalRequest(
                anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testSetMigratedCaseLinkDetails_InvalidDuplicateCases_Transferred() {
        SubmitEvent submitEventFour = new SubmitEvent();
        CaseData caseDataOne = new CaseData();
        caseDataOne.setCcdID("889900");
        submitEventFour.setCaseData(caseDataOne);
        submitEventFour.setState("Transferred");

        when(caseRetrievalForCaseWorkerService.transferSourceCaseRetrievalESRequest(anyString(), anyString(),
                anyString(), anyList())).thenReturn(List.of(Pair.of("testSourceCaseType",
                List.of(submitEventFour))));

        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseId("456");
        CaseData caseData = new CaseData();
        caseData.setCcdID("2277");
        caseDetails.setCaseData(caseData);
        caseManagementForCaseWorkerService.setMigratedCaseLinkDetails("authToken", caseDetails);
        assertNull(caseDetails.getCaseData().getTransferredCaseLink());
        verify(caseRetrievalForCaseWorkerService, times(0)).caseRefRetrievalRequest(
                anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testSetMigratedCaseLinkDetails_When_EthosCaseReference_IsNull() {
        CaseDetails caseDetails = getCaseDetails();
        caseDetails.setCaseTypeId("Newcastle");

        SubmitEvent fullSourceCase = getSubmitEvent();
        fullSourceCase.setCaseId(12345);
        fullSourceCase.getCaseData().setEthosCaseReference(null);
        String authToken = "authToken";

        when(caseRetrievalForCaseWorkerService.transferSourceCaseRetrievalESRequest(
                anyString(), anyString(), anyString(), anyList()))
                .thenReturn(List.of(Pair.of("Leeds", List.of(fullSourceCase))));
        when(caseRetrievalForCaseWorkerService.caseRefRetrievalRequest(
                authToken, "caseTypeId", EMPLOYMENT_JURISDICTION, "12345"))
                .thenReturn(fullSourceCase.getCaseData().getEthosCaseReference());
        caseManagementForCaseWorkerService.setMigratedCaseLinkDetails(authToken, caseDetails);
        assertNull(caseDetails.getCaseData().getTransferredCaseLink());
    }

    private List<RespondentSumTypeItem> createRespondentCollection(boolean single) {
        RespondentSumTypeItem respondentSumTypeItem1 = createRespondentSumType("RespondentName1", false);
        RespondentSumTypeItem respondentSumTypeItem2 = createRespondentSumType("RespondentName2", false);
        RespondentSumTypeItem respondentSumTypeItem3 = createRespondentSumType("RespondentName3", true);
        if (single) {
            return new ArrayList<>(Collections.singletonList(respondentSumTypeItem1));
        } else {
            return new ArrayList<>(Arrays.asList(respondentSumTypeItem1, respondentSumTypeItem2,
                    respondentSumTypeItem3));
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