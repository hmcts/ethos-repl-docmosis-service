package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseCreationException;
import uk.gov.hmcts.ecm.common.exceptions.CaseRetrievalException;
import uk.gov.hmcts.ecm.common.exceptions.DocumentManagementException;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;
import uk.gov.hmcts.ecm.common.model.listing.items.ListingTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ListingHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.CasesAwaitingJudgmentReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.CasesAwaitingJudgmentReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.CcdReportDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casescompleted.CasesCompletedReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesourcelocalreport.CaseSourceLocalReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype.HearingsByHearingTypeReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments.HearingsToJudgmentsCcdReportDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments.HearingsToJudgmentsReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments.HearingsToJudgmentsReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition.NoPositionChangeCcdDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition.NoPositionChangeReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition.NoPositionChangeReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.servingclaims.ServingClaimsReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.timetofirsthearing.TimeToFirstHearingReport;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ALL_VENUES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BROUGHT_FORWARD_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASES_AWAITING_JUDGMENT_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASES_COMPLETED_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASE_SOURCE_LOCAL_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMS_ACCEPTED_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARINGS_BY_HEARING_TYPE_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARINGS_TO_JUDGEMENTS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_DOC_ETCL;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_ETCL_STAFF;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_POSTPONED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_SETTLED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_WITHDRAWN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_MEDIATION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_MEDIATION_TCC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING_CM;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING_CM_TCC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PRIVATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LIVE_CASELOAD_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RANGE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SERVING_CLAIMS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TIME_TO_FIRST_HEARING_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper.CASES_SEARCHED;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.NO_CHANGE_IN_CURRENT_POSITION_REPORT;

@RequiredArgsConstructor
@Slf4j
@Service("listingService")
public class ListingService {

    private final TornadoService tornadoService;
    private final CcdClient ccdClient;
    private final CasesCompletedReport casesCompletedReport;
    private final TimeToFirstHearingReport timeToFirstHearingReport;
    private final ServingClaimsReport servingClaimsReport;
    private String listingDateFrom;
    private String listingDateTo;
    private final CaseSourceLocalReport caseSourceLocalReport;
    private final HearingsByHearingTypeReport hearingsByHearingTypeReport;

    private static final String MISSING_DOCUMENT_NAME = "Missing document name";
    private static final String MESSAGE = "Failed to generate document for case id : ";

    public ListingData listingCaseCreation(ListingDetails listingDetails) {

        var listingData = listingDetails.getCaseData();

        if (listingData.getHearingDocType() != null) {
            listingData.setDocumentName(listingData.getHearingDocType());
        } else if (listingData.getReportType() != null) {
            listingData.setDocumentName(listingData.getReportType());
        } else {
            listingData.setDocumentName(MISSING_DOCUMENT_NAME);
        }

        return listingData;
    }

