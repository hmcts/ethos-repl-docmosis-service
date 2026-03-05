package uk.gov.hmcts.ethos.replacement.docmosis.data.migration;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;

public class FlywayNoOpStrategy implements FlywayMigrationStrategy {

    @Override
    public void migrate(Flyway flyway) {
        // Intentionally no-op when RUN_DB_MIGRATION_ON_STARTUP=false.
    }
}
