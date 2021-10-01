package uk.gov.hmcts.ethos.replacement.docmosis.utils;

import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;

public class CCDRequestBuilder {

    private CaseData caseData = new CaseData();
    private String state;

    public static CCDRequestBuilder builder() {
        return new CCDRequestBuilder();
    }

    public CCDRequestBuilder withCaseData(CaseData caseData) {
        this.caseData = caseData;
        return this;
    }

    public CCDRequestBuilder withState(String state) {
        this.state = state;
        return this;
    }

    public CCDRequest build() {
        var caseDetails = new CaseDetails();
        caseDetails.setState(state);
        caseDetails.setCaseData(caseData);
        var ccdRequest = new CCDRequest();
        ccdRequest.setCaseDetails(caseDetails);
        return ccdRequest;
    }
}
