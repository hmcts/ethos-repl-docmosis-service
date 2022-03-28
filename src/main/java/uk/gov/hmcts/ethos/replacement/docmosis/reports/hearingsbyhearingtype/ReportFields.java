package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ReportFields {
    private String cmCount;
    private String costsCount;
    private String remedyCount;
    private String reconsiderCount;
    private String hearingPrelimCount;
    private String hearingCount;
    private String total;
}
