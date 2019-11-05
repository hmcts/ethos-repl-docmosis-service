package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.DocumentManagementException;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDocumentInfo;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.SearchTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            if (bulkDetails.getCaseData().getSearchCollection() != null && !bulkDetails.getCaseData().getSearchCollection().isEmpty()) {
                List<CaseData> caseDataResultList = filterCasesById(ccdClient.retrieveCases(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()),
                        bulkDetails.getJurisdiction()), bulkDetails.getCaseData().getSearchCollection());
                for (CaseData caseData : caseDataResultList) {
                    log.info("Generating document for: " + caseData.getEthosCaseReference());
                    caseData.setCorrespondenceType(bulkDetails.getCaseData().getCorrespondenceType());
                    caseData.setCorrespondenceScotType(bulkDetails.getCaseData().getCorrespondenceScotType());
                    documentInfoList.add(tornadoService.documentGeneration(authToken, caseData));
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

    private List<CaseData> filterCasesById(List<SubmitEvent> submitEvents, List<SearchTypeItem> searchTypeItems) {
        List<CaseData> caseDataResultList = new ArrayList<>();
        Map<String, CaseData> submitEventsList = submitEvents.stream().collect(Collectors.toMap(s -> String.valueOf(s.getCaseId()), SubmitEvent::getCaseData));
        for (SearchTypeItem searchTypeItem : searchTypeItems) {
            String caseId = searchTypeItem.getValue().getCaseIDS();
            if (submitEventsList.containsKey(caseId)) {
                log.info("Adding submitted key: " + caseId);
                caseDataResultList.add(submitEventsList.get(caseId));
            }
        }
        return caseDataResultList;
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

}
