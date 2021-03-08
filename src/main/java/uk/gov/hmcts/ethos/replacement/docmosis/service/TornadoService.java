package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.bulk.BulkData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceScotType;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceType;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ethos.replacement.docmosis.config.TornadoConfiguration;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.*;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static java.net.HttpURLConnection.HTTP_OK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OUTPUT_FILE_NAME;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.VENUE_ADDRESS_VALUES_FILE_PATH;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.DocumentManagementService.APPLICATION_DOCX_VALUE;

@Slf4j
@Service("tornadoService")
@RequiredArgsConstructor
public class TornadoService {

    private static final String VENUE_ADDRESS_INPUT_STREAM_ERROR = "Failed to get an inputStream for the venueAddressValues.xlsx file : ---> ";

    private final TornadoConfiguration tornadoConfiguration;
    private final DocumentManagementService documentManagementService;
    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;
    private final UserService userService;

    public DocumentInfo documentGeneration(String authToken, CaseData caseData, String caseTypeId,
                                           CorrespondenceType correspondenceType,
                                           CorrespondenceScotType correspondenceScotType,
                                           MultipleData multipleData) throws IOException {
        HttpURLConnection conn = null;
        DocumentInfo documentInfo = new DocumentInfo();
        try {
            conn = createConnection();
            log.info("Connected");
            UserDetails userDetails = userService.getUserDetails(authToken);
            String documentName = Helper.getDocumentName(correspondenceType, correspondenceScotType);
            buildInstruction(conn, caseData, userDetails, caseTypeId, correspondenceType, correspondenceScotType, multipleData);
            documentInfo = checkResponseStatus(authToken, conn, documentName);
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

    private void buildInstruction(HttpURLConnection conn, CaseData caseData, UserDetails userDetails,
                                  String caseTypeId, CorrespondenceType correspondenceType,
                                  CorrespondenceScotType correspondenceScotType,
                                  MultipleData multipleData) {

        try (InputStream venueAddressInputStream = getClass().getClassLoader().getResourceAsStream(VENUE_ADDRESS_VALUES_FILE_PATH)) {
            StringBuilder sb = DocumentHelper.buildDocumentContent(caseData, tornadoConfiguration.getAccessKey(),
                    userDetails, caseTypeId, venueAddressInputStream, correspondenceType,
                    correspondenceScotType, multipleData);
            log.info("Sending request: " + sb.toString());
            // send the instruction in UTF-8 encoding so that most character sets are available
            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
            os.write(sb.toString());
            os.flush();
        } catch (Exception ex) {
            log.error(VENUE_ADDRESS_INPUT_STREAM_ERROR + ex.getMessage());
        }
    }

    private byte[] getBytesFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }

    private DocumentInfo createDocument(String authToken, HttpURLConnection conn, String documentName) throws IOException {
        URI documentSelfPath = documentManagementService.uploadDocument(authToken, getBytesFromInputStream(conn.getInputStream()),
                OUTPUT_FILE_NAME, APPLICATION_DOCX_VALUE);
        log.info("URI documentSelfPath uploaded and created: " + documentSelfPath.toString());
        return generateDocumentInfo(documentName,
                documentSelfPath,
                documentManagementService.generateMarkupDocument(documentManagementService.generateDownloadableURL(documentSelfPath)));
    }

    private DocumentInfo generateDocumentInfo(String documentName, URI documentSelfPath, String markupURL) {
        log.info("MarkupURL: "+markupURL);
        return DocumentInfo.builder()
                .type(SignificantItemType.DOCUMENT.name())
                .description(documentName)
                .markUp(markupURL)
                .url(ccdGatewayBaseUrl + documentSelfPath.getRawPath() + "/binary")
                .build();
    }

    private DocumentInfo checkResponseStatus(String authToken, HttpURLConnection conn, String documentName) throws IOException {
        DocumentInfo documentInfo = new DocumentInfo();
        int status = conn.getResponseCode();
        if (status == HTTP_OK) {
            documentInfo = createDocument(authToken, conn, documentName);
        } else {
            log.error("message:" + conn.getResponseMessage());
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String msg;
            while ((msg = errorReader.readLine()) != null) {
                log.error(msg);
            }
        }
        return documentInfo;
    }

    DocumentInfo listingGeneration(String authToken, ListingData listingData, String caseType) throws IOException {
        HttpURLConnection conn = null;
        DocumentInfo documentInfo = new DocumentInfo();
        try {
            conn = createConnection();
            log.info("Connected");
            UserDetails userDetails = userService.getUserDetails(authToken);
            String documentName = ListingHelper.getListingDocName(listingData);
            buildListingInstruction(conn, listingData, documentName, userDetails, caseType);
            documentInfo = checkResponseStatus(authToken, conn, documentName);
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

    private void buildListingInstruction(HttpURLConnection conn, ListingData listingData, String documentName, UserDetails userDetails, String caseType) throws IOException {
        StringBuilder sb = ListingHelper.buildListingDocumentContent(listingData, tornadoConfiguration.getAccessKey(), documentName, userDetails, caseType);
        //log.info("Sending request: " + sb.toString());
        // send the instruction in UTF-8 encoding so that most character sets are available
        OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
        os.write(sb.toString());
        os.flush();
    }

    DocumentInfo scheduleGeneration(String authToken, BulkData bulkData) throws IOException {
        HttpURLConnection conn = null;
        DocumentInfo documentInfo = new DocumentInfo();
        try {
            conn = createConnection();
            log.info("Connected");
            String documentName = BulkHelper.getScheduleDocName(bulkData.getScheduleDocName());
            buildScheduleInstruction(conn, bulkData);
            documentInfo = checkResponseStatus(authToken, conn, documentName);
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

    private void buildScheduleInstruction(HttpURLConnection conn, BulkData bulkData) throws IOException {
        StringBuilder sb = BulkHelper.buildScheduleDocumentContent(bulkData, tornadoConfiguration.getAccessKey());
        //log.info("Sending request: " + sb.toString());
        // send the instruction in UTF-8 encoding so that most character sets are available
        OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
        os.write(sb.toString());
        os.flush();
    }

}
