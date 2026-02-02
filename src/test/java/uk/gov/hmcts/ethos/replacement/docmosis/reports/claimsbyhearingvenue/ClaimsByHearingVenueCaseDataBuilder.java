package uk.gov.hmcts.ethos.replacement.docmosis.reports.claimsbyhearingvenue;

import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantWorkAddressType;
import uk.gov.hmcts.ecm.common.model.reports.claimsbyhearingvenue.ClaimsByHearingVenueCaseData;
import uk.gov.hmcts.ecm.common.model.reports.claimsbyhearingvenue.ClaimsByHearingVenueSubmitEvent;
import java.util.List;

public class ClaimsByHearingVenueCaseDataBuilder {
    private final ClaimsByHearingVenueCaseData caseData = new ClaimsByHearingVenueCaseData();

    public ClaimsByHearingVenueCaseDataBuilder withEthosCaseReference(String ethosCaseReference) {
        caseData.setEthosCaseReference(ethosCaseReference);
        return this;
    }

    public ClaimsByHearingVenueCaseDataBuilder withReceiptDate(String receiptDate) {
        caseData.setReceiptDate(receiptDate);
        return this;
    }

    public ClaimsByHearingVenueCaseDataBuilder withClaimantType(ClaimantType claimantType) {
        caseData.setClaimantType(claimantType);
        return this;
    }

    public ClaimsByHearingVenueCaseDataBuilder withClaimantWorkAddressType(
        ClaimantWorkAddressType claimantWorkAddressType) {
        caseData.setClaimantWorkAddressType(claimantWorkAddressType);
        return this;
    }

    public ClaimsByHearingVenueCaseDataBuilder withRespondentCollection(
        List<RespondentSumTypeItem> respondentCollection) {
        if (respondentCollection != null) {
            caseData.setRespondentCollection(respondentCollection);
        }
        return this;
    }

    public ClaimsByHearingVenueCaseData build() {
        return caseData;
    }

    public ClaimsByHearingVenueSubmitEvent buildAsSubmitEvent(String state) {
        var submitEvent = new ClaimsByHearingVenueSubmitEvent();
        submitEvent.setCaseData(caseData);
        submitEvent.setState(state);
        return submitEvent;
    }
}
