package uk.gov.hmcts.ethos.replacement.docmosis.model.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class DefaultValues {
    private String positionType;
    private String claimantTypeOfClaimant;
    private String tribunalCorrespondenceAddressLine1;
    private String tribunalCorrespondenceAddressLine2;
    private String tribunalCorrespondenceAddressLine3;
    private String tribunalCorrespondenceTown;
    private String tribunalCorrespondencePostCode;
    private String tribunalCorrespondenceTelephone;
    private String tribunalCorrespondenceFax;
    private String tribunalCorrespondenceDX;
    private String tribunalCorrespondenceEmail;
}
