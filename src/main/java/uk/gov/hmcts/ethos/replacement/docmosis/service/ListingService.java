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
            return listingDetails;
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + listingDetails.getCaseId() + ex.getMessage());
        }
    }

    private List<ListingTypeItem> getListingTypeItems(HearingTypeItem hearingTypeItem, ListingData listingData, SubmitEvent submitEvent, int i, int hearingCollectionSize) {
        List<ListingTypeItem> listingTypeItems = new ArrayList<>();
        boolean dateRange = listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        String venueToSearch = ListingHelper.getVenueToSearch(listingData);
        for (DateListedTypeItem dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
            log.info("VenueToSearch: " + venueToSearch + "   VenueSearched: " + ListingHelper.getVenueFromDateListedType(dateListedTypeItem.getValue()));
            String dateListed = !isNullOrEmpty(dateListedTypeItem.getValue().getListedDate()) ? dateListedTypeItem.getValue().getListedDate() : "";
            String dateToSearch = listingData.getListingDate();
            boolean listingToBeAdded = false;
            if (dateRange) {
                String dateToSearchFrom = listingData.getListingDateFrom();
                String dateToSearchTo = listingData.getListingDateTo();
                log.info("RANGE: -> dateToSearchFrom: " + dateToSearchFrom + "   dateToSearchTo: " + dateToSearchTo + "   DateListed: " + dateListed);
                if (ListingHelper.getListingDateBetween(dateToSearchFrom, dateToSearchTo, dateListed) &&
                        ListingHelper.getVenueFromDateListedType(dateListedTypeItem.getValue()).equals(venueToSearch)) {
                    listingToBeAdded = true;
                }
            } else {
                log.info("SINGLE: -> dateToSearch: " + dateToSearch + "   DateListed: " + dateListed);
                if (ListingHelper.getListingDateBetween(dateToSearch, "", dateListed) &&
                        ListingHelper.getVenueFromDateListedType(dateListedTypeItem.getValue()).equals(venueToSearch)) {
                    listingToBeAdded = true;
                }
            }
            if (listingToBeAdded) {
                ListingTypeItem listingTypeItem = new ListingTypeItem();
                ListingType listingType = ListingHelper.getListingTypeFromSubmitData(submitEvent, hearingTypeItem.getValue(), dateListedTypeItem.getValue(), i, hearingCollectionSize);
                listingTypeItem.setId(String.valueOf(dateListedTypeItem.getId()));
                listingTypeItem.setValue(listingType);
                listingTypeItems.add(listingTypeItem);
            }
        }
        return listingTypeItems;
    }

    public DocumentInfo processHearingDocument(ListingDetails listingDetails, String authToken) {
        try {
            return tornadoService.listingGeneration(authToken, listingDetails.getCaseData(), listingDetails.getCaseTypeId());
        } catch (Exception ex) {
            throw new DocumentManagementException(MESSAGE + listingDetails.getCaseId() + ex.getMessage());
        }
    }
}