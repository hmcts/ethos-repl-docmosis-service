package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ethos.replacement.docmosis.service.AllocateHearingService;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.JsonMapper;

import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;

@ExtendWith(SpringExtension.class)
@WebMvcTest({AllocateHearingController.class, JsonMapper.class})
class AllocateHearingControllerTest {

    private static final String AUTH_TOKEN = "Bearer eyJhbGJbpjciOiJIUzI1NiJ9";
    private static final String INITIALISE_HEARINGS_URL = "/allocateHearing/initialiseHearings";
    private static final String POPULATE_HEARING_DETAILS_URL = "/allocateHearing/populateHearingDetails";

    @MockBean
    private AllocateHearingService allocateHearingService;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private MockMvc mockMvc;

    private CCDRequest ccdRequest;

    @BeforeEach
    void setUp() {
        CaseData caseData = createCaseData();
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(caseData);
        caseDetails.setCaseId("12345678");
        caseDetails.setState(ACCEPTED_STATE);
        caseDetails.setCaseTypeId("Manchester");

        ccdRequest = new CCDRequest();
        ccdRequest.setCaseDetails(caseDetails);
    }

    @Test
    void initialiseHearings_shouldReturnSuccess() throws Exception {
        mockMvc.perform(post(INITIALISE_HEARINGS_URL)
                .content(jsonMapper.toJson(ccdRequest))
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath(JsonMapper.DATA, notNullValue()))
            .andExpect(jsonPath(JsonMapper.ERRORS, nullValue()))
            .andExpect(jsonPath(JsonMapper.WARNINGS, nullValue()));

        verify(allocateHearingService, times(1)).initialiseAllocateHearing(any(CaseData.class));
    }

    @Test
    void populateHearingDetails_shouldReturnSuccess() throws Exception {
        // Set up a selected hearing
        DynamicFixedListType selectedHearing = new DynamicFixedListType();
        DynamicValueType dynamicValue = new DynamicValueType();
        dynamicValue.setCode("1");
        dynamicValue.setLabel("Hearing 1");
        selectedHearing.setValue(dynamicValue);
        ccdRequest.getCaseDetails().getCaseData().setSelectedHearingNumberForUpdate(selectedHearing);

        mockMvc.perform(post(POPULATE_HEARING_DETAILS_URL)
                .content(jsonMapper.toJson(ccdRequest))
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath(JsonMapper.DATA, notNullValue()))
            .andExpect(jsonPath(JsonMapper.ERRORS, nullValue()))
            .andExpect(jsonPath(JsonMapper.WARNINGS, nullValue()));

        verify(allocateHearingService, times(1)).populateHearingDetails(any(CaseData.class));
    }

    private CaseData createCaseData() {
        CaseData caseData = new CaseData();
        caseData.setEthosCaseReference("12345678/2024");

        // Create hearings
        HearingTypeItem hearing1 = createHearing("1", "Manchester", "2024-01-15T10:00:00.000");
        HearingTypeItem hearing2 = createHearing("2", "Leeds", "2024-02-20T14:00:00.000");
        caseData.setHearingCollection(List.of(hearing1, hearing2));

        return caseData;
    }

    private HearingTypeItem createHearing(String number, String venue, String listedDate) {
        HearingType hearingType = new HearingType();
        hearingType.setHearingNumber(number);
        hearingType.setHearingType("Hearing");
        hearingType.setHearingVenue(venue);
        hearingType.setHearingSitAlone("Sit Alone");
        hearingType.setHearingEstLengthNum("1");
        hearingType.setHearingEstLengthNumType("Days");

        DateListedType dateListedType = new DateListedType();
        dateListedType.setListedDate(listedDate);
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        dateListedTypeItem.setValue(dateListedType);
        hearingType.setHearingDateCollection(List.of(dateListedTypeItem));

        HearingTypeItem hearingTypeItem = new HearingTypeItem();
        hearingTypeItem.setValue(hearingType);
        hearingTypeItem.setId(number);
        return hearingTypeItem;
    }
}
