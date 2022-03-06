package uk.gov.hmcts.ethos.replacement.docmosis.reports.eccreport;

import java.util.Comparator;
import joptsimple.internal.Strings;
import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.items.EccCounterClaimTypeItem;
import uk.gov.hmcts.ecm.common.model.reports.eccreport.EccReportSubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;
import java.util.ArrayList;
import java.util.List;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays.SessionDaysReportDetail;

public class EccReport {

    private final EccReportDataSource reportDataSource;
    private String office;

    public EccReport(EccReportDataSource reportDataSource) {
        this.reportDataSource = reportDataSource;
    }

    public EccReportData generateReport(ReportParams params) {

        var submitEvents = getCases(params);
        var reportData = initReport(params.getCaseTypeId());

        if (CollectionUtils.isNotEmpty(submitEvents)) {
            executeReport(reportData, submitEvents);
        }
        return reportData;
    }

    private EccReportData initReport(String caseTypeId) {
        office = UtilHelper.getListingCaseTypeId(caseTypeId);
        return new EccReportData();
    }

    private List<EccReportSubmitEvent> getCases(ReportParams params) {
        return reportDataSource.getData(UtilHelper.getListingCaseTypeId(
                params.getCaseTypeId()), params.getDateFrom(), params.getDateTo());
    }

    private void executeReport(EccReportData eccReportData,
                               List<EccReportSubmitEvent> submitEvents) {
        eccReportData.addReportDetail(getReportDetail(submitEvents));
    }

    private List<EccReportDetail> getReportDetail(List<EccReportSubmitEvent> submitEvents) {
        var eccReportDetailList = new ArrayList<EccReportDetail>();
        for (EccReportSubmitEvent submitEvent : submitEvents) {
            var eccReportDetail = new EccReportDetail();
            var caseData = submitEvent.getCaseData();
            if (CollectionUtils.isNotEmpty(caseData.getEccCases())
                    && CollectionUtils.isNotEmpty(caseData.getRespondentCollection())) {
                eccReportDetail.setState(submitEvent.getState());
                eccReportDetail.setDate(caseData.getReceiptDate());
                eccReportDetail.setOffice(office);
                eccReportDetail.setCaseNumber(caseData.getEthosCaseReference());
                eccReportDetail.setEccCasesCount(String.valueOf(caseData.getEccCases().size()));
                eccReportDetail.setEccCaseList(getEccCases(caseData.getEccCases()));
                eccReportDetail.setRespondentsCount(String.valueOf(caseData.getRespondentCollection().size()));
                eccReportDetailList.add(eccReportDetail);
            }

        }
        eccReportDetailList.sort(Comparator.comparing(EccReportDetail::getCaseNumber));
        return eccReportDetailList;
    }

    private String getEccCases(List<EccCounterClaimTypeItem> eccItems) {
        StringBuilder eccCasesList = new StringBuilder(Strings.EMPTY);
        for (EccCounterClaimTypeItem eccItem : eccItems) {
            eccCasesList.append(eccItem.getValue().getCounterClaim()).append("\n");
        }
        return eccCasesList.toString().trim();
    }

}
