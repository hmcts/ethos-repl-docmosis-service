package uk.gov.hmcts.ethos.replacement.docmosis.reports.casescompleted;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class ReportHeaderValues {

    int casesCompletedHearingTotal;
    int sessionDaysTotal;
    double completedPerSessionTotal;
    String reportOffice;

    int conNoneCasesCompletedHearing;
    int conNoneSessionDays;
    double conNoneCompletedPerSession;

    int conFastCasesCompletedHearing;
    int conFastSessionDays;
    double conFastCompletedPerSession;

    int conStdCasesCompletedHearing;
    int conStdSessionDays;
    double conStdCompletedPerSession;

    int conOpenCasesCompletedHearing;
    int conOpenSessionDays;
    double conOpenCompletedPerSession;
}
