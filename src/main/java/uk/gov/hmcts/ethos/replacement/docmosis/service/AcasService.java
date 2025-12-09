package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.AcasCertificate;
import uk.gov.hmcts.ethos.replacement.docmosis.model.AcasCertificateRequest;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@Service
@Slf4j
public class AcasService {

    private static final String OCP_APIM_SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key";
    private static final String NOT_FOUND = "not found";
    private final TornadoService tornadoService;
    private final WebClient webClient;

    private final String acasApiUrl;
    private final String acasApiKey;

    public AcasService(TornadoService tornadoService, WebClient webClient,
                       @Value("${acas.api.url}") String acasApiUrl,
                       @Value("${acas.api.key}") String acasApiKey) {
        this.tornadoService = tornadoService;
        this.webClient = webClient;
        this.acasApiUrl = acasApiUrl;
        this.acasApiKey = acasApiKey;
    }

    public List<String> getAcasCertificate(CaseDetails caseDetails, String authToken) throws JsonProcessingException {
        CaseData caseData = caseDetails.getCaseData();
        if (isNullOrEmpty(caseData.getAcasCertificate())) {
            return List.of("ACAS Certificate cannot be null or empty");
        }

        Object acasCertificateObject;
        try {
            acasCertificateObject = fetchAcasCertificates(caseData.getAcasCertificate());
            if (ObjectUtils.isEmpty(acasCertificateObject)) {
                return List.of("Error reading ACAS Certificate");
            }
        } catch (Exception errorException) {
            log.error("Error retrieving ACAS Certificate with exception : " + errorException.getMessage());
            return List.of("Error retrieving ACAS Certificate");
        }

        AcasCertificate acasCertificate = convertAcasResponse((ArrayList) acasCertificateObject);
        if (NOT_FOUND.equals(acasCertificate.getCertificateDocument())) {
            return List.of("No ACAS Certificate found");
        }

        DocumentInfo documentInfo;
        try {
            documentInfo = convertCertificateToPdf(caseData, acasCertificate, authToken, caseDetails.getCaseTypeId());
        } catch (Exception exception) {
            log.error("Error converting ACAS Certificate with exception : " + exception.getMessage());
            return List.of("Error uploading ACAS Certificate");
        }

        documentInfo.setMarkUp(documentInfo.getMarkUp().replace("Document", documentInfo.getDescription()));
        caseData.setDocMarkUp(documentInfo.getMarkUp());
        caseData.setAcasCertificate(null);
        return new ArrayList<>();
    }

    private static AcasCertificate convertAcasResponse(ArrayList acasCertificate) throws JsonProcessingException {
        Object cert = acasCertificate.get(0);
        ObjectMapper objectMapper = new ObjectMapper();
        String certificate = objectMapper.writeValueAsString(cert);
        return objectMapper.readValue(certificate, AcasCertificate.class);
    }

    private DocumentInfo convertCertificateToPdf(CaseData caseData, AcasCertificate acasCertificate, String authToken,
                                                 String caseTypeId) {
        Optional<RespondentSumTypeItem> respondent = caseData.getRespondentCollection().stream()
                .filter(r -> acasCertificate.getCertificateNumber().equals(
                        defaultIfEmpty(r.getValue().getRespondentACAS(), "")))
                .findFirst();
        String acasName = "";
        if (respondent.isPresent()) {
            acasName = " - " + respondent.get().getValue().getRespondentName();
        }
        byte[] pdfData = Base64.getDecoder().decode(acasCertificate.getCertificateDocument());
        return tornadoService.createDocumentInfoFromBytes(authToken,
                "ACAS Certificate" + acasName + " - " + acasCertificate.getCertificateNumber() + ".pdf",
                caseTypeId, pdfData);
    }

    private List<AcasCertificate> fetchAcasCertificates(String... acasCertificate) {
        AcasCertificateRequest acasCertificateRequest = new AcasCertificateRequest();
        acasCertificateRequest.setCertificateNumbers(acasCertificate);

        return webClient.post()
            .uri(acasApiUrl)
            .header(OCP_APIM_SUBSCRIPTION_KEY, acasApiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(acasCertificateRequest)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<AcasCertificate>>() {})
            .block();
    }
}
