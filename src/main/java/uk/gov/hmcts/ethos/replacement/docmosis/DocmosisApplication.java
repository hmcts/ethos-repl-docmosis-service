package uk.gov.hmcts.ethos.replacement.docmosis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"uk.gov.hmcts.ethos", "uk.gov.hmcts.ecm.common"})
@EnableFeignClients(basePackages = {"uk.gov.hmcts.ethos.replacement"})
@EnableScheduling
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, this is not a utility class
public class DocmosisApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocmosisApplication.class, args);
    }

}

