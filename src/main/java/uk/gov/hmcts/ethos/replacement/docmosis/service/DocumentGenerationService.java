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
import uk.gov.hmcts.ecm.common.model.ccd.types.AddressLabelType;
import uk.gov.hmcts.ecm.common.model.ccd.types.AddressLabelsSelectionType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        log.info("WE ARE OFF ===============>");

        String templateName = Helper.getTemplateName(caseData);
        if (templateName.equals("EM-TRB-LBL-ENG-00000")) {
            String ewSection = Helper.getSectionName(caseData);
            String sectionName = ewSection.equals("") ? Helper.getScotSectionName(caseData) : ewSection;
            caseData.setAddressLabelCollection(new ArrayList<>());
            if (sectionName.equals("0.1")) {
                switch (sectionName) {
                    case "0.1":
                        customiseAddressLabels(caseData);
                        break;
                    case "0.2":
                        allAddressLabels(caseData);
                        break;
                    default:
                        return caseData;
                }
            }
        }

        return caseData;
    }

    private CaseData customiseAddressLabels(CaseData caseData) {
        if(caseData.getAddressLabelsSelectionType() != null) {
            AddressLabelsSelectionType addressLabelsSelection = caseData.getAddressLabelsSelectionType();
            String printClaimantLabel = addressLabelsSelection.getClaimantAddressLabel();
            String printClaimantRepLabel = addressLabelsSelection.getClaimantRepAddressLabel();
            String printRespondentsLabels = addressLabelsSelection.getRespondentsAddressLabel();
            String printRespondentsRepsLabels = addressLabelsSelection.getRespondentsRepsAddressLabel();

            caseData.getAddressLabelCollection().add(getClaimantAddressLabel(caseData, printClaimantLabel));
        }

        return caseData;
    }

    private CaseData allAddressLabels(CaseData caseData) {
        // ...
        return caseData;
    }

    private AddressLabelTypeItem getClaimantAddressLabel(CaseData caseData, String printClaimantLabel) {
        AddressLabelTypeItem addressLabelTypeItem = new AddressLabelTypeItem();
        AddressLabelType addressLabelType = new AddressLabelType();

        addressLabelType.setPrintLabel(printClaimantLabel);
        addressLabelType.setFullName("mark taylor");
        addressLabelType.setFullAddress("10 Brook Close");
        addressLabelType.setLabelEntityName01("");
        addressLabelType.setLabelEntityName02("");
        addressLabelType.setLabelEntityAddress(new Address());
        addressLabelType.setLabelEntityTelephone("");
        addressLabelType.setLabelEntityFax("");
        addressLabelType.setLabelEntityReference("");
        addressLabelType.setLabelCaseReference("");

        addressLabelTypeItem.setId("1");
        addressLabelTypeItem.setValue(addressLabelType);

        return addressLabelTypeItem;
    }

    public DocumentInfo processDocumentRequest(CCDRequest ccdRequest, String authToken) {
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        try {
            return tornadoService.documentGeneration(authToken, caseDetails.getCaseData());
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
                    documentInfoList.add(tornadoService.documentGeneration(authToken, submitEvent.getCaseData()));
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

//    private List<CaseData> filterCasesById(List<SubmitEvent> submitEvents, List<SearchTypeItem> searchTypeItems) {
//        List<CaseData> caseDataResultList = new ArrayList<>();
//        Map<String, CaseData> submitEventsList = submitEvents.stream().collect(Collectors.toMap(s -> String.valueOf(s.getCaseId()), SubmitEvent::getCaseData));
//        for (SearchTypeItem searchTypeItem : searchTypeItems) {
//            String caseId = searchTypeItem.getValue().getCaseIDS();
//            if (submitEventsList.containsKey(caseId)) {
//                log.info("Adding submitted key: " + caseId);
//                caseDataResultList.add(submitEventsList.get(caseId));
//            }
//        }
//        return caseDataResultList;
//    }

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

}
