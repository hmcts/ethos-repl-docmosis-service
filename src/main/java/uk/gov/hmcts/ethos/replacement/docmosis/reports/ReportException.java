package uk.gov.hmcts.ethos.replacement.docmosis.reports;

public class ReportException extends RuntimeException {
    public ReportException(String message, Throwable cause) {
        super(message, cause);
    }
}
