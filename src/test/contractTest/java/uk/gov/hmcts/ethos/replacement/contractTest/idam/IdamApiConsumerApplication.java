package uk.gov.hmcts.ethos.replacement.contractTest.idam;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.IdamApi;

@SpringBootApplication
@EnableFeignClients(clients = {
        IdamApi.class
})
public class IdamApiConsumerApplication {
}
