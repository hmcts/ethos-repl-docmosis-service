package uk.gov.hmcts.ethos.replacement.docmosis.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice.ContactDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice.TribunalOffice;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties("tribunal-offices")
@PropertySource(value = "classpath:defaults.yml", factory = YamlPropertySourceFactory.class)
@Getter
public class TribunalOfficesConfiguration {
    private Map<TribunalOffice, ContactDetails> contactDetails = new HashMap<>();
}
