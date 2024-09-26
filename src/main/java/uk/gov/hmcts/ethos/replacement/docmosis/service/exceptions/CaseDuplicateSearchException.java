package uk.gov.hmcts.ethos.replacement.docmosis.service.exceptions;

public class CaseDuplicateSearchException extends RuntimeException {
    public CaseDuplicateSearchException(String msg, Throwable exception) {
        super(msg, exception);
    }
}
