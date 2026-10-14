package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.Address;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceScotType;
import uk.gov.hmcts.ecm.common.model.helper.DefaultValues;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.items.ListingTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.ListingType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.compat.common.idam.models.UserDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.HelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.SignificantItemType;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.IdamApi;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.MockHttpURLConnectionFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.CLAIMS_ACCEPTED_REPORT;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.GLASGOW_OFFICE;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.HEARING_DOC_ETCL;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.HEARING_ETCL_STAFF;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.LETTER_ADDRESS_ALLOCATED_OFFICE;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.MANCHESTER_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.SINGLE_HEARING_DATE_TYPE;

public class TornadoServiceTest {
    private TornadoService tornadoService;
    private TornadoConnection tornadoConnection;
    private DocumentManagementService documentManagementService;
    private UserService userService;
    private DefaultValuesReaderService defaultValuesReaderService;
    private HttpURLConnection mockConnection;
    private final String authToken = "a-test-auth-token";
    private final String documentInfoMarkup = "<a>some test markup</a>";

    @Before
    public void setUp() throws IOException {
        createUserService();
        mockTornadoConnection();
        mockDocumentManagement();
        mockDefaultValuesReaderService();

        tornadoService = new TornadoService(tornadoConnection, documentManagementService,
            userService, defaultValuesReaderService);
    }

    @Test(expected = IOException.class)
    public void documentGenerationNoTornadoConnectionShouldThrowException() throws IOException {
        var caseData = new CaseData();
        when(tornadoConnection.createConnection()).thenThrow(IOException.class);

        tornadoService.documentGeneration(authToken, caseData, MANCHESTER_CASE_TYPE_ID,
                caseData.getCorrespondenceType(), caseData.getCorrespondenceScotType(), null);
    }

