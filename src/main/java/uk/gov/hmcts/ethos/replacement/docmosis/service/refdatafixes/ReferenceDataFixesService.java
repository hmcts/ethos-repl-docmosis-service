package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.common.Strings;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseRetrievalException;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.RefDataFixesData;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.RefDataFixesDetails;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RANGE_HEARING_DATE_TYPE;

@Slf4j
@RequiredArgsConstructor
@Service("referenceDataFixesService")
public class ReferenceDataFixesService {
    private static final String CASES_SEARCHED = "Cases searched: ";
    private static final String MESSAGE = "Failed to retrieve reference data for case id : ";
    private final CcdClient ccdClient;

    public RefDataFixesData updateJudgesItcoReferences(RefDataFixesDetails refDataFixesDetails, String authToken, RefDataFixesCcdDataSource dataSource) {

        RefDataFixesData refDataFixesData = refDataFixesDetails.getCaseData();
        String existingJudgeCode = refDataFixesData.getExistingJudgeCode();
        String requiredJudgeCode = refDataFixesData.getRequiredJudgeCode();
        String caseTypeId = refDataFixesDetails.getCaseTypeId();
        List<String> dates = getDateRangeForSearch(refDataFixesDetails);
        String dateFrom = dates.get(0);
        String dateTo = dates.get(1);
        try {
            List<SubmitEvent> submitEvents = dataSource.getData(caseTypeId, dateFrom, dateTo, ccdClient);
            if (CollectionUtils.isNotEmpty(submitEvents)) {
                log.info(CASES_SEARCHED + submitEvents.size());
                for (SubmitEvent submitEvent : submitEvents) {
                    CaseData caseData = submitEvent.getCaseData();
                    setJudgeName(caseData, existingJudgeCode, requiredJudgeCode);

                    CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, refDataFixesDetails.getCaseTypeId(),
                            refDataFixesDetails.getJurisdiction(), String.valueOf(submitEvent.getCaseId()));
                    ccdClient.submitEventForCase(authToken, caseData, refDataFixesDetails.getCaseTypeId(),
                            refDataFixesDetails.getJurisdiction(), returnedRequest, String.valueOf(submitEvent.getCaseId()));
                }
                log.info(String.format(
                        "Existing Judge's code in all cases from %s to %s is " +
                                "updated to required judge's code", dateFrom, dateTo));
            }
            return refDataFixesData;
        } catch (Exception ex) {
            log.error(MESSAGE + refDataFixesDetails.getCaseId(), ex);
            throw new CaseRetrievalException(MESSAGE + refDataFixesDetails.getCaseId() + ex.getMessage());
        }
    }

    private List<String> getDateRangeForSearch(RefDataFixesDetails refDataFixesDetails) {
        var refDataFixesData = refDataFixesDetails.getCaseData();
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
        return new ArrayList<>(List.of(dateFrom, dateTo));
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

    public RefDataFixesData insertClaimServedDate(RefDataFixesDetails refDataFixesDetails, String authToken) {

        var refDataFixesData = refDataFixesDetails.getCaseData();
        ReportParams params = new ReportParams(
                refDataFixesDetails.getCaseTypeId(),
                refDataFixesData.getDateFrom(),
                refDataFixesData.getDateTo());
        try {
            RefDataFixesCcdDataSource dataSource = new RefDataFixesCcdDataSource(authToken);
//            List<SubmitEvent> submitEvents = dataSource.getData(params);
//            if (CollectionUtils.isNotEmpty(submitEvents)) {
//                log.info(CASES_SEARCHED + submitEvents.size());
//                for (SubmitEvent submitEvent : submitEvents) {
//                    CaseData caseData = submitEvent.getCaseData();
//                }
//
//            }
            return refDataFixesData;
        } catch (Exception ex) {
            throw new CaseRetrievalException(MESSAGE + refDataFixesDetails.getCaseId() + ex.getMessage());
        }
    }

//    public RefDataFixesData insertClaimServedDate(RefDataFixesDetails refDataFixesDetails) {
//        RefDataFixesData refDataFixesData = refDataFixesDetails.getCaseData();
//        claimServedDateFixRepository.addClaimServedDate(Date.valueOf(refDataFixesData.getListingDateFrom()),
//                Date.valueOf(refDataFixesData.getListingDateTo()),refDataFixesDetails.getCaseTypeId());
//        return refDataFixesData;
//    }

}
