package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseCreationException;
import uk.gov.hmcts.ecm.common.exceptions.CaseRetrievalException;
import uk.gov.hmcts.ecm.common.exceptions.DocumentManagementException;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.BFActionType;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.BFDateTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.items.ListingTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.BFDateType;
import uk.gov.hmcts.ecm.common.model.listing.types.ListingType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ListingHelper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.helpers.ESHelper.LISTING_VENUE_FIELD_NAME;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
@Service("listingService")
public class ListingService {

    private final TornadoService tornadoService;
    private final CcdClient ccdClient;
    private static final String MISSING_DOCUMENT_NAME = "Missing document name";
    private static final String MESSAGE = "Failed to generate document for case id : ";

    @Autowired
    public ListingService(TornadoService tornadoService, CcdClient ccdClient) {
        this.tornadoService = tornadoService;
        this.ccdClient = ccdClient;
    }

    public ListingData listingCaseCreation(ListingDetails listingDetails) {

        ListingData listingData = listingDetails.getCaseData();

        if (listingData.getHearingDocType() != null) {
            listingData.setDocumentName(listingData.getHearingDocType());
        }
        else if (listingData.getReportType() != null) {
            listingData.setDocumentName(listingData.getReportType());
        }
        else {
            listingData.setDocumentName(MISSING_DOCUMENT_NAME);
        }

        return listingData;
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
            }
            return clearListingFields(listingDetails.getCaseData());
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + listingDetails.getCaseId() + ex.getMessage());
        }
    }

    public ListingData generateReportData(ListingDetails listingDetails, String authToken) {
        if (listingDetails.getCaseData().getReportType().equals(BROUGHT_FORWARD_REPORT)) {
            return processBroughtForwardDatesRequest(listingDetails, authToken);
        } else {
            return listingDetails.getCaseData();
        }
    }

    private ListingData processBroughtForwardDatesRequest(ListingDetails listingDetails, String authToken) {
        try {
            //List<SubmitEvent> submitEvents = ccdClient.retrieveCases(authToken, ListingHelper.getCaseTypeId(listingDetails.getCaseTypeId()), listingDetails.getJurisdiction());
            List<SubmitEvent> submitEvents = getGenericReportSearch(listingDetails, authToken);
            if (submitEvents != null && !submitEvents.isEmpty()) {
                log.info("Cases searched: " + submitEvents.size());
                List<BFDateTypeItem> bFDateTypeItems = new ArrayList<>();
                for (SubmitEvent submitEvent : submitEvents) {
                    if (submitEvent.getCaseData().getBfActions() != null && !submitEvent.getCaseData().getBfActions().isEmpty()) {
                        for (BFActionTypeItem bfActionTypeItem : submitEvent.getCaseData().getBfActions()) {
                            BFDateTypeItem bFDateTypeItem = getBFDateTypeItem(bfActionTypeItem, listingDetails.getCaseData(), submitEvent.getCaseData());
                            if (bFDateTypeItem.getValue() != null) {
                                bFDateTypeItems.add(bFDateTypeItem);
                            }
                        }
                    }
                }
                listingDetails.getCaseData().setBfDateCollection(bFDateTypeItems);
            }
            return clearListingFields(listingDetails.getCaseData());
        } catch (Exception ex) {
            throw new CaseRetrievalException(MESSAGE + listingDetails.getCaseId() + ex.getMessage());
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

    private List<SubmitEvent> getGenericReportSearch(ListingDetails listingDetails, String authToken) throws IOException {
        ListingData listingData = listingDetails.getCaseData();
        boolean dateRange = listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        if (!dateRange) {
            String dateToSearch = LocalDate.parse(listingData.getListingDate(), OLD_DATE_TIME_PATTERN2).toString();
            return ccdClient.retrieveCasesGenericReportElasticSearch(authToken, ListingHelper.getCaseTypeId(listingDetails.getCaseTypeId()),
                    dateToSearch, dateToSearch, listingData.getReportType());
        } else {
            String dateToSearchFrom = LocalDate.parse(listingData.getListingDateFrom(), OLD_DATE_TIME_PATTERN2).toString();
            String dateToSearchTo = LocalDate.parse(listingData.getListingDateTo(), OLD_DATE_TIME_PATTERN2).toString();
            return ccdClient.retrieveCasesGenericReportElasticSearch(authToken, ListingHelper.getCaseTypeId(listingDetails.getCaseTypeId()),
                    dateToSearchFrom, dateToSearchTo, listingData.getReportType());
        }
    }

    private ListingData clearListingFields(ListingData listingData) {
        listingData.setListingVenueOfficeAber(null);
        listingData.setListingVenueOfficeGlas(null);
        boolean dateRange = listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        if (dateRange) {
            listingData.setListingDateFrom(null);
            listingData.setListingDateTo(null);
        } else {
            listingData.setListingDate(null);
        }
        listingData.setClerkResponsible(null);
        return listingData;
    }

    private List<ListingTypeItem> getListingTypeItems(HearingTypeItem hearingTypeItem, ListingData listingData, CaseData caseData) {
        List<ListingTypeItem> listingTypeItems = new ArrayList<>();
        if (isHearingTypeValid(listingData, hearingTypeItem)) {
            int hearingDateCollectionSize = hearingTypeItem.getValue().getHearingDateCollection().size();
            for (int i = 0; i < hearingDateCollectionSize; i++) {
                DateListedTypeItem dateListedTypeItem = hearingTypeItem.getValue().getHearingDateCollection().get(i);
                boolean isListingVenueValid = isListingVenueValid(listingData, dateListedTypeItem);
                boolean isListingDateValid = isListingDateValid(listingData, dateListedTypeItem);
                boolean isListingStatusValid = isListingStatusValid(dateListedTypeItem);
                if (isListingDateValid && isListingVenueValid && isListingStatusValid) {
                    ListingTypeItem listingTypeItem = new ListingTypeItem();
                    ListingType listingType = ListingHelper.getListingTypeFromCaseData(listingData, caseData, hearingTypeItem.getValue(), dateListedTypeItem.getValue(), i, hearingDateCollectionSize);
                    listingTypeItem.setId(String.valueOf(dateListedTypeItem.getId()));
                    listingTypeItem.setValue(listingType);
                    listingTypeItems.add(listingTypeItem);
                }
            }
        }
        return listingTypeItems;
    }

    private BFDateTypeItem getBFDateTypeItem(BFActionTypeItem bfActionTypeItem, ListingData listingData, CaseData caseData) {
        BFDateTypeItem bFDateTypeItem = new BFDateTypeItem();
        BFActionType bfActionType = bfActionTypeItem.getValue();
        if (!isNullOrEmpty(bfActionType.getBfDate()) && isNullOrEmpty(bfActionType.getCleared())) {
            boolean matchingDateIsValid = validateMatchingDate(listingData, bfActionType.getBfDate());
            boolean clerkResponsibleIsValid = validateClerkResponsible(listingData, caseData);
            if (matchingDateIsValid && clerkResponsibleIsValid) {
                BFDateType bFDateType = new BFDateType();
                bFDateType.setCaseReference(caseData.getEthosCaseReference());
                bFDateType.setBroughtForwardDate(bfActionType.getBfDate());
                bFDateType.setBroughtForwardDateReason(bfActionType.getNotes());
                bFDateType.setBroughtForwardDateCleared(bfActionType.getCleared());
                bFDateTypeItem.setId(String.valueOf(bfActionTypeItem.getId()));
                bFDateTypeItem.setValue(bFDateType);
            }
        }
        return bFDateTypeItem;
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
                        ? ListingHelper.createMap(LISTING_VENUE_FIELD_NAME, listingData.getListingVenue())
                        : ListingHelper.createMap("","");
            }
        }
    }

    private boolean isListingVenueValid(ListingData listingData, DateListedTypeItem dateListedTypeItem) {
        Map<String, String> venueToSearchMap = getListingVenueToSearch(listingData);
        String venueToSearch = venueToSearchMap.entrySet().iterator().next().getValue();
        if (ALL_VENUES.equals(venueToSearch)) {
            return true;
        } else {
            String venueSearched;
            if (isAllVenuesGlasgowAndAberdeen(listingData)) {
                venueSearched = ListingHelper.getVenueFromDateListedType(dateListedTypeItem.getValue());
            } else {
                venueSearched = !isNullOrEmpty(dateListedTypeItem.getValue().getHearingVenueDay()) ? dateListedTypeItem.getValue().getHearingVenueDay() : " ";
            }
            return venueSearched.equals(venueToSearch);
        }
    }

    private boolean isListingDateValid(ListingData listingData, DateListedTypeItem dateListedTypeItem) {
        boolean dateRange = listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        String dateListed = !isNullOrEmpty(dateListedTypeItem.getValue().getListedDate()) ? dateListedTypeItem.getValue().getListedDate() : "";
        if (dateRange) {
            String dateToSearchFrom = listingData.getListingDateFrom();
            String dateToSearchTo = listingData.getListingDateTo();
            return ListingHelper.getListingDateBetween(dateToSearchFrom, dateToSearchTo, dateListed);
        } else {
            String dateToSearch = listingData.getListingDate();
            return ListingHelper.getListingDateBetween(dateToSearch, "", dateListed);
        }
    }

    private boolean isListingStatusValid(DateListedTypeItem dateListedTypeItem) {
        DateListedType dateListedType = dateListedTypeItem.getValue();

        if (dateListedType.getHearingStatus() != null) {
            List<String> invalidHearingStatuses = Arrays.asList(HEARING_STATUS_SETTLED, HEARING_STATUS_WITHDRAWN, HEARING_STATUS_POSTPONED);
            return invalidHearingStatuses.stream().noneMatch(str -> str.equals(dateListedType.getHearingStatus()));
        } else {
            return true;
        }
    }

    private boolean isHearingTypeValid(ListingData listingData, HearingTypeItem hearingTypeItem) {
        if (!isNullOrEmpty(listingData.getHearingDocType()) &&
                !isNullOrEmpty(listingData.getHearingDocETCL()) &&
                listingData.getHearingDocType().equals(HEARING_DOC_ETCL) &&
                !listingData.getHearingDocETCL().equals(HEARING_ETCL_STAFF)) {

            HearingType hearingType = hearingTypeItem.getValue();

            if (hearingType.getHearingType() != null) {
                if (hearingType.getHearingType().equals(HEARING_TYPE_PERLIMINARY_HEARING) && hearingType.getHearingPublicPrivate().equals(HEARING_TYPE_PRIVATE)) {
                    return false;
                } else {
                    List<String> invalidHearingTypes = Arrays.asList(HEARING_TYPE_JUDICIAL_MEDIATION, HEARING_TYPE_JUDICIAL_MEDIATION_TCC, HEARING_TYPE_PERLIMINARY_HEARING_CM, HEARING_TYPE_PERLIMINARY_HEARING_CM_TCC);
                    return invalidHearingTypes.stream().noneMatch(str -> str.equals(hearingType.getHearingType()));
                }
            }
        }
        return true;
    }

    private boolean validateMatchingDate(ListingData listingData, String matchingDate) {
        boolean dateRange = listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        if (!dateRange) {
            String dateToSearch = listingData.getListingDate();
            return ListingHelper.getMatchingDateBetween(dateToSearch, "", matchingDate, false);
        } else {
            String dateToSearchFrom = listingData.getListingDateFrom();
            String dateToSearchTo = listingData.getListingDateTo();
            return ListingHelper.getMatchingDateBetween(dateToSearchFrom, dateToSearchTo, matchingDate, true);
        }
    }

    private boolean validateClerkResponsible(ListingData listingData, CaseData caseData) {
        if (listingData.getClerkResponsible() != null) {
            if (caseData.getClerkResponsible() != null) {
                return listingData.getClerkResponsible().equals(caseData.getClerkResponsible());
            }
            return false;
        }
        return true;
    }

    public DocumentInfo processHearingDocument(ListingData listingData, String caseTypeId, String authToken) {
        try {
            return tornadoService.listingGeneration(authToken, listingData, caseTypeId);
        } catch (Exception ex) {
            throw new DocumentManagementException(MESSAGE + ex.getMessage());
        }
    }
}