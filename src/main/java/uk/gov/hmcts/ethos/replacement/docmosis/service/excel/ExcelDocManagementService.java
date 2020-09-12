package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.ccd.UploadedDocument;
import uk.gov.hmcts.ecm.common.model.ccd.types.UploadedDocumentType;
import uk.gov.hmcts.ecm.common.model.multiples.CaseImporterFile;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.DocumentManagementService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.DATE_TIME_USER_FRIENDLY_PATTERN;

@Slf4j
@Service("excelDocManagementService")
public class ExcelDocManagementService {

    public static final String APPLICATION_EXCEL_VALUE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String FILE_NAME = "Multiples.xlsx";

    @Value("${document_management.url}")
    private String ccdDMStoreBaseUrl;

    private final DocumentManagementService documentManagementService;
    private final ExcelCreationService excelCreationService;
    private final UserService userService;

    @Autowired
    public ExcelDocManagementService(DocumentManagementService documentManagementService,
                                     ExcelCreationService excelCreationService,
                                     UserService userService) {
        this.documentManagementService = documentManagementService;
        this.excelCreationService = excelCreationService;
        this.userService = userService;
    }

    public void uploadExcelDocument(String userToken, MultipleData multipleData, byte[] excelBytes) {

        URI documentSelfPath = documentManagementService.uploadDocument(userToken, excelBytes,
                FILE_NAME, APPLICATION_EXCEL_VALUE);

        log.info("URI documentSelfPath uploaded and created: " + documentSelfPath.toString());

        log.info("Add document to multiple");

        addDocumentToMultiple(userToken, multipleData, documentSelfPath);

    }

    public InputStream downloadExcelDocument(String userToken, String binaryUrl) throws IOException {

        UploadedDocument uploadedDocument = documentManagementService.downloadFile(userToken, binaryUrl);

        log.info("Downloaded excel name: " + uploadedDocument.getName());

        return uploadedDocument.getContent().getInputStream();

    }

    private void addDocumentToMultiple(String userToken, MultipleData multipleData, URI documentSelfPath) {

        UploadedDocumentType uploadedDocumentType = new UploadedDocumentType();
        uploadedDocumentType.setDocumentBinaryUrl(ccdDMStoreBaseUrl + documentSelfPath.getRawPath() + "/binary");
        uploadedDocumentType.setDocumentFilename(FILE_NAME);
        uploadedDocumentType.setDocumentUrl(ccdDMStoreBaseUrl + documentSelfPath.getRawPath());

        multipleData.setCaseImporterFile(populateCaseImporterFile(userToken, uploadedDocumentType));
    }

    public void generateAndUploadExcel(List<?> multipleCollection, String userToken, MultipleData multipleData) {

        List<String> subMultipleCollection = MultiplesHelper.generateSubMultipleStringCollection(multipleData);

        byte[] excelBytes = excelCreationService.writeExcel(multipleCollection, subMultipleCollection);

        uploadExcelDocument(userToken, multipleData, excelBytes);

    }

    public CaseImporterFile populateCaseImporterFile(String userToken, UploadedDocumentType uploadedDocumentType) {

        CaseImporterFile caseImporterFile = new CaseImporterFile();
        LocalDateTime dateTime = LocalDateTime.now();
        UserDetails userDetails = userService.getUserDetails(userToken);

        caseImporterFile.setUploadedDocument(uploadedDocumentType);
        caseImporterFile.setUploadedDateTime(dateTime.format(DATE_TIME_USER_FRIENDLY_PATTERN));
        caseImporterFile.setUploadUser(userDetails.getFirstName() + " " + userDetails.getLastName());

        return caseImporterFile;

    }

}
