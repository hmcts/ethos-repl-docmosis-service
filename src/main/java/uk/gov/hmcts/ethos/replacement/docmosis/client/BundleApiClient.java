package uk.gov.hmcts.ethos.replacement.docmosis.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.ecm.common.model.bundle.BundleCreateRequest;
import uk.gov.hmcts.ecm.common.model.bundle.BundleCreateResponse;

@FeignClient(name = "bundle", url = "${em-ccd-orchestrator.api.url}",
        configuration = FeignClientProperties.FeignClientConfiguration.class)
public interface BundleApiClient {
    @PostMapping(value = "api/stitch-ccd-bundles", consumes = "application/json")
    BundleCreateResponse stitchBundle(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestHeader("ServiceAuthorization") String serviceAuthorization,
            @RequestBody
            BundleCreateRequest bundleCreateRequest
    );
}
