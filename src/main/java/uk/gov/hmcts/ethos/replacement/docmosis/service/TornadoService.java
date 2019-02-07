package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static java.net.HttpURLConnection.HTTP_OK;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.outputFileName;

@Service("tornadoService")
public class TornadoService {

    private static final Logger log = LoggerFactory.getLogger(TornadoService.class);

    //private static final String DWS_RENDER_URL = "http://tornado:8080/rs/render";

    private static final String DWS_RENDER_URL = "https://docmosis-development.platform.hmcts.net/rs/render";

    void documentGeneration(CaseDetails caseDetails, String templateName) throws IOException {
        // Set your access Key if you configure it in Tornado
        //String accessKey = "";
        HttpURLConnection conn = null;
        try {
            conn = createConnection();
            log.info("Connected");
            buildInstruction(conn, caseDetails, templateName);
            int status = conn.getResponseCode();
            if (status == HTTP_OK) {
                createDocument(conn);
            } else {
                log.error("Our call failed: status = " + status);
                log.error("message:" + conn.getResponseMessage());
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String msg;
                while ((msg = errorReader.readLine()) != null) {
                    log.error(msg);
                }
            }
        } catch (ConnectException e) {
            log.error("Unable to connect to Docmosis:" + e.getMessage());
            log.error("If you have a proxy, you will need the Proxy aware example code.");
            System.exit(2);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private HttpURLConnection createConnection() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(DWS_RENDER_URL).openConnection();
        log.info("Connecting [directly] to " + DWS_RENDER_URL);
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.connect();
        return conn;
    }

    private void buildInstruction(HttpURLConnection conn, CaseDetails caseDetails, String templateName) throws IOException {
        StringBuffer sb = Helper.buildDocumentContent(caseDetails, templateName);
        log.info("Sending request:" + sb.toString());
        // send the instruction in UTF-8 encoding so that most character sets are available
        OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
        os.write(sb.toString());
        os.flush();
    }

    private void createDocument(HttpURLConnection conn) throws IOException{
        byte[] buff = new byte[1000];
        int bytesRead;
        File file = new File(outputFileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            while ((bytesRead = conn.getInputStream().read(buff, 0, buff.length)) != -1) {
                fos.write(buff, 0, bytesRead);
            }
        }
        log.info("File created:" + file.getAbsolutePath());
    }

}
