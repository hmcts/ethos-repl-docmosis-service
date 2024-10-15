package uk.gov.hmcts.ethos.replacement.docmosis.idam;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;

@FeignClient(name = "idam-api", url = "${idam.api.url}")
public interface IdamApi {
    @RequestMapping(method = RequestMethod.GET, value = "/o/userinfo")
    UserDetails retrieveUserDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation);

    @GetMapping("/api/v1/users/{userId}")
    UserDetails getUserByUserId(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
            @PathVariable("userId") String userId
    );
}
