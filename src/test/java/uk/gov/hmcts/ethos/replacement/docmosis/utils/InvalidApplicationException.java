package uk.gov.hmcts.ethos.replacement.docmosis.utils;

class InvalidApplicationException extends RuntimeException {
    InvalidApplicationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
