package uk.gov.hmcts.ethos.replacement.docmosis.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages =
        {"uk.gov.hmcts.ethos.replacement",
                "uk.gov.hmcts.reform.authorisation"
        })
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}