package uk.gov.hmcts.ethos.replacement.docmosis.data.exception;

public class PendingMigrationScriptException extends RuntimeException {

    public PendingMigrationScriptException(String scriptName) {
        super("Pending migration detected: " + scriptName);
    }
}
