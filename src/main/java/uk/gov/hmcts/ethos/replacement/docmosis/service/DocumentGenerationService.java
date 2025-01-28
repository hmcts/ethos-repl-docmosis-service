package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bouncycastle.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.DocumentManagementException;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDetails;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDocumentInfo;
import uk.gov.hmcts.ecm.common.model.bulk.BulkRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.AddressLabelTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.AddressLabelsAttributesType;
import uk.gov.hmcts.ecm.common.model.ccd.types.BFActionType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DocumentHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.LabelsHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.ADDRESS_LABELS_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ALL_AVAILABLE_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_ADDRESS;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_AND_CLAIMANT_REP_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_AND_RESPONDENTS_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_AND_RESPONDENTS_REPS_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_REP_ADDRESS;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_REP_AND_RESPONDENTS_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_REP_AND_RESPONDENTS_REPS_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CUSTOMISE_SELECTED_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESPONDENTS_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESPONDENTS_AND_RESPONDENTS_REPS_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESPONDENTS_REPS_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Slf4j
@Service("documentGenerationService")
public class DocumentGenerationService {

    private final TornadoService tornadoService;
    private final CcdClient ccdClient;
    private static final String MESSAGE = "Failed to generate document for case id : ";

    @Autowired
    public DocumentGenerationService(TornadoService tornadoService, CcdClient ccdClient) {
        this.tornadoService = tornadoService;
        this.ccdClient = ccdClient;
    }

    public CaseData midAddressLabels(CaseData caseData) {
        String templateName = DocumentHelper.getTemplateName(caseData.getCorrespondenceType(),
                caseData.getCorrespondenceScotType());
        log.info("midAddressLabels - templateName : " + templateName);
        if (templateName.equals(ADDRESS_LABELS_TEMPLATE)) {
            String ewSection = DocumentHelper.getEWSectionName(caseData.getCorrespondenceType());
            caseData.setAddressLabelCollection(new ArrayList<>());
            String sectionName = ewSection.equals("")
                    ? DocumentHelper.getScotSectionName(caseData.getCorrespondenceScotType())
                    : ewSection;
            log.info("midAddressLabels - sectionName : " + sectionName);
            switch (sectionName) {
                case CUSTOMISE_SELECTED_ADDRESSES:
                    caseData.setAddressLabelCollection(customiseSelectedAddresses(caseData));
                    break;
                case ALL_AVAILABLE_ADDRESSES:
                    caseData.setAddressLabelCollection(allAvailableAddresses(caseData));
                    break;
                case CLAIMANT_ADDRESS:
                    caseData.setAddressLabelCollection(claimantAddress(caseData));
                    break;
                case CLAIMANT_REP_ADDRESS:
                    caseData.setAddressLabelCollection(claimantRepAddress(caseData));
                    break;
                case CLAIMANT_AND_CLAIMANT_REP_ADDRESSES:
                    caseData.setAddressLabelCollection(claimantAndClaimantRepAddresses(caseData));
                    break;
                case RESPONDENTS_ADDRESSES:
                    caseData.setAddressLabelCollection(respondentsAddresses(caseData));
                    break;
                case RESPONDENTS_REPS_ADDRESSES:
                    caseData.setAddressLabelCollection(respondentsRepsAddresses(caseData));
                    break;
                case RESPONDENTS_AND_RESPONDENTS_REPS_ADDRESSES:
                    caseData.setAddressLabelCollection(respondentsAndRespondentsRepsAddresses(caseData));
                    break;
                case CLAIMANT_AND_RESPONDENTS_ADDRESSES:
                    caseData.setAddressLabelCollection(claimantAndRespondentsAddresses(caseData));
                    break;
                case CLAIMANT_REP_AND_RESPONDENTS_REPS_ADDRESSES:
                    caseData.setAddressLabelCollection(claimantRepAndRespondentsRepsAddresses(caseData));
                    break;
                case CLAIMANT_AND_RESPONDENTS_REPS_ADDRESSES:
                    caseData.setAddressLabelCollection(claimantAndRespondentsRepsAddresses(caseData));
                    break;
                case CLAIMANT_REP_AND_RESPONDENTS_ADDRESSES:
                    caseData.setAddressLabelCollection(claimantRepAndRespondentsAddresses(caseData));
                    break;
                default:
                    return caseData;
            }
        } else {
            caseData.setAddressLabelCollection(null);
        }

        return caseData;

    }

