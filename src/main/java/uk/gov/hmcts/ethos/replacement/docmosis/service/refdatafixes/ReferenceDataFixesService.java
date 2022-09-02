package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes;

import java.time.LocalDate;
import java.util.List;
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
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.AdminData;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.AdminDetails;

@Slf4j
@RequiredArgsConstructor
@Service("referenceDataFixesService")
public class ReferenceDataFixesService {
    private static final String CASES_SEARCHED = "Cases searched: ";
    private static final String MESSAGE = "Failed to retrieve reference data for case id : ";
    private final CcdClient ccdClient;

    public AdminData updateJudgesItcoReferences(AdminDetails adminDetails, String authToken, RefDataFixesCcdDataSource dataSource) {

        AdminData adminData = adminDetails.getCaseData();
        String existingJudgeCode = adminData.getExistingJudgeCode();
        String requiredJudgeCode = adminData.getRequiredJudgeCode();
        String caseTypeId = adminData.getTribunalOffice();
        List<String> dates = getDateRangeForSearch(adminDetails);
        String dateFrom = dates.get(0);
        String dateTo = dates.get(1);
        try {
            List<SubmitEvent> submitEvents = dataSource.getData(caseTypeId, dateFrom, dateTo, ccdClient);
            if (CollectionUtils.isNotEmpty(submitEvents)) {
                log.info(CASES_SEARCHED + submitEvents.size());
                for (SubmitEvent submitEvent : submitEvents) {
                    CaseData caseData = submitEvent.getCaseData();
                    setJudgeName(caseData, existingJudgeCode, requiredJudgeCode);
                    CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, caseTypeId,
                            adminDetails.getJurisdiction(), String.valueOf(submitEvent.getCaseId()));
                    ccdClient.submitEventForCase(authToken, caseData, caseTypeId,
                            adminDetails.getJurisdiction(), returnedRequest, String.valueOf(submitEvent.getCaseId()));
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

    private List<String> getDateRangeForSearch(AdminDetails adminDetails) {
        var refDataFixesData = adminDetails.getCaseData();
        boolean isRangeHearingDateType = refDataFixesData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        String dateFrom;
        String dateTo;
        if (!isRangeHearingDateType) {
            dateFrom = LocalDate.parse(refDataFixesData.getDate(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().format(OLD_DATE_TIME_PATTERN);
            dateTo = LocalDate.parse(refDataFixesData.getDate(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().plusDays(1).minusSeconds(1).format(OLD_DATE_TIME_PATTERN);
        } else {
            dateFrom = LocalDate.parse(refDataFixesData.getDateFrom(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().format(OLD_DATE_TIME_PATTERN);
            dateTo = LocalDate.parse(refDataFixesData.getDateTo(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().plusDays(1).minusSeconds(1).format(OLD_DATE_TIME_PATTERN);
        }
        return List.of(dateFrom, dateTo);
    }

    private void setJudgeName(CaseData caseData, String existingJudgeCode, String requiredJudgeCode) {
        for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
            HearingType hearingType = hearingTypeItem.getValue();
            if (!Strings.isNullOrEmpty(hearingType.getJudge()) && hearingType.getJudge().equals(existingJudgeCode)) {
                hearingType.setJudge(requiredJudgeCode);
                log.info(String.format("Judge's code in Case with ethosReference %s is updated", caseData.getEthosCaseReference()));
            }
        }
    }

    /**
     * This method does not return anything. Initializes AdminData to null values
     * to not show any existing values for update of judge codes
     *
     * @param  adminData  AdminData which is a generic data type for most of the
     *                    methods which holds judge code, dates
     *                    and tribunal office
     */
    public void initAdminData(AdminData adminData) {
        adminData.setDate(null);
        adminData.setDateFrom(null);
        adminData.setDateTo(null);
        adminData.setHearingDateType(null);
        adminData.setExistingJudgeCode(null);
        adminData.setRequiredJudgeCode(null);
    }
}
