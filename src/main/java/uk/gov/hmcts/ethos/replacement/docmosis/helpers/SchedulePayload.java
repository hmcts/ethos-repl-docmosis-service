package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SchedulePayload {
    private String claimantName;
    private String respondentName;
    private String positionType;
    private String ethosCaseRef;
    private String claimantAddressLine1;
    private String claimantPostCode;
    private String respondentAddressLine1;
    private String respondentPostCode;
}

