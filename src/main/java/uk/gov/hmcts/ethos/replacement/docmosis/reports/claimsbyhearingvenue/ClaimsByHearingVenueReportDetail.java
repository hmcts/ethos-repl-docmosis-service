package uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ClaimsByHearingVenueReportDetail {
    @JsonProperty("caseReference")
    private String caseReference;
    @JsonProperty("dateOfReceipt")
    private String dateOfReceipt;
    @JsonProperty("claimantPostcode")
    private String claimantPostcode;
    @JsonProperty("claimantWorkPostcode")
    private String claimantWorkPostcode;
    @JsonProperty("respondentPostcode")
    private String respondentPostcode;
    @JsonProperty("respondentET3Postcode")
    private String respondentET3Postcode;
    @JsonProperty("managingOffice")
    private String managingOffice;
}
