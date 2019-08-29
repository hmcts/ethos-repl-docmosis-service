package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.ethos.replacement.docmosis.config.TornadoConfiguration;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.IdamApi;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.models.UserDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.DocumentInfo;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.SetUpUtils.feignError;

public class TornadoServiceTest {

    @InjectMocks
    private TornadoService tornadoService;
    @Mock
    private DocumentManagementService documentManagementService;
    private UserService userService;
    private DocumentInfo documentInfo;
    private CaseData caseData;
    private UserDetails userDetails;

    @Before
    public void setUp() {
        documentInfo = new DocumentInfo();
        TornadoConfiguration tornadoConfiguration = new TornadoConfiguration();
        tornadoConfiguration.setUrl("http://google.com");
        caseData = new CaseData();
        userDetails = new UserDetails("1", "example@hotmail.com", "Mike", "Jordan", new ArrayList<>());
        IdamApi idamApi = authorisation -> userDetails;
        userService = new UserService(idamApi);
        tornadoService = new TornadoService(tornadoConfiguration, documentManagementService, userService);
    }

    @Test(expected = Exception.class)
    public void documentGenerationError() throws IOException {
        when(userService.getUserDetails(anyString())).thenThrow(feignError());
        tornadoService.documentGeneration("TOKEN", caseData);
    }

    @Test
    public void documentGeneration() throws IOException {
        DocumentInfo documentInfo1 = tornadoService.documentGeneration("TOKEN", caseData);
        assertEquals(documentInfo.toString(), documentInfo1.toString());
    }
}