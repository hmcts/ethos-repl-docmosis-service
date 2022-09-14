package uk.gov.hmcts.ethos.replacement.docmosis.reports;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ReportParams {
    private final String caseTypeId;
    private final String dateFrom;
    private final String dateTo;
}