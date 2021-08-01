package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.BulkData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceScotType;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceType;
import uk.gov.hmcts.ecm.common.model.helper.DefaultValues;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ethos.replacement.docmosis.config.TornadoConfiguration;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DocumentHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ListingHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.ReportDocHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.SignificantItemType;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static java.net.HttpURLConnection.HTTP_OK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LETTER_ADDRESS_ALLOCATED_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OUTPUT_FILE_NAME;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.VENUE_ADDRESS_VALUES_FILE_PATH;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.DocumentManagementService.APPLICATION_DOCX_VALUE;

@Slf4j
@RequiredArgsConstructor
@Service("tornadoService")
public class TornadoService {
    private static final String VENUE_ADDRESS_INPUT_STREAM_ERROR = "Failed to get an inputStream for the "
            + "venueAddressValues.xlsx file : ---> ";
    private static final String UNABLE_TO_CONNECT_TO_DOCMOSIS = "Unable to connect to Docmosis: ";

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
        try {
            conn = createConnection();

            buildInstruction(conn, caseData, authToken, caseTypeId,
                    correspondenceType, correspondenceScotType, multipleData);
            var documentName = Helper.getDocumentName(correspondenceType, correspondenceScotType);
            return checkResponseStatus(authToken, conn, documentName);
        } catch (ConnectException e) {
            log.error(UNABLE_TO_CONNECT_TO_DOCMOSIS, e);
            return new DocumentInfo();
        } finally {
            closeConnection(conn);
        }
    }

    private void buildInstruction(HttpURLConnection conn, CaseData caseData, String authToken,
                                  String caseTypeId, CorrespondenceType correspondenceType,
                                  CorrespondenceScotType correspondenceScotType,
                                  MultipleData multipleData) {
        try (var venueAddressInputStream = getVenueAddressInputStream();
            var os = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
            var allocatedCourtAddress = getAllocatedCourtAddress(caseData, caseTypeId, multipleData);
            var userDetails = userService.getUserDetails(authToken);

            var documentContent = DocumentHelper.buildDocumentContent(caseData,
                    tornadoConfiguration.getAccessKey(),
                    userDetails, caseTypeId, venueAddressInputStream, correspondenceType,
                    correspondenceScotType, multipleData, allocatedCourtAddress);

            writeOutputStream(os, documentContent);
        } catch (Exception ex) {
            log.error(VENUE_ADDRESS_INPUT_STREAM_ERROR, ex);
        }
    }

    private InputStream getVenueAddressInputStream() {
        return getClass().getClassLoader().getResourceAsStream(VENUE_ADDRESS_VALUES_FILE_PATH);
    }

    private DefaultValues getAllocatedCourtAddress(CaseData caseData, String caseTypeId, MultipleData multipleData) {
        if ((multipleData != null && isAllocatedOffice(caseTypeId, multipleData.getCorrespondenceScotType()))
                || isAllocatedOffice(caseTypeId, caseData.getCorrespondenceScotType())) {
            return defaultValuesReaderService.getDefaultValues(caseData.getAllocatedOffice(), caseTypeId);
        }
        return null;
    }

    private boolean isAllocatedOffice(String caseTypeId, CorrespondenceScotType correspondenceScotType) {
        return caseTypeId.equals(SCOTLAND_CASE_TYPE_ID)
                && correspondenceScotType != null
                && correspondenceScotType.getLetterAddress().equals(LETTER_ADDRESS_ALLOCATED_OFFICE);
    }

    DocumentInfo listingGeneration(String authToken, ListingData listingData, String caseType) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = createConnection();

            var documentName = ListingHelper.getListingDocName(listingData);
            buildListingInstruction(conn, listingData, documentName, authToken, caseType);
            return checkResponseStatus(authToken, conn, documentName);
        } catch (ConnectException e) {
            log.error(UNABLE_TO_CONNECT_TO_DOCMOSIS, e);
            return new DocumentInfo();
        } finally {
            closeConnection(conn);
        }
    }

    private void buildListingInstruction(HttpURLConnection conn, ListingData listingData,
                                         String documentName, String authToken, String caseType) throws IOException {
        var userDetails = userService.getUserDetails(authToken);
        StringBuilder sb;
        if (ListingHelper.isReportType(listingData.getReportType())) {
            sb = ReportDocHelper.buildReportDocumentContent(listingData, tornadoConfiguration.getAccessKey(),
                    documentName, userDetails);
        } else {
            sb = ListingHelper.buildListingDocumentContent(listingData, tornadoConfiguration.getAccessKey(),
                    documentName, userDetails, caseType);
        }

        try (var outputStreamWriter = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
            writeOutputStream(outputStreamWriter, sb);
        }
    }

    DocumentInfo scheduleGeneration(String authToken, BulkData bulkData) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = createConnection();

            var documentName = BulkHelper.getScheduleDocName(bulkData.getScheduleDocName());
            buildScheduleInstruction(conn, bulkData);
            return checkResponseStatus(authToken, conn, documentName);
        } catch (ConnectException e) {
            log.error(UNABLE_TO_CONNECT_TO_DOCMOSIS, e);
            return new DocumentInfo();
        } finally {
            closeConnection(conn);
        }
    }

    private void buildScheduleInstruction(HttpURLConnection conn, BulkData bulkData) throws IOException {
        var sb = BulkHelper.buildScheduleDocumentContent(bulkData, tornadoConfiguration.getAccessKey());

        try (var outputStreamWriter = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
            writeOutputStream(outputStreamWriter, sb);
        }
    }

    private HttpURLConnection createConnection() throws IOException {
        var tornadoURL = tornadoConfiguration.getUrl();
        log.info("Tornado URL: " + tornadoURL);
        var conn = (HttpURLConnection) new URL(tornadoURL).openConnection();
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.connect();
        return conn;
    }

    private void closeConnection(HttpURLConnection conn) {
        if (conn != null) {
            conn.disconnect();
        }
    }

    private DocumentInfo checkResponseStatus(String authToken, HttpURLConnection conn, String documentName)
            throws IOException {
        try (var os = new ByteArrayOutputStream()) {
            if (conn.getResponseCode() == HTTP_OK) {
                return createDocument(authToken, conn, documentName, os);
            } else {
                logResponseErrorMessage(conn);
                return new DocumentInfo();
            }
        }
    }

    private DocumentInfo createDocument(String authToken, HttpURLConnection conn, String documentName,
                                        ByteArrayOutputStream os) throws IOException {
        var documentSelfPath = documentManagementService.uploadDocument(authToken,
                getBytesFromInputStream(os, conn.getInputStream()),
                OUTPUT_FILE_NAME, APPLICATION_DOCX_VALUE);
        log.info("URI documentSelfPath uploaded and created: " + documentSelfPath.toString());
        return generateDocumentInfo(documentName,
                documentSelfPath,
                documentManagementService.generateMarkupDocument(documentManagementService
                        .generateDownloadableURL(documentSelfPath)));
    }

    private byte[] getBytesFromInputStream(ByteArrayOutputStream os, InputStream is) throws IOException {
        var buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }

    private DocumentInfo generateDocumentInfo(String documentName, URI documentSelfPath, String markupURL) {
        return DocumentInfo.builder()
                .type(SignificantItemType.DOCUMENT.name())
                .description(documentName)
                .markUp(markupURL)
                .url(ccdGatewayBaseUrl + documentSelfPath.getRawPath() + "/binary")
                .build();
    }

    private void logResponseErrorMessage(HttpURLConnection conn) throws IOException {
        log.error("Response message:" + conn.getResponseMessage());

        try (var inputStreamReader = new InputStreamReader(conn.getErrorStream());
             var errorReader = new BufferedReader(inputStreamReader)) {
            String msg;
            while ((msg = errorReader.readLine()) != null) {
                log.error(msg);
            }
        }
    }

    private void writeOutputStream(OutputStreamWriter outputStreamWriter, StringBuilder sb) throws IOException {
        outputStreamWriter.write(sb.toString());
        outputStreamWriter.flush();
    }
}