    @Test(expected = IOException.class)
    public void listingGenerationNoTornadoConnectionShouldThrowException() throws IOException {
        when(tornadoConnection.createConnection()).thenThrow(IOException.class);

        tornadoService.listingGeneration(authToken, createListingData(), MANCHESTER_LISTING_CASE_TYPE_ID);
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionWhenTornadoReturnsErrorResponse() throws IOException {
        mockConnectionError();
        var caseData = new CaseData();

        tornadoService.documentGeneration(authToken, caseData, MANCHESTER_CASE_TYPE_ID,
                caseData.getCorrespondenceType(), caseData.getCorrespondenceScotType(), null);
    }

    @Test
    public void shouldCreateDocumentInfoForDocumentGeneration() throws IOException {
        mockConnectionSuccess();
        var caseData = new CaseData();

        var documentInfo = tornadoService.documentGeneration(authToken, caseData, MANCHESTER_CASE_TYPE_ID,
                caseData.getCorrespondenceType(), caseData.getCorrespondenceScotType(), null);

        verifyDocumentInfo(documentInfo);
    }

    @Test
    public void shouldCreateDocumentInfoForDocumentGenerationAllocatedOffice() throws IOException {
        mockConnectionSuccess();
        var defaultValues = mock(DefaultValues.class);
        when(defaultValuesReaderService.getDefaultValues(GLASGOW_OFFICE, SCOTLAND_CASE_TYPE_ID))
            .thenReturn(defaultValues);
        var caseData = new CaseData();
        caseData.setAllocatedOffice(GLASGOW_OFFICE);
        var correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments("test-template");
        correspondenceScotType.setLetterAddress(LETTER_ADDRESS_ALLOCATED_OFFICE);
        caseData.setCorrespondenceScotType(correspondenceScotType);

        var documentInfo = tornadoService.documentGeneration(authToken, caseData, SCOTLAND_CASE_TYPE_ID,
                caseData.getCorrespondenceType(), caseData.getCorrespondenceScotType(), null);

        verifyDocumentInfo(documentInfo);
    }

    @Test
    public void shouldCreateDocumentInfoForDocumentGenerationAllocatedOfficeMultiples() throws IOException {
        mockConnectionSuccess();
        var defaultValues = mock(DefaultValues.class);
        when(defaultValuesReaderService.getDefaultValues(GLASGOW_OFFICE, SCOTLAND_CASE_TYPE_ID))
            .thenReturn(defaultValues);
        var caseData = new CaseData();
        caseData.setAllocatedOffice(GLASGOW_OFFICE);
        var correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments("test-template");
        correspondenceScotType.setLetterAddress(LETTER_ADDRESS_ALLOCATED_OFFICE);
        caseData.setCorrespondenceScotType(correspondenceScotType);

        var documentInfo = tornadoService.documentGeneration(authToken, caseData, SCOTLAND_CASE_TYPE_ID,
                caseData.getCorrespondenceType(), caseData.getCorrespondenceScotType(), new MultipleData());

        verifyDocumentInfo(documentInfo);
    }

    @Test
    public void joinsTheGlasgowStreetOntoTheLetterPayloadOnly() throws IOException {
        mockConnectionSuccess();
        ByteArrayOutputStream output = capturedOutput();
        when(defaultValuesReaderService.getDefaultValues(GLASGOW_OFFICE, SCOTLAND_CASE_TYPE_ID))
            .thenReturn(DefaultValues.builder()
                .tribunalCorrespondenceAddressLine3("Glasgow Tribunals Centre")
                .tribunalCorrespondenceAddressLine4("3 Atlantic Quay, 20 York Street")
                .build());
        CaseData caseData = glasgowCase("Glasgow Tribunals Centre");

        tornadoService.documentGeneration(authToken, caseData, SCOTLAND_CASE_TYPE_ID,
            caseData.getCorrespondenceType(), caseData.getCorrespondenceScotType(), null);

        assertTrue(output.toString(StandardCharsets.UTF_8)
            .contains("Glasgow Tribunals Centre\\n3 Atlantic Quay, 20 York Street"));
        assertEquals("Glasgow Tribunals Centre",
            caseData.getTribunalCorrespondenceAddress().getAddressLine3());
    }

    @Test
    public void joinsTheAllocatedOfficeStreetOntoTheLetter() throws IOException {
        mockConnectionSuccess();
        when(defaultValuesReaderService.getDefaultValues(GLASGOW_OFFICE, SCOTLAND_CASE_TYPE_ID))
            .thenReturn(DefaultValues.builder()
                .tribunalCorrespondenceAddressLine3("Glasgow Tribunals Centre")
                .tribunalCorrespondenceAddressLine4("3 Atlantic Quay, 20 York Street")
                .build());
        CaseData caseData = glasgowCase("Glasgow Tribunals Centre");
        caseData.setAllocatedOffice(GLASGOW_OFFICE);
        CorrespondenceScotType correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setTopLevelScotDocuments("test-template");
        correspondenceScotType.setLetterAddress(LETTER_ADDRESS_ALLOCATED_OFFICE);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        ByteArrayOutputStream output = capturedOutput();

        tornadoService.documentGeneration(authToken, caseData, SCOTLAND_CASE_TYPE_ID,
            null, correspondenceScotType, null);

        assertTrue(output.toString(StandardCharsets.UTF_8)
            .contains("Glasgow Tribunals Centre\\n3 Atlantic Quay, 20 York Street"));
    }

    @Test
    public void leavesAnEditedAddressLineWithoutTheOfficeStreet() throws IOException {
        mockConnectionSuccess();
        ByteArrayOutputStream output = capturedOutput();
        when(defaultValuesReaderService.getDefaultValues(GLASGOW_OFFICE, SCOTLAND_CASE_TYPE_ID))
            .thenReturn(DefaultValues.builder()
                .tribunalCorrespondenceAddressLine3("Glasgow Tribunals Centre")
                .tribunalCorrespondenceAddressLine4("3 Atlantic Quay, 20 York Street")
                .build());
        CaseData caseData = glasgowCase("Edited line");

        tornadoService.documentGeneration(authToken, caseData, SCOTLAND_CASE_TYPE_ID,
            caseData.getCorrespondenceType(), caseData.getCorrespondenceScotType(), null);

        String payload = output.toString(StandardCharsets.UTF_8);
        assertTrue(payload.contains("Edited line"));
        assertFalse(payload.contains("3 Atlantic Quay"));
    }

    @Test
    public void skipsTheStreetLookupWhenTheOfficeOrAddressIsMissing() throws IOException {
        mockConnectionSuccess();
        CaseData noOffice = new CaseData();
        noOffice.setTribunalCorrespondenceAddress(new Address());
        verifyDocumentInfo(tornadoService.documentGeneration(authToken, noOffice, SCOTLAND_CASE_TYPE_ID,
            null, null, null));

        when(defaultValuesReaderService.getDefaultValues(GLASGOW_OFFICE, SCOTLAND_CASE_TYPE_ID)).thenReturn(null);
        verifyDocumentInfo(tornadoService.documentGeneration(authToken, glasgowCase("Glasgow Tribunals Centre"),
            SCOTLAND_CASE_TYPE_ID, null, null, null));

        when(defaultValuesReaderService.getDefaultValues(GLASGOW_OFFICE, SCOTLAND_CASE_TYPE_ID))
            .thenReturn(DefaultValues.builder()
                .tribunalCorrespondenceAddressLine3("Glasgow Tribunals Centre")
                .build());
        CaseData noLine3 = new CaseData();
        noLine3.setManagingOffice(GLASGOW_OFFICE);
        noLine3.setTribunalCorrespondenceAddress(new Address());
        verifyDocumentInfo(tornadoService.documentGeneration(authToken, noLine3, SCOTLAND_CASE_TYPE_ID,
            null, null, null));
    }

    private CaseData glasgowCase(String addressLine3) {
        Address address = new Address();
        address.setAddressLine3(addressLine3);
        CaseData caseData = new CaseData();
        caseData.setManagingOffice(GLASGOW_OFFICE);
        caseData.setTribunalCorrespondenceAddress(address);
        return caseData;
    }

    private ByteArrayOutputStream capturedOutput() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        when(mockConnection.getOutputStream()).thenReturn(output);
        return output;
    }

