package uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RespondentsReportParams {
    private final String caseTypeId;
    private final String dateFrom;
    private final String dateTo;
}