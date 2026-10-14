package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

/**
 * Utility class that can be used to filter document data before submission to the Tornado service.
 */
public final class TornadoDocumentFilter {

    private TornadoDocumentFilter() {
        // All access through static methods
    }

    /**
     * Escapes backslashes, and writes a newline inside a value as a JSON line break.
     * Newlines between fields are left unchanged because they structure the document.
     * @param json Tornado document data in Json format
     * @return Json string that can be submitted to Tornado
     */
    public static String filterJson(String json) {
        return escapeBackslash(json);
    }

    private static String escapeBackslash(String value) {
        StringBuilder escaped = new StringBuilder(value.length());
        boolean insideString = false;
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            if (current == '"') {
                insideString = !insideString;
                escaped.append(current);
            } else if (insideString && current == '\\') {
                escaped.append('\\');
                escaped.append('\\');
            } else if (insideString && current == '\n') {
                escaped.append('\\');
                escaped.append('n');
            } else if (insideString && current == '\r') {
                escaped.append('\\');
                escaped.append('r');
            } else {
                escaped.append(current);
            }
        }
        return escaped.toString();
    }
}
