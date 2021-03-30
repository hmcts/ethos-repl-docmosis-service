package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDetails;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
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
import uk.gov.hmcts.ecm.common.model.servicebus.datamodel.UpdateDataModel;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.List;
import java.util.Optional;

@Slf4j
public class PersistentQHelper {

    private PersistentQHelper() {
    }

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

    public static void sendUpdatesPersistentQ(BulkDetails bulkDetails, String username,
                                              List<String> ethosCaseRefCollection,
                                              DataModelParent dataModelParent, List<String> errors,
                                              String multipleRef, CreateUpdatesBusSender createUpdatesBusSender,
                                              String updateSize) {
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

    public static void sendSingleUpdatesPersistentQ(String caseTypeId, String jurisdiction, String username,
                                                    List<String> ethosCaseRefCollection,
                                                    DataModelParent dataModelParent,
                                                    List<String> errors, String multipleRef, String confirmation,
                                                    CreateUpdatesBusSender createUpdatesBusSender, String updateSize) {
        log.info("Case Ref collection: " + ethosCaseRefCollection);
        if (!ethosCaseRefCollection.isEmpty()) {
            CreateUpdatesDto createUpdatesDto = PersistentQHelper.getMultipleCreateUpdatesDto(caseTypeId, jurisdiction,
                    ethosCaseRefCollection, username, multipleRef, confirmation);

            createUpdatesBusSender.sendUpdatesToQueue(
                    createUpdatesDto,
                    dataModelParent,
                    errors,
                    updateSize);
        } else {
            log.info("Case Ref collection is empty");
        }
    }

    private static CreateUpdatesDto getMultipleCreateUpdatesDto(String caseTypeId, String jurisdiction,
                                                                List<String> ethosCaseRefCollection, String email,
                                                                String multipleRef, String confirmation) {
        return CreateUpdatesDto.builder()
                .caseTypeId(caseTypeId)
                .jurisdiction(jurisdiction)
                .multipleRef(multipleRef)
                .username(email)
                .confirmation(confirmation)
                .ethosCaseRefCollection(ethosCaseRefCollection)
                .build();
    }

    public static CreationDataModel getCreationDataModel(String lead, String multipleRef) {
        return CreationDataModel.builder()
                .lead(lead)
                .multipleRef(multipleRef)
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

    public static CreationSingleDataModel getCreationSingleDataModel(String ccdGatewayBaseUrl,
                                                                     String officeCT, String positionTypeCT) {
        return CreationSingleDataModel.builder()
                .officeCT(officeCT)
                .positionTypeCT(positionTypeCT)
                .ccdGatewayBaseUrl(ccdGatewayBaseUrl)
                .build();
    }

    public static UpdateDataModel getUpdateDataModel(MultipleData multipleData, CaseData caseData) {
        return UpdateDataModel.builder()
                .managingOffice(multipleData.getManagingOffice())
                .fileLocation(multipleData.getFileLocation())
                .fileLocationGlasgow(multipleData.getFileLocationGlasgow())
                .fileLocationAberdeen(multipleData.getFileLocationAberdeen())
                .fileLocationDundee(multipleData.getFileLocationDundee())
                .fileLocationEdinburgh(multipleData.getFileLocationEdinburgh())
                .clerkResponsible(multipleData.getClerkResponsible())
                .positionType(multipleData.getPositionType())
                .receiptDate(multipleData.getReceiptDate())
                .hearingStage(multipleData.getHearingStage())
                .representativeClaimantType(caseData != null && caseData.getRepresentativeClaimantType() != null
                        ? caseData.getRepresentativeClaimantType()
                        : null)
                .jurCodesType(getJurCodesType(multipleData, caseData))
                .respondentSumType(getRespondentSumType(multipleData, caseData))
                .build();
    }

    private static JurCodesType getJurCodesType(MultipleData multipleData, CaseData caseData) {

        if (caseData != null) {

            List<JurCodesTypeItem> jurCodesCollection = caseData.getJurCodesCollection();

            if (multipleData.getBatchUpdateJurisdiction().getValue() != null
                    && jurCodesCollection != null) {

                String jurCodeToSearch = multipleData.getBatchUpdateJurisdiction().getValue().getLabel();

                Optional<JurCodesTypeItem> jurCodesTypeItemOptional =
                        jurCodesCollection.stream()
                                .filter(jurCodesTypeItem ->
                                        jurCodesTypeItem.getValue().getJuridictionCodesList().equals(jurCodeToSearch))
                                .findAny();

                if (jurCodesTypeItemOptional.isPresent()) {

                    return jurCodesTypeItemOptional.get().getValue();

                }

            }

        }

        return null;

    }

    private static RespondentSumType getRespondentSumType(MultipleData multipleData, CaseData caseData) {

        if (caseData != null) {

            List<RespondentSumTypeItem> respondentCollection = caseData.getRespondentCollection();

            if (multipleData.getBatchUpdateRespondent().getValue() != null
                    && respondentCollection != null) {

                String respondentToSearch = multipleData.getBatchUpdateRespondent().getValue().getLabel();

                Optional<RespondentSumTypeItem> respondentSumTypeItemOptional =
                        respondentCollection.stream()
                                .filter(respondentSumTypeItem ->
                                        respondentSumTypeItem.getValue().getRespondentName().equals(respondentToSearch))
                                .findAny();

                if (respondentSumTypeItemOptional.isPresent()) {

                    return respondentSumTypeItemOptional.get().getValue();

                }

            }

        }

        return null;

    }

}
