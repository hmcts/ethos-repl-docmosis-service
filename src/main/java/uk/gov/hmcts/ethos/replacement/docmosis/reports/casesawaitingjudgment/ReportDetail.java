package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import lombok.Data;

@Data
public class ReportDetail {

    static final String NO_MULTIPLE_REFERENCE = "0/0";

    private String positionType;
    private long weeksSinceHearing;
    private long daysSinceHearing;
    private String caseNumber;
    private String multipleReference;
    private String lastHeardHearingDate;
    private String hearingNumber;
    private String hearingType;
    private String judge;
    private String currentPosition;
    private String dateToPosition;
    private String conciliationTrack;
}
