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

import java.util.ArrayList;
import java.util.List;

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

    public ListingDetails processListingHearingsRequest(ListingDetails listingDetails, String authToken) {
        try {
            List<SubmitEvent> submitEvents = ccdClient.retrieveCases(authToken, ListingHelper.getCaseTypeId(listingDetails.getCaseTypeId()), listingDetails.getJurisdiction());
            if (submitEvents != null) {
                List<ListingTypeItem> listingTypeItems = new ArrayList<>();
                for (SubmitEvent submitEvent : submitEvents) {
                    if (submitEvent.getCaseData().getHearingCollection() != null && !submitEvent.getCaseData().getHearingCollection().isEmpty()) {
                        int hearingCollectionSize = submitEvent.getCaseData().getHearingCollection().size();
                        for (int i = 0 ; i < hearingCollectionSize ; i ++) {
                            HearingTypeItem hearingTypeItem = submitEvent.getCaseData().getHearingCollection().get(i);
                            log.info("HEARING: " + hearingTypeItem.getValue());
                            if (hearingTypeItem.getValue().getHearingDateCollection() != null) {
                                listingTypeItems.addAll(getListingTypeItems(hearingTypeItem, listingDetails.getCaseData(), submitEvent, i, hearingCollectionSize));
                            }
                        }
                    }
                }
                log.info("listingTypeItems: " + listingDetails.toString());
                listingDetails.getCaseData().setListingCollection(listingTypeItems);
            }
            return clearListingFields(listingDetails);
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + listingDetails.getCaseId() + ex.getMessage());
        }
    }

    private ListingDetails clearListingFields(ListingDetails listingDetails) {
        listingDetails.getCaseData().setListingVenueOfficeAber(null);
        listingDetails.getCaseData().setListingVenueOfficeGlas(null);
        boolean dateRange = listingDetails.getCaseData().getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        if (dateRange) {
            listingDetails.getCaseData().setListingDate(null);
        } else {
            listingDetails.getCaseData().setListingDateFrom(null);
            listingDetails.getCaseData().setListingDateTo(null);
        }
        return listingDetails;
    }

    private List<ListingTypeItem> getListingTypeItems(HearingTypeItem hearingTypeItem, ListingData listingData, SubmitEvent submitEvent, int i, int hearingCollectionSize) {
        List<ListingTypeItem> listingTypeItems = new ArrayList<>();
        for (DateListedTypeItem dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
            boolean isListingVenueValid = isListingVenueValid(listingData, dateListedTypeItem);
            boolean isListingDateValid = isListingDateValid(listingData, dateListedTypeItem);
            if (isListingDateValid && isListingVenueValid) {
                ListingTypeItem listingTypeItem = new ListingTypeItem();
                ListingType listingType = ListingHelper.getListingTypeFromSubmitData(submitEvent, hearingTypeItem.getValue(), dateListedTypeItem.getValue(), i, hearingCollectionSize);
                listingTypeItem.setId(String.valueOf(dateListedTypeItem.getId()));
                listingTypeItem.setValue(listingType);
                listingTypeItems.add(listingTypeItem);
            }
        }
        return listingTypeItems;
    }

    private boolean isListingVenueValid(ListingData listingData, DateListedTypeItem dateListedTypeItem) {
        boolean allLocations = listingData.getListingVenue().equals(ALL_VENUES);
        if (allLocations) {
            log.info("Searching by all venues");
            return true;
        } else {
            boolean allVenuesGlasgow = !isNullOrEmpty(listingData.getListingVenueOfficeGlas()) && listingData.getListingVenueOfficeGlas().equals(ALL_VENUES);
            boolean allVenuesAberdeen = !isNullOrEmpty(listingData.getListingVenueOfficeAber()) && listingData.getListingVenueOfficeAber().equals(ALL_VENUES);
            String venueToSearch, venueSearched;
            if (!allVenuesGlasgow && !allVenuesAberdeen) {
                venueToSearch = ListingHelper.getVenueToSearch(listingData);
                venueSearched = ListingHelper.getVenueFromDateListedType(dateListedTypeItem.getValue());
            } else {
                venueToSearch = !isNullOrEmpty(listingData.getListingVenue()) ? listingData.getListingVenue() : " ";;
                venueSearched = !isNullOrEmpty(dateListedTypeItem.getValue().getHearingVenueDay()) ? dateListedTypeItem.getValue().getHearingVenueDay() : " ";
            }
            log.info("VenueToSearch: " + venueToSearch + "   VenueSearched: " + venueSearched);
            return venueSearched.equals(venueToSearch);
        }
    }

    private boolean isListingDateValid(ListingData listingData, DateListedTypeItem dateListedTypeItem) {
        boolean dateRange = listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        String dateListed = !isNullOrEmpty(dateListedTypeItem.getValue().getListedDate()) ? dateListedTypeItem.getValue().getListedDate() : "";
        String dateToSearch = listingData.getListingDate();
        if (dateRange) {
            String dateToSearchFrom = listingData.getListingDateFrom();
            String dateToSearchTo = listingData.getListingDateTo();
            log.info("RANGE: -> dateToSearchFrom: " + dateToSearchFrom + "   dateToSearchTo: " + dateToSearchTo + "   DateListed: " + dateListed);
            return ListingHelper.getListingDateBetween(dateToSearchFrom, dateToSearchTo, dateListed);
        } else {
            log.info("SINGLE: -> dateToSearch: " + dateToSearch + "   DateListed: " + dateListed);
            return ListingHelper.getListingDateBetween(dateToSearch, "", dateListed);
        }
    }

    public DocumentInfo processHearingDocument(ListingDetails listingDetails, String authToken) {
        try {
            return tornadoService.listingGeneration(authToken, listingDetails.getCaseData(), listingDetails.getCaseTypeId());
        } catch (Exception ex) {
            throw new DocumentManagementException(MESSAGE + listingDetails.getCaseId() + ex.getMessage());
        }
    }
}