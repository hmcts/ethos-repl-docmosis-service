package uk.gov.hmcts.ethos.replacement.docmosis.data.migration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

class FlywayNoOpStrategyTest {

    private final FlywayNoOpStrategy strategy = new FlywayNoOpStrategy();

    @Test
    void shouldNotThrowWhenThereArePendingMigrations() {
        Flyway flyway = mock(Flyway.class);
        assertDoesNotThrow(() -> strategy.migrate(flyway));
    }
}
