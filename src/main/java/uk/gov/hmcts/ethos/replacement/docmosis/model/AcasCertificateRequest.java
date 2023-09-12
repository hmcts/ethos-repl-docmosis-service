package uk.gov.hmcts.ethos.replacement.docmosis.model;

import java.util.Arrays;

/**
 * Represents the content of a request sent to ACAS, containing the certificate numbers to be requesting certificate
 * data for.
 */
public class AcasCertificateRequest {

    private String[] certificateNumbers;

    public String[] getCertificateNumbers() {
        return Arrays.copyOf(certificateNumbers, certificateNumbers.length);
    }

    public void setCertificateNumbers(String... certificateNumbers) {
        this.certificateNumbers = Arrays.copyOf(certificateNumbers, certificateNumbers.length);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AcasCertificateRequest)) {
            return false;
        }
        AcasCertificateRequest that = (AcasCertificateRequest) object;
        return Arrays.equals(certificateNumbers, that.certificateNumbers);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(certificateNumbers);
    }
}
