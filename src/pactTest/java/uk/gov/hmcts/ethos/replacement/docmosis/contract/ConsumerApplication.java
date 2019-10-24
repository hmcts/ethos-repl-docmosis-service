package uk.gov.hmcts.ethos.replacement.docmosis.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.IdamApi;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.models.UserDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.util.ArrayList;

@ComponentScan(basePackages = "uk.gov.hmcts.reform.bulkscanprocessor.ocrvalidation.client")
@ComponentScan(basePackages = "uk.gov.hmcts.ethos.replacement.docmosis.client")


//@EnableFeignClients(basePackages =
//        {"uk.gov.hmcts.ethos.replacement.docmosis.idam"
//        })
public class ConsumerApplication {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    @Bean
    FeignContext feignContext() {
        return new FeignContext();
    }

    @Bean
    FeignClientProperties feignClientProperties() {
        return new FeignClientProperties();
    }

    @Bean
    IdamApi idamApi() {
        UserDetails userDetails;
        userDetails = new UserDetails("1", "example@hotmail.com", "Mike", "Jordan", new ArrayList<>());
        IdamApi idamApi = authorisation -> userDetails;
        return idamApi;
    }
    @Bean
    UserService userService() {
        return new UserService(idamApi());
    }

    @Bean
    AuthTokenGenerator authTokenGenerator() {
        return new AuthTokenGeneratorStub();
    }

}
