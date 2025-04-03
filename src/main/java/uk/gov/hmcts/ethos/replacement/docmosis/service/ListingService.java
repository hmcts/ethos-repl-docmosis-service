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
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction.BfActionReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction.BfActionReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.CasesAwaitingJudgmentReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.CasesAwaitingJudgmentReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment.CcdReportDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casescompleted.CasesCompletedReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.casesourcelocalreport.CaseSourceLocalReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueCcdReportDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue.ClaimsByHearingVenueReportParams;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.eccreport.EccReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.eccreport.EccReportCcdDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.eccreport.EccReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype.HearingsByHearingTypeCcdReportDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype.HearingsByHearingTypeReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype.HearingsByHearingTypeReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments.HearingsToJudgmentsCcdReportDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments.HearingsToJudgmentsReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments.HearingsToJudgmentsReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays.MemberDaysReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition.NoPositionChangeCcdDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition.NoPositionChangeReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition.NoPositionChangeReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport.RespondentsReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport.RespondentsReportCcdDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport.RespondentsReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.servingclaims.ServingClaimsReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays.SessionDaysCcdReportDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays.SessionDaysReport;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays.SessionDaysReportData;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.timetofirsthearing.TimeToFirstHearingReport;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.ExcelReportDocumentInfoService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.jpaservice.JpaJudgeService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import static com.google.common.base.Strings.isNullOrEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ALL_VENUES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BROUGHT_FORWARD_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASES_AWAITING_JUDGMENT_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASES_COMPLETED_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASE_SOURCE_LOCAL_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMS_ACCEPTED_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMS_BY_HEARING_VENUE_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARINGS_BY_HEARING_TYPE_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARINGS_TO_JUDGEMENTS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_DOC_ETCL;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_ETCL_STAFF;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_POSTPONED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_SETTLED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_VACATED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_WITHDRAWN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_MEDIATION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_MEDIATION_TCC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING_CM;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PERLIMINARY_HEARING_CM_TCC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_PRIVATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LIVE_CASELOAD_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MEMBER_DAYS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CFCTC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CFT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RANGE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SERVING_CLAIMS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SESSION_DAYS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TEESSIDE_JUSTICE_CENTRE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TEESSIDE_MAGS;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TIME_TO_FIRST_HEARING_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.ListingHelper.CAUSE_LIST_DATE_TIME_PATTERN;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportHelper.CASES_SEARCHED;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.ECC_REPORT;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.NO_CHANGE_IN_CURRENT_POSITION_REPORT;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.RESPONDENTS_REPORT;


@RequiredArgsConstructor
@Slf4j
@Service("listingService")
public class ListingService {

    private final TornadoService tornadoService;
    private final CcdClient ccdClient;
    private final JpaJudgeService jpaJudgeService;
    private final CasesCompletedReport casesCompletedReport;
    private final TimeToFirstHearingReport timeToFirstHearingReport;
    private final ServingClaimsReport servingClaimsReport;
    private final CaseSourceLocalReport caseSourceLocalReport;
    private static final String MISSING_DOCUMENT_NAME = "Missing document name";
    private static final String MESSAGE = "Failed to generate document for case id : ";
    private final ExcelReportDocumentInfoService excelReportDocumentInfoService;
    private final UserService userService;
    private final BfActionReport bfActionReport;

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

