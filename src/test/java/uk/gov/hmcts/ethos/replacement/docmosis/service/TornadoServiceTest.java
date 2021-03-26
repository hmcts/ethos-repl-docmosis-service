package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.bulk.BulkData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceScotType;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.items.ListingTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.ListingType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ethos.replacement.docmosis.config.TornadoConfiguration;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.HelperTest;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.IdamApi;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException.ERROR_MESSAGE;

@RunWith(SpringJUnit4ClassRunner.class)
public class TornadoServiceTest {

    @InjectMocks
    private TornadoService tornadoService;
    @Mock
    private DocumentManagementService documentManagementService;
    private UserService userService;
    private DocumentInfo documentInfo;
    private CaseData caseData;
    private BulkData bulkData;
    private ListingData listingData;
    private UserDetails userDetails;
    private String userToken;

    @Before
    public void setUp() {
        documentInfo = new DocumentInfo();
        TornadoConfiguration tornadoConfiguration = new TornadoConfiguration();
        tornadoConfiguration.setUrl("http://google.com");
        caseData = new CaseData();
        bulkData = new BulkData();
        bulkData.setScheduleDocName(LIST_CASES_CONFIG);
        bulkData.setSearchCollection(new ArrayList<>());
        listingData = new ListingData();
        ListingTypeItem listingTypeItem = new ListingTypeItem();
        ListingType listingType = new ListingType();
        listingType.setCauseListDate("2019-12-12");
        listingTypeItem.setId("1111");
        listingTypeItem.setValue(listingType);
        listingData.setHearingDocType(HEARING_DOC_ETCL);
        listingData.setHearingDocETCL(HEARING_ETCL_STAFF);
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingData.setListingVenue("Glasgow");
        listingData.setListingCollection(new ArrayList<>(Collections.singleton(listingTypeItem)));
        userDetails = HelperTest.getUserDetails();
        IdamApi idamApi = authorisation -> userDetails;
        userService = new UserService(idamApi);
        tornadoService = new TornadoService(tornadoConfiguration, documentManagementService, userService, null);
        userToken = "authToken";
    }

    @Test(expected = Exception.class)
    public void documentGenerationError() throws IOException {
        when(userService.getUserDetails(anyString())).thenThrow(new InternalException(ERROR_MESSAGE));
        tornadoService.documentGeneration(userToken, caseData, MANCHESTER_CASE_TYPE_ID,
                caseData.getCorrespondenceType(), caseData.getCorrespondenceScotType(), null);
    }

    @Test
    public void documentGeneration() throws IOException {
        DocumentInfo documentInfo1 = tornadoService.documentGeneration(userToken, caseData, MANCHESTER_CASE_TYPE_ID,
                caseData.getCorrespondenceType(), caseData.getCorrespondenceScotType(), null);
        assertEquals(documentInfo.toString(), documentInfo1.toString());
    }

    @Test
    public void documentGenerationAllocatedOffice() throws IOException {
        caseData.setAllocatedOffice(GLASGOW_OFFICE);
        CorrespondenceScotType correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setLetterAddress(LETTER_ADDRESS_ALLOCATED_OFFICE);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        DocumentInfo documentInfo1 = tornadoService.documentGeneration(userToken, caseData, SCOTLAND_CASE_TYPE_ID,
                caseData.getCorrespondenceType(), caseData.getCorrespondenceScotType(), null);
        assertEquals(documentInfo.toString(), documentInfo1.toString());
    }

    @Test
    public void documentGenerationAllocatedOfficeMultiples() throws IOException {
        caseData.setAllocatedOffice(GLASGOW_OFFICE);
        CorrespondenceScotType correspondenceScotType = new CorrespondenceScotType();
        correspondenceScotType.setLetterAddress(LETTER_ADDRESS_ALLOCATED_OFFICE);
        caseData.setCorrespondenceScotType(correspondenceScotType);
        DocumentInfo documentInfo1 = tornadoService.documentGeneration(userToken, caseData, SCOTLAND_CASE_TYPE_ID,
                caseData.getCorrespondenceType(), caseData.getCorrespondenceScotType(), new MultipleData());
        assertEquals(documentInfo.toString(), documentInfo1.toString());
    }

    @Test
    public void listingGeneration() throws IOException {
        DocumentInfo documentInfo1 = tornadoService.listingGeneration(userToken, listingData, MANCHESTER_LISTING_CASE_TYPE_ID);
        assertEquals(documentInfo.toString(), documentInfo1.toString());
    }

    @Test
    public void scheduleGeneration() throws IOException {
        DocumentInfo documentInfo1 = tornadoService.scheduleGeneration(userToken, bulkData);
        assertEquals(documentInfo.toString(), documentInfo1.toString());
    }

    @Test
    public void reportGeneration() throws IOException {
        listingData.setReportType(CLAIMS_ACCEPTED_REPORT);
        DocumentInfo documentInfo1 = tornadoService.listingGeneration(userToken, listingData, MANCHESTER_LISTING_CASE_TYPE_ID);
        assertEquals(documentInfo.toString(), documentInfo1.toString());
    }
}