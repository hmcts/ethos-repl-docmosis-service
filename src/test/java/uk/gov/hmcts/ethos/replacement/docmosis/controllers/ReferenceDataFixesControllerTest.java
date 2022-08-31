package uk.gov.hmcts.ethos.replacement.docmosis.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.DocmosisApplication;
import uk.gov.hmcts.ethos.replacement.docmosis.service.VerifyTokenService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.RefDataFixesCcdDataSource;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.ReferenceDataFixesService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.RefDataFixesData;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.RefDataFixesDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException.ERROR_MESSAGE;

@RunWith(SpringRunner.class)
@WebMvcTest(ReferenceDataController.class)
@ContextConfiguration(classes = DocmosisApplication.class)
public class ReferenceDataFixesControllerTest {

    private static final String AUTH_TOKEN = "Bearer eyJhbGJbpjciOiJIUzI1NiJ9";
    private static final String UPDATE_JUDGES = "/updateJudgesItcoReferences";
    private static final String DATE_LISTED_REFERENCE_DATA = "/dateListedReferenceData";

    @Autowired
    private WebApplicationContext applicationContext;

    @MockBean
    private ReferenceDataFixesService referenceDataFixesService;

    @MockBean
    private VerifyTokenService verifyTokenService;

    private MockMvc mvc;
    private JsonNode requestContent;
    private SubmitEvent submitEvent;

    private void doRequestSetUp() throws IOException, URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        requestContent = objectMapper.readTree(new File(getClass()
                .getResource("/exampleV1.json").toURI()));
    }

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
        doRequestSetUp();
        submitEvent = new SubmitEvent();
        submitEvent.setCaseData(new CaseData());
    }

    @Test
    public void updateJudgeCodeTest() throws Exception {
        when(referenceDataFixesService.updateJudgesItcoReferences(isA(RefDataFixesDetails.class), eq(AUTH_TOKEN), new RefDataFixesCcdDataSource(AUTH_TOKEN))).thenReturn(new RefDataFixesData());
        when(verifyTokenService.verifyTokenSignature(AUTH_TOKEN)).thenReturn(true);
        mvc.perform(post(UPDATE_JUDGES)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()))
                .andExpect(jsonPath("$.errors", nullValue()))
                .andExpect(jsonPath("$.warnings", nullValue()));
    }

    @Test
    public void updateJudgeCodeTestError400() throws Exception {
        mvc.perform(post(UPDATE_JUDGES)
                .content("error")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateJudgeCodeTestError500() throws Exception {
        when(referenceDataFixesService.updateJudgesItcoReferences(isA(RefDataFixesDetails.class), AUTH_TOKEN, new RefDataFixesCcdDataSource(AUTH_TOKEN))).thenThrow(new InternalException(ERROR_MESSAGE));
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(true);
        mvc.perform(post(UPDATE_JUDGES)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void updateJudgeCodeTestForbidden() throws Exception {
        when(verifyTokenService.verifyTokenSignature(eq(AUTH_TOKEN))).thenReturn(false);
        mvc.perform(post(UPDATE_JUDGES)
                .content(requestContent.toString())
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