    public CaseData midSelectedAddressLabels(CaseData caseData) {
        caseData.setAddressLabelsAttributesType(new AddressLabelsAttributesType());
        List<AddressLabelTypeItem> selectedAddressLabels =
                DocumentHelper.getSelectedAddressLabels(caseData.getAddressLabelCollection());
        caseData.getAddressLabelsAttributesType()
                .setNumberOfSelectedLabels(String.valueOf(selectedAddressLabels.size()));
        return caseData;
    }

    public List<String> midValidateAddressLabels(CaseData caseData) {

        return LabelsHelper.midValidateAddressLabelsErrors(caseData.getAddressLabelsAttributesType(), SINGLE_CASE_TYPE);

    }

    public void clearUserChoices(CaseDetails caseDetails) {
        var caseData = caseDetails.getCaseData();

        if (caseDetails.getCaseTypeId().equals(SCOTLAND_CASE_TYPE_ID)) {
            caseData.setCorrespondenceScotType(null);
        } else {
            caseData.setCorrespondenceType(null);
        }

        caseData.setAddressLabelsSelectionType(null);
        caseData.setAddressLabelCollection(null);
        caseData.setAddressLabelsAttributesType(null);
    }

    public void clearUserChoicesForMultiples(BulkDetails bulkDetails) {
        var bulkData = bulkDetails.getCaseData();

        if (bulkDetails.getCaseTypeId().equals(SCOTLAND_BULK_CASE_TYPE_ID)) {
            bulkData.setCorrespondenceScotType(null);
        } else {
            bulkData.setCorrespondenceType(null);
        }
    }

    public DocumentInfo processDocumentRequest(CCDRequest ccdRequest, String authToken) {
        var caseDetails = ccdRequest.getCaseDetails();
        try {
            return tornadoService.documentGeneration(
                    authToken, caseDetails.getCaseData(), caseDetails.getCaseTypeId(),
                    caseDetails.getCaseData().getCorrespondenceType(),
                    caseDetails.getCaseData().getCorrespondenceScotType(), null);
        } catch (Exception ex) {
            throw new DocumentManagementException(MESSAGE + caseDetails.getCaseId() + ex.getMessage());
        }
    }

    public CaseData updateBfActions(DocumentInfo documentInfo, CaseData caseData) {
        var sectionName = Strings.split(documentInfo.getDescription(), '_')[1];
        if (areBfActionsForEnglandOrWalesToBeUpdated(caseData, sectionName)
            || areBfActionsForScotlandToBeUpdated(caseData, sectionName)) {
            return setBfActions(caseData);
        }
        return caseData;
    }

    public boolean areBfActionsForEnglandOrWalesToBeUpdated(CaseData caseData, String sectionName) {
        if (caseData.getCorrespondenceType() != null) {
            var values = new String[]{"2.6", "2.7", "2.8", "2.7A", "2.8A"};
            return Arrays.asList(values).contains(sectionName);
        }
        return false;
    }

    public boolean areBfActionsForScotlandToBeUpdated(CaseData caseData, String sectionName) {
        if (caseData.getCorrespondenceScotType() != null) {
            var values = new String[]{"7", "72", "75", "76", "77.2"};
            return Arrays.asList(values).contains(sectionName);
        }
        return false;
    }

    public CaseData setBfActions(CaseData caseData) {

        var bfActionType = new BFActionType();
        bfActionType.setLetters(YES);
        bfActionType.setDateEntered(LocalDate.now().toString());
        bfActionType.setCwActions("Other action");
        bfActionType.setAllActions("Claim served");
        bfActionType.setBfDate(LocalDate.now().plusDays(29).toString());
        var bfActionTypeItem = new BFActionTypeItem();
        bfActionTypeItem.setId(UUID.randomUUID().toString());
        bfActionTypeItem.setValue(bfActionType);

        if (CollectionUtils.isEmpty(caseData.getBfActions())) {
            caseData.setBfActions(new ArrayList<>(Collections.singletonList(bfActionTypeItem)));
            caseData.setClaimServedDate(bfActionType.getDateEntered());
        } else {
            List<BFActionTypeItem> tmp = caseData.getBfActions();
            tmp.add(bfActionTypeItem);
            caseData.setBfActions(tmp);
            var dateEntered = caseData.getBfActions().get(0).getValue().getDateEntered().substring(0, 10);
            LocalDate date = LocalDate.parse(dateEntered);
            caseData.setClaimServedDate(String.valueOf(date));
        }
        return caseData;
    }

