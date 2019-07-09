package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CCDRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.DocumentInfo;

import java.io.IOException;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.SetUpUtils.feignError;

@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentGenerationServiceTest {

    @InjectMocks
    private DocumentGenerationService documentGenerationService;
    @Mock
    private TornadoService tornadoService;
    private CCDRequest ccdRequest;
    private DocumentInfo documentInfo;

    @Before
    public void setUp() {
        ccdRequest = new CCDRequest();
        documentGenerationService = new DocumentGenerationService(tornadoService);
        documentInfo = DocumentInfo.builder().description("resources/example.json").build();
    }

    @Test(expected = Exception.class)
    public void processDocumentRequestException() throws IOException {
        when(tornadoService.documentGeneration(anyString(), any())).thenThrow(feignError());
        documentGenerationService.processDocumentRequest(ccdRequest, "authToken");
    }

    @Test
    public void processDocumentRequest() throws IOException {
        when(tornadoService.documentGeneration(anyString(), any())).thenReturn(documentInfo);
        DocumentInfo documentInfo1 = documentGenerationService.processDocumentRequest(ccdRequest, "authToken");
        assertEquals(documentInfo, documentInfo1);
    }

}