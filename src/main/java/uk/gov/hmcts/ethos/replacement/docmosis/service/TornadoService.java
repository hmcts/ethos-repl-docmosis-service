package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.config.TornadoConfiguration;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.SignificantItemType;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.models.UserDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.DocumentInfo;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static java.net.HttpURLConnection.HTTP_OK;

@Slf4j
@Service("tornadoService")
@RequiredArgsConstructor
public class TornadoService {

    private final TornadoConfiguration tornadoConfiguration;
    private final DocumentManagementService documentManagementService;
    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;
    private final UserService userService;

    DocumentInfo documentGeneration(String authToken, CaseData caseData) throws IOException {
        HttpURLConnection conn = null;
        DocumentInfo documentInfo = new DocumentInfo();
        try {
            conn = createConnection();
            log.info("Connected");
            UserDetails userDetails = userService.getUserDetails(authToken);
            log.info("Coming to build instruction");
            buildInstruction(conn, caseData, userDetails);
            log.info("End build instruction");
            int status = conn.getResponseCode();
            if (status == HTTP_OK) {
                log.info("HTTP_OK");
                documentInfo = createDocument(authToken, conn, caseData);
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
            log.error("Unable to connect to Docmosis: " + e.getMessage());
            log.error("If you have a proxy, you will need the Proxy aware example code.");
            System.exit(2);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return documentInfo;
    }

    private HttpURLConnection createConnection() throws IOException {
        String tornadoURL = tornadoConfiguration.getUrl();
        log.info("TORNADO URL: " + tornadoURL);
        HttpURLConnection conn = (HttpURLConnection) new URL(tornadoURL).openConnection();
        log.info("Connecting [directly] to " + tornadoURL);
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.connect();
        return conn;
    }

    private void buildInstruction(HttpURLConnection conn, CaseData caseData, UserDetails userDetails) throws IOException {
        StringBuilder sb = Helper.buildDocumentContent(caseData, tornadoConfiguration.getAccessKey(), userDetails);
        //log.info("Sending request: " + sb.toString());
        // send the instruction in UTF-8 encoding so that most character sets are available
        log.info("Creating outputstreamwriter");
        OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
        log.info("Writing toString");
        os.write(sb.toString());
        log.info("Flushing");
        os.flush();
    }

    private byte[] getBytesFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }

    private DocumentInfo createDocument(String authToken, HttpURLConnection conn, CaseData caseData) throws IOException {
        log.info("Create document");
        URI documentSelfPath = documentManagementService.uploadDocument(authToken, getBytesFromInputStream(conn.getInputStream()));
        log.info("URI documentSelfPath uploaded and created: " + documentSelfPath.toString());
        return generateDocumentInfo(caseData,
                documentSelfPath,
                documentManagementService.generateMarkupDocument(documentManagementService.generateDownloadableURL(documentSelfPath)));
    }

    private DocumentInfo generateDocumentInfo(CaseData caseData, URI documentSelfPath, String markupURL) {
        log.info("MarkupURL: "+markupURL);
        return DocumentInfo.builder()
                .type(SignificantItemType.DOCUMENT.name())
                .description(Helper.getDocumentName(caseData))
                .markUp(markupURL)
                .url(ccdGatewayBaseUrl + documentSelfPath.getRawPath() + "/binary")
                .build();
    }
}
