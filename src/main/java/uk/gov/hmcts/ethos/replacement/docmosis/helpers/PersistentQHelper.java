package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDetails;
import uk.gov.hmcts.ecm.common.model.servicebus.CreateUpdatesDto;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.CreationDataModel;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.DataModelParent;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.DetachDataModel;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.PreAcceptDataModel;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.List;

@Slf4j
public class PersistentQHelper {

    public static CreateUpdatesDto getCreateUpdatesDto(BulkDetails bulkDetails, List<String> ethosCaseRefCollection,
                                                       String email, String multipleRef) {
        return CreateUpdatesDto.builder()
                .caseTypeId(bulkDetails.getCaseTypeId())
                .jurisdiction(bulkDetails.getJurisdiction())
                .multipleRef(multipleRef)
                .username(email)
                .ethosCaseRefCollection(ethosCaseRefCollection)
                .build();
    }

    public static CreationDataModel getCreationDataModel(String lead, String multipleRef) {
        return CreationDataModel.builder()
                .lead(lead)
                .multipleRef(multipleRef)
                .build();
    }

    public static PreAcceptDataModel getPreAcceptDataModel() {
        return PreAcceptDataModel.builder()
                .build();
    }

    public static DetachDataModel getDetachDataModel() {
        return DetachDataModel.builder()
                .build();
    }

    public static void sendUpdatesPersistentQ(BulkDetails bulkDetails, String username, List<String> ethosCaseRefCollection,
                                              DataModelParent dataModelParent, List<String> errors, String multipleRef,
                                              CreateUpdatesBusSender createUpdatesBusSender) {
        log.info("ETHOS CASE REF COLLECTION: " + ethosCaseRefCollection);
        if (!ethosCaseRefCollection.isEmpty()) {
            CreateUpdatesDto createUpdatesDto = PersistentQHelper.getCreateUpdatesDto(bulkDetails,
                    ethosCaseRefCollection, username, multipleRef);

            createUpdatesBusSender.sendUpdatesToQueue(
                    createUpdatesDto,
                    dataModelParent,
                    errors);
        } else {
            log.info("EMPTY CASE REF COLLECTION");
        }
    }
}
