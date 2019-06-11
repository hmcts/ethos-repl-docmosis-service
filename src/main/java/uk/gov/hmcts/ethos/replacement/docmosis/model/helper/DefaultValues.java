package uk.gov.hmcts.ethos.replacement.docmosis.model.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DefaultValues {
    private String positionType;
    private String claimantTypeOfClaimant;
    private String tribunalCorrespondenceAddress;
    private String tribunalCorrespondenceTelephone;
    private String tribunalCorrespondenceFax;
    private String tribunalCorrespondenceDX;
    private String tribunalCorrespondenceEmail;
}
