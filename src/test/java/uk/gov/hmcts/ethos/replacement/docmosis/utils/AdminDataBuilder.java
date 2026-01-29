package uk.gov.hmcts.ethos.replacement.docmosis.utils;

import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.AdminData;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.AdminDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.referencedata.CCDAdminRequest;

public class AdminDataBuilder {

    private final AdminData adminData = new AdminData();

    public static AdminDataBuilder builder() {
        return new AdminDataBuilder();
    }

    public CCDAdminRequest buildAsCCDRequest() {
        var ccdRequest = new CCDAdminRequest();
        var caseDetails = new AdminDetails();
        caseDetails.setCaseData(adminData);
        ccdRequest.setCaseDetails(caseDetails);
        return ccdRequest;
    }
}
