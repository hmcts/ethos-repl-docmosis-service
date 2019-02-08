package uk.gov.hmcts.ethos.replacement.docmosis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tornado")
public class TornadoConfiguration {
    private String url;
    private String accessKey;
}
