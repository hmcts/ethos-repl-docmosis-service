package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.ethos.replacement.docmosis.config.TornadoConfiguration;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.IdamApi;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.models.UserDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.DocumentInfo;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.ListingData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.items.ListingTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.types.ListingType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.SetUpUtils.feignError;

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
        userDetails = new UserDetails("1", "example@hotmail.com", "Mike", "Jordan", new ArrayList<>());
        IdamApi idamApi = authorisation -> userDetails;
        userService = new UserService(idamApi);
        tornadoService = new TornadoService(tornadoConfiguration, documentManagementService, userService);
    }

    @Test(expected = Exception.class)
    public void documentGenerationError() throws IOException {
        when(userService.getUserDetails(anyString())).thenThrow(feignError());
        tornadoService.documentGeneration("TOKEN", caseData);
    }

    @Test
    public void documentGeneration() throws IOException {
        DocumentInfo documentInfo1 = tornadoService.documentGeneration("TOKEN", caseData);
        assertEquals(documentInfo.toString(), documentInfo1.toString());
    }

    @Test
    public void listingGeneration() throws IOException {
        DocumentInfo documentInfo1 = tornadoService.listingGeneration("TOKEN", listingData, MANCHESTER_LISTING_CASE_TYPE_ID);
        assertEquals(documentInfo.toString(), documentInfo1.toString());
    }

    @Test
    public void scheduleGeneration() throws IOException {
        DocumentInfo documentInfo1 = tornadoService.scheduleGeneration("TOKEN", bulkData);
        assertEquals(documentInfo.toString(), documentInfo1.toString());
    }

}