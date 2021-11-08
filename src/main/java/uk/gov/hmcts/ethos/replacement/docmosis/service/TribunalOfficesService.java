package uk.gov.hmcts.ethos.replacement.docmosis.service;

import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.config.TribunalOfficesConfiguration;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice.ContactDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice.TribunalOffice;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.ABERDEEN_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DUNDEE_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.EDINBURGH_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_USERS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_USERS_CASE_TYPE_ID;

@Service
@Slf4j
public class TribunalOfficesService {
    private final TribunalOfficesConfiguration config;

    public TribunalOfficesService(TribunalOfficesConfiguration config) {
        this.config = config;
    }

    public ContactDetails getTribunalContactDetails(String caseTypeId, String managingOffice) {
        TribunalOffice tribunalName;

        switch (caseTypeId) {
            case MANCHESTER_DEV_CASE_TYPE_ID:
            case MANCHESTER_USERS_CASE_TYPE_ID:
            case MANCHESTER_CASE_TYPE_ID:
                tribunalName = TribunalOffice.MANCHESTER;
                break;
            case BRISTOL_DEV_CASE_TYPE_ID:
            case BRISTOL_USERS_CASE_TYPE_ID:
            case BRISTOL_CASE_TYPE_ID:
                tribunalName = TribunalOffice.BRISTOL;
                break;
            case LEEDS_DEV_CASE_TYPE_ID:
            case LEEDS_USERS_CASE_TYPE_ID:
            case LEEDS_CASE_TYPE_ID:
                tribunalName = TribunalOffice.LEEDS;
                break;
            case LONDON_CENTRAL_DEV_CASE_TYPE_ID:
            case LONDON_CENTRAL_USERS_CASE_TYPE_ID:
            case LONDON_CENTRAL_CASE_TYPE_ID:
                tribunalName = TribunalOffice.LONDON_CENTRAL;
                break;
            case LONDON_EAST_DEV_CASE_TYPE_ID:
            case LONDON_EAST_USERS_CASE_TYPE_ID:
            case LONDON_EAST_CASE_TYPE_ID:
                tribunalName = TribunalOffice.LONDON_EAST;
                break;
            case LONDON_SOUTH_DEV_CASE_TYPE_ID:
            case LONDON_SOUTH_USERS_CASE_TYPE_ID:
            case LONDON_SOUTH_CASE_TYPE_ID:
                tribunalName = TribunalOffice.LONDON_SOUTH;
                break;
            case MIDLANDS_EAST_DEV_CASE_TYPE_ID:
            case MIDLANDS_EAST_USERS_CASE_TYPE_ID:
            case MIDLANDS_EAST_CASE_TYPE_ID:
                tribunalName = TribunalOffice.MIDLANDS_EAST;
                break;
            case MIDLANDS_WEST_DEV_CASE_TYPE_ID:
            case MIDLANDS_WEST_USERS_CASE_TYPE_ID:
            case MIDLANDS_WEST_CASE_TYPE_ID:
                tribunalName = TribunalOffice.MIDLANDS_WEST;
                break;
            case NEWCASTLE_DEV_CASE_TYPE_ID:
            case NEWCASTLE_USERS_CASE_TYPE_ID:
            case NEWCASTLE_CASE_TYPE_ID:
                tribunalName = TribunalOffice.NEWCASTLE;
                break;
            case WALES_DEV_CASE_TYPE_ID:
            case WALES_USERS_CASE_TYPE_ID:
            case WALES_CASE_TYPE_ID:
                tribunalName = TribunalOffice.WALES;
                break;
            case WATFORD_DEV_CASE_TYPE_ID:
            case WATFORD_USERS_CASE_TYPE_ID:
            case WATFORD_CASE_TYPE_ID:
                tribunalName = TribunalOffice.WATFORD;
                break;
            case SCOTLAND_DEV_CASE_TYPE_ID:
            case SCOTLAND_USERS_CASE_TYPE_ID:
            case SCOTLAND_CASE_TYPE_ID:
                tribunalName = getScottishTribunalOffice(managingOffice);
                break;
            default:
                log.warn(String.format("Unexpected case type %s therefore defaulting to %s tribunal office",
                        caseTypeId, TribunalOffice.MANCHESTER));
                tribunalName = TribunalOffice.MANCHESTER;
        }

        return config.getContactDetails().get(tribunalName);
    }

    private TribunalOffice getScottishTribunalOffice(String managingOffice) {
        if (!Strings.isNullOrEmpty(managingOffice)) {
            switch (managingOffice) {
                case EDINBURGH_OFFICE:
                    return TribunalOffice.EDINBURGH;
                case ABERDEEN_OFFICE:
                    return TribunalOffice.ABERDEEN;
                case DUNDEE_OFFICE:
                    return TribunalOffice.DUNDEE;
                default:
                    log.warn(String.format("Unexpected managing office %s therefore defaulting to %s", managingOffice,
                            TribunalOffice.GLASGOW));
                    return TribunalOffice.GLASGOW;
            }
        } else {
            return TribunalOffice.GLASGOW;
        }
    }
}

