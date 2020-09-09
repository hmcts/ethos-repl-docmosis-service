package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.ecm.common.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.types.CaseType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.Address;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.UploadedDocument;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ecm.common.model.ccd.types.UploadedDocumentType;
import uk.gov.hmcts.ecm.common.model.multiples.CaseImporterFile;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;

import java.io.IOException;
import java.util.*;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_SCHEDULE_CONFIG;
import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.excel.ExcelDocManagementService.FILE_NAME;

public class MultipleUtil {

    public static final String TESTING_FILE_NAME = "MyFirstExcel.xlsx";
    public static final String TESTING_FILE_NAME_ERROR = "MyFirstExcelError.xlsx";
    public static final String TESTING_FILE_NAME_WITH_TWO = "MyFirstExcel2.xlsx";
    public static final String TESTING_FILE_NAME_WRONG_COLUMN_ROW = "MyFirstExcelWrongColumnRow.xlsx";
    public static final String TESTING_FILE_NAME_EMPTY = "MyFirstExcelEmpty.xlsx";

    public static TreeMap<String, Object> getMultipleObjectsAll() {
        TreeMap<String, Object> multipleObjectTreeMap = new TreeMap<>();
        multipleObjectTreeMap.put("245000/2020",  MultipleObject.builder()
                .subMultiple("245000")
                .ethosCaseRef("245000/2020")
                .flag1("AA")
                .flag2("BB")
                .flag3("")
                .flag4("")
                .build());
        multipleObjectTreeMap.put("245003/2020",  MultipleObject.builder()
                .subMultiple("245003")
                .ethosCaseRef("245003/2020")
                .flag1("AA")
                .flag2("EE")
                .flag3("")
                .flag4("")
                .build());
        return multipleObjectTreeMap;
    }

    public static TreeMap<String, Object> getMultipleObjectsFlags() {
        TreeMap<String, Object> multipleObjectTreeMap = new TreeMap<>();
        multipleObjectTreeMap.put("245000/2020",  "245000/2020");
        multipleObjectTreeMap.put("245003/2020",  "245003/2020");
        return multipleObjectTreeMap;
    }

    public static TreeMap<String, Object> getMultipleObjectsSubMultiple() {
        TreeMap<String, Object> multipleObjectTreeMap = new TreeMap<>();
        multipleObjectTreeMap.put("245000", new ArrayList<>(Collections.singletonList("245000/2020")));
        multipleObjectTreeMap.put("245003", new ArrayList<>(Collections.singletonList("245003/2020")));
        return multipleObjectTreeMap;
    }

    public static TreeMap<String, Object> getMultipleObjectsDLFlags() {
        TreeMap<String, Object> multipleObjectTreeMap = new TreeMap<>();
        multipleObjectTreeMap.put(HEADER_3, new HashSet<>(Collections.singletonList("AA")));
        multipleObjectTreeMap.put(HEADER_4, new HashSet<>(Arrays.asList("BB", "CC")));
        return multipleObjectTreeMap;
    }

    public static CaseData getCaseData(String ethosCaseReference) {
        CaseData caseData = new CaseData();
        caseData.setClerkResponsible("JuanFran");
        ClaimantType claimantType = new ClaimantType();
        Address address = new Address();
        address.setPostCode("M2 45GD");
        claimantType.setClaimantAddressUK(address);
        caseData.setClaimantType(claimantType);
        ClaimantIndType claimantIndType = new ClaimantIndType();
        claimantIndType.setClaimantLastName("Mike");
        caseData.setClaimantIndType(claimantIndType);
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName("Andrew Smith");
        respondentSumType.setRespondentAddress(address);
        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setValue(respondentSumType);
        caseData.setRespondentCollection(new ArrayList<>(Collections.singletonList(respondentSumTypeItem)));
        caseData.setFileLocation("Manchester");
        caseData.setEthosCaseReference(ethosCaseReference);
        return caseData;
    }

