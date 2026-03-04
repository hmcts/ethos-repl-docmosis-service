package uk.gov.hmcts.ethos.replacement.docmosis.domain.messagequeue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class QueueMessageStatusTest {

    @Test
    void shouldContainExpectedStatusValues() {
        QueueMessageStatus[] values = QueueMessageStatus.values();
        assertEquals(4, values.length);
        assertNotNull(QueueMessageStatus.valueOf("PENDING"));
        assertNotNull(QueueMessageStatus.valueOf("PROCESSING"));
        assertNotNull(QueueMessageStatus.valueOf("COMPLETED"));
        assertNotNull(QueueMessageStatus.valueOf("FAILED"));
    }
}
