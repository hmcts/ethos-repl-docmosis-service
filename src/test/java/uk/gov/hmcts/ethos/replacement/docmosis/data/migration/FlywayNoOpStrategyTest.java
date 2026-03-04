package uk.gov.hmcts.ethos.replacement.docmosis.data.migration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.MigrationState;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ethos.replacement.docmosis.data.exception.PendingMigrationScriptException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FlywayNoOpStrategyTest {

    private final FlywayNoOpStrategy strategy = new FlywayNoOpStrategy();

    @Test
    void shouldThrowWhenThereArePendingMigrations() {
        Flyway flyway = mock(Flyway.class);
        MigrationInfoService infoService = mock(MigrationInfoService.class);
        MigrationInfo pendingMigration = mock(MigrationInfo.class);

        when(flyway.info()).thenReturn(infoService);
        when(infoService.all()).thenReturn(new MigrationInfo[]{pendingMigration});
        when(pendingMigration.getState()).thenReturn(MigrationState.PENDING);
        when(pendingMigration.getScript()).thenReturn("V1__CreateEcmQueueTables.sql");

        assertThrows(PendingMigrationScriptException.class, () -> strategy.migrate(flyway));
    }

    @Test
    void shouldNotThrowWhenAllMigrationsAreApplied() {
        Flyway flyway = mock(Flyway.class);
        MigrationInfoService infoService = mock(MigrationInfoService.class);
        MigrationInfo appliedMigration = mock(MigrationInfo.class);

        when(flyway.info()).thenReturn(infoService);
        when(infoService.all()).thenReturn(new MigrationInfo[]{appliedMigration});
        when(appliedMigration.getState()).thenReturn(MigrationState.SUCCESS);

        assertDoesNotThrow(() -> strategy.migrate(flyway));
    }
}
