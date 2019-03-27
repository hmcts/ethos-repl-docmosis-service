package uk.gov.hmcts.ethos.replacement.docmosis.exceptions;

public class CaseCreationException extends RuntimeException {
    public CaseCreationException(String message) {
        super(message);
    }

    public CaseCreationException(String message, Throwable t) {
        super(message, t);
    }
}
