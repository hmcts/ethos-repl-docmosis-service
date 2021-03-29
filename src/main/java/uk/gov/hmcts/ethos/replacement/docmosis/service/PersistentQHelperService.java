package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.PersistentQHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;

@Slf4j
@RequiredArgsConstructor
@Service("persistentQHelperService")
public class PersistentQHelperService {

    private final CreateUpdatesBusSender createUpdatesBusSender;
    private final UserService userService;

    public void sendCreationEventToSinglesWithoutConfirmation(String userToken, String caseTypeId, String jurisdiction,
                                                              List<String> errors, String ethosCaseReference,
                                                              String officeCT, String positionTypeCT,
                                                              String ccdGatewayBaseUrl) {

        String username = userService.getUserDetails(userToken).getEmail();

        PersistentQHelper.sendSingleUpdatesPersistentQ(caseTypeId,
                jurisdiction,
                username,
                new ArrayList<>(Collections.singletonList(ethosCaseReference)),
                PersistentQHelper.getCreationSingleDataModel(ccdGatewayBaseUrl, officeCT, positionTypeCT),
                errors,
                SINGLE_CASE_TYPE,
                NO,
                createUpdatesBusSender,
                "1");

    }

}
