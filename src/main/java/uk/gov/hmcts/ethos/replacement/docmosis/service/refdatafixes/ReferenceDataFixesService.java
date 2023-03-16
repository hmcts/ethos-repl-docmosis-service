package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.common.Strings;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.helper.CaseEventDetail;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.AdminData;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.AdminDetails;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReferenceDataFixesService {
    private static final String CASES_SEARCHED = "Cases searched: ";
    private static final String MESSAGE = "Failed to retrieve reference data for case id : ";
    public static final String GENERATE_CORRESPONDENCE = "generateCorrespondence";
    private final CcdClient ccdClient;

    public AdminData updateJudgesItcoReferences(AdminDetails adminDetails, String authToken, RefDataFixesCcdDataSource dataSource) {

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
                    if (setJudgeName(caseData, existingJudgeCode, requiredJudgeCode)) {
                        ccdClient.submitEventForCase(authToken, caseData, caseTypeId,
                                adminDetails.getJurisdiction(), returnedRequest, String.valueOf(submitEvent.getCaseId()));
                    }
                }
                log.info(String.format(
                        "Existing Judge's code in all cases from %s to %s is " +
                                "updated to required judge's code", dateFrom, dateTo));
            }
            return adminData;
        } catch (Exception ex) {
            log.error(MESSAGE + adminDetails.getCaseId(), ex);
            return null;
        }
    }

    private String getTribunalOffice(String tribunalOffice) {
        switch (tribunalOffice) {
            case "London Central":
                return LONDON_CENTRAL_CASE_TYPE_ID;
            case "London East":
                return LONDON_EAST_CASE_TYPE_ID;
            case "London South":
                return LONDON_SOUTH_CASE_TYPE_ID;
            case "Midlands East":
                return MIDLANDS_EAST_CASE_TYPE_ID;
            case "Midlands West":
                return MIDLANDS_WEST_CASE_TYPE_ID;
            default:
                return tribunalOffice;
        }
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

    private boolean setJudgeName(CaseData caseData, String existingJudgeCode, String requiredJudgeCode) {
       boolean judgeChanged = false;
        for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
            HearingType hearingType = hearingTypeItem.getValue();
            if (!Strings.isNullOrEmpty(hearingType.getJudge()) && hearingType.getJudge().equals(existingJudgeCode)) {
                hearingType.setJudge(requiredJudgeCode);
                log.info(String.format("Judge's code in Case with ethosReference %s is updated", caseData.getEthosCaseReference()));
                judgeChanged = true;
            }
        }
        return judgeChanged;
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
            List<SubmitEvent> submitEvents = dataSource.getDataForInsertClaimDate(caseTypeId, dateFrom, dateTo, ccdClient);
            if (CollectionUtils.isNotEmpty(submitEvents)) {
                log.info("Cases Searched for inserting claim served date: " + submitEvents.size());
                for (SubmitEvent submitEvent : submitEvents) {
                    insertClaimServedDateInSingleCase(submitEvent, authToken, caseTypeId, adminDetails, errors);
                }
                log.info(String.format("Inserting Claim served date is completed in cases with receipt " +
                        "date from %s to %s", dateFrom, dateTo));
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
                                && i.getStateId().equals(ACCEPTED_STATE)).collect(Collectors.toList());
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
                .collect(Collectors.toList())
                .get(0).getCreatedDate().toLocalDate();
        CaseData returnedRequestCaseData = returnedRequest.getCaseDetails().getCaseData();
        if (returnedRequestCaseData.getClaimServedDate() == null) {
            returnedRequestCaseData.setClaimServedDate(claimServedDate.toString());
        }

        ccdClient.submitEventForCase(authToken, returnedRequestCaseData, caseTypeId,
                adminDetails.getJurisdiction(), returnedRequest, String.valueOf(submitEvent.getCaseId()));

    }
}


