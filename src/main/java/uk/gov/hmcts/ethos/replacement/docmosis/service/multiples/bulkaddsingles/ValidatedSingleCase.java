package uk.gov.hmcts.ethos.replacement.docmosis.service.multiples.bulkaddsingles;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
class ValidatedSingleCase {
    private final String ethosReference;
    private boolean valid;
    private String invalidReason;

    static ValidatedSingleCase createValidCase(String ethosReference) {
        var validatedSingleCase = new ValidatedSingleCase(ethosReference);
        validatedSingleCase.valid = true;
        return validatedSingleCase;
    }

    static ValidatedSingleCase createInvalidCase(String ethosReference, String invalidReason) {
        var validatedSingleCase = new ValidatedSingleCase(ethosReference);
        validatedSingleCase.valid = false;
        validatedSingleCase.invalidReason = invalidReason;
        return validatedSingleCase;
    }
}
