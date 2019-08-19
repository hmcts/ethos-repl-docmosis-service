package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.ethos.replacement.docmosis.config.TornadoConfiguration;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.DocumentInfo;

import java.io.IOException;
import static org.junit.Assert.assertEquals;
public class TornadoServiceTest {

    @InjectMocks
    private TornadoService tornadoService;
    @Mock
    private DocumentManagementService documentManagementService;
    private DocumentInfo documentInfo;
    private CaseData caseData;

    @Before
    public void setUp() {
        documentInfo = new DocumentInfo();
        TornadoConfiguration tornadoConfiguration = new TornadoConfiguration();
        tornadoConfiguration.setUrl("http://google.com");
        caseData = new CaseData();
        tornadoService = new TornadoService(tornadoConfiguration, documentManagementService);
    }

    @Test
    public void documentGeneration() throws IOException {
        DocumentInfo documentInfo1 = tornadoService.documentGeneration("TOKEN", caseData);
        assertEquals(documentInfo.toString(), documentInfo1.toString());
    }
}