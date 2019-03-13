package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.SetUpUtils.STATUS_CODE;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.SetUpUtils.feignError;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    public void handleFeignException() {
        ResponseEntity<Object> actual = exceptionHandler.handleFeignException(feignError());
        assertThat(actual.getStatusCodeValue(), Matchers.is(STATUS_CODE));
    }
}