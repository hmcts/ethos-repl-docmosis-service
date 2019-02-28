package uk.gov.hmcts.ethos.replacement.docmosis.utils;

import uk.gov.hmcts.ethos.replacement.docmosis.config.JacksonConfiguration;

final class JsonMapperFactory {

    private JsonMapperFactory() {
        // Utility class
    }

    static JsonMapper create() {
        return new JsonMapper(new JacksonConfiguration().objectMapper());
    }
}