    public BulkDocumentInfo processBulkDocumentRequest(BulkRequest bulkRequest, String authToken) {
        var bulkDocumentInfo = new BulkDocumentInfo();
        var bulkDetails = bulkRequest.getCaseDetails();
        List<String> errors = new ArrayList<>();
        var markUps = "";
        try {
            List<DocumentInfo> documentInfoList = new ArrayList<>();
            if (bulkDetails.getCaseData().getSearchCollection() != null
                    && !bulkDetails.getCaseData().getSearchCollection().isEmpty()) {
                List<String> caseIds =
                        BulkHelper.getEthosRefNumsFromSearchCollection(bulkDetails.getCaseData().getSearchCollection());
                List<SubmitEvent> submitEventList = ccdClient.retrieveCasesElasticSearch(authToken,
                        UtilHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), caseIds);
                for (SubmitEvent submitEvent : submitEventList) {
                    log.info("Generating document for: " + submitEvent.getCaseData().getEthosCaseReference());
                    submitEvent.getCaseData().setCorrespondenceType(bulkDetails.getCaseData().getCorrespondenceType());
                    submitEvent.getCaseData().setCorrespondenceScotType(
                            bulkDetails.getCaseData().getCorrespondenceScotType());
                    documentInfoList.add(tornadoService.documentGeneration(authToken, submitEvent.getCaseData(),
                            bulkDetails.getCaseTypeId(), submitEvent.getCaseData().getCorrespondenceType(),
                            submitEvent.getCaseData().getCorrespondenceScotType(), null));
                }
            }
            if (documentInfoList.isEmpty()) {
                errors.add("There are not cases searched to generate letters");
            } else {
                markUps = documentInfoList.stream().map(DocumentInfo::getMarkUp)
                        .collect(Collectors.joining(", "));
            }
        } catch (Exception ex) {
            throw new DocumentManagementException(MESSAGE + bulkDetails.getCaseId() + ex.getMessage());
        }
        bulkDocumentInfo.setErrors(errors);
        bulkDocumentInfo.setMarkUps(markUps);
        log.info("Markups: " + markUps);
        return bulkDocumentInfo;
    }

    public BulkDocumentInfo processBulkScheduleRequest(BulkRequest bulkRequest, String authToken) {
        var bulkDocumentInfo = new BulkDocumentInfo();
        var bulkData = bulkRequest.getCaseDetails().getCaseData();
        var caseTypeId = bulkRequest.getCaseDetails().getCaseTypeId();
        List<String> errors = new ArrayList<>();
        var documentInfo = new DocumentInfo();
        try {
            if (bulkData.getSearchCollection() != null && !bulkData.getSearchCollection().isEmpty()) {
                documentInfo = tornadoService.scheduleGeneration(authToken, bulkData, caseTypeId);
            } else {
                errors.add("There are not cases searched to generate schedules");
            }
        } catch (Exception ex) {
            throw new DocumentManagementException(MESSAGE + bulkRequest.getCaseDetails().getCaseId() + ex.getMessage());
        }
        bulkDocumentInfo.setErrors(errors);
        bulkDocumentInfo.setMarkUps(documentInfo.getMarkUp() != null ? documentInfo.getMarkUp() : " ");
        bulkDocumentInfo.setDocumentInfo(documentInfo);
        return bulkDocumentInfo;
    }

    private List<AddressLabelTypeItem> customiseSelectedAddresses(CaseData caseData) {

        if (caseData.getAddressLabelsSelectionType() != null) {
            var addressLabelsSelection = caseData.getAddressLabelsSelectionType();
            if (addressLabelsSelection.getClaimantAddressLabel() != null
                    && addressLabelsSelection.getClaimantRepAddressLabel() != null
                    && addressLabelsSelection.getRespondentsAddressLabel() != null
                    && addressLabelsSelection.getRespondentsRepsAddressLabel() != null) {

                List<AddressLabelTypeItem> addressLabelTypeItems = new ArrayList<>();

                String printClaimantLabel = addressLabelsSelection.getClaimantAddressLabel();
                String printClaimantRepLabel = addressLabelsSelection.getClaimantRepAddressLabel();
                String printRespondentsLabels = addressLabelsSelection.getRespondentsAddressLabel();

                addressLabelTypeItems.add(LabelsHelper.getClaimantAddressLabelCaseData(caseData, printClaimantLabel));

                var addressLabelTypeItem =
                        LabelsHelper.getClaimantRepAddressLabelCaseData(caseData, printClaimantRepLabel);
                if (addressLabelTypeItem != null) {
                    addressLabelTypeItems.add(addressLabelTypeItem);
                }

                List<AddressLabelTypeItem> addressLabelTypeItemsAux =
                        LabelsHelper.getRespondentsAddressLabelsCaseData(caseData, printRespondentsLabels);
                if (!addressLabelTypeItemsAux.isEmpty()) {
                    addressLabelTypeItems.addAll(addressLabelTypeItemsAux);
                }

                String printRespondentsRepsLabels = addressLabelsSelection.getRespondentsRepsAddressLabel();
                addressLabelTypeItemsAux =
                        LabelsHelper.getRespondentsRepsAddressLabelsCaseData(caseData, printRespondentsRepsLabels);
                if (!addressLabelTypeItemsAux.isEmpty()) {
                    addressLabelTypeItems.addAll(addressLabelTypeItemsAux);
                }

                return addressLabelTypeItems;

            } else {

                return null;

            }

        } else {

            return null;

        }

    }

    private List<AddressLabelTypeItem> allAvailableAddresses(CaseData caseData) {

        List<AddressLabelTypeItem> addressLabelTypeItems = new ArrayList<>();

        addressLabelTypeItems.add(LabelsHelper.getClaimantAddressLabelCaseData(caseData, YES));

        var addressLabelTypeItem = LabelsHelper.getClaimantRepAddressLabelCaseData(caseData, YES);
        if (addressLabelTypeItem != null) {
            addressLabelTypeItems.add(addressLabelTypeItem);
        }

        List<AddressLabelTypeItem> addressLabelTypeItemsAux =
                LabelsHelper.getRespondentsAddressLabelsCaseData(caseData, YES);
        if (!addressLabelTypeItemsAux.isEmpty()) {
            addressLabelTypeItems.addAll(addressLabelTypeItemsAux);
        }

        addressLabelTypeItemsAux = LabelsHelper.getRespondentsRepsAddressLabelsCaseData(caseData, YES);
        if (!addressLabelTypeItemsAux.isEmpty()) {
            addressLabelTypeItems.addAll(addressLabelTypeItemsAux);
        }

        return addressLabelTypeItems;

    }

    private List<AddressLabelTypeItem> claimantAddress(CaseData caseData) {

        List<AddressLabelTypeItem> addressLabelTypeItems = new ArrayList<>();

        addressLabelTypeItems.add(LabelsHelper.getClaimantAddressLabelCaseData(caseData, YES));

        return addressLabelTypeItems;

    }

    private List<AddressLabelTypeItem> claimantRepAddress(CaseData caseData) {

        List<AddressLabelTypeItem> addressLabelTypeItems = new ArrayList<>();

        var addressLabelTypeItem = LabelsHelper.getClaimantRepAddressLabelCaseData(caseData, YES);
        if (addressLabelTypeItem != null) {
            addressLabelTypeItems.add(addressLabelTypeItem);
        }

        return addressLabelTypeItems;

    }

    private List<AddressLabelTypeItem> claimantAndClaimantRepAddresses(CaseData caseData) {

        List<AddressLabelTypeItem> addressLabelTypeItems = new ArrayList<>();

        addressLabelTypeItems.add(LabelsHelper.getClaimantAddressLabelCaseData(caseData, YES));

        var addressLabelTypeItem = LabelsHelper.getClaimantRepAddressLabelCaseData(caseData, YES);
        if (addressLabelTypeItem != null) {
            addressLabelTypeItems.add(addressLabelTypeItem);
        }

        return addressLabelTypeItems;

    }

    private List<AddressLabelTypeItem> respondentsAddresses(CaseData caseData) {

        List<AddressLabelTypeItem> addressLabelTypeItems = new ArrayList<>();

        List<AddressLabelTypeItem> addressLabelTypeItemsAux =
                LabelsHelper.getRespondentsAddressLabelsCaseData(caseData, YES);
        if (!addressLabelTypeItemsAux.isEmpty()) {
            addressLabelTypeItems.addAll(addressLabelTypeItemsAux);
        }

        return addressLabelTypeItems;

    }

    private List<AddressLabelTypeItem> respondentsRepsAddresses(CaseData caseData) {

        List<AddressLabelTypeItem> addressLabelTypeItems = new ArrayList<>();

        List<AddressLabelTypeItem> addressLabelTypeItemsAux =
                LabelsHelper.getRespondentsRepsAddressLabelsCaseData(caseData, YES);
        if (!addressLabelTypeItemsAux.isEmpty()) {
            addressLabelTypeItems.addAll(addressLabelTypeItemsAux);
        }

        return addressLabelTypeItems;

    }

    private List<AddressLabelTypeItem> respondentsAndRespondentsRepsAddresses(CaseData caseData) {

        List<AddressLabelTypeItem> addressLabelTypeItems = new ArrayList<>();

        List<AddressLabelTypeItem> addressLabelTypeItemsAux =
                LabelsHelper.getRespondentsAddressLabelsCaseData(caseData, YES);
        if (!addressLabelTypeItemsAux.isEmpty()) {
            addressLabelTypeItems.addAll(addressLabelTypeItemsAux);
        }

        addressLabelTypeItemsAux = LabelsHelper.getRespondentsRepsAddressLabelsCaseData(caseData, YES);
        if (!addressLabelTypeItemsAux.isEmpty()) {
            addressLabelTypeItems.addAll(addressLabelTypeItemsAux);
        }

        return addressLabelTypeItems;

    }

    private List<AddressLabelTypeItem> claimantAndRespondentsAddresses(CaseData caseData) {

        List<AddressLabelTypeItem> addressLabelTypeItems = new ArrayList<>();

        addressLabelTypeItems.add(LabelsHelper.getClaimantAddressLabelCaseData(caseData, YES));

        List<AddressLabelTypeItem> addressLabelTypeItemsAux =
                LabelsHelper.getRespondentsAddressLabelsCaseData(caseData, YES);
        if (!addressLabelTypeItemsAux.isEmpty()) {
            addressLabelTypeItems.addAll(addressLabelTypeItemsAux);
        }

        return addressLabelTypeItems;

    }

    private List<AddressLabelTypeItem> claimantRepAndRespondentsRepsAddresses(CaseData caseData) {

        List<AddressLabelTypeItem> addressLabelTypeItems = new ArrayList<>();

        var addressLabelTypeItem = LabelsHelper.getClaimantRepAddressLabelCaseData(caseData, YES);
        if (addressLabelTypeItem != null) {
            addressLabelTypeItems.add(addressLabelTypeItem);
        }

        List<AddressLabelTypeItem> addressLabelTypeItemsAux =
                LabelsHelper.getRespondentsRepsAddressLabelsCaseData(caseData, YES);
        if (!addressLabelTypeItemsAux.isEmpty()) {
            addressLabelTypeItems.addAll(addressLabelTypeItemsAux);
        }

        return addressLabelTypeItems;

    }

    private List<AddressLabelTypeItem> claimantAndRespondentsRepsAddresses(CaseData caseData) {

        List<AddressLabelTypeItem> addressLabelTypeItems = new ArrayList<>();

        addressLabelTypeItems.add(LabelsHelper.getClaimantAddressLabelCaseData(caseData, YES));

        List<AddressLabelTypeItem> addressLabelTypeItemsAux =
                LabelsHelper.getRespondentsRepsAddressLabelsCaseData(caseData, YES);
        if (!addressLabelTypeItemsAux.isEmpty()) {
            addressLabelTypeItems.addAll(addressLabelTypeItemsAux);
        }

        return addressLabelTypeItems;

    }

    private List<AddressLabelTypeItem> claimantRepAndRespondentsAddresses(CaseData caseData) {

        List<AddressLabelTypeItem> addressLabelTypeItems = new ArrayList<>();

        var addressLabelTypeItem = LabelsHelper.getClaimantRepAddressLabelCaseData(caseData, YES);
        if (addressLabelTypeItem != null) {
            addressLabelTypeItems.add(addressLabelTypeItem);
        }

        List<AddressLabelTypeItem> addressLabelTypeItemsAux =
                LabelsHelper.getRespondentsAddressLabelsCaseData(caseData, YES);
        if (!addressLabelTypeItemsAux.isEmpty()) {
            addressLabelTypeItems.addAll(addressLabelTypeItemsAux);
        }

        return addressLabelTypeItems;

    }

}
