package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TornadoDocumentFilterTest {

    @Test
    void testFilterJsonEscapesBackslashCharacter() {
        String json = "{\"respondentCollection\":[{\"value\":{\"respondent_name\":\"Respondent\\WithBlackslash\"}}]}";

        String result = TornadoDocumentFilter.filterJson(json);

        assertEquals(
                "{\"respondentCollection\":[{\"value\":{\"respondent_name\":\"Respondent\\\\WithBlackslash\"}}]}",
                result);
    }

    @Test
    void newlineInsideAnAddressBecomesAJsonLineBreak() {
        String json = "{\"Court_addressLine3\":\"Glasgow Tribunals Centre\n3 Atlantic Quay, 20 York Street\",\n"
                + "\"Court_town\":\"Glasgow\"}";

        String result = TornadoDocumentFilter.filterJson(json);

        assertEquals("{\"Court_addressLine3\":\"Glasgow Tribunals Centre\\n3 Atlantic Quay, "
                + "20 York Street\",\n\"Court_town\":\"Glasgow\"}", result);
    }
}
