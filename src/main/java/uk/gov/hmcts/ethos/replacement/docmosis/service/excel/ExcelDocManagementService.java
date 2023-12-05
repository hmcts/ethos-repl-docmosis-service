package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.ccd.DocumentInfo;
import uk.gov.hmcts.ecm.common.model.ccd.types.UploadedDocumentType;
import uk.gov.hmcts.ecm.common.model.helper.SchedulePayload;
import uk.gov.hmcts.ecm.common.model.multiples.CaseImporterFile;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesScheduleHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.SignificantItemType;
import uk.gov.hmcts.ethos.replacement.docmosis.service.DocumentManagementService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.SortedMap;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.DATE_TIME_USER_FRIENDLY_PATTERN;

@Slf4j
@RequiredArgsConstructor
@Service("excelDocManagementService")
public class ExcelDocManagementService {

    public static final String APPLICATION_EXCEL_VALUE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Value("${document_management.url}")
    private String ccdDMStoreBaseUrl;

    @Value("${document_management.ccdCaseDocument.url}")
    private String ccdCaseDocumentUrl;
    private final DocumentManagementService documentManagementService;
    private final ExcelCreationService excelCreationService;
    private final UserService userService;
    private final ScheduleCreationService scheduleCreationService;

    public void uploadExcelDocument(String userToken, MultipleDetails multipleDetails, byte[] excelBytes) {
        var multipleData = multipleDetails.getCaseData();
        log.info("Multiple Name is: " + multipleData.getMultipleName() + "for multiple reference: "
                + multipleData.getMultipleReference());
        URI documentSelfPath = documentManagementService.uploadDocument(userToken, excelBytes,
                MultiplesHelper.generateExcelDocumentName(multipleData), APPLICATION_EXCEL_VALUE,
                multipleDetails.getCaseTypeId());

        log.info("URI documentSelfPath uploaded and created: " + documentSelfPath.toString());
        log.info("Add document to multiple with reference:" + multipleData.getMultipleReference());
        addDocumentToMultiple(userToken, multipleData, documentSelfPath);
    }

    public InputStream downloadExcelDocument(String userToken, String binaryUrl) throws IOException {

        var uploadedDocument = documentManagementService.downloadFile(userToken, binaryUrl);

        log.info("Downloaded excel name: " + uploadedDocument.getName());

        return uploadedDocument.getContent().getInputStream();

    }

    private void addDocumentToMultiple(String userToken, MultipleData multipleData, URI documentSelfPath) {
        var uploadedDocumentType = new UploadedDocumentType();
        uploadedDocumentType.setDocumentBinaryUrl(ccdCaseDocumentUrl + documentSelfPath.getRawPath() + "/binary");
        uploadedDocumentType.setDocumentFilename(MultiplesHelper.generateExcelDocumentName(multipleData));
        uploadedDocumentType.setDocumentUrl(ccdCaseDocumentUrl + documentSelfPath.getRawPath());

        multipleData.setCaseImporterFile(populateCaseImporterFile(userToken, uploadedDocumentType));
    }

    public void generateAndUploadExcel(List<?> multipleCollection, String userToken, MultipleDetails multipleDetails) {
        List<String> subMultipleCollection = MultiplesHelper.generateSubMultipleStringCollection(
                multipleDetails.getCaseData());
        writeAndUploadExcelDocument(multipleCollection, userToken, multipleDetails, subMultipleCollection);
    }

    public void writeAndUploadExcelDocument(List<?> multipleCollection, String userToken,
                                            MultipleDetails multipleDetails, List<String> subMultipleCollection) {
        var multipleData = multipleDetails.getCaseData();
        log.info("MultipleName is: " + multipleData.getMultipleName() + "for multiple reference: "
                + multipleData.getMultipleReference());
        byte[] excelBytes = excelCreationService.writeExcel(multipleCollection, subMultipleCollection,
                multipleData.getLeadCase());
        uploadExcelDocument(userToken, multipleDetails, excelBytes);

        log.info("Add multiple case counter for multipleReference:" + multipleData.getMultipleReference());

        multipleData.setCaseCounter(String.valueOf(multipleCollection.size()));

    }

    public CaseImporterFile populateCaseImporterFile(String userToken, UploadedDocumentType uploadedDocumentType) {

        CaseImporterFile caseImporterFile = new CaseImporterFile();
        LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("Europe/London"));
        UserDetails userDetails = userService.getUserDetails(userToken);

        caseImporterFile.setUploadedDocument(uploadedDocumentType);
        caseImporterFile.setUploadedDateTime(dateTime.format(DATE_TIME_USER_FRIENDLY_PATTERN));
        caseImporterFile.setUploadUser(userDetails.getFirstName() + " " + userDetails.getLastName());

        return caseImporterFile;
    }

    public DocumentInfo writeAndUploadScheduleDocument(String userToken,
                                                       SortedMap<String, Object> multipleObjectsFiltered,
                                                       MultipleDetails multipleDetails,
                                                       List<SchedulePayload> schedulePayloads) {

        byte[] excelBytes = scheduleCreationService.writeSchedule(multipleDetails.getCaseData(),
                schedulePayloads, multipleObjectsFiltered);

        return uploadScheduleDocument(userToken, multipleDetails, excelBytes);

    }

    private DocumentInfo uploadScheduleDocument(String userToken, MultipleDetails multipleDetails, byte[] excelBytes) {

        String documentName = MultiplesScheduleHelper.generateScheduleDocumentName(multipleDetails.getCaseData());

        URI documentSelfPath = documentManagementService.uploadDocument(userToken, excelBytes,
                documentName, APPLICATION_EXCEL_VALUE, multipleDetails.getCaseTypeId());

        log.info("URI documentSelfPath uploaded and created: " + documentSelfPath.toString());

        return getScheduleDocument(documentSelfPath, documentName);

    }

    private DocumentInfo getScheduleDocument(URI documentSelfPath, String documentName) {

        return DocumentInfo.builder()
                .type(SignificantItemType.DOCUMENT.name())
                .description(documentName)
                .markUp(documentManagementService.generateMarkupDocument(
                        documentManagementService.generateDownloadableURL(documentSelfPath)))
                .url(documentManagementService.generateDownloadableURL(documentSelfPath))
                .build();
    }

    public DocumentInfo uploadExcelReportDocument(String userToken, String documentName, byte[] excelBytes) {
        URI documentUri = documentManagementService.uploadDocument(userToken, excelBytes,
            documentName, APPLICATION_EXCEL_VALUE, "Listings_Type");

        log.info("Excel Report - URI documentSelfPath uploaded and created: " + documentUri.toString());

        return getScheduleDocument(documentUri, documentName);
    }
}
