package uk.gov.hmcts.ethos.replacement.docmosis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties("case-default-values")
@PropertySource(value = "classpath:defaults.yml", factory = YamlPropertySourceFactory.class)
@Data
public class CaseDefaultValuesConfiguration {

    private String claimantTypeOfClaimant;

    private String positionType;

    private String caseType;
}
