package uk.gov.hmcts.ethos.replacement.docmosis.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(
    prefix = "queue",
    name = "enabled",
    havingValue = "true"
)
public class SchedulingConfiguration {
}
