package uk.gov.hmcts.ethos.replacement.docmosis.servicebus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ecm.common.helpers.CreateUpdatesHelper;
import uk.gov.hmcts.ecm.common.model.servicebus.CreateUpdatesDto;
import uk.gov.hmcts.ecm.common.model.servicebus.CreateUpdatesMsg;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.DataModelParent;
import uk.gov.hmcts.ecm.common.servicebus.ServiceBusSender;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sends create updates messages to create-updates queue.
 */
@Slf4j
@Component
public class CreateUpdatesBusSender {

    private static final String ERROR_MESSAGE = "Failed to send the message to the queue";
    private final ServiceBusSender serviceBusSender;

    public CreateUpdatesBusSender(
            @Qualifier("create-updates-send-helper") ServiceBusSender serviceBusSender) {
        this.serviceBusSender = serviceBusSender;
    }

    public void sendUpdatesToQueue(CreateUpdatesDto createUpdatesDto, DataModelParent dataModelParent, List<String> errors, String updateSize) {
        log.info("Started sending messages to create-updates queue");

        AtomicInteger successCount = new AtomicInteger(0);

        List<CreateUpdatesMsg> createUpdatesMsgList =
                CreateUpdatesHelper.getCreateUpdatesMessagesCollection(
                        createUpdatesDto,
                        dataModelParent,
                        500,
                        updateSize);

        createUpdatesMsgList
                .forEach(msg -> {

                    try {
                        serviceBusSender.sendMessage(msg);
                        log.info("SENT -----> " + msg.toString());
                        successCount.incrementAndGet();

                    } catch (Exception e) {
                        log.error("Error sending messages to create-updates queue", e);
                        errors.add(ERROR_MESSAGE);
                    }
                });

        log.info(
                "Finished sending messages to create-updates queue. Successful: {}. Failures {}.",
                successCount.get(),
                createUpdatesMsgList.size() - successCount.get()
        );
    }

}
