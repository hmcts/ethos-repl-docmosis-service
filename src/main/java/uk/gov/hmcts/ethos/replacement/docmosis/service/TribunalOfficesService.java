package uk.gov.hmcts.ethos.replacement.docmosis.service;

import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.config.TribunalOfficesConfiguration;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice.ContactDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice.TribunalOffice;

@Service
@Slf4j
public class TribunalOfficesService {
    private final TribunalOfficesConfiguration config;

    public TribunalOfficesService(TribunalOfficesConfiguration config) {
        this.config = config;
    }

    public TribunalOffice getTribunalOffice(String owningOffice) {
        if (Strings.isNullOrEmpty(owningOffice)) {
            log.warn(String.format("Unexpected owning office %s therefore defaulting to %s tribunal office", owningOffice,
                    TribunalOffice.MANCHESTER));
            return TribunalOffice.MANCHESTER;
        } else {
            return TribunalOffice.valueOf(owningOffice);
        }
    }

    public ContactDetails getTribunalContactDetails(String caseTypeId) {
        var tribunalName = getTribunalOffice(caseTypeId);
        return config.getContactDetails().get(tribunalName);
    }

}

