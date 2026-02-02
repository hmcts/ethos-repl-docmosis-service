package uk.gov.hmcts.ethos.replacement.docmosis.utils;

import uk.gov.hmcts.ecm.common.model.bundle.BundleCreateResponse;
import uk.gov.hmcts.reform.document.domain.UploadResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.Objects.requireNonNull;

public class ResourceLoader {
    private static final JsonMapper jsonMapper = JsonMapperFactory.create();

    private ResourceLoader() {
    }

    public static BundleCreateResponse stitchBundleRequest() throws URISyntaxException, IOException {
        String response = new String(Files.readAllBytes(Paths.get(requireNonNull(ResourceLoader.class.getClassLoader()
                .getResource("stitchBundleRequest.json")).toURI())));
        return jsonMapper.fromJson(response, BundleCreateResponse.class);
    }

    public static UploadResponse successfulDocumentManagementUploadResponse() throws URISyntaxException, IOException {
        String response = new String(Files.readAllBytes(Paths.get(requireNonNull(ResourceLoader.class.getClassLoader()
                .getResource("response.success.json")).toURI())));
        return jsonMapper.fromJson(response, UploadResponse.class);
    }

    public static UploadResponse unsuccessfulDocumentManagementUploadResponse() throws URISyntaxException, IOException {
        String response = new String(Files.readAllBytes(Paths.get(requireNonNull(ResourceLoader.class.getClassLoader()
                .getResource("response.failure.json")).toURI())));
        return jsonMapper.fromJson(response, UploadResponse.class);
    }

    public static uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse successfulDocStoreUpload()
        throws URISyntaxException, IOException {
        String response = new String(Files.readAllBytes(Paths.get(requireNonNull(ResourceLoader.class.getClassLoader()
                .getResource("responseDocStore.success.json")).toURI())));
        return jsonMapper.fromJson(response, uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse.class);
    }

}
