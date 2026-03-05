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
    void shouldNotThrowWhenOnlyBaselineMigrationIsPending() {
        Flyway flyway = mock(Flyway.class);
        MigrationInfoService infoService = mock(MigrationInfoService.class);
        MigrationInfo baselinePendingMigration = mock(MigrationInfo.class);

        when(flyway.info()).thenReturn(infoService);
        when(infoService.all()).thenReturn(new MigrationInfo[]{baselinePendingMigration});
        when(baselinePendingMigration.getState()).thenReturn(MigrationState.PENDING);
        when(baselinePendingMigration.getScript()).thenReturn("V000__EthosBaselineSchema.sql");

        assertDoesNotThrow(() -> strategy.migrate(flyway));
    }

    @Test
    void shouldThrowWhenNonBaselineMigrationIsPending() {
        Flyway flyway = mock(Flyway.class);
        MigrationInfoService infoService = mock(MigrationInfoService.class);
        MigrationInfo baselinePendingMigration = mock(MigrationInfo.class);
        MigrationInfo nonBaselinePendingMigration = mock(MigrationInfo.class);

        when(flyway.info()).thenReturn(infoService);
        when(infoService.all()).thenReturn(
            new MigrationInfo[]{baselinePendingMigration, nonBaselinePendingMigration}
        );

        when(baselinePendingMigration.getState()).thenReturn(MigrationState.PENDING);
        when(baselinePendingMigration.getScript()).thenReturn("V000__EthosBaselineSchema.sql");

        when(nonBaselinePendingMigration.getState()).thenReturn(MigrationState.PENDING);
        when(nonBaselinePendingMigration.getScript()).thenReturn("V1__CreateEcmQueueTables.sql");

        assertThrows(PendingMigrationScriptException.class, () -> strategy.migrate(flyway));
    }
}
