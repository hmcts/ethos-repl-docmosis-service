package uk.gov.hmcts.ethos.replacement.docmosis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.servicebus.IQueueClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.gov.hmcts.ecm.common.servicebus.ServiceBusSender;

@AutoConfigureAfter(QueueClientConfiguration.class)
@Configuration
@PropertySource(value={"classpath:application.properties"})
public class ServiceBusSenderConfiguration {

    private final ObjectMapper objectMapper;

    public ServiceBusSenderConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean(name = "create-updates-send-helper")
    public ServiceBusSender createUpdatesSendHelper(
        @Qualifier("create-updates-send-client") IQueueClient queueClient) {
        return new ServiceBusSender(queueClient, objectMapper);
    }

}
