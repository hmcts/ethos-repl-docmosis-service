package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.exceptions.DocumentManagementException;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.labels.LabelPayloadEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DocumentHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.LabelsHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.EventValidationService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.TornadoService;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.ADDRESS_LABELS_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO_CASES_SEARCHED;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.LabelsHelper.MAX_NUMBER_LABELS;

@Slf4j
@RequiredArgsConstructor
@Service("multipleLetterService")
public class MultipleLetterService {

    private static final String MESSAGE = "Failed to generate document for case id : ";
    private static final String NUMBER_CASES_LIMIT_ERROR = "Number of cases to generate labels should be less or equal than "  + MAX_NUMBER_LABELS;

    private final TornadoService tornadoService;
    private final ExcelReadingService excelReadingService;
    private final SingleCasesReadingService singleCasesReadingService;
    private final EventValidationService eventValidationService;

    public DocumentInfo bulkLetterLogic(String userToken, MultipleDetails multipleDetails, List<String> errors, boolean validation) {

        log.info("Read excel for letter logic");

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleDetails.getCaseData()),
                        errors,
                        multipleDetails.getCaseData(),
                        FilterExcelType.FLAGS);

        DocumentInfo documentInfo = new DocumentInfo();

        if (!multipleObjects.keySet().isEmpty()) {

            log.info("Check top level document to generate");

            documentInfo = generateLetterOrLabel(userToken, multipleDetails, multipleObjects, errors, documentInfo, validation);

        } else {

            errors.add(NO_CASES_SEARCHED);

        }

        if (!validation) {

            log.info("Resetting mid fields");

            MultiplesHelper.resetMidFields(multipleDetails.getCaseData());

        }

        return documentInfo;

    }

    private DocumentInfo generateLetterOrLabel(String userToken, MultipleDetails multipleDetails,
                                               TreeMap<String, Object> multipleObjects,
                                               List<String> errors, DocumentInfo documentInfo, boolean validation) {

        String templateName = DocumentHelper.getTemplateName(multipleDetails.getCaseData().getCorrespondenceType(),
                multipleDetails.getCaseData().getCorrespondenceScotType());

        log.info("midAddressLabels - templateName : " + templateName);

        if (templateName.equals(ADDRESS_LABELS_TEMPLATE)) {

            return generateLabelLogic(userToken, multipleDetails, multipleObjects, errors, documentInfo, validation);

        } else {

            return generateLetterLogic(userToken, multipleDetails, multipleObjects, errors, documentInfo);

        }

    }

    private DocumentInfo generateLabelLogic(String userToken, MultipleDetails multipleDetails,
                                    TreeMap<String, Object> multipleObjects, List<String> errors,
                                    DocumentInfo documentInfo, boolean validation) {

        MultipleData multipleData = multipleDetails.getCaseData();

        List<String> caseRefCollection = new ArrayList<>(multipleObjects.keySet());

        if (caseRefCollection.size() > MAX_NUMBER_LABELS) {

            errors.add(NUMBER_CASES_LIMIT_ERROR);

        } else {

            List<LabelPayloadEvent> labelPayloadEvents = singleCasesReadingService.retrieveLabelCases(userToken,
                    multipleDetails.getCaseTypeId(), caseRefCollection);

            log.info("Generating labels");

            multipleData.setAddressLabelCollection(
                    LabelsHelper.customiseSelectedAddressesMultiples(labelPayloadEvents, multipleData));

            multipleDetails.setCaseData(multipleData);

        }

        log.info("Check if it needs to generate a letter or just to populate the number of labels");

        if (validation) {

            return documentInfo;

        } else {

            log.info("No validation then will generate a label document");

            SubmitEvent submitEvent = singleCasesReadingService.retrieveSingleCase(userToken,
                    multipleDetails.getCaseTypeId(), multipleObjects.firstKey(), multipleData.getMultipleSource());

            return generateLetterOrLabel(userToken, multipleDetails, submitEvent);

        }

    }

    private DocumentInfo generateLetterLogic(String userToken, MultipleDetails multipleDetails,
                                     TreeMap<String, Object> multipleObjects,
                                     List<String> errors, DocumentInfo documentInfo) {

        log.info("Pull information from first case filtered");

        SubmitEvent submitEvent = singleCasesReadingService.retrieveSingleCase(userToken,
                multipleDetails.getCaseTypeId(), multipleObjects.firstKey(), multipleDetails.getCaseData().getMultipleSource());

        log.info("Validating hearing number");

        errors.addAll(eventValidationService.validateHearingNumber(submitEvent.getCaseData(),
                multipleDetails.getCaseData().getCorrespondenceType(),
                multipleDetails.getCaseData().getCorrespondenceScotType()));

        if (errors.isEmpty()) {

            log.info("Generate letter for document");

            return generateLetterOrLabel(userToken, multipleDetails, submitEvent);

        }

        return documentInfo;

    }

    private DocumentInfo generateLetterOrLabel(String userToken, MultipleDetails multipleDetails, SubmitEvent submitEvent) {

        DocumentInfo documentInfo;

        try {
            documentInfo = tornadoService.documentGeneration(userToken,
                    submitEvent.getCaseData(),
                    UtilHelper.getCaseTypeId(multipleDetails.getCaseTypeId()),
                    multipleDetails.getCaseData().getCorrespondenceType(),
                    multipleDetails.getCaseData().getCorrespondenceScotType(),
                    multipleDetails.getCaseData());

        } catch (Exception ex) {

            throw new DocumentManagementException(MESSAGE + multipleDetails.getCaseId() + ex.getMessage());

        }

        return documentInfo;

    }

}
