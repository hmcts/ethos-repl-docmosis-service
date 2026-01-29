package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.helper.CaseEventDetail;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.AdminData;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.AdminDetails;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RANGE_HEARING_DATE_TYPE;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReferenceDataFixesService {
    private static final String CASES_SEARCHED = "Cases searched: ";
    private static final String MESSAGE = "Failed to retrieve reference data for case id : ";
    public static final String GENERATE_CORRESPONDENCE = "generateCorrespondence";
    private final CcdClient ccdClient;

    public AdminData updateJudgesItcoReferences(AdminDetails adminDetails, String authToken,
                                                RefDataFixesCcdDataSource dataSource) {

        AdminData adminData = adminDetails.getCaseData();
        String existingJudgeCode = adminData.getExistingJudgeCode();
        String requiredJudgeCode = adminData.getRequiredJudgeCode();
        String caseTypeId = getTribunalOffice(adminData.getTribunalOffice());
        List<String> dates = getDateRangeForSearch(adminDetails, OLD_DATE_TIME_PATTERN);

        String dateFrom = dates.get(0);
        String dateTo = dates.get(1);
        try {
            List<SubmitEvent> submitEvents = dataSource.getDataForJudges(caseTypeId, dateFrom, dateTo, ccdClient);
            if (CollectionUtils.isNotEmpty(submitEvents)) {
                log.info(CASES_SEARCHED + submitEvents.size());
                for (SubmitEvent submitEvent : submitEvents) {
                    CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, caseTypeId,
                            adminDetails.getJurisdiction(), String.valueOf(submitEvent.getCaseId()));
                    CaseData caseData = returnedRequest.getCaseDetails().getCaseData();
                    if (setJudgeName(caseData, existingJudgeCode, requiredJudgeCode, dateFrom, dateTo)) {
                        ccdClient.submitEventForCase(authToken, caseData, caseTypeId,
                                adminDetails.getJurisdiction(), returnedRequest,
                                String.valueOf(submitEvent.getCaseId()));
                    }
                }
                log.info(String.format("Existing Judge's code in all cases from %s to %s is "
                        + "updated to required judge's code", dateFrom, dateTo));
            }
            return adminData;
        } catch (Exception ex) {
            log.error(MESSAGE + adminDetails.getCaseId(), ex);
            return null;
        }
    }

    private String getTribunalOffice(String tribunalOffice) {
        return switch (tribunalOffice) {
            case "London Central" -> LONDON_CENTRAL_CASE_TYPE_ID;
            case "London East" -> LONDON_EAST_CASE_TYPE_ID;
            case "London South" -> LONDON_SOUTH_CASE_TYPE_ID;
            case "Midlands East" -> MIDLANDS_EAST_CASE_TYPE_ID;
            case "Midlands West" -> MIDLANDS_WEST_CASE_TYPE_ID;
            default -> tribunalOffice;
        };
    }

    private List<String> getDateRangeForSearch(AdminDetails adminDetails, DateTimeFormatter pattern) {

        var refDataFixesData = adminDetails.getCaseData();
        boolean isRangeHearingDateType = refDataFixesData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        String dateFrom;
        String dateTo;
        if (!isRangeHearingDateType) {
            dateFrom = LocalDate.parse(refDataFixesData.getDate(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().format(pattern);
            dateTo = LocalDate.parse(refDataFixesData.getDate(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().plusDays(1).minusSeconds(1).format(pattern);
        } else {
            dateFrom = LocalDate.parse(refDataFixesData.getDateFrom(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().format(pattern);
            dateTo = LocalDate.parse(refDataFixesData.getDateTo(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().plusDays(1).minusSeconds(1).format(pattern);
        }
        return List.of(dateFrom, dateTo);
    }

    private boolean setJudgeName(CaseData caseData, String existingJudgeCode, String requiredJudgeCode,
                                 String dateFrom, String dateTo) {
        boolean judgeChanged = false;
        for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
            HearingType hearingType = hearingTypeItem.getValue();
            if (checkJudge(hearingType.getJudge(), existingJudgeCode)
                && (hearingInDateRange(hearingType, dateFrom, dateTo))) {
                hearingType.setJudge(requiredJudgeCode);
                log.info("Judge changed from " + existingJudgeCode + " to " + requiredJudgeCode);
                judgeChanged = true;
            }
        }
        return judgeChanged;
    }

    private boolean checkJudge(String judge, String existingJudgeCode) {
        return !isNullOrEmpty(judge) && judge.equals(existingJudgeCode);
    }

    private boolean hearingInDateRange(HearingType hearingType, String dateFrom, String dateTo) {
        LocalDate from = LocalDateTime.parse(dateFrom, OLD_DATE_TIME_PATTERN).toLocalDate();
        LocalDate to = LocalDateTime.parse(dateTo, OLD_DATE_TIME_PATTERN).toLocalDate();
        return hearingType.getHearingDateCollection().stream()
                .anyMatch(hearingDateItem -> isHearingWithinFilterRange(hearingDateItem, from, to)
                );
    }

    private static boolean isHearingWithinFilterRange(DateListedTypeItem hearingDateItem, LocalDate from,
                                                      LocalDate to) {
        LocalDate listedDate = LocalDateTime.parse(hearingDateItem.getValue().getListedDate(),
                OLD_DATE_TIME_PATTERN).toLocalDate();
        return (listedDate.isAfter(from) || listedDate.equals(from))
               && (listedDate.isBefore(to) || listedDate.equals(to));
    }

    /**
     * This method does not return anything. Initializes AdminData to null values
     * to not show any existing values for update of judge codes
     *
     * @param adminData AdminData which is a generic data type for most of the
     *                  methods which holds judge code, dates
     *                  and tribunal office
     */
    public void initAdminData(AdminData adminData) {
        adminData.setDate(null);
        adminData.setDateFrom(null);
        adminData.setDateTo(null);
        adminData.setHearingDateType(null);
        adminData.setExistingJudgeCode(null);
        adminData.setRequiredJudgeCode(null);
    }

    public AdminData insertClaimServedDate(AdminDetails adminDetails,
                                           String authToken,
                                           RefDataFixesCcdDataSource dataSource,
                                           List<String> errors) {
        AdminData adminData = adminDetails.getCaseData();
        String caseTypeId = getTribunalOffice(adminData.getTribunalOffice());
        List<String> dates = getDateRangeForSearch(adminDetails, OLD_DATE_TIME_PATTERN);
        String dateFrom = dates.get(0);
        String dateTo = dates.get(1);
        try {
            List<SubmitEvent> submitEvents =
                    dataSource.getDataForInsertClaimDate(caseTypeId, dateFrom, dateTo, ccdClient);
            if (CollectionUtils.isNotEmpty(submitEvents)) {
                log.info("Cases Searched for inserting claim served date: " + submitEvents.size());
                for (SubmitEvent submitEvent : submitEvents) {
                    insertClaimServedDateInSingleCase(submitEvent, authToken, caseTypeId, adminDetails, errors);
                }
                log.info(String.format("Inserting Claim served date is completed in cases with receipt "
                                       + "date from %s to %s", dateFrom, dateTo));
            }
            return adminData;

        } catch (Exception ex) {
            errors.add(ex.getMessage());
            log.error(MESSAGE + adminDetails.getCaseId(), ex);
            return null;
        }
    }

    private void insertClaimServedDateInSingleCase(SubmitEvent submitEvent,
                                       String authToken,
                                       String caseTypeId,
                                       AdminDetails adminDetails, List<String> errors) {
        try {
            List<CaseEventDetail> caseEventDetails = ccdClient.retrieveCaseEventDetails(authToken,
                    getTribunalOffice(adminDetails.getCaseData().getTribunalOffice()),
                    adminDetails.getJurisdiction(), String.valueOf(submitEvent.getCaseId()));
            if (CollectionUtils.isNotEmpty(caseEventDetails)) {
                List<CaseEventDetail> generateCorrespondenceEvents = caseEventDetails
                        .stream().filter(i -> i.getId().equals(
                                GENERATE_CORRESPONDENCE)
                                && i.getStateId() != null
                                && i.getStateId().equals(ACCEPTED_STATE)).toList();
                if (CollectionUtils.isNotEmpty(generateCorrespondenceEvents)) {
                    setClaimServedDate(generateCorrespondenceEvents, submitEvent, authToken, caseTypeId, adminDetails);
                } else {
                    log.error("There was no generateCorrespondence event for case: " + adminDetails.getCaseId());
                }
            }
        } catch (Exception ex) {
            errors.add(ex.getMessage());
            log.error(MESSAGE + adminDetails.getCaseId(), ex);
        }

    }

    private void setClaimServedDate(List<CaseEventDetail> generateCorrespondenceEvents,
                                         SubmitEvent submitEvent,
                                         String authToken,
                                         String caseTypeId,
                                         AdminDetails adminDetails) throws IOException {
        LocalDate claimServedDate;
        CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, caseTypeId,
                adminDetails.getJurisdiction(), String.valueOf(submitEvent.getCaseId()));
        claimServedDate = generateCorrespondenceEvents.stream()
                .sorted(CaseEventDetail::comparedTo)
                .toList()
                .get(0).getCreatedDate().toLocalDate();
        CaseData returnedRequestCaseData = returnedRequest.getCaseDetails().getCaseData();
        if (returnedRequestCaseData.getClaimServedDate() == null) {
            returnedRequestCaseData.setClaimServedDate(claimServedDate.toString());
        }

        ccdClient.submitEventForCase(authToken, returnedRequestCaseData, caseTypeId,
                adminDetails.getJurisdiction(), returnedRequest, String.valueOf(submitEvent.getCaseId()));

    }
}


