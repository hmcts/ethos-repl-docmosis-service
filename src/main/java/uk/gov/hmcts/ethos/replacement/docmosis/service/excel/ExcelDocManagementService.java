package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.UploadedDocument;
import uk.gov.hmcts.ecm.common.model.ccd.items.DocumentTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DocumentType;
import uk.gov.hmcts.ecm.common.model.ccd.types.UploadedDocumentType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ethos.replacement.docmosis.service.DocumentManagementService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("excelDocManagementService")
public class ExcelDocManagementService {

    public static final String APPLICATION_EXCEL_VALUE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String FILE_NAME = "MyFirstExcel.xlsx";

    @Value("${document_management.url}")
    private String ccdDMStoreBaseUrl;

    private final DocumentManagementService documentManagementService;
    private final ExcelCreationService excelCreationService;

    @Autowired
    public ExcelDocManagementService(DocumentManagementService documentManagementService,
                                     ExcelCreationService excelCreationService) {
        this.documentManagementService = documentManagementService;
        this.excelCreationService = excelCreationService;
    }

    public void uploadExcelDocument(String userToken, MultipleData multipleData, byte[] excelBytes) {

        URI documentSelfPath = documentManagementService.uploadDocument(userToken, excelBytes,
                FILE_NAME, APPLICATION_EXCEL_VALUE);

        log.info("URI documentSelfPath uploaded and created: " + documentSelfPath.toString());

        log.info("Add document to multiple");

        addDocumentToMultiple(multipleData, documentSelfPath);

    }

    public InputStream downloadExcelDocument(String userToken, String binaryUrl) throws IOException {

        UploadedDocument uploadedDocument = documentManagementService.downloadFile(userToken, binaryUrl);

        log.info("Downloaded excel name: " + uploadedDocument.getName());

        return uploadedDocument.getContent().getInputStream();

    }

    private void addDocumentToMultiple(MultipleData multipleData, URI documentSelfPath) {

        List<DocumentTypeItem> documentCollection = new ArrayList<>();

        DocumentTypeItem documentTypeItem = new DocumentTypeItem();

        DocumentType documentType = new DocumentType();
        documentType.setShortDescription("Excel for: " + multipleData.getMultipleReference());
        UploadedDocumentType uploadedDocumentType = new UploadedDocumentType();
        uploadedDocumentType.setDocumentBinaryUrl(ccdDMStoreBaseUrl + documentSelfPath.getRawPath() + "/binary");
        uploadedDocumentType.setDocumentFilename(FILE_NAME);
        uploadedDocumentType.setDocumentUrl(ccdDMStoreBaseUrl + documentSelfPath.getRawPath());
        documentType.setUploadedDocument(uploadedDocumentType);

        documentTypeItem.setId(LocalDate.now().toString());
        documentTypeItem.setValue(documentType);

        documentCollection.add(documentTypeItem);

        multipleData.setDocumentCollection(documentCollection);
    }

    public void generateAndUploadExcel(List<?> multipleCollection, String userToken, MultipleData multipleData) {

        byte[] excelBytes = excelCreationService.writeExcel(multipleCollection);

        uploadExcelDocument(userToken, multipleData, excelBytes);

    }

}
