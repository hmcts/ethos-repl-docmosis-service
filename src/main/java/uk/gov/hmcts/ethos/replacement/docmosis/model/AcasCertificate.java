package uk.gov.hmcts.ethos.replacement.docmosis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Objects;

/**
 * Simply holds the certificate blob data as a string (in Base64 encoded format) along with the associated ACAS number.
 * You can find detailed documentation in the
 * <a href="https://tools.hmcts.net/confluence/display/RET/ACAS+Integration+Consume">confluence page</a>.
 */
@Data
public class AcasCertificate {

    @JsonProperty("CertificateDocument")
    private String certificateDocument;
    @JsonProperty("CertificateNumber")
    private String certificateNumber;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        AcasCertificate that = (AcasCertificate) object;
        return Objects.equals(certificateDocument, that.certificateDocument)
               && Objects.equals(certificateNumber, that.certificateNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateDocument, certificateNumber);
    }
}
