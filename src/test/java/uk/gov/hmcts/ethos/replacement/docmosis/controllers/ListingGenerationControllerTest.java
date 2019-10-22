package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.ethos.replacement.docmosis.DocmosisApplication;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.DefaultValues;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.ListingData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.ListingDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.service.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.SCOTLAND_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.SetUpUtils.feignError;

@RunWith(SpringRunner.class)
@WebMvcTest(ListingGenerationController.class)
@ContextConfiguration(classes = DocmosisApplication.class)
public class ListingGenerationControllerTest {

    private static final String AUTH_TOKEN = "Bearer eyJhbGJbpjciOiJIUzI1NiJ9";
    private static final String LISTING_HEARINGS_URL = "/listingHearings";
    private static final String GENERATE_HEARING_DOCUMENT_URL = "/generateHearingDocument";

    @Autowired
    private WebApplicationContext applicationContext;

    @MockBean
    private ListingService listingService;

    @MockBean
    private DefaultValuesReaderService defaultValuesReaderService;

    private MockMvc mvc;
    private JsonNode requestContent;
    private JsonNode requestContent1;
    private ListingDetails listingDetails;
    private ListingData listingData;
    private DocumentInfo documentInfo;
    private DefaultValues defaultValues;

    private void doRequestSetUp() throws IOException, URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        requestContent = objectMapper.readTree(new File(getClass()
                .getResource("/exampleListingV1.json").toURI()));
        requestContent1 = objectMapper.readTree(new File(getClass()
                .getResource("/exampleListingV2.json").toURI()));
    }

    private ListingData getListingData() {
        listingData = new ListingData();
        listingData.setTribunalCorrespondenceDX("DX");
        listingData.setTribunalCorrespondenceEmail("m@m.com");
        listingData.setTribunalCorrespondenceFax("100300200");
        listingData.setTribunalCorrespondenceTelephone("077123123");
        Address address = new Address();
        address.setAddressLine1("AddressLine1");
        address.setAddressLine2("AddressLine2");
        address.setAddressLine3("AddressLine3");
        address.setPostTown("Manchester");
        address.setCountry("UK");
        address.setPostCode("L1 122");
        listingData.setTribunalCorrespondenceAddress(address);
        return listingData;
    }

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
        doRequestSetUp();
        listingDetails = new ListingDetails();
        listingDetails.setCaseTypeId(SCOTLAND_LISTING_CASE_TYPE_ID);
        listingDetails.setCaseData(getListingData());
        documentInfo = new DocumentInfo();
        defaultValues = DefaultValues.builder()
                .positionType("Awaiting ET3")
                .claimantTypeOfClaimant("Individual")
                .managingOffice("Glasgow")
                .tribunalCorrespondenceAddressLine1("")
                .tribunalCorrespondenceAddressLine2("")
                .tribunalCorrespondenceAddressLine3("")
                .tribunalCorrespondenceTown("")
                .tribunalCorrespondencePostCode("")
                .tribunalCorrespondenceTelephone("3577131270")
                .tribunalCorrespondenceFax("7577126570")
                .tribunalCorrespondenceDX("123456")
                .tribunalCorrespondenceEmail("manchester@gmail.com")
                .build();
    }

    @Test
    public void listingHearings() throws Exception {
        when(listingService.processListingHearingsRequest(isA(ListingDetails.class), eq(AUTH_TOKEN))).thenReturn(listingDetails);
        when(defaultValuesReaderService.getDefaultValues(isA(String.class), isA(String.class), isA(String.class))).thenReturn(defaultValues);
        when(defaultValuesReaderService.getListingData(isA(ListingData.class), isA(DefaultValues.class))).thenReturn(listingData);
        mvc.perform(post(LISTING_HEARINGS_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void listingHearingsError400() throws Exception {
        mvc.perform(post(LISTING_HEARINGS_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void listingHearingsError500() throws Exception {
        when(listingService.processListingHearingsRequest(isA(ListingDetails.class), eq(AUTH_TOKEN))).thenThrow(feignError());
        mvc.perform(post(LISTING_HEARINGS_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void generateHearingDocument() throws Exception {
        when(listingService.processHearingDocument(isA(ListingDetails.class), eq(AUTH_TOKEN))).thenReturn(documentInfo);
        mvc.perform(post(GENERATE_HEARING_DOCUMENT_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void generateHearingDocumentWithErrors() throws Exception {
        when(listingService.processHearingDocument(isA(ListingDetails.class), eq(AUTH_TOKEN))).thenReturn(documentInfo);
        mvc.perform(post(GENERATE_HEARING_DOCUMENT_URL)
                .content(requestContent1.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void generateHearingDocumentError400() throws Exception {
        mvc.perform(post(GENERATE_HEARING_DOCUMENT_URL)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void generateHearingDocumentError500() throws Exception {
        when(listingService.processHearingDocument(isA(ListingDetails.class), eq(AUTH_TOKEN))).thenThrow(feignError());
        mvc.perform(post(GENERATE_HEARING_DOCUMENT_URL)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

}