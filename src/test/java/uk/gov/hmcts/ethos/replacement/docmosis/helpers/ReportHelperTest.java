package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ReportHelperTest {

    @Test
    public void shouldReturnNullForInputWithLengthLessThanTenChars() {
        assertNull(ReportHelper.getFormattedLocalDate("2021-3-2"));
    }

    @Test
    public void shouldReturnNullForUnParseableInput() {
        // When un-parseable input string is supplied, an exception is thrown and handled.
        // After logging an error message, null is returned.
        assertNull(ReportHelper.getFormattedLocalDate("2021-06-25BAD 21:12:12"));
    }

    @Test
    public void shouldReturnLocalDateForValidInput() {
        var inputDateTimeWithMillisecondsAndDateTimeSeparator = "2021-12-12T21:12:12.000";
        var expectedLocalDate = "2021-12-12";
        assertEquals(expectedLocalDate, getActual(inputDateTimeWithMillisecondsAndDateTimeSeparator));

        var inputDateTimeWithOnlyDateTimeSeparator = "2021-10-18T21:12:12";
        var expectedLocalDateTwo = "2021-10-18";
        assertEquals(expectedLocalDateTwo, getActual(inputDateTimeWithOnlyDateTimeSeparator));

        var inputDateTimeWithBlankSpace = "2021-06-25 21:12:12";
        var expectedLocalDateThree = "2021-06-25";
        assertEquals(expectedLocalDateThree, getActual(inputDateTimeWithBlankSpace));
    }

    private String getActual(String inputDateTime) {
       return ReportHelper.getFormattedLocalDate(inputDateTime);
    }
}
