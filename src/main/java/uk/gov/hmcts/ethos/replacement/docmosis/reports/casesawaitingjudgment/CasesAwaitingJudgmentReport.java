package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.helper.Constants;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSED_STATE;

@Service
public class CasesAwaitingJudgmentReport {

    static final Collection<String> VALID_POSITION_TYPES = List.of(
            "Draft with members",
            "Heard awaiting judgment being sent to the parties",
            "Awaiting judgment being sent to the parties, other",
            "Awaiting chairman's notes of evidence",
            "Awaiting draft judgment from chairman",
            "Draft judgment received, awaiting typing",
            "Draft judgment typed, to chairman for amendment",
            "Revised draft received, awaiting typing",
            "Fair copy, to chairman for signature",
            "Signed fair copy received",
            "Judgment photocopied, awaiting being sent to the parties",
            "Awaiting written reasons"
    );

    private final ReportDataSource reportDataSource;

    public CasesAwaitingJudgmentReport(ReportDataSource reportDataSource) {
        this.reportDataSource = reportDataSource;
    }

    public CasesAwaitingJudgmentReportData runReport(ListingData listingData, Collection<String> caseTypeIds, String user) {
        var submitEvents = getCases(caseTypeIds);

        var reportData = initReport(listingData, user);
        populateData(reportData, submitEvents);

        return reportData;
    }

    private CasesAwaitingJudgmentReportData initReport(ListingData listingData, String user) {
        var reportSummary = new ReportSummary(user);
        return new CasesAwaitingJudgmentReportData(listingData, reportSummary);
    }

    private List<SubmitEvent> getCases(Collection<String> caseTypeIds) {
        return reportDataSource.getData(caseTypeIds);
    }

    private void populateData(CasesAwaitingJudgmentReportData reportData, List<SubmitEvent> submitEvents) {
        for (SubmitEvent submitEvent : submitEvents) {
            if (!isValidCase(submitEvent)) {
                continue;
            }

            var reportDetail = new ReportDetail();
            var caseData = submitEvent.getCaseData();
            reportDetail.setPositionType(caseData.getPositionType());
            reportDetail.setCaseNumber(caseData.getEthosCaseReference());
            reportData.addReportDetail(reportDetail);
        }

        addReportSummary(reportData);
    }

    private boolean isValidCase(SubmitEvent submitEvent) {
        if (CLOSED_STATE.equals(submitEvent.getState())) {
            return false;
        }

        var caseData = submitEvent.getCaseData();
        if (!VALID_POSITION_TYPES.contains(caseData.getPositionType())) {
            return false;
        }

        if (!isCaseWithValidHearing(caseData)) {
            return false;
        }

        return isCaseAwaitingJudgment(caseData);
    }

    private boolean isCaseWithValidHearing(CaseData caseData) {
        if (CollectionUtils.isEmpty(caseData.getHearingCollection())) {
            return false;
        }

        for (var hearingTypeItem : caseData.getHearingCollection()) {
            if (isValidHearing(hearingTypeItem)) {
                return true;
            }

        }

        return false;
    }

    private boolean isValidHearing(HearingTypeItem hearingTypeItem) {
        var hearingType = hearingTypeItem.getValue();
        if (hearingType == null || CollectionUtils.isEmpty(hearingType.getHearingDateCollection())) {
            return false;
        }

        for (var dateListedItemType : hearingType.getHearingDateCollection()) {
            if (Constants.HEARING_STATUS_HEARD.equals(dateListedItemType.getValue().getHearingStatus())) {
                return true;
            }
        }

        return false;
    }

    private boolean isCaseAwaitingJudgment(CaseData caseData) {
        return CollectionUtils.isEmpty(caseData.getJudgementCollection());
    }

    private void addReportSummary(CasesAwaitingJudgmentReportData reportData) {
        var positionTypes = new HashMap<String, Integer>();
        reportData.getReportDetails().forEach(rd -> positionTypes.merge(rd.getPositionType(), 1, Integer::sum));

        reportData.getReportSummary().getPositionTypes().putAll(positionTypes);
    }
}
