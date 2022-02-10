package uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport;

import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.reports.respondentsreport.RespondentsReportCaseData;
import uk.gov.hmcts.ecm.common.model.reports.respondentsreport.RespondentsReportSubmitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RespondentsReport {

    private final RespondentsReportDataSource reportDataSource;
    private final String listingDateFrom;
    private final String listingDateTo;

    public RespondentsReport(RespondentsReportDataSource reportDataSource,
                             String listingDateFrom, String listingDateTo) {
        this.reportDataSource = reportDataSource;
        this.listingDateFrom = listingDateFrom;
        this.listingDateTo = listingDateTo;
    }

    public RespondentsReportData generateReport(String caseTypeId) {

        var submitEvents = getCases(caseTypeId, listingDateFrom, listingDateTo);
        var reportData = initReport(caseTypeId);

        if (CollectionUtils.isNotEmpty(submitEvents)) {
            executeReport(reportData, submitEvents);
        }
        return reportData;
    }

    private RespondentsReportData initReport(String caseTypeId) {
        var office = UtilHelper.getListingCaseTypeId(caseTypeId);
        var reportSummary = new RespondentsReportSummary();
        reportSummary.setOffice(office);
        return new RespondentsReportData(reportSummary);
    }

    private List<RespondentsReportSubmitEvent> getCases(
            String caseTypeId, String listingDateFrom, String listingDateTo) {
        return reportDataSource.getData(UtilHelper.getListingCaseTypeId(
                caseTypeId), listingDateFrom, listingDateTo);
    }

    private void executeReport(RespondentsReportData respondentReportData,
                               List<RespondentsReportSubmitEvent> submitEvents) {
        var moreThan1Resp =  (int) submitEvents.stream()
                .filter(s -> CollectionUtils.isNotEmpty(s.getCaseData().getRespondentCollection())
               && s.getCaseData().getRespondentCollection().size() > 1).count();

        respondentReportData.getReportSummary().setTotalCasesWithMoreThanOneRespondent(String.valueOf(moreThan1Resp));
        respondentReportData.addReportDetail(getReportDetail(submitEvents));

    }

    private List<RespondentsReportDetail> getReportDetail(List<RespondentsReportSubmitEvent> submitEvents) {
        var respondentsReportDetailList = new ArrayList<RespondentsReportDetail>();
        for (RespondentsReportSubmitEvent submitEvent : submitEvents) {
            var caseData = submitEvent.getCaseData();
            if (hasMultipleRespondents(caseData)) {

                for (RespondentSumTypeItem r : caseData.getRespondentCollection()) {
                    RespondentsReportDetail detail = new RespondentsReportDetail();
                    detail.setCaseNumber(caseData.getEthosCaseReference());
                    detail.setRespondentName(r.getValue().getRespondentName());
                    var rep = getRepresentative(r.getValue().getRespondentName(), caseData);
                    detail.setRepresentativeName(rep);
                    detail.setRepresentativeHasMoreThanOneRespondent(
                        isRepresentativeRepresentingMoreThanOneRespondent(rep, caseData) ? "Y" : "N");
                    respondentsReportDetailList.add(detail);
                }
            }
        }
        return respondentsReportDetailList;
    }

    private boolean hasMultipleRespondents(RespondentsReportCaseData caseData) {
        if (CollectionUtils.isNotEmpty(caseData.getRespondentCollection())
                && caseData.getRespondentCollection().size() > 1) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isRepresentativeRepresentingMoreThanOneRespondent(String rep, RespondentsReportCaseData caseData) {
        var count = 0;
        if (CollectionUtils.isNotEmpty(caseData.getRepCollection())) {
            for (RepresentedTypeRItem repItem : caseData.getRepCollection()) {
                if (repItem.getValue().getNameOfRepresentative().equals(rep)) {
                    for (RespondentSumTypeItem respItem : caseData.getRespondentCollection()) {
                        if (respItem.getValue().getRespondentName().equals(repItem.getValue().getRespRepName())) {
                            count++;
                        }
                    }
                }
            }
        }
        return count > 1;
    }

    private String getRepresentative(String respName, RespondentsReportCaseData caseData) {
        if (CollectionUtils.isNotEmpty(caseData.getRepCollection())) {
            Optional<RepresentedTypeRItem> rep = caseData.getRepCollection().stream()
                    .filter(a -> a.getValue().getRespRepName().equals(respName)).findFirst();
            if (rep.isPresent()) {
                return rep.get().getValue().getNameOfRepresentative();
            }
        }
        return "N/A";
    }
}
