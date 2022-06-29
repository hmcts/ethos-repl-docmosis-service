package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.PersistentQHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service("persistentQHelperService")
public class PersistentQHelperService {

    private final CreateUpdatesBusSender createUpdatesBusSender;
    private final UserService userService;

    public void sendCreationEventToSingles(String userToken, String caseTypeId, String jurisdiction,
                                           List<String> errors, List<String> ethosCaseRefCollection, String officeCT,
                                           String positionTypeCT, String ccdGatewayBaseUrl,
                                           String reasonForCT, String multipleRef, String confirmation,
                                           String multipleReferenceLinkMarkUp) {

        String username = userService.getUserDetails(userToken).getEmail();

        PersistentQHelper.sendSingleUpdatesPersistentQ(caseTypeId,
                jurisdiction,
                username,
                ethosCaseRefCollection,
                PersistentQHelper.getCreationSingleDataModel(ccdGatewayBaseUrl, officeCT, positionTypeCT, reasonForCT),
                errors,
                multipleRef,
                confirmation,
                createUpdatesBusSender,
                String.valueOf(ethosCaseRefCollection.size()),
                multipleReferenceLinkMarkUp
        );

    }

    public void sendCreationEventToSinglesReformECM(String userToken, String caseTypeId, String jurisdiction,
                                           List<String> errors, List<String> ethosCaseRefCollection, String officeCT,
                                           String positionTypeCT, String ccdGatewayBaseUrl,
                                           String reasonForCT, String multipleRef, String confirmation,
                                           String multipleReferenceLinkMarkUp) {

        String username = userService.getUserDetails(userToken).getEmail();

        PersistentQHelper.sendSingleUpdatesPersistentQ(caseTypeId,
                jurisdiction,
                username,
                ethosCaseRefCollection,
                PersistentQHelper.getDataModelForTransferToReformECM(ccdGatewayBaseUrl, officeCT, positionTypeCT,
                        reasonForCT),
                errors,
                multipleRef,
                confirmation,
                createUpdatesBusSender,
                String.valueOf(ethosCaseRefCollection.size()),
                multipleReferenceLinkMarkUp
        );

    }

}
