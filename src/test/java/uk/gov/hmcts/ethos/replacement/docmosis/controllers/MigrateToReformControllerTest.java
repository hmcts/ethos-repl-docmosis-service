package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.service.MigrateToReformService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.JsonMapper;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest({MigrateToReformController.class, JsonMapper.class})
class MigrateToReformControllerTest {

    private static final String AUTH_TOKEN = "Bearer eyJhbGJbpjciOiJIUzI1NiJ9";
    private static final String ABOUT_TO_SUBMIT_URL = "/migrateToReform/aboutToSubmit";

    @MockBean
    private MigrateToReformService migrateToReformService;
    @MockBean
    private VerifyTokenService verifyTokenService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void migrateToReformAboutToSubmit() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        CCDRequest ccdRequest = new CCDRequest();
        CaseDetails caseDetails = generateCaseDetails("migrateEcmToReformLeeds-ECM.json");
        ccdRequest.setCaseDetails(caseDetails);
        mockMvc.perform(post(ABOUT_TO_SUBMIT_URL)
                .contentType("application/json")
                .header("Authorization", AUTH_TOKEN)
                .content(jsonMapper.toJson(ccdRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void migrateToReformAboutToSubmit_invalidToken() throws Exception {
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(false);
        CCDRequest ccdRequest = new CCDRequest();
        CaseDetails caseDetails = generateCaseDetails("migrateEcmToReformLeeds-ECM.json");
        ccdRequest.setCaseDetails(caseDetails);
        mockMvc.perform(post(ABOUT_TO_SUBMIT_URL)
                .contentType("application/json")
                .header("Authorization", AUTH_TOKEN)
                .content(jsonMapper.toJson(ccdRequest)))
                .andExpect(status().isForbidden());
    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }
}
