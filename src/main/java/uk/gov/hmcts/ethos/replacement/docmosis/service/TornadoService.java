package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ClaimantType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.DocumentRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.RepresentativeType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.RespondentType;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static java.net.HttpURLConnection.HTTP_OK;

@Service("tornadoService")
public class TornadoService {

    private static final Logger log = LoggerFactory.getLogger(TornadoService.class);

    private static final String DWS_RENDER_URL = "http://localhost:8080/rs/render";

    private static final String outputFileName = "myWelcome.doc";


    void documentGeneration(DocumentRequest documentRequest, String templateName) throws IOException {

        // Set your access Key if you configure it in Tornado
        //String accessKey = "";

        HttpURLConnection conn = null;
        try {
            conn = createConnection();
            log.info("Connected");
            buildInstruction(conn, documentRequest, templateName);

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
        // set connection parameters
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.connect();
        return conn;
    }

    private void buildInstruction(HttpURLConnection conn, DocumentRequest documentRequest, String templateName) throws IOException {
        StringBuffer sb = new StringBuffer();

        // Start building the instruction
        sb.append("{\n");
        //sb.append("\"accessKey\":\"").append(accessKey).append("\",\n");
        sb.append("\"templateName\":\"").append(templateName).append("\",\n");
        sb.append("\"outputName\":\"").append(outputFileName).append("\",\n");

        // Building the document data
        sb.append("\"data\":{\n");

        sb.append("\"ifRepresented\":\"").append(documentRequest.getIfRepresented()).append("\",\n");
        sb.append("\"ifRefused\":\"").append(documentRequest.getIfRefused()).append("\",\n");

        RepresentativeType representativeType = documentRequest.getRepresentativeType();
        sb.append("\"nameOfRepresentative\":\"").append(representativeType.getNameOfRepresentative()).append("\",\n");
        sb.append("\"nameOfOrganisation\":\"").append(representativeType.getNameOfOrganisation()).append("\",\n");
        sb.append("\"representativeAddress\":\"").append(representativeType.getRepresentativeAddress()).append("\",\n");
        sb.append("\"representativePhoneNumber\":\"").append(representativeType.getRepresentativePhoneNumber()).append("\",\n");
        sb.append("\"representativeFaxNumber\":\"").append(representativeType.getRepresentativeFaxNumber()).append("\",\n");
        sb.append("\"representativeDxNumber\":\"").append(representativeType.getRepresentativeDxNumber()).append("\",\n");
        sb.append("\"representativeEmailAddress\":\"").append(representativeType.getRepresentativeEmailAddress()).append("\",\n");
        sb.append("\"representativeReference\":\"").append(representativeType.getRepresentativeReference()).append("\",\n");

        sb.append("\"createdDate\":\"").append(documentRequest.getCreatedDate()).append("\",\n");
        sb.append("\"receivedDate\":\"").append(documentRequest.getReceivedDate()).append("\",\n");
        sb.append("\"hearingDate\":\"").append(documentRequest.getHearingDate()).append("\",\n");
        sb.append("\"caseNo\":\"").append(documentRequest.getCaseNo()).append("\",\n");
        sb.append("\"claimant\":\"").append(documentRequest.getClaimant()).append("\",\n");
        sb.append("\"respondent\":\"").append(documentRequest.getRespondent()).append("\",\n");
        sb.append("\"clerk\":\"").append(documentRequest.getClerk()).append("\",\n");
        sb.append("\"judgeSurname\":\"").append(documentRequest.getJudgeSurname()).append("\",\n");

        ClaimantType claimantType = documentRequest.getClaimantType();
        sb.append("\"claimantTitle\":\"").append(claimantType.getClaimantTitle()).append("\",\n");
        sb.append("\"claimantFirstName\":\"").append(claimantType.getClaimantFirstName()).append("\",\n");
        sb.append("\"claimantInitials\":\"").append(claimantType.getClaimantInitials()).append("\",\n");
        sb.append("\"claimantLastName\":\"").append(claimantType.getClaimantLastName()).append("\",\n");
        sb.append("\"claimantDateOfBirth\":\"").append(claimantType.getClaimantDateOfBirth()).append("\",\n");
        sb.append("\"claimantGender\":\"").append(claimantType.getClaimantGender()).append("\",\n");
        sb.append("\"claimantAddressUK\":\"").append(claimantType.getClaimantAddressUK()).append("\",\n");
        sb.append("\"claimantPhoneNumber\":\"").append(claimantType.getClaimantPhoneNumber()).append("\",\n");
        sb.append("\"claimantMobileNumber\":\"").append(claimantType.getClaimantMobileNumber()).append("\",\n");
        sb.append("\"claimantFaxNumber\":\"").append(claimantType.getClaimantFaxNumber()).append("\",\n");
        sb.append("\"claimantEmailAddress\":\"").append(claimantType.getClaimantEmailAddress()).append("\",\n");
        sb.append("\"claimantContactPreference\":\"").append(claimantType.getClaimantContactPreference()).append("\",\n");

        RespondentType respondentType = documentRequest.getRespondentType();
        sb.append("\"respondentName\":\"").append(respondentType.getRespondentName()).append("\",\n");
        sb.append("\"respondentAddress\":\"").append(respondentType.getRespondentAddress()).append("\",\n");

        sb.append("}\n");
        sb.append("}\n");


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
        log.info("Created file:" + file.getAbsolutePath());
    }
}
