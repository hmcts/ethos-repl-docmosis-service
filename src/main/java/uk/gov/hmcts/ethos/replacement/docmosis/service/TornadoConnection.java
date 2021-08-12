package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.ethos.replacement.docmosis.config.TornadoConfiguration;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class TornadoConnection {

    private final TornadoConfiguration tornadoConfiguration;

    public TornadoConnection(TornadoConfiguration tornadoConfiguration) {
        this.tornadoConfiguration = tornadoConfiguration;
    }

    public HttpURLConnection createConnection() throws IOException {
        var tornadoURL = tornadoConfiguration.getUrl();
        var conn = (HttpURLConnection) new URL(tornadoURL).openConnection();
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.connect();

        return conn;
    }

    public String getAccessKey() {
        return tornadoConfiguration.getAccessKey();
    }
}