    @Test
    public void shouldCreateDocumentInfoForListingGeneration() throws IOException {
        mockConnectionSuccess();
        var listingData = createListingData();

        var documentInfo = tornadoService.listingGeneration(authToken, listingData, MANCHESTER_LISTING_CASE_TYPE_ID);

        verifyDocumentInfo(documentInfo);
    }

    @Test
    public void shouldCreateDocumentInfoForReportGeneration() throws IOException {
        mockConnectionSuccess();
        var listingData = createListingData();
        listingData.setReportType(CLAIMS_ACCEPTED_REPORT);

        var documentInfo = tornadoService.listingGeneration(authToken, listingData,
                MANCHESTER_LISTING_CASE_TYPE_ID);

        verifyDocumentInfo(documentInfo);
    }

    private void createUserService() {
        IdamApi idamApi = new IdamApi() {
            @Override
            public UserDetails retrieveUserDetails(String authorisation) {
                return HelperTest.getUserDetails();
            }

            @Override
            public UserDetails getUserByUserId(String authorisation, String userId) {
                return HelperTest.getUserDetails();
            }
        };
        userService = new UserService(idamApi);
    }

    private void mockTornadoConnection() throws IOException {
        mockConnection = MockHttpURLConnectionFactory.create("http://testdocmosis");
        tornadoConnection = mock(TornadoConnection.class);
        when(tornadoConnection.createConnection()).thenReturn(mockConnection);
    }

    private void mockDocumentManagement() {
        documentManagementService = mock(DocumentManagementService.class);
        var documentUrl = "http://testdocumentserver/testdocument";
        var uri = URI.create(documentUrl);
        when(documentManagementService
            .uploadDocument(anyString(), any(byte[].class), anyString(), anyString(), anyString()))
            .thenReturn(uri);
        when(documentManagementService.generateDownloadableURL(uri)).thenReturn(documentUrl);
        when(documentManagementService.generateMarkupDocument(anyString())).thenReturn(documentInfoMarkup);
    }

    private void mockDefaultValuesReaderService() {
        defaultValuesReaderService = mock(DefaultValuesReaderService.class);
    }

    private void mockConnectionSuccess() throws IOException {
        var mockInputStream = mock(InputStream.class);
        when(mockInputStream.read(any(byte[].class))).thenReturn(-1);
        var mockOutputStream = mock(OutputStream.class);
        when(mockConnection.getInputStream()).thenReturn(mockInputStream);
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);
        when(mockConnection.getResponseCode()).thenReturn(HTTP_OK);
    }

    private void mockConnectionError() throws IOException {
        var mockInputStream = mock(InputStream.class);
        when(mockInputStream.read(any(byte[].class))).thenReturn(-1);
        when(mockInputStream.read(any(byte[].class), anyInt(), anyInt())).thenReturn(-1);
        var mockOutputStream = mock(OutputStream.class);
        when(mockConnection.getErrorStream()).thenReturn(mockInputStream);
        when(mockConnection.getOutputStream()).thenReturn(mockOutputStream);
        when(mockConnection.getResponseCode()).thenReturn(HTTP_INTERNAL_ERROR);
    }

    private ListingData createListingData() {
        var listingTypeItem = new ListingTypeItem();
        var listingType = new ListingType();
        listingType.setCauseListDate("2019-12-12");
        listingTypeItem.setId("1111");
        listingTypeItem.setValue(listingType);
        var listingData = new ListingData();
        listingData.setHearingDocType(HEARING_DOC_ETCL);
        listingData.setHearingDocETCL(HEARING_ETCL_STAFF);
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingData.setListingVenue("Glasgow");
        listingData.setListingCollection(new ArrayList<>(Collections.singleton(listingTypeItem)));

        return listingData;
    }

    private void verifyDocumentInfo(DocumentInfo documentInfo) {
        assertEquals(documentInfoMarkup, documentInfo.getMarkUp());
        assertEquals(SignificantItemType.DOCUMENT.name(), documentInfo.getType());
    }
}