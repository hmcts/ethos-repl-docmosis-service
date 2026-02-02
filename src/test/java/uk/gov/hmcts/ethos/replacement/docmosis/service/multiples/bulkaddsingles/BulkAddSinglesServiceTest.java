package uk.gov.hmcts.ethos.replacement.docmosis.service.multiples.bulkaddsingles;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.service.excel.MultipleAmendService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BulkAddSinglesServiceTest {
    private BulkAddSinglesService bulkAddSinglesService;
    private SingleCasesImporter singleCasesImporter;
    private MultipleAmendService multipleAmendService;
    private MultipleDetails multipleDetails;
    private final String authToken = "some-token";

    @Before
    public void setup() {
        multipleDetails = createMultipleDetails();

        singleCasesImporter = mock(SingleCasesImporter.class);
        multipleAmendService = mock(MultipleAmendService.class);
        bulkAddSinglesService = new BulkAddSinglesService(singleCasesImporter, multipleAmendService);
    }

    @Test
    public void shouldSubmitCases() throws ImportException {
        var ethosCaseReferences = List.of("case1");
        when(singleCasesImporter.importCases(multipleDetails.getCaseData(), authToken)).thenReturn(ethosCaseReferences);

        var errors = bulkAddSinglesService.execute(multipleDetails, authToken);

        assertTrue(errors.isEmpty());
        verify(multipleAmendService, times(1)).bulkAmendMultipleLogic(anyString(), any(MultipleDetails.class),
                anyList());
    }

    @Test
    public void shouldReturnErrorWhenImportCasesFails() throws ImportException {
        when(singleCasesImporter.importCases(multipleDetails.getCaseData(), authToken))
                .thenThrow(ImportException.class);

        var errors = bulkAddSinglesService.execute(multipleDetails, authToken);

        assertEquals(1, errors.size());
        assertEquals("Unexpected error when importing single cases", errors.getFirst());
    }

    private MultipleDetails createMultipleDetails() {
        var multipleData = new MultipleData();
        var multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(multipleData);
        return multipleDetails;
    }
}
