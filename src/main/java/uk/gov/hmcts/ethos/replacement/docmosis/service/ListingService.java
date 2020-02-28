package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.CaseCreationException;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.DocumentManagementException;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ListingHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.ListingData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.ListingDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.items.ListingTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.types.ListingType;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@Slf4j
@Service("listingService")
public class ListingService {

    private final TornadoService tornadoService;
    private final CcdClient ccdClient;
    private static final String MESSAGE = "Failed to generate document for case id : ";

    @Autowired
    public ListingService(TornadoService tornadoService, CcdClient ccdClient) {
        this.tornadoService = tornadoService;
        this.ccdClient = ccdClient;
    }

    public CaseData processListingSingleCasesRequest(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getCaseData();
        List<ListingTypeItem> listingTypeItems = new ArrayList<>();
        if (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty()) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                if (hearingTypeItem.getValue().getHearingDateCollection() != null) {
                    listingTypeItems.addAll(getListingTypeItems(hearingTypeItem, caseData.getPrintHearingDetails(), caseData));
                }
            }
        }
        caseData.setPrintHearingCollection(caseData.getPrintHearingDetails());
        caseData.getPrintHearingCollection().setListingCollection(listingTypeItems);
        caseData.setPrintHearingCollection(clearListingFields(caseData.getPrintHearingCollection()));
        return caseData;
    }

    public ListingData setCourtAddressFromCaseData(CaseData caseData) {
        ListingData listingData = caseData.getPrintHearingCollection();
        listingData.setTribunalCorrespondenceAddress(caseData.getTribunalCorrespondenceAddress());
        listingData.setTribunalCorrespondenceTelephone(caseData.getTribunalCorrespondenceTelephone());
        listingData.setTribunalCorrespondenceFax(caseData.getTribunalCorrespondenceFax());
        listingData.setTribunalCorrespondenceEmail(caseData.getTribunalCorrespondenceEmail());
        listingData.setTribunalCorrespondenceDX(caseData.getTribunalCorrespondenceDX());
        return listingData;
    }

    public ListingData processListingHearingsRequest(ListingDetails listingDetails, String authToken) {
        try {
            //List<SubmitEvent> submitEvents = ccdClient.retrieveCases(authToken, ListingHelper.getCaseTypeId(listingDetails.getCaseTypeId()), listingDetails.getJurisdiction());
            List<SubmitEvent> submitEvents = getListingHearingsSearch(listingDetails, authToken);
            if (submitEvents != null) {
                log.info("Cases searched: " + submitEvents.size());
                log.info("Cases info: " + submitEvents);
                List<ListingTypeItem> listingTypeItems = new ArrayList<>();
                for (SubmitEvent submitEvent : submitEvents) {
                    if (submitEvent.getCaseData().getHearingCollection() != null && !submitEvent.getCaseData().getHearingCollection().isEmpty()) {
                        for (HearingTypeItem hearingTypeItem : submitEvent.getCaseData().getHearingCollection()) {
                            if (hearingTypeItem.getValue().getHearingDateCollection() != null) {
                                listingTypeItems.addAll(getListingTypeItems(hearingTypeItem, listingDetails.getCaseData(), submitEvent.getCaseData()));
                            }
                        }
                    }
                }
                listingDetails.getCaseData().setListingCollection(listingTypeItems);
            } else {
                log.info("Cases searched are 0");
            }
            return clearListingFields(listingDetails.getCaseData());
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + listingDetails.getCaseId() + ex.getMessage());
        }
    }

    private List<SubmitEvent> getListingHearingsSearch(ListingDetails listingDetails, String authToken) throws IOException {
        ListingData listingData = listingDetails.getCaseData();
        Map.Entry<String, String> entry = getListingVenueToSearch(listingData).entrySet().iterator().next();
        String venueToSearchMapping = entry.getKey();
        String venueToSearch = entry.getValue();
        boolean dateRange = listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        if (dateRange) {
            String dateToSearchFrom = LocalDate.parse(listingData.getListingDateFrom(), OLD_DATE_TIME_PATTERN2).toString();
            String dateToSearchTo = LocalDate.parse(listingData.getListingDateTo(), OLD_DATE_TIME_PATTERN2).toString();
            return ccdClient.retrieveCasesVenueAndDateElasticSearch(authToken, ListingHelper.getCaseTypeId(listingDetails.getCaseTypeId()),
                    dateToSearchFrom, dateToSearchTo, venueToSearch, venueToSearchMapping);
        } else {
            String dateToSearch = LocalDate.parse(listingData.getListingDate(), OLD_DATE_TIME_PATTERN2).toString();
            return ccdClient.retrieveCasesVenueAndDateElasticSearch(authToken, ListingHelper.getCaseTypeId(listingDetails.getCaseTypeId()),
                    dateToSearch, dateToSearch, venueToSearch, venueToSearchMapping);
        }
    }

    private ListingData clearListingFields(ListingData listingData) {
        listingData.setListingVenueOfficeAber(null);
        listingData.setListingVenueOfficeGlas(null);
        boolean dateRange = listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        if (dateRange) {
            listingData.setListingDate(null);
        } else {
            listingData.setListingDateFrom(null);
            listingData.setListingDateTo(null);
        }
        return listingData;
    }

    private List<ListingTypeItem> getListingTypeItems(HearingTypeItem hearingTypeItem, ListingData listingData, CaseData caseData) {
        List<ListingTypeItem> listingTypeItems = new ArrayList<>();
        int hearingDateCollectionSize = hearingTypeItem.getValue().getHearingDateCollection().size();
        for (int i = 0; i < hearingDateCollectionSize; i++) {
            DateListedTypeItem dateListedTypeItem = hearingTypeItem.getValue().getHearingDateCollection().get(i);
            boolean isListingVenueValid = isListingVenueValid(listingData, dateListedTypeItem);
            boolean isListingDateValid = isListingDateValid(listingData, dateListedTypeItem);
            if (isListingDateValid && isListingVenueValid) {
                ListingTypeItem listingTypeItem = new ListingTypeItem();
                ListingType listingType = ListingHelper.getListingTypeFromCaseData(listingData, caseData, hearingTypeItem.getValue(), dateListedTypeItem.getValue(), i, hearingDateCollectionSize);
                listingTypeItem.setId(String.valueOf(dateListedTypeItem.getId()));
                listingTypeItem.setValue(listingType);
                listingTypeItems.add(listingTypeItem);
            }
        }
        return listingTypeItems;
    }

    private boolean isAllVenuesGlasgowAndAberdeen(ListingData listingData) {
        boolean allVenuesGlasgow = !isNullOrEmpty(listingData.getListingVenueOfficeGlas()) && listingData.getListingVenueOfficeGlas().equals(ALL_VENUES);
        boolean allVenuesAberdeen = !isNullOrEmpty(listingData.getListingVenueOfficeAber()) && listingData.getListingVenueOfficeAber().equals(ALL_VENUES);
        return !allVenuesGlasgow && !allVenuesAberdeen;
    }

    private Map<String, String> getListingVenueToSearch(ListingData listingData) {
        boolean allLocations = listingData.getListingVenue().equals(ALL_VENUES);
        if (allLocations) {
            return ListingHelper.createMap(ALL_VENUES, ALL_VENUES);
        } else {
            if (isAllVenuesGlasgowAndAberdeen(listingData)) {
                return ListingHelper.getVenueToSearch(listingData);
            } else {
                return !isNullOrEmpty(listingData.getListingVenue())
                        ? ListingHelper.createMap("hearingVenueDay", listingData.getListingVenue())
                        : ListingHelper.createMap("","");
            }
        }
    }

    private boolean isListingVenueValid(ListingData listingData, DateListedTypeItem dateListedTypeItem) {
        Map<String, String> venueToSearchMap = getListingVenueToSearch(listingData);
        String venueToSearch = venueToSearchMap.entrySet().iterator().next().getValue();
        if (ALL_VENUES.equals(venueToSearch)) {
            log.info("Searching by all venues");
            return true;
        } else {
            String venueSearched;
            if (isAllVenuesGlasgowAndAberdeen(listingData)) {
                venueSearched = ListingHelper.getVenueFromDateListedType(dateListedTypeItem.getValue());
            } else {
                venueSearched = !isNullOrEmpty(dateListedTypeItem.getValue().getHearingVenueDay()) ? dateListedTypeItem.getValue().getHearingVenueDay() : " ";
            }
            log.info("VenueToSearch: " + venueToSearch + "   VenueSearched: " + venueSearched);
            return venueSearched.equals(venueToSearch);
        }
    }

    private boolean isListingDateValid(ListingData listingData, DateListedTypeItem dateListedTypeItem) {
        boolean dateRange = listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        String dateListed = !isNullOrEmpty(dateListedTypeItem.getValue().getListedDate()) ? dateListedTypeItem.getValue().getListedDate() : "";
        if (dateRange) {
            String dateToSearchFrom = listingData.getListingDateFrom();
            String dateToSearchTo = listingData.getListingDateTo();
            log.info("RANGE: -> dateToSearchFrom: " + dateToSearchFrom + "   dateToSearchTo: " + dateToSearchTo + "   DateListed: " + dateListed);
            return ListingHelper.getListingDateBetween(dateToSearchFrom, dateToSearchTo, dateListed);
        } else {
            String dateToSearch = listingData.getListingDate();
            log.info("SINGLE: -> dateToSearch: " + dateToSearch + "   DateListed: " + dateListed);
            return ListingHelper.getListingDateBetween(dateToSearch, "", dateListed);
        }
    }

    public DocumentInfo processHearingDocument(ListingData listingData, String caseTypeId, String authToken) {
        try {
            return tornadoService.listingGeneration(authToken, listingData, caseTypeId);
        } catch (Exception ex) {
            throw new DocumentManagementException(MESSAGE + ex.getMessage());
        }
    }
}