    public ListingData processListingHearingsRequest(ListingDetails listingDetails,
                                                     String authToken) {
        try {
            List<SubmitEvent> submitEvents = getListingHearingsSearch(listingDetails, authToken);
            if (submitEvents != null) {
                log.info(CASES_SEARCHED + submitEvents.size());
                List<ListingTypeItem> listingTypeItems = new ArrayList<>();
                for (SubmitEvent submitEvent : submitEvents) {
                    if (isNotEmpty(submitEvent.getCaseData().getHearingCollection())
                        && !"Migrated".equals(submitEvent.getState())) {
                        addListingTypeItems(submitEvent, listingTypeItems, listingDetails);
                    }
                }
                listingTypeItems.sort(Comparator.comparing(o -> LocalDate.parse(o.getValue().getCauseListDate(),
                        CAUSE_LIST_DATE_TIME_PATTERN)));
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
        String venueToSearch = getCheckedHearingVenueToSearch(entry.getValue());
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

    private String getCheckedHearingVenueToSearch(String venueToCheck) {
        if (NEWCASTLE_CFCTC.equals(venueToCheck)) {
            return NEWCASTLE_CFT;
        }

        if (TEESSIDE_JUSTICE_CENTRE.equals(venueToCheck)) {
            return TEESSIDE_MAGS;
        }

        return venueToCheck;
    }

    private List<ListingTypeItem> getListingTypeItems(HearingTypeItem hearingTypeItem, ListingData listingData,
                                                      CaseData caseData) {
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

                setCauseListVenueForNewcastle(dateListedTypeItem, listingTypeItem);

                listingTypeItems.add(listingTypeItem);
            }
        }
        return listingTypeItems;
    }

    private void setCauseListVenueForNewcastle(DateListedTypeItem dateListedTypeItem,
                                               ListingTypeItem listingTypeItem) {
        if (listingTypeItem.getValue().getCauseListVenue().contains(NEWCASTLE_CFCTC) &&
            dateListedTypeItem.getValue().getHearingVenueNameForNewcastleCFT() != null) {
                listingTypeItem.getValue().setCauseListVenue(
                    dateListedTypeItem.getValue().getHearingVenueNameForNewcastleCFT());
        }

        if (listingTypeItem.getValue().getCauseListVenue().contains(TEESSIDE_JUSTICE_CENTRE) &&
            dateListedTypeItem.getValue().getHearingVenueNameForTeessideMags() != null) {
                listingTypeItem.getValue().setCauseListVenue(
                    dateListedTypeItem.getValue().getHearingVenueNameForTeessideMags());
        }
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
                case RESPONDENTS_REPORT:
                    return getRespondentsReport(listingDetails, authToken);
                case SESSION_DAYS_REPORT:
                    return getSessionDaysReport(listingDetails, authToken);
                case ECC_REPORT:
                    return getEccReport(listingDetails, authToken);
                case HEARINGS_BY_HEARING_TYPE_REPORT:
                    return getHearingsByHearingTypeReport(listingDetails, authToken);
                case CLAIMS_BY_HEARING_VENUE_REPORT:
                    return getClaimsByHearingVenueReport(listingDetails, authToken);
                default:
                    return getDateRangeReport(listingDetails, authToken, getUserFullName(authToken));
            }
        } catch (Exception ex) {
            throw new CaseRetrievalException(MESSAGE + listingDetails.getCaseId(), ex);
        }
    }

    private ClaimsByHearingVenueReportData getClaimsByHearingVenueReport(ListingDetails listingDetails,
                                                                         String authToken) {
        log.info("Claims By Hearing Venue Report for {}", listingDetails.getCaseTypeId());
        var genericReportParams = setListingDateRangeForSearch(listingDetails);
        var listingData = listingDetails.getCaseData();
        var claimsByHearingVenueReportParams = new ClaimsByHearingVenueReportParams(
                genericReportParams.getCaseTypeId(), genericReportParams.getDateFrom(),
                genericReportParams.getDateTo(), listingData.getHearingDateType(), getUserFullName(authToken));
        var reportDataSource = new ClaimsByHearingVenueCcdReportDataSource(authToken, ccdClient);
        var claimsByHearingVenueReport = new ClaimsByHearingVenueReport(reportDataSource);
        return claimsByHearingVenueReport.generateReport(claimsByHearingVenueReportParams,
                UtilHelper.getListingCaseTypeId(listingDetails.getCaseTypeId()));
    }

    private String getUserFullName(String userToken) {
        var userDetails = userService.getUserDetails(userToken);
        var firstName = userDetails.getFirstName() != null ? userDetails.getFirstName() : "";
        var lastName = userDetails.getLastName() != null ? userDetails.getLastName() : "";
        return firstName + " " + lastName;
    }

    private EccReportData getEccReport(ListingDetails listingDetails, String authToken) {
        log.info("Ecc Report for {}", listingDetails.getCaseTypeId());
        var reportDataSource = new EccReportCcdDataSource(authToken, ccdClient);
        var listingData = listingDetails.getCaseData();
        var params = setListingDateRangeForSearch(listingDetails);
        var eccReport = new EccReport(reportDataSource);
        var reportData = eccReport.generateReport(params);
        setReportData(reportData, listingData);
        return reportData;
    }

    private void setReportData(ListingData reportData, ListingData listingData) {
        reportData.setDocumentName(listingData.getDocumentName());
        reportData.setReportType(listingData.getReportType());
        reportData.setHearingDateType(listingData.getHearingDateType());
        reportData.setListingDateFrom(listingData.getListingDateFrom());
        reportData.setListingDateTo(listingData.getListingDateTo());
        reportData.setListingDate(listingData.getListingDate());
    }

    private RespondentsReportData getRespondentsReport(ListingDetails listingDetails, String authToken) {
        log.info("Respondents Report for {}", listingDetails.getCaseTypeId());
        var reportDataSource = new RespondentsReportCcdDataSource(authToken, ccdClient);
        var listingData = listingDetails.getCaseData();
        var params = setListingDateRangeForSearch(listingDetails);
        var respondentsReport = new RespondentsReport(reportDataSource);
        var reportData = respondentsReport.generateReport(params);
        setReportData(reportData, listingData);
        return reportData;
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

    private SessionDaysReportData getSessionDaysReport(ListingDetails listingDetails,
                                                       String authToken) {
        log.info("Session Days Report for {}", listingDetails.getCaseTypeId());
        var reportDataSource = new SessionDaysCcdReportDataSource(authToken, ccdClient);
        var params = setListingDateRangeForSearch(listingDetails);
        var listingData = listingDetails.getCaseData();
        var sessionDaysReport = new SessionDaysReport(reportDataSource, jpaJudgeService);
        var reportData = sessionDaysReport.generateReport(params);
        setReportData(reportData, listingData);
        return reportData;
    }

    private HearingsByHearingTypeReportData getHearingsByHearingTypeReport(ListingDetails listingDetails,
                                                                           String authToken) {
        log.info("Hearings By Hearing Type Report for {}", listingDetails.getCaseTypeId());
        var reportDataSource = new HearingsByHearingTypeCcdReportDataSource(authToken, ccdClient);
        setListingDateRangeForSearch(listingDetails);
        var listingData = listingDetails.getCaseData();
        var hearingsByHearingTypeReport = new HearingsByHearingTypeReport(reportDataSource);
        var params = setListingDateRangeForSearch(listingDetails);
        var reportData = hearingsByHearingTypeReport
                .generateReport(params);
        setReportData(reportData, listingData);
        return reportData;
    }

    private HearingsToJudgmentsReportData getHearingsToJudgmentsReport(ListingDetails listingDetails,
                                                                       String authToken) {
        log.info("Hearings To Judgments for {}", listingDetails.getCaseTypeId());
        var params = setListingDateRangeForSearch(listingDetails);
        var reportDataSource = new HearingsToJudgmentsCcdReportDataSource(authToken, ccdClient);
        var hearingsToJudgmentsReport = new HearingsToJudgmentsReport(reportDataSource, params);
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
        var noPositionChangeReport = new NoPositionChangeReport(reportDataSource,
                listingDetails.getCaseData().getReportDate());
        var reportData = noPositionChangeReport.runReport(
                listingDetails.getCaseTypeId());
        reportData.setDocumentName(listingDetails.getCaseData().getDocumentName());
        reportData.setReportType(listingDetails.getCaseData().getReportType());
        return reportData;
    }

    private ListingData getDateRangeReport(ListingDetails listingDetails,
                                           String authToken,
                                           String userName) throws IOException {
        clearListingFields(listingDetails.getCaseData());
        List<SubmitEvent> submitEvents = getDateRangeReportSearch(listingDetails, authToken);

        switch (listingDetails.getCaseData().getReportType()) {
            case BROUGHT_FORWARD_REPORT:
                return bfActionReport.runReport(listingDetails,
                        submitEvents, userName);
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
            case MEMBER_DAYS_REPORT:
                return new MemberDaysReport().runReport(listingDetails, submitEvents);
            default:
                return listingDetails.getCaseData();
        }
    }

    private void clearListingFields(ListingData listingData) {
        listingData.setLocalReportsSummary(null);
        listingData.setLocalReportsSummaryHdr(null);
        listingData.setLocalReportsSummaryHdr2(null);
        listingData.setLocalReportsSummary2(null);
        listingData.setLocalReportsDetailHdr(null);
        listingData.setLocalReportsDetail(null);
    }

    private ReportParams setListingDateRangeForSearch(ListingDetails listingDetails) {
        var listingData = listingDetails.getCaseData();
        boolean isRangeHearingDateType = listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        String listingDateFrom;
        String listingDateTo;
        if (!isRangeHearingDateType) {
            listingDateFrom = LocalDate.parse(listingData.getListingDate(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().format(OLD_DATE_TIME_PATTERN);
            listingDateTo = LocalDate.parse(listingData.getListingDate(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().plusDays(1).minusSeconds(1).format(OLD_DATE_TIME_PATTERN);
        } else {
            listingDateFrom = LocalDate.parse(listingData.getListingDateFrom(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().format(OLD_DATE_TIME_PATTERN);
            listingDateTo = LocalDate.parse(listingData.getListingDateTo(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().plusDays(1).minusSeconds(1).format(OLD_DATE_TIME_PATTERN);
        }
        return new ReportParams(listingDetails.getCaseTypeId(), listingDateFrom, listingDateTo);
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
                    HEARING_STATUS_WITHDRAWN, HEARING_STATUS_POSTPONED, HEARING_STATUS_VACATED);
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
            if (CLAIMS_BY_HEARING_VENUE_REPORT.equals(listingData.getReportType())) {
                return excelReportDocumentInfoService.generateClaimsByHearingVenueExcelReportDocumentInfo(
                        (ClaimsByHearingVenueReportData)listingData, caseTypeId, authToken);
            }
            if (BROUGHT_FORWARD_REPORT.equals(listingData.getReportType())) {
                return excelReportDocumentInfoService.generateBfExcelReportDocumentInfo(
                        (BfActionReportData)listingData, caseTypeId, authToken);
            }

            return tornadoService.listingGeneration(authToken, listingData, caseTypeId);

        } catch (Exception ex) {
            throw new DocumentManagementException(MESSAGE + caseTypeId, ex);
        }
    }
}