    public CaseData processListingSingleCasesRequest(CaseDetails caseDetails) {
        var caseData = caseDetails.getCaseData();
        List<ListingTypeItem> listingTypeItems = new ArrayList<>();
        if (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty()) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                if (hearingTypeItem.getValue().getHearingDateCollection() != null) {
                    log.info("Processing listing single cases");
                    listingTypeItems.addAll(getListingTypeItems(hearingTypeItem,
                            caseData.getPrintHearingDetails(), caseData));
                }
            }
        }
        caseData.setPrintHearingCollection(caseData.getPrintHearingDetails());
        caseData.getPrintHearingCollection().setListingCollection(listingTypeItems);
        caseData.getPrintHearingCollection().clearReportFields();

        return caseData;
    }

    public ListingData setCourtAddressFromCaseData(CaseData caseData) {
        var listingData = caseData.getPrintHearingCollection();
        listingData.setTribunalCorrespondenceAddress(caseData.getTribunalCorrespondenceAddress());
        listingData.setTribunalCorrespondenceTelephone(caseData.getTribunalCorrespondenceTelephone());
        listingData.setTribunalCorrespondenceFax(caseData.getTribunalCorrespondenceFax());
        listingData.setTribunalCorrespondenceEmail(caseData.getTribunalCorrespondenceEmail());
        listingData.setTribunalCorrespondenceDX(caseData.getTribunalCorrespondenceDX());
        return listingData;
    }

    public ListingData processListingHearingsRequest(ListingDetails listingDetails, String authToken) {
        try {
            List<SubmitEvent> submitEvents = getListingHearingsSearch(listingDetails, authToken);
            if (submitEvents != null) {
                log.info(CASES_SEARCHED + submitEvents.size());
                List<ListingTypeItem> listingTypeItems = new ArrayList<>();
                for (SubmitEvent submitEvent : submitEvents) {
                    if (submitEvent.getCaseData().getHearingCollection() != null
                            && !submitEvent.getCaseData().getHearingCollection().isEmpty()) {
                        addListingTypeItems(submitEvent, listingTypeItems, listingDetails);
                    }
                }
                listingDetails.getCaseData().setListingCollection(listingTypeItems);
            }

            listingDetails.getCaseData().clearReportFields();
            return listingDetails.getCaseData();
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + listingDetails.getCaseId() + ex.getMessage());
        }
    }

    private void addListingTypeItems(SubmitEvent submitEvent,
                                     List<ListingTypeItem> listingTypeItems,
                                     ListingDetails listingDetails) {
        for (HearingTypeItem hearingTypeItem : submitEvent.getCaseData().getHearingCollection()) {
            if (hearingTypeItem.getValue().getHearingDateCollection() != null) {
                listingTypeItems.addAll(getListingTypeItems(hearingTypeItem,
                        listingDetails.getCaseData(), submitEvent.getCaseData()));
            }
        }
    }

    private List<SubmitEvent> getListingHearingsSearch(ListingDetails listingDetails, String authToken)
            throws IOException {
        var listingData = listingDetails.getCaseData();
        Map.Entry<String, String> entry =
                ListingHelper.getListingVenueToSearch(listingData).entrySet().iterator().next();
        String venueToSearchMapping = entry.getKey();
        String venueToSearch = entry.getValue();
        String dateFrom;
        String dateTo;
        boolean dateRange = listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        if (dateRange) {
            dateFrom = listingData.getListingDateFrom();
            dateTo = listingData.getListingDateTo();
        } else {
            dateFrom = listingData.getListingDate();
            dateTo = listingData.getListingDate();
        }

        return ccdClient.retrieveCasesVenueAndDateElasticSearch(
                authToken, UtilHelper.getListingCaseTypeId(listingDetails.getCaseTypeId()),
                dateFrom, dateTo, venueToSearch, venueToSearchMapping);
    }

    private List<ListingTypeItem> getListingTypeItems(HearingTypeItem hearingTypeItem,
                                                      ListingData listingData, CaseData caseData) {
        List<ListingTypeItem> listingTypeItems = new ArrayList<>();
        if (isHearingTypeValid(listingData, hearingTypeItem)) {
            int hearingDateCollectionSize = hearingTypeItem.getValue().getHearingDateCollection().size();
            for (var i = 0; i < hearingDateCollectionSize; i++) {
                log.info("EthosCaseRef Listing: " + caseData.getEthosCaseReference());
                hearingTypeItem.getValue().getHearingNumber();
                log.info("Hearing number: " + hearingTypeItem.getValue().getHearingNumber());
                var dateListedTypeItem = hearingTypeItem.getValue().getHearingDateCollection().get(i);
                boolean isListingVenueValid = isListingVenueValid(listingData, dateListedTypeItem);
                boolean isListingDateValid = isListingDateValid(listingData, dateListedTypeItem);
                log.info("isListingVenueValid: " + isListingVenueValid);
                log.info("isListingDateValid: " + isListingDateValid);
                var isListingStatusValid = true;
                if (!showAllHearingType(listingData)) {
                    isListingStatusValid = isListingStatusValid(dateListedTypeItem);
                    log.info("isListingStatusValid: " + isListingStatusValid);
                }
                if (!isListingVenueValid || !isListingDateValid || !isListingStatusValid) {
                    continue;
                }
                var listingTypeItem = new ListingTypeItem();
                var listingType = ListingHelper.getListingTypeFromCaseData(
                        listingData, caseData, hearingTypeItem.getValue(), dateListedTypeItem.getValue(),
                        i, hearingDateCollectionSize);
                listingTypeItem.setId(String.valueOf(dateListedTypeItem.getId()));
                listingTypeItem.setValue(listingType);
                listingTypeItems.add(listingTypeItem);
            }
        }
        return listingTypeItems;
    }

    public ListingData generateReportData(ListingDetails listingDetails, String authToken) {
        try {
            String reportType = listingDetails.getCaseData().getReportType();
            switch (reportType) {
                case CASES_AWAITING_JUDGMENT_REPORT:
                    return getCasesAwaitingJudgmentReport(listingDetails, authToken);
                case HEARINGS_TO_JUDGEMENTS_REPORT:
                    return getHearingsToJudgmentsReport(listingDetails, authToken);
                case NO_CHANGE_IN_CURRENT_POSITION_REPORT:
                    return getNoPositionChangeReport(listingDetails, authToken);
                default:
                    return getDateRangeReport(listingDetails, authToken);
            }
        } catch (Exception ex) {
            throw new CaseRetrievalException(MESSAGE + listingDetails.getCaseId(), ex);
        }
    }

    private CasesAwaitingJudgmentReportData getCasesAwaitingJudgmentReport(ListingDetails listingDetails,
                                                                           String authToken) {
        log.info("Cases Awaiting Judgment for {}", listingDetails.getCaseTypeId());
        var reportDataSource = new CcdReportDataSource(authToken, ccdClient);

        var casesAwaitingJudgmentReport = new CasesAwaitingJudgmentReport(reportDataSource);
        var reportData = casesAwaitingJudgmentReport.runReport(
                listingDetails.getCaseTypeId());

        reportData.setDocumentName(listingDetails.getCaseData().getDocumentName());
        reportData.setReportType(listingDetails.getCaseData().getReportType());
        return reportData;
    }

    private HearingsToJudgmentsReportData getHearingsToJudgmentsReport(ListingDetails listingDetails,
                                                                       String authToken) {
        log.info("Hearings To Judgments for {}", listingDetails.getCaseTypeId());
        setListingDateRangeForSearch(listingDetails);
        var reportDataSource = new HearingsToJudgmentsCcdReportDataSource(authToken, ccdClient);
        var hearingsToJudgmentsReport = new HearingsToJudgmentsReport(reportDataSource, listingDateFrom, listingDateTo);
        var reportData = hearingsToJudgmentsReport.runReport(
                listingDetails.getCaseTypeId());
        reportData.setDocumentName(listingDetails.getCaseData().getDocumentName());
        reportData.setReportType(listingDetails.getCaseData().getReportType());
        reportData.setHearingDateType(listingDetails.getCaseData().getHearingDateType());
        reportData.setListingDateFrom(listingDetails.getCaseData().getListingDateFrom());
        reportData.setListingDateTo(listingDetails.getCaseData().getListingDateTo());
        reportData.setListingDate(listingDetails.getCaseData().getListingDate());
        return reportData;
    }

    private NoPositionChangeReportData getNoPositionChangeReport(ListingDetails listingDetails, String authToken) {
        log.info("No Change In Current Position for {}", listingDetails.getCaseTypeId());
        var reportDataSource = new NoPositionChangeCcdDataSource(authToken, ccdClient);
        var hearingsToJudgmentsReport = new NoPositionChangeReport(reportDataSource,
                listingDetails.getCaseData().getReportDate());
        var reportData = hearingsToJudgmentsReport.runReport(
                listingDetails.getCaseTypeId());
        reportData.setDocumentName(listingDetails.getCaseData().getDocumentName());
        reportData.setReportType(listingDetails.getCaseData().getReportType());
        reportData.setReportDate(listingDetails.getCaseData().getReportDate());
        return reportData;
    }

    private ListingData getDateRangeReport(ListingDetails listingDetails, String authToken) throws IOException {
        List<SubmitEvent> submitEvents = getDateRangeReportSearch(listingDetails, authToken);

        switch (listingDetails.getCaseData().getReportType()) {
            case BROUGHT_FORWARD_REPORT:
                return ReportHelper.processBroughtForwardDatesRequest(listingDetails, submitEvents);
            case CLAIMS_ACCEPTED_REPORT:
                return ReportHelper.processClaimsAcceptedRequest(listingDetails, submitEvents);
            case LIVE_CASELOAD_REPORT:
                return ReportHelper.processLiveCaseloadRequest(listingDetails, submitEvents);
            case CASES_COMPLETED_REPORT:
                return casesCompletedReport.generateReportData(listingDetails, submitEvents);
            case TIME_TO_FIRST_HEARING_REPORT:
                return timeToFirstHearingReport.generateReportData(listingDetails, submitEvents);
            case SERVING_CLAIMS_REPORT:
                return servingClaimsReport.generateReportData(listingDetails, submitEvents);
            case CASE_SOURCE_LOCAL_REPORT:
                return caseSourceLocalReport.generateReportData(listingDetails, submitEvents);
            case HEARINGS_BY_HEARING_TYPE_REPORT:
                return hearingsByHearingTypeReport.processHearingsByHearingTypeRequest(
                        listingDetails, submitEvents);
            default:
                return listingDetails.getCaseData();
        }
    }

    private void setListingDateRangeForSearch(ListingDetails listingDetails) {
        var listingData = listingDetails.getCaseData();
        boolean isRangeHearingDateType = listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        if (!isRangeHearingDateType) {
            listingDateFrom = LocalDate.parse(listingData.getListingDate(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().format(OLD_DATE_TIME_PATTERN);
            listingDateTo = LocalDate.parse(listingData.getListingDate(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().plusDays(1).minusSeconds(1).format(OLD_DATE_TIME_PATTERN);
        } else {
            listingDateFrom = LocalDate.parse(listingData.getListingDateFrom(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().format(OLD_DATE_TIME_PATTERN);
            listingDateTo = LocalDate.parse(listingData.getListingDateTo(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().format(OLD_DATE_TIME_PATTERN);
        }
    }

    private List<SubmitEvent> getDateRangeReportSearch(ListingDetails listingDetails, String authToken)
            throws IOException {
        var listingData = listingDetails.getCaseData();
        boolean isRangeHearingDateType = listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        if (!isRangeHearingDateType) {
            var dateToSearch = LocalDate.parse(listingData.getListingDate(), OLD_DATE_TIME_PATTERN2).toString();
            return ccdClient.retrieveCasesGenericReportElasticSearch(authToken, UtilHelper.getListingCaseTypeId(
                    listingDetails.getCaseTypeId()), dateToSearch, dateToSearch, listingData.getReportType());
        } else {
            var dateToSearchFrom = LocalDate.parse(listingData.getListingDateFrom(),
                    OLD_DATE_TIME_PATTERN2).toString();
            var dateToSearchTo = LocalDate.parse(listingData.getListingDateTo(),
                    OLD_DATE_TIME_PATTERN2).toString();
            return ccdClient.retrieveCasesGenericReportElasticSearch(authToken, UtilHelper.getListingCaseTypeId(
                    listingDetails.getCaseTypeId()), dateToSearchFrom, dateToSearchTo, listingData.getReportType());
        }
    }

    private boolean isListingVenueValid(ListingData listingData, DateListedTypeItem dateListedTypeItem) {
        if (listingData.getListingVenue().equals(ALL_VENUES)) {
            return true;
        } else {
            String venueSearched;
            String venueToSearch = ListingHelper.getListingVenue(listingData);
            log.info("VENUE TO SEARCH: " + venueToSearch);

            if (ListingHelper.isAllScottishVenues(listingData)) {
                venueSearched = dateListedTypeItem.getValue().getHearingVenueDay() != null
                        ? dateListedTypeItem.getValue().getHearingVenueDay()
                        : " ";
                log.info("Checking venue for all scottish level (HearingVenueDay): " + venueSearched);
            } else {
                venueSearched = ListingHelper.getVenueFromDateListedType(dateListedTypeItem.getValue());
                log.info("Checking venue low level: " + venueSearched);
            }
            return venueSearched.trim().equals(venueToSearch.trim());
        }
    }

    private boolean isListingDateValid(ListingData listingData, DateListedTypeItem dateListedTypeItem) {
        boolean dateRange = listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        String dateListed = !isNullOrEmpty(dateListedTypeItem.getValue().getListedDate())
                ? dateListedTypeItem.getValue().getListedDate()
                : "";
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
        var dateListedType = dateListedTypeItem.getValue();

        if (dateListedType.getHearingStatus() != null) {
            List<String> invalidHearingStatuses = Arrays.asList(HEARING_STATUS_SETTLED,
                    HEARING_STATUS_WITHDRAWN, HEARING_STATUS_POSTPONED);
            return invalidHearingStatuses.stream().noneMatch(str -> str.equals(dateListedType.getHearingStatus()));
        } else {
            return true;
        }
    }

    private boolean showAllHearingType(ListingData listingData) {
        return !isNullOrEmpty(listingData.getHearingDocType())
                && !isNullOrEmpty(listingData.getHearingDocETCL())
                && listingData.getHearingDocType().equals(HEARING_DOC_ETCL)
                && listingData.getHearingDocETCL().equals(HEARING_ETCL_STAFF)
                && !isNullOrEmpty(listingData.getShowAll())
                && listingData.getShowAll().equals(YES);
    }

    private boolean isHearingTypeValid(ListingData listingData, HearingTypeItem hearingTypeItem) {
        if (!isNullOrEmpty(listingData.getHearingDocType())
                && !isNullOrEmpty(listingData.getHearingDocETCL())
                && listingData.getHearingDocType().equals(HEARING_DOC_ETCL)
                && !listingData.getHearingDocETCL().equals(HEARING_ETCL_STAFF)) {

            var hearingType = hearingTypeItem.getValue();

            if (hearingType.getHearingType() != null) {
                if (hearingType.getHearingType().equals(HEARING_TYPE_PERLIMINARY_HEARING)
                        && hearingType.getHearingPublicPrivate() != null
                        && hearingType.getHearingPublicPrivate().equals(HEARING_TYPE_PRIVATE)) {
                    return false;
                } else {
                    List<String> invalidHearingTypes = Arrays.asList(HEARING_TYPE_JUDICIAL_MEDIATION,
                            HEARING_TYPE_JUDICIAL_MEDIATION_TCC, HEARING_TYPE_PERLIMINARY_HEARING_CM,
                            HEARING_TYPE_PERLIMINARY_HEARING_CM_TCC);
                    return invalidHearingTypes.stream().noneMatch(str -> str.equals(hearingType.getHearingType()));
                }
            }
        }
        return true;
    }

    public DocumentInfo processHearingDocument(ListingData listingData, String caseTypeId, String authToken) {
        try {
            return tornadoService.listingGeneration(authToken, listingData, caseTypeId);
        } catch (Exception ex) {
            throw new DocumentManagementException(MESSAGE + caseTypeId, ex);
        }
    }
}