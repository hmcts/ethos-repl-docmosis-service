package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.DocumentManagementException;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.bulk.BulkData;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDetails;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDocumentInfo;
import uk.gov.hmcts.ecm.common.model.bulk.BulkRequest;
import uk.gov.hmcts.ecm.common.model.ccd.Address;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.AddressLabelTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.AddressLabelType;
import uk.gov.hmcts.ecm.common.model.ccd.types.AddressLabelsAttributesType;
import uk.gov.hmcts.ecm.common.model.ccd.types.AddressLabelsSelectionType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeC;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeR;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ADDRESS_LABELS_COPIES_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ADDRESS_LABELS_SELECT_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ADDRESS_LABELS_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ALL_AVAILABLE_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_ADDRESS;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_AND_CLAIMANT_REP_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_AND_RESPONDENTS_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_AND_RESPONDENTS_REPS_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_REP;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_REP_ADDRESS;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_REP_AND_RESPONDENTS_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_REP_AND_RESPONDENTS_REPS_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CUSTOMISE_SELECTED_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.INDIVIDUAL_TYPE_CLAIMANT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.REF;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESPONDENT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESPONDENTS_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESPONDENTS_AND_RESPONDENTS_REPS_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESPONDENTS_REPS_ADDRESSES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESPONDENT_REP;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TEL;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.getActiveRespondents;

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
        String templateName = Helper.getTemplateName(caseData.getCorrespondenceType(), caseData.getCorrespondenceScotType());
        log.info("midAddressLabels - templateName : " + templateName);
        if (templateName.equals(ADDRESS_LABELS_TEMPLATE)) {
            String ewSection = Helper.getEWSectionName(caseData.getCorrespondenceType());
            caseData.setAddressLabelCollection(new ArrayList<>());
            String sectionName = ewSection.equals("")
                    ? Helper.getScotSectionName(caseData.getCorrespondenceScotType())
                    : ewSection;
            log.info("midAddressLabels - sectionName : " + sectionName);
            switch (sectionName) {
                case CUSTOMISE_SELECTED_ADDRESSES:
                    customiseSelectedAddresses(caseData);
                    break;
                case ALL_AVAILABLE_ADDRESSES:
                    allAvailableAddresses(caseData);
                    break;
                case CLAIMANT_ADDRESS:
                    claimantAddress(caseData);
                    break;
                case CLAIMANT_REP_ADDRESS:
                    claimantRepAddress(caseData);
                    break;
                case CLAIMANT_AND_CLAIMANT_REP_ADDRESSES:
                    claimantAndClaimantRepAddresses(caseData);
                    break;
                case RESPONDENTS_ADDRESSES:
                    respondentsAddresses(caseData);
                    break;
                case RESPONDENTS_REPS_ADDRESSES:
                    respondentsRepsAddresses(caseData);
                    break;
                case RESPONDENTS_AND_RESPONDENTS_REPS_ADDRESSES:
                    respondentsAndRespondentsRepsAddresses(caseData);
                    break;
                case CLAIMANT_AND_RESPONDENTS_ADDRESSES:
                    claimantAndRespondentsAddresses(caseData);
                    break;
                case CLAIMANT_REP_AND_RESPONDENTS_REPS_ADDRESSES:
                    claimantRepAndRespondentsRepsAddresses(caseData);
                    break;
                case CLAIMANT_AND_RESPONDENTS_REPS_ADDRESSES:
                    claimantAndRespondentsRepsAddresses(caseData);
                    break;
                case CLAIMANT_REP_AND_RESPONDENTS_ADDRESSES:
                    claimantRepAndRespondentsAddresses(caseData);
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
        List<AddressLabelTypeItem> selectedAddressLabels = Helper.getSelectedAddressLabels(caseData);
        caseData.getAddressLabelsAttributesType().setNumberOfSelectedLabels(String.valueOf(selectedAddressLabels.size()));
        return caseData;
    }

    public List<String> midValidateAddressLabels(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        if (Integer.parseInt(caseData.getAddressLabelsAttributesType().getNumberOfSelectedLabels()) == 0) {
            errors.add(ADDRESS_LABELS_SELECT_ERROR);
        }
        if (caseData.getAddressLabelsAttributesType().getNumberOfCopies().contains(".")) {
            errors.add(ADDRESS_LABELS_COPIES_ERROR);
        }
        return errors;
    }

    public void clearUserChoices(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getCaseData();

        if(caseDetails.getCaseTypeId().equals(SCOTLAND_CASE_TYPE_ID)) {
            caseData.setCorrespondenceScotType(null);
        } else {
            caseData.setCorrespondenceType(null);
        }

        caseData.setAddressLabelsSelectionType(null);
        caseData.setAddressLabelCollection(null);
        caseData.setAddressLabelsAttributesType(null);
    }

    public void clearUserChoicesForMultiples(BulkDetails bulkDetails) {
        BulkData bulkData = bulkDetails.getCaseData();

        if(bulkDetails.getCaseTypeId().equals(SCOTLAND_BULK_CASE_TYPE_ID)) {
            bulkData.setCorrespondenceScotType(null);
        } else {
            bulkData.setCorrespondenceType(null);
        }
    }

    public DocumentInfo processDocumentRequest(CCDRequest ccdRequest, String authToken) {
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        try {
            return tornadoService.documentGeneration(authToken, caseDetails.getCaseData(), caseDetails.getCaseTypeId(),
                    caseDetails.getCaseData().getCorrespondenceType(), caseDetails.getCaseData().getCorrespondenceScotType());
        } catch (Exception ex) {
            throw new DocumentManagementException(MESSAGE + caseDetails.getCaseId() + ex.getMessage());
        }
    }

    public BulkDocumentInfo processBulkDocumentRequest(BulkRequest bulkRequest, String authToken) {
        BulkDocumentInfo bulkDocumentInfo = new BulkDocumentInfo();
        BulkDetails bulkDetails = bulkRequest.getCaseDetails();
        List<String> errors = new ArrayList<>();
        String markUps = "";
        try {
            List<DocumentInfo> documentInfoList = new ArrayList<>();
//            if (bulkDetails.getCaseData().getSearchCollection() != null && !bulkDetails.getCaseData().getSearchCollection().isEmpty()) {
//                List<CaseData> caseDataResultList = filterCasesById(ccdClient.retrieveCases(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()),
//                        bulkDetails.getJurisdiction()), bulkDetails.getCaseData().getSearchCollection());
//                for (CaseData caseData : caseDataResultList) {
//                    log.info("Generating document for: " + caseData.getEthosCaseReference());
//                    caseData.setCorrespondenceType(bulkDetails.getCaseData().getCorrespondenceType());
//                    caseData.setCorrespondenceScotType(bulkDetails.getCaseData().getCorrespondenceScotType());
//                    documentInfoList.add(tornadoService.documentGeneration(authToken, caseData));
//                }
//            }
            if (bulkDetails.getCaseData().getSearchCollection() != null && !bulkDetails.getCaseData().getSearchCollection().isEmpty()) {
                List<String> caseIds = BulkHelper.getEthosRefNumsFromSearchCollection(bulkDetails.getCaseData().getSearchCollection());
                List<SubmitEvent> submitEventList = ccdClient.retrieveCasesElasticSearch(authToken,
                        UtilHelper.getCaseTypeId(bulkDetails.getCaseTypeId()), caseIds);
                for (SubmitEvent submitEvent : submitEventList) {
                    log.info("Generating document for: " + submitEvent.getCaseData().getEthosCaseReference());
                    submitEvent.getCaseData().setCorrespondenceType(bulkDetails.getCaseData().getCorrespondenceType());
                    submitEvent.getCaseData().setCorrespondenceScotType(bulkDetails.getCaseData().getCorrespondenceScotType());
                    documentInfoList.add(tornadoService.documentGeneration(authToken, submitEvent.getCaseData(),
                            bulkDetails.getCaseTypeId(), submitEvent.getCaseData().getCorrespondenceType(),
                            submitEvent.getCaseData().getCorrespondenceScotType()));
                }
            }
            if (documentInfoList.isEmpty()) {
                errors.add("There are not cases searched to generate letters");
            } else {
                markUps = documentInfoList.stream().map(DocumentInfo::getMarkUp).collect(Collectors.joining(", "));
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
        BulkDocumentInfo bulkDocumentInfo = new BulkDocumentInfo();
        BulkData bulkData = bulkRequest.getCaseDetails().getCaseData();
        List<String> errors = new ArrayList<>();
        DocumentInfo documentInfo = new DocumentInfo();
        try {
            if (bulkData.getSearchCollection() != null && !bulkData.getSearchCollection().isEmpty()) {
                documentInfo = tornadoService.scheduleGeneration(authToken, bulkData);
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

    private CaseData customiseSelectedAddresses(CaseData caseData) {
        if(caseData.getAddressLabelsSelectionType() != null) {
            AddressLabelsSelectionType addressLabelsSelection = caseData.getAddressLabelsSelectionType();
            if (addressLabelsSelection.getClaimantAddressLabel() != null &&
                    addressLabelsSelection.getClaimantRepAddressLabel() != null &&
                    addressLabelsSelection.getRespondentsAddressLabel() != null &&
                    addressLabelsSelection.getRespondentsRepsAddressLabel() != null) {

                String printClaimantLabel = addressLabelsSelection.getClaimantAddressLabel();
                String printClaimantRepLabel = addressLabelsSelection.getClaimantRepAddressLabel();
                String printRespondentsLabels = addressLabelsSelection.getRespondentsAddressLabel();
                String printRespondentsRepsLabels = addressLabelsSelection.getRespondentsRepsAddressLabel();

                getClaimantAddressLabel(caseData, printClaimantLabel);
                getClaimantRepAddressLabel(caseData, printClaimantRepLabel);
                getRespondentsAddressLabels(caseData, printRespondentsLabels);
                getRespondentsRepsAddressLabels(caseData, printRespondentsRepsLabels);
            } else {
                caseData.setAddressLabelCollection(null);
            }
        } else {
            caseData.setAddressLabelCollection(null);
        }
        return caseData;
    }

    private CaseData allAvailableAddresses(CaseData caseData) {
        getClaimantAddressLabel(caseData, YES);
        getClaimantRepAddressLabel(caseData, YES);
        getRespondentsAddressLabels(caseData, YES);
        getRespondentsRepsAddressLabels(caseData, YES);
        return caseData;
    }

    private CaseData claimantAddress(CaseData caseData) {
        getClaimantAddressLabel(caseData, YES);
        return caseData;
    }

    private CaseData claimantRepAddress(CaseData caseData) {
        getClaimantRepAddressLabel(caseData, YES);
        return caseData;
    }

    private CaseData claimantAndClaimantRepAddresses(CaseData caseData) {
        getClaimantAddressLabel(caseData, YES);
        getClaimantRepAddressLabel(caseData, YES);
        return caseData;
    }

    private CaseData respondentsAddresses(CaseData caseData) {
        getRespondentsAddressLabels(caseData, YES);
        return caseData;
    }

    private CaseData respondentsRepsAddresses(CaseData caseData) {
        getRespondentsRepsAddressLabels(caseData, YES);
        return caseData;
    }

    private CaseData respondentsAndRespondentsRepsAddresses(CaseData caseData) {
        getRespondentsAddressLabels(caseData, YES);
        getRespondentsRepsAddressLabels(caseData, YES);
        return caseData;
    }

    private CaseData claimantAndRespondentsAddresses(CaseData caseData) {
        getClaimantAddressLabel(caseData, YES);
        getRespondentsAddressLabels(caseData, YES);
        return caseData;
    }

    private CaseData claimantRepAndRespondentsRepsAddresses(CaseData caseData) {
        getClaimantRepAddressLabel(caseData, YES);
        getRespondentsRepsAddressLabels(caseData, YES);
        return caseData;
    }

    private CaseData claimantAndRespondentsRepsAddresses(CaseData caseData) {
        getClaimantAddressLabel(caseData, YES);
        getRespondentsRepsAddressLabels(caseData, YES);
        return caseData;
    }

    private CaseData claimantRepAndRespondentsAddresses(CaseData caseData) {
        getClaimantRepAddressLabel(caseData, YES);
        getRespondentsAddressLabels(caseData, YES);
        return caseData;
    }

    private void getClaimantAddressLabel(CaseData caseData, String printClaimantLabel) {
        AddressLabelTypeItem addressLabelTypeItem = new AddressLabelTypeItem();
        AddressLabelType addressLabelType = new AddressLabelType();

        Optional<String> claimantTypeOfClaimant = Optional.ofNullable(caseData.getClaimantTypeOfClaimant());
        Optional<ClaimantIndType> claimantIndType = Optional.ofNullable(caseData.getClaimantIndType());
        Optional<ClaimantType> claimantType = Optional.ofNullable(caseData.getClaimantType());

        addressLabelType.setPrintLabel(printClaimantLabel);

        if (claimantTypeOfClaimant.isPresent() && caseData.getClaimantTypeOfClaimant().equals(INDIVIDUAL_TYPE_CLAIMANT)) {
            if (claimantIndType.isPresent()) {
                addressLabelType.setFullName(CLAIMANT + Helper.nullCheck(claimantIndType.get().claimantFullName()));
                addressLabelType.setLabelEntityName01(Helper.nullCheck(claimantIndType.get().claimantFullName()));
                addressLabelType.setLabelEntityName02("");
            }
        } else {
            addressLabelType.setFullName(CLAIMANT + Helper.nullCheck(caseData.getClaimantCompany()));
            addressLabelType.setLabelEntityName01("");
            addressLabelType.setLabelEntityName02((Helper.nullCheck(caseData.getClaimantCompany())));
        }

        if (claimantType.isPresent()) {
            getEntityAddress(addressLabelType, claimantType.get().getClaimantAddressUK());
            getEntityTelephone(addressLabelType, claimantType.get().getClaimantPhoneNumber());
            getEntityFax(addressLabelType, claimantType.get().getClaimantMobileNumber());
        } else {
            addressLabelType.setFullAddress("");
            addressLabelType.setLabelEntityAddress(new Address());
            addressLabelType.setLabelEntityTelephone("");
            addressLabelType.setLabelEntityFax("");
        }

        addressLabelType.setLabelEntityReference(REF);
        addressLabelType.setLabelCaseReference(caseData.getEthosCaseReference());

        addressLabelTypeItem.setId(String.valueOf(caseData.getAddressLabelCollection().size()));
        addressLabelTypeItem.setValue(addressLabelType);

        caseData.getAddressLabelCollection().add(addressLabelTypeItem);
    }

    private void getClaimantRepAddressLabel(CaseData caseData, String printClaimantRepLabel) {
        if (caseData.getRepresentativeClaimantType() != null && caseData.getClaimantRepresentedQuestion().equals(YES)) {

            AddressLabelTypeItem addressLabelTypeItem = new AddressLabelTypeItem();
            AddressLabelType addressLabelType = new AddressLabelType();

            RepresentedTypeC representedTypeC = caseData.getRepresentativeClaimantType();

            addressLabelType.setPrintLabel(printClaimantRepLabel);

            addressLabelType.setFullName(CLAIMANT_REP + Helper.nullCheck(representedTypeC.getNameOfRepresentative()));
            addressLabelType.setLabelEntityName01(Helper.nullCheck(representedTypeC.getNameOfRepresentative()));
            addressLabelType.setLabelEntityName02(Helper.nullCheck(representedTypeC.getNameOfOrganisation()));
            getEntityAddress(addressLabelType, representedTypeC.getRepresentativeAddress());
            getEntityTelephone(addressLabelType, representedTypeC.getRepresentativePhoneNumber());
            getEntityFax(addressLabelType, representedTypeC.getRepresentativeMobileNumber());

            if (!isNullOrEmpty(Helper.nullCheck(representedTypeC.getRepresentativeReference()))) {
                addressLabelType.setLabelEntityReference(REF + Helper.nullCheck(representedTypeC.getRepresentativeReference()));
            } else {
                addressLabelType.setLabelEntityReference(REF);
            }

            addressLabelType.setLabelCaseReference(caseData.getEthosCaseReference());

            addressLabelTypeItem.setId(String.valueOf(caseData.getAddressLabelCollection().size()));
            addressLabelTypeItem.setValue(addressLabelType);

            caseData.getAddressLabelCollection().add(addressLabelTypeItem);
        }
    }

    private void getRespondentsAddressLabels(CaseData caseData, String printRespondentsLabels) {
        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
            List<RespondentSumTypeItem> activeRespondents = getActiveRespondents(caseData);
            if(!activeRespondents.isEmpty()) {
                ListIterator<RespondentSumTypeItem> itr = activeRespondents.listIterator();
                while (itr.hasNext()) {

                    AddressLabelTypeItem addressLabelTypeItem = new AddressLabelTypeItem();
                    AddressLabelType addressLabelType = new AddressLabelType();

                    RespondentSumType respondentSumType = itr.next().getValue();

                    addressLabelType.setPrintLabel(printRespondentsLabels);
                    addressLabelType.setFullName(RESPONDENT + Helper.nullCheck(respondentSumType.getRespondentName()));
                    addressLabelType.setLabelEntityName01(Helper.nullCheck(respondentSumType.getRespondentName()));
                    addressLabelType.setLabelEntityName02("");
                    getEntityAddress(addressLabelType, respondentSumType.getRespondentAddress());
                    getEntityTelephone(addressLabelType, respondentSumType.getRespondentPhone1());
                    getEntityFax(addressLabelType, respondentSumType.getRespondentPhone2());
                    addressLabelType.setLabelEntityReference(REF);
                    addressLabelType.setLabelCaseReference(caseData.getEthosCaseReference());

                    addressLabelTypeItem.setId(String.valueOf(caseData.getAddressLabelCollection().size()));
                    addressLabelTypeItem.setValue(addressLabelType);

                    caseData.getAddressLabelCollection().add(addressLabelTypeItem);
                }
            }
        }
    }

    private void getRespondentsRepsAddressLabels(CaseData caseData, String printRespondentsRepsLabels) {
        if (caseData.getRepCollection() != null && !caseData.getRepCollection().isEmpty()) {
            ListIterator<RepresentedTypeRItem> itr = caseData.getRepCollection().listIterator();
            while (itr.hasNext()) {

                AddressLabelTypeItem addressLabelTypeItem = new AddressLabelTypeItem();
                AddressLabelType addressLabelType = new AddressLabelType();

                RepresentedTypeR representedTypeR = itr.next().getValue();

                addressLabelType.setPrintLabel(printRespondentsRepsLabels);

                addressLabelType.setFullName(RESPONDENT_REP + Helper.nullCheck(representedTypeR.getNameOfRepresentative()));
                addressLabelType.setLabelEntityName01(Helper.nullCheck(representedTypeR.getNameOfRepresentative()));
                addressLabelType.setLabelEntityName02(Helper.nullCheck(representedTypeR.getNameOfOrganisation()));
                getEntityAddress(addressLabelType, representedTypeR.getRepresentativeAddress());
                getEntityTelephone(addressLabelType, representedTypeR.getRepresentativePhoneNumber());
                getEntityFax(addressLabelType, representedTypeR.getRepresentativeMobileNumber());

                if (!isNullOrEmpty(Helper.nullCheck(representedTypeR.getRepresentativeReference()))) {
                    addressLabelType.setLabelEntityReference(REF + Helper.nullCheck(representedTypeR.getRepresentativeReference()));
                } else {
                    addressLabelType.setLabelEntityReference(REF);
                }

                addressLabelType.setLabelCaseReference(caseData.getEthosCaseReference());

                addressLabelTypeItem.setId(String.valueOf(caseData.getAddressLabelCollection().size()));
                addressLabelTypeItem.setValue(addressLabelType);

                caseData.getAddressLabelCollection().add(addressLabelTypeItem);
            }
        }
    }

    private void getEntityAddress(AddressLabelType addressLabelType, Address entityAddress) {
        if (entityAddress != null) {
            addressLabelType.setFullAddress(getFullAddressOneLine(entityAddress).toString());
            addressLabelType.setLabelEntityAddress(entityAddress);
        } else {
            addressLabelType.setFullAddress("");
            addressLabelType.setLabelEntityAddress(new Address());
        }
    }

    private void getEntityTelephone(AddressLabelType addressLabelType, String telephone) {
        if (!isNullOrEmpty(Helper.nullCheck(telephone))) {
            addressLabelType.setLabelEntityTelephone(TEL + Helper.nullCheck(telephone));
        } else {
            addressLabelType.setLabelEntityTelephone("");
        }
    }

    private void getEntityFax(AddressLabelType addressLabelType, String fax) {
        if (!isNullOrEmpty(Helper.nullCheck(fax))) {
            addressLabelType.setLabelEntityFax(TEL + Helper.nullCheck(fax));
        } else {
            addressLabelType.setLabelEntityFax("");
        }
    }

    private StringBuilder getFullAddressOneLine(Address address) {
        StringBuilder sb = new StringBuilder();
        sb.append(Helper.nullCheck(address.getAddressLine1()));
        sb.append(!isNullOrEmpty(Helper.nullCheck(address.getAddressLine2())) && sb.length() > 0  ? ", " : "");
        sb.append(Helper.nullCheck(address.getAddressLine2()));
        sb.append(!isNullOrEmpty(Helper.nullCheck(address.getAddressLine3())) && sb.length() > 0  ? ", " : "");
        sb.append(Helper.nullCheck(address.getAddressLine3()));
        sb.append(!isNullOrEmpty(Helper.nullCheck(address.getPostTown())) && sb.length() > 0  ? ", " : "");
        sb.append(Helper.nullCheck(address.getPostTown()));
        sb.append(!isNullOrEmpty(Helper.nullCheck(address.getCounty())) && sb.length() > 0  ? ", " : "");
        sb.append(Helper.nullCheck(address.getCounty()));
        sb.append(!isNullOrEmpty(Helper.nullCheck(address.getPostCode())) && sb.length() > 0  ? ", " : "");
        sb.append(Helper.nullCheck(address.getPostCode()));
        sb.append(!isNullOrEmpty(Helper.nullCheck(address.getCountry())) && sb.length() > 0  ? ", " : "");
        sb.append(Helper.nullCheck(address.getCountry()));
        sb.append(sb.length() > 0  ? "." : "");
        return sb;
    }

}
