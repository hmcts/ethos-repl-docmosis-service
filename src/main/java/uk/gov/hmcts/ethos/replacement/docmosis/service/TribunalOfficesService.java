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
        TribunalOffice tribunalName = switch (caseTypeId) {
            case MANCHESTER_DEV_CASE_TYPE_ID, MANCHESTER_USERS_CASE_TYPE_ID, MANCHESTER_CASE_TYPE_ID ->
                TribunalOffice.MANCHESTER;
            case BRISTOL_DEV_CASE_TYPE_ID, BRISTOL_USERS_CASE_TYPE_ID, BRISTOL_CASE_TYPE_ID -> TribunalOffice.BRISTOL;
            case LEEDS_DEV_CASE_TYPE_ID, LEEDS_USERS_CASE_TYPE_ID, LEEDS_CASE_TYPE_ID -> TribunalOffice.LEEDS;
            case LONDON_CENTRAL_DEV_CASE_TYPE_ID, LONDON_CENTRAL_USERS_CASE_TYPE_ID, LONDON_CENTRAL_CASE_TYPE_ID ->
                TribunalOffice.LONDON_CENTRAL;
            case LONDON_EAST_DEV_CASE_TYPE_ID, LONDON_EAST_USERS_CASE_TYPE_ID, LONDON_EAST_CASE_TYPE_ID ->
                TribunalOffice.LONDON_EAST;
            case LONDON_SOUTH_DEV_CASE_TYPE_ID, LONDON_SOUTH_USERS_CASE_TYPE_ID, LONDON_SOUTH_CASE_TYPE_ID ->
                TribunalOffice.LONDON_SOUTH;
            case MIDLANDS_EAST_DEV_CASE_TYPE_ID, MIDLANDS_EAST_USERS_CASE_TYPE_ID, MIDLANDS_EAST_CASE_TYPE_ID ->
                TribunalOffice.MIDLANDS_EAST;
            case MIDLANDS_WEST_DEV_CASE_TYPE_ID, MIDLANDS_WEST_USERS_CASE_TYPE_ID, MIDLANDS_WEST_CASE_TYPE_ID ->
                TribunalOffice.MIDLANDS_WEST;
            case NEWCASTLE_DEV_CASE_TYPE_ID, NEWCASTLE_USERS_CASE_TYPE_ID, NEWCASTLE_CASE_TYPE_ID ->
                TribunalOffice.NEWCASTLE;
            case WALES_DEV_CASE_TYPE_ID, WALES_USERS_CASE_TYPE_ID, WALES_CASE_TYPE_ID -> TribunalOffice.WALES;
            case WATFORD_DEV_CASE_TYPE_ID, WATFORD_USERS_CASE_TYPE_ID, WATFORD_CASE_TYPE_ID -> TribunalOffice.WATFORD;
            case SCOTLAND_DEV_CASE_TYPE_ID, SCOTLAND_USERS_CASE_TYPE_ID, SCOTLAND_CASE_TYPE_ID ->
                getScottishTribunalOffice(managingOffice);
            default -> {
                log.warn(String.format("Unexpected case type %s therefore defaulting to %s tribunal office",
                        caseTypeId, TribunalOffice.MANCHESTER));
                yield TribunalOffice.MANCHESTER;
            }
        };

        return config.getContactDetails().get(tribunalName);
    }

    private TribunalOffice getScottishTribunalOffice(String managingOffice) {
        if (!Strings.isNullOrEmpty(managingOffice)) {
            return switch (managingOffice) {
                case EDINBURGH_OFFICE -> TribunalOffice.EDINBURGH;
                case ABERDEEN_OFFICE -> TribunalOffice.ABERDEEN;
                case DUNDEE_OFFICE -> TribunalOffice.DUNDEE;
                default -> {
                    log.warn(String.format("Unexpected managing office %s therefore defaulting to %s", managingOffice,
                        TribunalOffice.GLASGOW));
                    yield TribunalOffice.GLASGOW;
                }
            };
        } else {
            return TribunalOffice.GLASGOW;
        }
    }
}

