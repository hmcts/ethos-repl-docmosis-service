package uk.gov.hmcts.ethos.replacement.docmosis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages =
        {"uk.gov.hmcts.ethos.replacement"
        })
public class DocmosisApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocmosisApplication.class, args);
    }

}

