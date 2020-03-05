package uk.gov.hmcts.ethos.replacement.docmosis.idam;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.models.*;

@FeignClient(name = "idam-api", url = "${idam.api.url}")
public interface IdamApi {
    @RequestMapping(method = RequestMethod.GET, value = "/o/userinfo")
    UserDetails retrieveUserDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation);
}
