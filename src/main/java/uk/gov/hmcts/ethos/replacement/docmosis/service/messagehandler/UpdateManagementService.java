package uk.gov.hmcts.ethos.replacement.docmosis.service.messagehandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.UpdateCaseMsg;
import uk.gov.hmcts.ecm.compat.common.model.servicebus.datamodel.ResetStateDataModel;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.MultipleErrors;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleCounterRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleErrorsRepository;

import java.io.IOException;
import java.util.List;

import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.compat.common.model.helper.Constants.YES;

@Slf4j
@RequiredArgsConstructor
@Service
public class UpdateManagementService {

    private static final String UNPROCESSABLE_MESSAGE = "Unprocessable message";

    private final MultipleCounterRepository multipleCounterRepository;
    private final MultipleErrorsRepository multipleErrorsRepository;
    @Qualifier("messageHandlerMultipleUpdateService")
    private final MultipleUpdateService multipleUpdateService;
    private final SingleReadingService singleReadingService;

    public void updateLogic(UpdateCaseMsg updateCaseMsg) throws IOException {

        if (updateCaseMsg.getDataModelParent() instanceof ResetStateDataModel) {
            log.info("Resetting state of multiple to Open State");
            deleteMultipleRefDatabase(updateCaseMsg.getMultipleRef());
        } else {
            singleReadingService.sendUpdateToSingleLogic(updateCaseMsg);

            if (!updateCaseMsg.getMultipleRef().equals(SINGLE_CASE_TYPE)) {
                checkIfFinish(updateCaseMsg);
            }
        }
    }

    public void checkIfFinish(UpdateCaseMsg updateCaseMsg) throws IOException {

        int counter = getNextCounterNumber(updateCaseMsg.getMultipleRef());

        log.info("COUNTER: {} TOTAL CASES: {}", counter, updateCaseMsg.getTotalCases());

        if (counter == Integer.parseInt(updateCaseMsg.getTotalCases())) {
            log.info("----- MULTIPLE UPDATE FINISHED: sending update to multiple ------");

            if (updateCaseMsg.getConfirmation().equals(YES)) {
                List<MultipleErrors> multipleErrorsList =
                    multipleErrorsRepository.findByMultipleref(updateCaseMsg.getMultipleRef());

                multipleUpdateService.sendUpdateToMultipleLogic(updateCaseMsg, multipleErrorsList);
            }

            deleteMultipleRefDatabase(updateCaseMsg.getMultipleRef());
        }
    }

    private int getNextCounterNumber(String multipleRef) {
        return multipleCounterRepository.persistentQGetNextMultipleCountVal(multipleRef);
    }

    private void deleteMultipleRefDatabase(String multipleRef) {

        log.info("Clearing all multipleRef from DBs: {}", multipleRef);

        log.info("Clearing multiple counter repository");
        multipleCounterRepository.deleteByMultipleref(multipleRef);

        log.info("Clearing multiple errors repository");
        multipleErrorsRepository.deleteByMultipleref(multipleRef);

        log.info("Deleted repositories");
    }

    public void addUnrecoverableErrorToDatabase(UpdateCaseMsg updateCaseMsg) {

        multipleErrorsRepository.persistentQLogMultipleError(updateCaseMsg.getMultipleRef(),
                                                             updateCaseMsg.getEthosCaseReference(),
                                                             UNPROCESSABLE_MESSAGE);
    }
}
