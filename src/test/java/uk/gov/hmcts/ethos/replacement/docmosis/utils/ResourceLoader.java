package uk.gov.hmcts.ethos.replacement.docmosis.utils;

import uk.gov.hmcts.reform.document.domain.UploadResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class ResourceLoader {
    private static final JsonMapper jsonMapper = JsonMapperFactory.create();

    private ResourceLoader() {
    }

    public static UploadResponse successfulDocumentManagementUploadResponse() throws URISyntaxException, IOException {
        String response = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(ResourceLoader.class.getClassLoader()
                .getResource("response.success.json")).toURI())));
        return jsonMapper.fromJson(response, UploadResponse.class);
    }

    public static UploadResponse unsuccessfulDocumentManagementUploadResponse() throws URISyntaxException, IOException {
        String response = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(ResourceLoader.class.getClassLoader()
                .getResource("response.failure.json")).toURI())));
        return jsonMapper.fromJson(response, UploadResponse.class);
    }

}
