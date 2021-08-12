package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Test;
import uk.gov.hmcts.ethos.replacement.docmosis.config.TornadoConfiguration;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.MockHttpURLConnectionFactory;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TornadoConnectionTest {

    @Test
    public void shouldCreateConnection() throws IOException {
        var url = "http://tornadotest";
        var tornadoConfiguration = new TornadoConfiguration();
        tornadoConfiguration.setUrl(url);
        var mockConnection = MockHttpURLConnectionFactory.create(url);

        var tornadoConnection = new TornadoConnection(tornadoConfiguration);
        var connection = tornadoConnection.createConnection();

        assertEquals(mockConnection, connection);
        verify(mockConnection, times(1)).connect();
    }

    @Test
    public void shouldReturnAccessKey() {
        var accessKey = "test-access-key";
        var tornadoConfiguration = new TornadoConfiguration();
        tornadoConfiguration.setAccessKey(accessKey);

        var tornadoConnection = new TornadoConnection(tornadoConfiguration);
        var actualAccessKey = tornadoConnection.getAccessKey();

        assertEquals(actualAccessKey, accessKey);
    }
}
