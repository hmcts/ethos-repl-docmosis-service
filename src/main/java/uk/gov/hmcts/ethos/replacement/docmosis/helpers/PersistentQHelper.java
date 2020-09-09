package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.servicebus.CreateUpdatesDto;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.*;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.List;

@Slf4j
public class PersistentQHelper {

    //********************
    /* BULK DETAILS */
    //********************

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

    public static void sendUpdatesPersistentQ(BulkDetails bulkDetails, String username, List<String> ethosCaseRefCollection,
                                              DataModelParent dataModelParent, List<String> errors, String multipleRef,
                                              CreateUpdatesBusSender createUpdatesBusSender, String updateSize) {
        log.info("Case Ref collection: " + ethosCaseRefCollection);
        if (!ethosCaseRefCollection.isEmpty()) {
            CreateUpdatesDto createUpdatesDto = PersistentQHelper.getCreateUpdatesDto(bulkDetails,
                    ethosCaseRefCollection, username, multipleRef);

            createUpdatesBusSender.sendUpdatesToQueue(
                    createUpdatesDto,
                    dataModelParent,
                    errors,
                    updateSize);
        } else {
            log.info("Case Ref collection is empty");
        }
    }

    //********************
    /* MULTIPLE DETAILS */
    //********************

    public static void sendSingleUpdatesPersistentQ(MultipleDetails multipleDetails, String username, List<String> ethosCaseRefCollection,
                                                    DataModelParent dataModelParent, List<String> errors, String multipleRef,
                                                    CreateUpdatesBusSender createUpdatesBusSender, String updateSize) {
        log.info("Case Ref collection: " + ethosCaseRefCollection);
        if (!ethosCaseRefCollection.isEmpty()) {
            CreateUpdatesDto createUpdatesDto = PersistentQHelper.getMultipleCreateUpdatesDto(multipleDetails,
                    ethosCaseRefCollection, username, multipleRef);

            createUpdatesBusSender.sendUpdatesToQueue(
                    createUpdatesDto,
                    dataModelParent,
                    errors,
                    updateSize);
        } else {
            log.info("Case Ref collection is empty");
        }
    }

    private static CreateUpdatesDto getMultipleCreateUpdatesDto(MultipleDetails multipleDetails, List<String> ethosCaseRefCollection,
                                                                String email, String multipleRef) {
        return CreateUpdatesDto.builder()
                .caseTypeId(multipleDetails.getCaseTypeId())
                .jurisdiction(multipleDetails.getJurisdiction())
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

    public static UpdateDataModel getUpdateDataModel(MultipleData multipleData) {
        return UpdateDataModel.builder()
                //.claimantName(multipleData.g)
                //.claimantRep("ClaimantRep")
                //.respondentRep("RespondentRep")
                .managingOffice(multipleData.getManagingOffice())
                .fileLocation(multipleData.getFileLocation())
                .fileLocationGlasgow(multipleData.getFileLocationGlasgow())
                .fileLocationAberdeen(multipleData.getFileLocationAberdeen())
                .fileLocationDundee(multipleData.getFileLocationDundee())
                .fileLocationEdinburgh(multipleData.getFileLocationEdinburgh())
                //.newMultipleReference("2440001")
                .clerkResponsible(multipleData.getClerkResponsible())
                .positionType(multipleData.getPositionType())
                .receiptDate(multipleData.getReceiptDate())
                .hearingStage(multipleData.getHearingStage())
                //.jurisdictionCode("ECM")
                //.outcomeUpdate("OutcomeUpdate")
                .build();
    }

}
