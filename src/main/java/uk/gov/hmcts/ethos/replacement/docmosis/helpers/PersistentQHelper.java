package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDetails;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.servicebus.CreateUpdatesDto;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.CloseDataModel;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.CreationDataModel;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.CreationSingleDataModel;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.DataModelParent;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.DetachDataModel;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.PreAcceptDataModel;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.RejectDataModel;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.ResetStateDataModel;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.TransferToReformECMDataModel;
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.UpdateDataModel;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.List;

@Slf4j
public class PersistentQHelper {

    private PersistentQHelper() {
    }

    //********************
    /* BULK DETAILS */
    //********************

    public static CreateUpdatesDto getCreateUpdatesDto(BulkDetails bulkDetails, List<String> ethosCaseRefCollection,
                                                       String email, String multipleRef,
                                                       String multipleRefLinkMarkUp) {
        return CreateUpdatesDto.builder()
                .caseTypeId(bulkDetails.getCaseTypeId())
                .jurisdiction(bulkDetails.getJurisdiction())
                .multipleRef(multipleRef)
                .multipleReferenceLinkMarkUp(multipleRefLinkMarkUp)
                .username(email)
                .ethosCaseRefCollection(ethosCaseRefCollection)
                .build();
    }

    public static void sendUpdatesPersistentQ(BulkDetails bulkDetails, String username,
                                              List<String> ethosCaseRefCollection,
                                              DataModelParent dataModelParent, List<String> errors,
                                              String multipleRef, CreateUpdatesBusSender createUpdatesBusSender,
                                              String updateSize, String multipleRefLinkMarkUp) {
        log.info("Case Ref collection: " + ethosCaseRefCollection);
        if (!ethosCaseRefCollection.isEmpty()) {
            var createUpdatesDto = PersistentQHelper.getCreateUpdatesDto(bulkDetails,
                    ethosCaseRefCollection, username, multipleRef, multipleRefLinkMarkUp);

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

    public static void sendSingleUpdatesPersistentQ(String caseTypeId, String jurisdiction, String username,
                                                    List<String> ethosCaseRefCollection,
                                                    DataModelParent dataModelParent,
                                                    List<String> errors, String multipleRef, String confirmation,
                                                    CreateUpdatesBusSender createUpdatesBusSender, String updateSize,
                                                    String multipleReferenceLinkMarkUp
                                                    ) {
        log.info("Case Ref collection: " + ethosCaseRefCollection);
        if (!ethosCaseRefCollection.isEmpty()) {
            var createUpdatesDto = PersistentQHelper.getMultipleCreateUpdatesDto(caseTypeId,
                    jurisdiction, ethosCaseRefCollection, username, multipleRef, confirmation,
                    multipleReferenceLinkMarkUp);

            createUpdatesBusSender.sendUpdatesToQueue(
                    createUpdatesDto,
                    dataModelParent,
                    errors,
                    updateSize);
        } else {
            log.warn("Case Ref collection is empty");
        }
    }

    private static CreateUpdatesDto getMultipleCreateUpdatesDto(String caseTypeId, String jurisdiction,
                                                                List<String> ethosCaseRefCollection, String email,
                                                                String multipleRef, String confirmation,
                                                                String multipleReferenceLinkMarkUp) {
        return CreateUpdatesDto.builder()
                .caseTypeId(caseTypeId)
                .jurisdiction(jurisdiction)
                .multipleRef(multipleRef)
                .multipleReferenceLinkMarkUp(multipleReferenceLinkMarkUp)
                .username(email)
                .confirmation(confirmation)
                .ethosCaseRefCollection(ethosCaseRefCollection)
                .build();
    }

    public static CreationDataModel getCreationDataModel(String lead, String multipleRef,
                                                         String multipleReferenceLinkMarkUp) {
        return CreationDataModel.builder()
                .lead(lead)
                .multipleRef(multipleRef)
                .multipleReferenceLinkMarkUp(multipleReferenceLinkMarkUp)
                .build();
    }

    public static PreAcceptDataModel getPreAcceptDataModel(String dateAccepted) {
        return PreAcceptDataModel.builder()
                .dateAccepted(dateAccepted)
                .build();
    }

    public static RejectDataModel getRejectDataModel(String dateRejected, List<String> rejectedReason) {
        return RejectDataModel.builder()
                .dateRejected(dateRejected)
                .rejectReason(rejectedReason)
                .build();
    }

    public static CloseDataModel getCloseDataModel(MultipleData multipleData) {
        return CloseDataModel.builder()
                .clerkResponsible(multipleData.getClerkResponsible())
                .fileLocation(multipleData.getFileLocation())
                .notes(multipleData.getNotes())
                .managingOffice(multipleData.getManagingOffice())
                .fileLocationGlasgow(multipleData.getFileLocationGlasgow())
                .fileLocationAberdeen(multipleData.getFileLocationAberdeen())
                .fileLocationDundee(multipleData.getFileLocationDundee())
                .fileLocationEdinburgh(multipleData.getFileLocationEdinburgh())
                .build();
    }

    public static DetachDataModel getDetachDataModel() {
        return DetachDataModel.builder()
                .build();
    }

    public static ResetStateDataModel getResetStateModel() {
        return ResetStateDataModel.builder()
                .build();
    }

    public static CreationSingleDataModel getCreationSingleDataModel(String ccdGatewayBaseUrl, String officeCT,
                                                                     String positionTypeCT, String reasonForCT) {
        return CreationSingleDataModel.builder()
                .officeCT(officeCT)
                .positionTypeCT(positionTypeCT)
                .ccdGatewayBaseUrl(ccdGatewayBaseUrl)
                .reasonForCT(reasonForCT)
                .build();
    }

    public static TransferToReformECMDataModel getDataModelForTransferToReformECM(String ccdGatewayBaseUrl, String officeCT,
                                                                                  String positionType, String reasonForCT) {
        return TransferToReformECMDataModel.builder()
                .officeCT(officeCT)
                .positionType(positionType)
                .ccdGatewayBaseUrl(ccdGatewayBaseUrl)
                .reasonForCT(reasonForCT)
                .build();
    }

    public static UpdateDataModel getUpdateDataModel(MultipleData multipleData, CaseData caseData) {
        return UpdateDataModelBuilder.build(multipleData, caseData);
    }
}
