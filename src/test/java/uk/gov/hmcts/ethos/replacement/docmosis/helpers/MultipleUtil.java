package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.apache.poi.xssf.usermodel.XSSFSheet;
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
import uk.gov.hmcts.ecm.common.model.multiples.SubmitMultipleEvent;
import uk.gov.hmcts.ecm.common.model.multiples.items.CaseMultipleTypeItem;
import uk.gov.hmcts.ecm.common.model.multiples.items.SubMultipleTypeItem;
import uk.gov.hmcts.ecm.common.model.multiples.types.MultipleObjectType;
import uk.gov.hmcts.ecm.common.model.multiples.types.SubMultipleActionType;
import uk.gov.hmcts.ecm.common.model.multiples.types.SubMultipleType;
import uk.gov.hmcts.ecm.common.model.schedule.ScheduleAddress;
import uk.gov.hmcts.ecm.common.model.schedule.SchedulePayloadES;
import uk.gov.hmcts.ecm.common.model.schedule.SchedulePayloadEvent;
import uk.gov.hmcts.ecm.common.model.schedule.items.ScheduleRespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.schedule.types.ScheduleClaimantIndType;
import uk.gov.hmcts.ecm.common.model.schedule.types.ScheduleClaimantType;
import uk.gov.hmcts.ecm.common.model.schedule.types.ScheduleRespondentSumType;

