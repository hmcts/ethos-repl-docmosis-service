package uk.gov.hmcts.ethos.replacement.docmosis.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalException extends RuntimeException {

    public static final String ERROR_MESSAGE = "Internal Server Exception";

    public InternalException(String message) {
        super(message);
    }
}
