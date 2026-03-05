package uk.gov.hmcts.ethos.replacement.docmosis.data.migration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import uk.gov.hmcts.ethos.replacement.docmosis.data.exception.PendingMigrationScriptException;

import java.util.stream.Stream;

public class FlywayNoOpStrategy implements FlywayMigrationStrategy {
    private static final String BASELINE_SCRIPT_PREFIX = "V000__";

    @Override
    public void migrate(Flyway flyway) {
        Stream.of(flyway.info().all())
            .filter(info -> !info.getState().isApplied())
            .filter(info -> !isBaselineMigration(info))
            .findFirst()
            .ifPresent(info -> {
                throw new PendingMigrationScriptException(info.getScript());
            });
    }

    private boolean isBaselineMigration(MigrationInfo migrationInfo) {
        return migrationInfo.getScript() != null
            && migrationInfo.getScript().startsWith(BASELINE_SCRIPT_PREFIX);
    }
}
