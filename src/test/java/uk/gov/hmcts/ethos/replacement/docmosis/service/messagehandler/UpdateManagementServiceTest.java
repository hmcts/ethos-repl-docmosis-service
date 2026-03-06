package uk.gov.hmcts.ethos.replacement.docmosis.service.messagehandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.UpdateCaseMsg;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.datamodel.ResetStateDataModel;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.MultipleErrors;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleCounterRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleErrorsRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.service.TestMessageHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class UpdateManagementServiceTest {

    @InjectMocks
    private UpdateManagementService updateManagementService;
    @Mock
    private MultipleCounterRepository multipleCounterRepository;
    @Mock
    private MultipleErrorsRepository multipleErrorsRepository;
    @Mock
    private MultipleUpdateService multipleUpdateService;
    @Mock
    private SingleReadingService singleReadingService;

    private UpdateCaseMsg updateCaseMsg;

    @Before
    public void setUp() {
        updateCaseMsg = TestMessageHelper.generateUpdateCaseMsg();
    }

    @Test
    public void shouldRunUpdateLogicAndCompleteMultiple() throws IOException, InterruptedException {
        when(multipleCounterRepository.persistentQGetNextMultipleCountVal(updateCaseMsg.getMultipleRef()))
            .thenReturn(1);
        when(multipleErrorsRepository.findByMultipleref(updateCaseMsg.getMultipleRef()))
            .thenReturn(new ArrayList<>());

        updateManagementService.updateLogic(updateCaseMsg);

        verify(singleReadingService).sendUpdateToSingleLogic(updateCaseMsg);
        verify(multipleUpdateService).sendUpdateToMultipleLogic(eq(updateCaseMsg), any());
        verify(multipleCounterRepository).persistentQGetNextMultipleCountVal(updateCaseMsg.getMultipleRef());
        verify(multipleCounterRepository).deleteByMultipleref(updateCaseMsg.getMultipleRef());
        verify(multipleErrorsRepository).findByMultipleref(updateCaseMsg.getMultipleRef());
        verify(multipleErrorsRepository).deleteByMultipleref(updateCaseMsg.getMultipleRef());
        verifyNoMoreInteractions(singleReadingService, multipleUpdateService, multipleCounterRepository,
            multipleErrorsRepository);
    }

    @Test
    public void shouldRunUpdateLogicWithErrors() throws IOException, InterruptedException {
        when(multipleCounterRepository.persistentQGetNextMultipleCountVal(updateCaseMsg.getMultipleRef()))
            .thenReturn(1);
        when(multipleErrorsRepository.findByMultipleref(updateCaseMsg.getMultipleRef()))
            .thenReturn(new ArrayList<>(Collections.singletonList(new MultipleErrors())));

        updateManagementService.updateLogic(updateCaseMsg);

        verify(singleReadingService).sendUpdateToSingleLogic(updateCaseMsg);
        verify(multipleUpdateService).sendUpdateToMultipleLogic(eq(updateCaseMsg), any());
        verify(multipleCounterRepository).deleteByMultipleref(updateCaseMsg.getMultipleRef());
        verify(multipleErrorsRepository).deleteByMultipleref(updateCaseMsg.getMultipleRef());
    }

    @Test
    public void shouldAddUnrecoverableErrorToDatabase() {
        updateManagementService.addUnrecoverableErrorToDatabase(updateCaseMsg);

        verify(multipleErrorsRepository).persistentQLogMultipleError(
            eq(updateCaseMsg.getMultipleRef()),
            eq(updateCaseMsg.getEthosCaseReference()),
            eq("Unprocessable message")
        );
    }

    @Test
    public void shouldResetState() throws IOException, InterruptedException {
        updateCaseMsg.setDataModelParent(ResetStateDataModel.builder().build());

        updateManagementService.updateLogic(updateCaseMsg);

        verify(multipleCounterRepository).deleteByMultipleref(updateCaseMsg.getMultipleRef());
        verify(multipleErrorsRepository).deleteByMultipleref(updateCaseMsg.getMultipleRef());
    }
}
