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
import uk.gov.hmcts.ecm.common.model.helper.DefaultValues;
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
import java.util.Arrays;

import static java.net.HttpURLConnection.HTTP_OK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.DocumentManagementService.APPLICATION_DOCX_VALUE;

@Slf4j
@RequiredArgsConstructor
@Service("tornadoService")
public class TornadoService {

    private static final String VENUE_ADDRESS_INPUT_STREAM_ERROR = "Failed to get an inputStream for the "
            + "venueAddressValues.xlsx file : ---> ";

    private final TornadoConfiguration tornadoConfiguration;
    private final DocumentManagementService documentManagementService;
    private final UserService userService;
    private final DefaultValuesReaderService defaultValuesReaderService;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    public DocumentInfo documentGeneration(String authToken, CaseData caseData, String caseTypeId,
                                           CorrespondenceType correspondenceType,
                                           CorrespondenceScotType correspondenceScotType,
                                           MultipleData multipleData) throws IOException {
        HttpURLConnection conn = null;
        OutputStreamWriter outputStreamWriter = null;
        ByteArrayOutputStream os = null;
        DocumentInfo documentInfo = new DocumentInfo();
        try {
            conn = createConnection();
            log.info("Connected");
            UserDetails userDetails = userService.getUserDetails(authToken);
            String documentName = Helper.getDocumentName(correspondenceType, correspondenceScotType);
            outputStreamWriter = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
            buildInstruction(outputStreamWriter, caseData, userDetails, caseTypeId,
                    correspondenceType, correspondenceScotType, multipleData);
            os = new ByteArrayOutputStream();
            documentInfo = checkResponseStatus(authToken, conn, documentName, os);
        } catch (ConnectException e) {
            log.error("Unable to connect to Docmosis: " + e.getMessage());
            log.error("If you have a proxy, you will need the Proxy aware example code.");
            System.exit(2);
        } finally {
            releaseResources(conn, outputStreamWriter, os);
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

    private boolean isAllocatedOffice(String caseTypeId, CorrespondenceScotType correspondenceScotType) {
        return caseTypeId.equals(SCOTLAND_CASE_TYPE_ID)
                && correspondenceScotType != null
                && correspondenceScotType.getLetterAddress().equals(LETTER_ADDRESS_ALLOCATED_OFFICE);
    }

    private DefaultValues getAllocatedCourtAddress(CaseData caseData, String caseTypeId, MultipleData multipleData) {
        if ((multipleData != null && isAllocatedOffice(caseTypeId, multipleData.getCorrespondenceScotType()))
                || isAllocatedOffice(caseTypeId, caseData.getCorrespondenceScotType())) {
            return defaultValuesReaderService.getDefaultValues(caseData.getAllocatedOffice(), caseTypeId);
        }
        return null;
    }

    private void buildInstruction(OutputStreamWriter outputStreamWriter, CaseData caseData, UserDetails userDetails,
                                  String caseTypeId, CorrespondenceType correspondenceType,
                                  CorrespondenceScotType correspondenceScotType,
                                  MultipleData multipleData) {

        try (InputStream venueAddressInputStream = getClass().getClassLoader()
                .getResourceAsStream(VENUE_ADDRESS_VALUES_FILE_PATH)) {
            DefaultValues allocatedCourtAddress = getAllocatedCourtAddress(caseData, caseTypeId, multipleData);
            writeOutputStream(outputStreamWriter, DocumentHelper.buildDocumentContent(caseData,
                tornadoConfiguration.getAccessKey(),
                    userDetails, caseTypeId, venueAddressInputStream, correspondenceType,
                    correspondenceScotType, multipleData, allocatedCourtAddress));
        } catch (Exception ex) {
            log.error(VENUE_ADDRESS_INPUT_STREAM_ERROR + ex.getMessage());
        }
    }

    private DocumentInfo generateDocumentInfo(String documentName, URI documentSelfPath, String markupURL) {
        log.info("MarkupURL: " + markupURL);
        return DocumentInfo.builder()
                .type(SignificantItemType.DOCUMENT.name())
                .description(documentName)
                .markUp(markupURL)
                .url(ccdGatewayBaseUrl + documentSelfPath.getRawPath() + "/binary")
                .build();
    }

    DocumentInfo listingGeneration(String authToken, ListingData listingData, String caseType) throws IOException {
        HttpURLConnection conn = null;
        OutputStreamWriter outputStreamWriter = null;
        ByteArrayOutputStream os = null;
        DocumentInfo documentInfo = new DocumentInfo();
        try {
            conn = createConnection();
            log.info("Connected");
            UserDetails userDetails = userService.getUserDetails(authToken);
            String documentName = ListingHelper.getListingDocName(listingData);
            outputStreamWriter = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
            buildListingInstruction(outputStreamWriter, listingData, documentName, userDetails, caseType);
            os = new ByteArrayOutputStream();
            documentInfo = checkResponseStatus(authToken, conn, documentName, os);
        } catch (ConnectException e) {
            log.error("Unable to connect to Docmosis: " + e.getMessage());
            log.error("If you have a proxy, you will need the Proxy aware example code.");
            System.exit(2);
        } finally {
            releaseResources(conn, outputStreamWriter, os);
        }
        return documentInfo;
    }

    private void buildListingInstruction(OutputStreamWriter outputStreamWriter, ListingData listingData,
                                         String documentName, UserDetails userDetails, String caseType)
            throws IOException {
        StringBuilder sb;
        if (ListingHelper.isReportType(listingData.getReportType())) {
            sb = ReportDocHelper.buildReportDocumentContent(listingData, tornadoConfiguration.getAccessKey(),
                    documentName, userDetails);
        } else {
            sb = ListingHelper.buildListingDocumentContent(listingData, tornadoConfiguration.getAccessKey(),
                    documentName, userDetails, caseType);
        }
        writeOutputStream(outputStreamWriter, sb);
    }

    DocumentInfo scheduleGeneration(String authToken, BulkData bulkData) throws IOException {
        HttpURLConnection conn = null;
        OutputStreamWriter outputStreamWriter = null;
        ByteArrayOutputStream os = null;
        DocumentInfo documentInfo = new DocumentInfo();
        try {
            conn = createConnection();
            log.info("Connected");
            String documentName = BulkHelper.getScheduleDocName(bulkData.getScheduleDocName());
            outputStreamWriter = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
            buildScheduleInstruction(outputStreamWriter, bulkData);
            os = new ByteArrayOutputStream();
            documentInfo = checkResponseStatus(authToken, conn, documentName, os);
        } catch (ConnectException e) {
            log.error("Unable to connect to Docmosis: " + e.getMessage());
            log.error("If you have a proxy, you will need the Proxy aware example code.");
            System.exit(2);
        } finally {
            releaseResources(conn, outputStreamWriter, os);
        }
        return documentInfo;
    }

    private void buildScheduleInstruction(OutputStreamWriter outputStreamWriter, BulkData bulkData) throws IOException {
        writeOutputStream(outputStreamWriter, BulkHelper.buildScheduleDocumentContent(bulkData,
                tornadoConfiguration.getAccessKey()));
    }

    private DocumentInfo checkResponseStatus(String authToken, HttpURLConnection conn, String documentName,
                                             ByteArrayOutputStream os) throws IOException {
        DocumentInfo documentInfo = new DocumentInfo();
        int status = conn.getResponseCode();
        if (status == HTTP_OK) {
            documentInfo = createDocument(authToken, conn, documentName, os);
        } else {
            log.error("message:" + conn.getResponseMessage());
            InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
            BufferedReader errorReader = new BufferedReader(inputStreamReader);
            String msg;
            while ((msg = errorReader.readLine()) != null) {
                log.error(msg);
            }
            inputStreamReader.close();
            errorReader.close();
        }
        return documentInfo;
    }

    private byte[] getBytesFromInputStream(ByteArrayOutputStream os, InputStream is) throws IOException {
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }

    private DocumentInfo createDocument(String authToken, HttpURLConnection conn, String documentName,
                                        ByteArrayOutputStream os) throws IOException {
        URI documentSelfPath = documentManagementService.uploadDocument(authToken,
                getBytesFromInputStream(os, conn.getInputStream()),
                OUTPUT_FILE_NAME, APPLICATION_DOCX_VALUE);
        log.info("URI documentSelfPath uploaded and created: " + documentSelfPath.toString());
        return generateDocumentInfo(documentName,
                documentSelfPath,
                documentManagementService.generateMarkupDocument(documentManagementService
                        .generateDownloadableURL(documentSelfPath)));
    }

    private void releaseResources(HttpURLConnection conn, OutputStreamWriter outputStreamWriter,
                                  ByteArrayOutputStream os) throws IOException {
        if (conn != null) {
            conn.disconnect();
        }
        if (outputStreamWriter != null) {
            outputStreamWriter.close();
        }
        if (os != null) {
            os.close();
        }
    }

    private void writeOutputStream(OutputStreamWriter outputStreamWriter, StringBuilder sb) throws IOException {
        outputStreamWriter.write(sb.toString());
        outputStreamWriter.flush();
    }

}
