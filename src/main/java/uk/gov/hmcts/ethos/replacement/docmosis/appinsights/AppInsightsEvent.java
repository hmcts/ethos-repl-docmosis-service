package uk.gov.hmcts.ethos.replacement.docmosis.appinsights;

public enum AppInsightsEvent {

    DOCUMENT_MANAGEMENT_UPLOAD_FAILURE("Document management upload - failure");

    private String displayName;

    AppInsightsEvent(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
