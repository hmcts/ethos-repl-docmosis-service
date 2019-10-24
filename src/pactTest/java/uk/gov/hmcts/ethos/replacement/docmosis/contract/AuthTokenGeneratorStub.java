package uk.gov.hmcts.ethos.replacement.docmosis.contract;

import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

public class AuthTokenGeneratorStub implements AuthTokenGenerator {
    @Override
    public String generate() {
        return "authToken";
    }
}