import java.io.IOException;
import java.util.*;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.*;

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
        multipleObjectTreeMap.put("245004/2020",  MultipleObject.builder()
                .subMultiple("245002")
                .ethosCaseRef("245004/2020")
                .flag1("AA")
                .flag2("BB")
                .flag3("")
                .flag4("")
                .build());
        multipleObjectTreeMap.put("245005/2020",  MultipleObject.builder()
                .subMultiple("SubMultiple")
                .ethosCaseRef("245005/2020")
                .flag1("AA")
                .flag2("BB")
                .flag3("")
                .flag4("")
                .build());
        return multipleObjectTreeMap;
    }

    public static TreeMap<String, Object> getMultipleObjectsFlags() {
        TreeMap<String, Object> multipleObjectTreeMap = new TreeMap<>();
        multipleObjectTreeMap.put("245000/2020", "245000/2020");
        multipleObjectTreeMap.put("245003/2020", "245003/2020");
        multipleObjectTreeMap.put("21006/2020", "21006/2020");
        return multipleObjectTreeMap;
    }

    public static TreeMap<String, Object> getMultipleObjectsSubMultiple() {
        TreeMap<String, Object> multipleObjectTreeMap = new TreeMap<>();
        multipleObjectTreeMap.put("SubMultiple", new ArrayList<>(Collections.singletonList("245000/2020")));
        multipleObjectTreeMap.put("SubMultiple3", new ArrayList<>(Collections.singletonList("245003/2020")));
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

    public static SchedulePayloadEvent getSchedulePayloadEventData(String ethosCaseReference) {
        SchedulePayloadES schedulePayloadES = new SchedulePayloadES();
        schedulePayloadES.setClaimantCompany("JuanFran");
        ScheduleClaimantType claimantType = new ScheduleClaimantType();
        ScheduleAddress address = new ScheduleAddress();
        address.setPostCode("M2 45GD");
        address.setAddressLine1("12 Sillavan Way");
        claimantType.setClaimantAddressUK(address);
        schedulePayloadES.setClaimantType(claimantType);
        ScheduleClaimantIndType claimantIndType = new ScheduleClaimantIndType();
        claimantIndType.setClaimantLastName("Mike");
        schedulePayloadES.setClaimantIndType(claimantIndType);
        ScheduleRespondentSumType respondentSumType = new ScheduleRespondentSumType();
        respondentSumType.setRespondentName("Andrew Smith");
        respondentSumType.setRespondentAddress(address);
        ScheduleRespondentSumTypeItem respondentSumTypeItem = new ScheduleRespondentSumTypeItem();
        respondentSumTypeItem.setValue(respondentSumType);
        schedulePayloadES.setRespondentCollection(new ArrayList<>(Collections.singletonList(respondentSumTypeItem)));
        schedulePayloadES.setEthosCaseReference(ethosCaseReference);

        SchedulePayloadEvent schedulePayloadEvent = new SchedulePayloadEvent();
        schedulePayloadEvent.setSchedulePayloadES(schedulePayloadES);
        return schedulePayloadEvent;
    }

    public static List<SubmitEvent> getSubmitEvents() {
        SubmitEvent submitEvent1 = new SubmitEvent();
        submitEvent1.setCaseData(getCaseData("245000/2020"));
        submitEvent1.setCaseId(1232121232);
        SubmitEvent submitEvent2 = new SubmitEvent();
        submitEvent2.setCaseData(getCaseData("245003/2020"));
        submitEvent2.setCaseId(1232121233);
        return new ArrayList<>(Arrays.asList(submitEvent1, submitEvent2));
    }

    public static HashSet<SchedulePayloadEvent> getSchedulePayloadEvents() {
        return new HashSet<>(Arrays.asList(
                getSchedulePayloadEventData("245000/2020"),
                getSchedulePayloadEventData("245003/2020")));
    }

    public static List<SubmitMultipleEvent> getSubmitMultipleEvents() {
        SubmitMultipleEvent submitMultipleEventSearched = new SubmitMultipleEvent();
        submitMultipleEventSearched.setCaseData(MultipleUtil.getMultipleData());
        submitMultipleEventSearched.getCaseData().setMultipleReference("246001");
        return new ArrayList<>(Collections.singletonList(submitMultipleEventSearched));
    }

    public static List<SubMultipleTypeItem> getSubMultipleCollection() {
        SubMultipleTypeItem subMultipleTypeItem = new SubMultipleTypeItem();
        SubMultipleType subMultipleType = new SubMultipleType();
        subMultipleType.setSubMultipleName("SubMultiple");
        subMultipleType.setSubMultipleRef("246000/1");
        subMultipleTypeItem.setId("11111");
        subMultipleTypeItem.setValue(subMultipleType);
        SubMultipleTypeItem subMultipleTypeItem1 = new SubMultipleTypeItem();
        SubMultipleType subMultipleType1 = new SubMultipleType();
        subMultipleType1.setSubMultipleName("SubMultiple2");
        subMultipleType1.setSubMultipleRef("246000/2");
        subMultipleTypeItem1.setId("22222");
        subMultipleTypeItem1.setValue(subMultipleType1);
        return new ArrayList<>(Arrays.asList(subMultipleTypeItem, subMultipleTypeItem1));
    }

    public static SubMultipleActionType getSubMultipleActionType() {
        SubMultipleActionType subMultipleActionType = new SubMultipleActionType();
        subMultipleActionType.setActionType(CREATE_ACTION);
        subMultipleActionType.setAmendSubMultipleNameExisting("SubMultiple");
        subMultipleActionType.setAmendSubMultipleNameNew("SubMultipleAmended");
        subMultipleActionType.setCreateSubMultipleName("NewSubMultiple");
        subMultipleActionType.setDeleteSubMultipleName("SubMultiple");
        return subMultipleActionType;
    }

    public static List<CaseMultipleTypeItem> getCaseMultipleCollection() {
        List<CaseMultipleTypeItem> caseMultipleTypeItemList = new ArrayList<>();
        caseMultipleTypeItemList.add(generateMultipleObjectType("1", "21006/2020", "Sub1", "DD"));
        caseMultipleTypeItemList.add(generateMultipleObjectType("2", "245000/2020", "Sub2", "DD"));
        caseMultipleTypeItemList.add(generateMultipleObjectType("3", "245001/2020", "Sub3", "CC"));
        return caseMultipleTypeItemList;
    }

    private static CaseMultipleTypeItem generateMultipleObjectType(String id, String ethosCaseRef, String subMultiple, String flag1) {
        CaseMultipleTypeItem caseMultipleTypeItem = new CaseMultipleTypeItem();
        MultipleObjectType multipleObjectType = new MultipleObjectType();
        multipleObjectType.setEthosCaseRef(ethosCaseRef);
        multipleObjectType.setSubMultiple(subMultiple);
        multipleObjectType.setFlag1(flag1);
        caseMultipleTypeItem.setId(id);
        caseMultipleTypeItem.setValue(multipleObjectType);
        return caseMultipleTypeItem;
    }

    public static List<MultipleObject> getCaseMultipleObjectCollection() {
        List<MultipleObject> multipleObjects = new ArrayList<>();
        multipleObjects.add(generateMultipleObject("21006/2020", "Sub1", "DD"));
        multipleObjects.add(generateMultipleObject("245000/2020", "Sub2", "DD"));
        multipleObjects.add(generateMultipleObject("245001/2020", "Sub3", "CC"));
        return multipleObjects;
    }

    private static MultipleObject generateMultipleObject(String ethosCaseRef, String subMultiple, String flag1) {
        return MultipleObject.builder()
                .ethosCaseRef(ethosCaseRef)
                .subMultiple(subMultiple)
                .flag1(flag1)
                .flag2("")
                .flag3("")
                .flag4("")
                .build();
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
        multipleData.setMultipleReference("246000");
        multipleData.setCaseIdCollection(caseIdCollection);
        multipleData.setScheduleDocName(MULTIPLE_SCHEDULE_CONFIG);
        multipleData.setBatchUpdateType(BATCH_UPDATE_TYPE_1);
        getDocumentCollection(multipleData);
        multipleData.setSubMultipleCollection(getSubMultipleCollection());
        multipleData.setSubMultipleAction(getSubMultipleActionType());
        multipleData.setLeadCase("21006/2020");
        multipleData.setState(OPEN_STATE);
        multipleData.setCaseCounter("2");
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
        uploadedDocumentType.setDocumentFilename(MultiplesHelper.generateExcelDocumentName(new MultipleData()));
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

    public static DynamicFixedListType generateDynamicList(String value) {
        DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
        DynamicValueType dynamicValueType = new DynamicValueType();
        dynamicValueType.setLabel(value);
        dynamicValueType.setCode(value);
        dynamicFixedListType.setValue(dynamicValueType);
        dynamicFixedListType.setListItems(new ArrayList<>(Collections.singleton(dynamicValueType)));
        return dynamicFixedListType;
    }

    public static XSSFSheet getDataTypeSheet(String fileName) throws IOException {

        Resource body = new ClassPathResource(fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(body.getInputStream());
        return workbook.getSheet(SHEET_NAME);

    }

    public static CaseData getCaseDataForSinglesToBeMoved() {

        CaseData caseData = new CaseData();

        caseData.setLeadClaimant(YES);
        caseData.setMultipleReference("246000");
        caseData.setSubMultipleName("updatedSubMultipleName");

        caseData.setCaseType(MULTIPLE_CASE_TYPE);
        caseData.setMultipleFlag(NO);

        return caseData;

    }
}
