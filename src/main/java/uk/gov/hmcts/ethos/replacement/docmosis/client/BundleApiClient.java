package uk.gov.hmcts.ethos.replacement.docmosis.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.ecm.common.model.bundle.BundleCreateRequest;
import uk.gov.hmcts.ecm.common.model.bundle.BundleCreateResponse;

@FeignClient(name = "bundle-api-client", url = "${bundle.api.url}",
    configuration = FeignClientProperties.FeignClientConfiguration.class)
public interface BundleApiClient {
    @PostMapping(value = "api/new-bundle", consumes = "application/json")
    BundleCreateResponse newBundle(
            @RequestHeader("Authorization") String authorisation,
            @RequestHeader("ServiceAuthorization") String serviceAuthorisation,
            @RequestBody BundleCreateRequest bundleCreateRequest
    );

    @PostMapping(value = "api/stitch-ccd-bundles", consumes = "application/json")
    BundleCreateResponse stitchCcdBundles(
            @RequestHeader("Authorization") String authorisation,
            @RequestHeader("ServiceAuthorization") String serviceAuthorisation,
            @RequestBody BundleCreateRequest bundleCreateRequest
    );
}