    public static List<SubmitEvent> getSubmitEvents() {
        SubmitEvent submitEvent1 = new SubmitEvent();
        submitEvent1.setCaseData(getCaseData("245000/2020"));
        SubmitEvent submitEvent2 = new SubmitEvent();
        submitEvent2.setCaseData(getCaseData("245003/2020"));
        return new ArrayList<>(Arrays.asList(submitEvent1, submitEvent2));
    }

    public static MultipleData getMultipleData() {
        MultipleData multipleData = new MultipleData();
        List<CaseIdTypeItem> caseIdCollection = new ArrayList<>();
        CaseType caseType1 = new CaseType();
        caseType1.setEthosCaseReference("245000/2020");
        CaseIdTypeItem caseIdTypeItem1 = new CaseIdTypeItem();
        caseIdTypeItem1.setId("1");
        caseIdTypeItem1.setValue(caseType1);
        caseIdCollection.add(caseIdTypeItem1);

        CaseType caseType2 = new CaseType();
        caseType2.setEthosCaseReference("245001/2020");
        CaseIdTypeItem caseIdTypeItem2 = new CaseIdTypeItem();
        caseIdTypeItem2.setId("2");
        caseIdTypeItem2.setValue(caseType2);
        caseIdCollection.add(caseIdTypeItem2);

        multipleData.setFlag1(generateDynamicList("AA"));
        multipleData.setFlag2(generateDynamicList(""));
        multipleData.setFlag4(generateDynamicList(""));
        multipleData.setCaseIdCollection(caseIdCollection);
        multipleData.setScheduleDocName(MULTIPLE_SCHEDULE_CONFIG);
        getDocumentCollection(multipleData);
        return multipleData;
    }

    public static void getDocumentCollection(MultipleData multipleData) {
        CaseImporterFile caseImporterFile = new CaseImporterFile();
        caseImporterFile.setUploadedDocument(getUploadedDocumentType());
        caseImporterFile.setUploadUser("Eric Cooper");
        caseImporterFile.setUploadedDateTime("05-02-2020 10:12:46");
        multipleData.setCaseImporterFile(caseImporterFile);
    }

    public static UploadedDocumentType getUploadedDocumentType() {
        UploadedDocumentType uploadedDocumentType = new UploadedDocumentType();
        uploadedDocumentType.setDocumentBinaryUrl("http://127.0.0.1:3453/documents/20d8a494-4232-480a-aac3-23ad0746c07b/binary");
        uploadedDocumentType.setDocumentFilename(FILE_NAME);
        uploadedDocumentType.setDocumentUrl("http://127.0.0.1:3453/documents/20d8a494-4232-480a-aac3-23ad0746c07b");
        return uploadedDocumentType;
    }

    public static UploadedDocument getUploadedDocument() {
        ResponseEntity<Resource> response = getResponseOK();

        return UploadedDocument.builder()
                .content(response.getBody())
                .name(Objects.requireNonNull(response.getHeaders().get("originalfilename")).get(0))
                .contentType(Objects.requireNonNull(response.getHeaders().get(HttpHeaders.CONTENT_TYPE)).get(0))
                .build();
    }

    public static ResponseEntity<Resource> getResponseOK() {
        Resource body = new ClassPathResource(TESTING_FILE_NAME);
        HttpHeaders headers = new HttpHeaders();
        headers.add("originalfilename", "fileName");
        headers.add(HttpHeaders.CONTENT_TYPE, "xslx");
        return new ResponseEntity<>(body, headers, HttpStatus.OK);

    }

    private static DynamicFixedListType generateDynamicList(String value) {
        DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
        DynamicValueType dynamicValueType = new DynamicValueType();
        dynamicValueType.setLabel(value);
        dynamicValueType.setCode(value);
        dynamicFixedListType.setValue(dynamicValueType);
        dynamicFixedListType.setListItems(new ArrayList<>(Collections.singleton(dynamicValueType)));
        return dynamicFixedListType;
    }

    public static Sheet getDataTypeSheet(String fileName) throws IOException {

        Resource body = new ClassPathResource(fileName);
        Workbook workbook = new XSSFWorkbook(body.getInputStream());
        return workbook.getSheet(SHEET_NAME);

    }
}
