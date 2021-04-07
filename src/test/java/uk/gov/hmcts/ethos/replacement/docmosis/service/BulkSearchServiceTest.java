package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.bulk.BulkData;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDetails;
import uk.gov.hmcts.ecm.common.model.bulk.BulkRequest;
import uk.gov.hmcts.ecm.common.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.items.MidSearchTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.items.MultipleTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.types.CaseType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.bulk.types.MultipleType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ecm.common.model.helper.BulkCasesPayload;
import uk.gov.hmcts.ecm.common.model.helper.BulkRequestPayload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANUALLY_CREATED_POSITION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@RunWith(SpringJUnit4ClassRunner.class)
public class BulkSearchServiceTest {

    @InjectMocks
    private BulkSearchService bulkSearchService;
    @Mock
    private CcdClient ccdClient;
    @Mock
    private MultipleReferenceService multipleReferenceService;
    private BulkRequest bulkRequest;
    private BulkDetails bulkDetails;
    private SubmitEvent submitEvent;

    @Before
    public void setUp() {
        bulkRequest = new BulkRequest();
        bulkDetails = new BulkDetails();
        BulkData bulkData = new BulkData();
        bulkData.setMultipleSource(MANUALLY_CREATED_POSITION);
        bulkData.setMultipleReference("1111");
        bulkData.setClaimantSurname("");
        bulkData.setEthosCaseReference("222");
        MultipleType multipleType = new MultipleType();
        multipleType.setEthosCaseReferenceM("2222");
        multipleType.setRespondentRepM("JuanPedro");
        multipleType.setSubMultipleM(" ");
        MultipleTypeItem multipleTypeItem = new MultipleTypeItem();
        multipleTypeItem.setValue(multipleType);
        multipleTypeItem.setId("2222");
        bulkData.setMultipleCollection(new ArrayList<>(Collections.singletonList(multipleTypeItem)));
        MidSearchTypeItem midSearchTypeItem = new MidSearchTypeItem();
        midSearchTypeItem.setId("1111");
        midSearchTypeItem.setValue("1111");
        MidSearchTypeItem midSearchTypeItem1 = new MidSearchTypeItem();
        midSearchTypeItem1.setId("2222");
        midSearchTypeItem1.setValue("2222");
        bulkData.setMidSearchCollection(new ArrayList<>(Arrays.asList(midSearchTypeItem, midSearchTypeItem1)));
        bulkDetails.setJurisdiction("TRIBUNALS");
        bulkDetails.setCaseTypeId("Manchester_Multiple");
        bulkDetails.setCaseData(bulkData);
        bulkRequest.setCaseDetails(bulkDetails);
        submitEvent = new SubmitEvent();
        submitEvent.setState("Accepted");
        submitEvent.setCaseData(getCaseData());

        bulkSearchService = new BulkSearchService(ccdClient, multipleReferenceService);
    }

    @Test
    public void bulkSearchLogic() {
        String result = "[SearchTypeItem(id=2222, value=SearchType(caseIDS=null, ethosCaseReferenceS=2222, leadClaimantS=null, clerkRespS=null, " +
                "claimantSurnameS=null, respondentSurnameS=null, claimantRepS=null, respondentRepS=JuanPedro, fileLocS=null, receiptDateS=null, " +
                "positionTypeS=null, feeGroupReferenceS=null, jurCodesCollectionS=null, stateS=null, currentPositionS=null, claimantAddressLine1S=null, " +
                "claimantPostCodeS=null, respondentAddressLine1S=null, respondentPostCodeS=null, flag1S=null, flag2S=null, EQPS=null, " +
                "respondentRepOrgS=null, claimantRepOrgS=null))]";
        BulkRequestPayload bulkRequestPayload = bulkSearchService.bulkSearchLogic(bulkDetails);
        assertEquals(result, bulkRequestPayload.getBulkDetails().getCaseData().getSearchCollection().toString());
    }

    @Test
    public void bulkSearchLogicWithErrors() {
        String result = "[No cases have been found]";
        bulkDetails.getCaseData().setMidSearchCollection(null);
        BulkRequestPayload bulkRequestPayload = bulkSearchService.bulkSearchLogic(bulkDetails);
        assertEquals(result, bulkRequestPayload.getErrors().toString());
    }

    @Test
    public void bulkMidSearchLogic() {
        String result = "BulkDetails(super=GenericCaseDetails(caseId=null, jurisdiction=TRIBUNALS, state=null, caseTypeId=Manchester_Multiple, createdDate=null, " +
                "lastModified=null, dataClassification=null), caseData=BulkData(bulkCaseTitle=null, multipleReference=1111, feeGroupReference=null, claimantSurname=, " +
                "respondentSurname=null, claimantRep=null, respondentRep=JuanPedro, ethosCaseReference=222, clerkResponsible=null, fileLocation=null, " +
                "jurCodesCollection=null, fileLocationV2=null, feeGroupReferenceV2=null, claimantSurnameV2=null, respondentSurnameV2=null, multipleReferenceV2=null, " +
                "clerkResponsibleV2=null, positionTypeV2=null, claimantRepV2=null, respondentRepV2=null, fileLocationGlasgow=null, fileLocationAberdeen=null, " +
                "fileLocationDundee=null, fileLocationEdinburgh=null, managingOffice=null, subMultipleName=null, subMultipleRef=null, caseIdCollection=null, " +
                "searchCollection=null, midSearchCollection=[MidSearchTypeItem(id=2222, value=2222)], multipleCollection=[MultipleTypeItem(id=2222, value=MultipleType" +
                "(caseIDM=null, ethosCaseReferenceM=2222, leadClaimantM=null, multipleReferenceM=null, clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null," +
                " claimantRepM=null, respondentRepM=JuanPedro, fileLocM=null, receiptDateM=null, positionTypeM=null, feeGroupReferenceM=null, jurCodesCollectionM=null," +
                " stateM=null, subMultipleM= , subMultipleTitleM=null, currentPositionM=null, claimantAddressLine1M=null, claimantPostCodeM=null, " +
                "respondentAddressLine1M=null, respondentPostCodeM=null, flag1M=null, flag2M=null, EQPM=null, respondentRepOrgM=null, claimantRepOrgM=null))], " +
                "subMultipleCollection=null, subMultipleDynamicList=null, searchCollectionCount=null, multipleCollectionCount=null, correspondenceType=null, " +
                "correspondenceScotType=null, selectAll=null, scheduleDocName=null, positionType=null, flag1=null, flag2=null, EQP=null, submissionRef=null, " +
                "claimantOrg=null, respondentOrg=null, state=null, flag1Update=null, flag2Update=null, EQPUpdate=null, jurCodesDynamicList=null, outcomeUpdate=null, " +
                "filterCases=null, docMarkUp=null, multipleSource=Manually Created))";
        bulkDetails.getCaseData().setRespondentRep("JuanPedro");
        BulkRequestPayload bulkRequestPayload = bulkSearchService.bulkMidSearchLogic(bulkDetails, false);
        assertEquals(result, bulkRequestPayload.getBulkDetails().toString());
    }

    @Test
    public void bulkMidSearchLogicWithSelectAll() {
        String result = "BulkDetails(super=GenericCaseDetails(caseId=null, jurisdiction=TRIBUNALS, state=null, caseTypeId=Manchester_Multiple, createdDate=null, " +
                "lastModified=null, dataClassification=null), caseData=BulkData(bulkCaseTitle=null, multipleReference=1111, feeGroupReference=null, claimantSurname=, " +
                "respondentSurname=null, claimantRep=null, respondentRep=JuanPedro, ethosCaseReference=222, clerkResponsible=null, fileLocation=null, " +
                "jurCodesCollection=null, fileLocationV2=null, feeGroupReferenceV2=null, claimantSurnameV2=null, respondentSurnameV2=null, multipleReferenceV2=null, " +
                "clerkResponsibleV2=null, positionTypeV2=null, claimantRepV2=null, respondentRepV2=null, fileLocationGlasgow=null, fileLocationAberdeen=null, " +
                "fileLocationDundee=null, fileLocationEdinburgh=null, managingOffice=null, subMultipleName=null, subMultipleRef=null, caseIdCollection=null, " +
                "searchCollection=null, midSearchCollection=[MidSearchTypeItem(id=2222, value=2222)], multipleCollection=[MultipleTypeItem(id=2222, value=MultipleType" +
                "(caseIDM=null, ethosCaseReferenceM=2222, leadClaimantM=null, multipleReferenceM=null, clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null," +
                " claimantRepM=null, respondentRepM=JuanPedro, fileLocM=null, receiptDateM=null, positionTypeM=null, feeGroupReferenceM=null, jurCodesCollectionM=null," +
                " stateM=null, subMultipleM= , subMultipleTitleM=null, currentPositionM=null, claimantAddressLine1M=null, claimantPostCodeM=null, " +
                "respondentAddressLine1M=null, respondentPostCodeM=null, flag1M=null, flag2M=null, EQPM=null, respondentRepOrgM=null, claimantRepOrgM=null))], " +
                "subMultipleCollection=null, subMultipleDynamicList=null, searchCollectionCount=null, multipleCollectionCount=null, correspondenceType=null, " +
                "correspondenceScotType=null, selectAll=Yes, scheduleDocName=null, positionType=null, flag1=null, flag2=null, EQP=null, submissionRef=null, " +
                "claimantOrg=null, respondentOrg=null, state=null, flag1Update=null, flag2Update=null, EQPUpdate=null, jurCodesDynamicList=null, outcomeUpdate=null, " +
                "filterCases=null, docMarkUp=null, multipleSource=Manually Created))";
        bulkDetails.getCaseData().setRespondentRep("JuanPedro");
        bulkDetails.getCaseData().setSelectAll(YES);
        BulkRequestPayload bulkRequestPayload = bulkSearchService.bulkMidSearchLogic(bulkDetails, false);
        assertEquals(result, bulkRequestPayload.getBulkDetails().toString());
    }

    @Test
    public void bulkMidSearchLogicWithErrors() {
        String result = "[No cases have been found in this multiple]";
        bulkDetails.getCaseData().setMultipleCollection(null);
        BulkRequestPayload bulkRequestPayload = bulkSearchService.bulkMidSearchLogic(bulkDetails, false);
        assertEquals(result, bulkRequestPayload.getErrors().toString());
    }

    @Test
    public void bulkMidSearchSubMultipleLogic() {
        String result = "BulkDetails(super=GenericCaseDetails(caseId=null, jurisdiction=TRIBUNALS, state=null, caseTypeId=Manchester_Multiple, createdDate=null, " +
                "lastModified=null, dataClassification=null), caseData=BulkData(bulkCaseTitle=null, multipleReference=1111, feeGroupReference=null, claimantSurname=, " +
                "respondentSurname=null, claimantRep=null, respondentRep=JuanPedro, ethosCaseReference=222, clerkResponsible=null, fileLocation=null, " +
                "jurCodesCollection=null, fileLocationV2=null, feeGroupReferenceV2=null, claimantSurnameV2=null, respondentSurnameV2=null, multipleReferenceV2=null, " +
                "clerkResponsibleV2=null, positionTypeV2=null, claimantRepV2=null, respondentRepV2=null, fileLocationGlasgow=null, fileLocationAberdeen=null, " +
                "fileLocationDundee=null, fileLocationEdinburgh=null, managingOffice=null, subMultipleName=null, subMultipleRef=null, caseIdCollection=null, " +
                "searchCollection=null, midSearchCollection=[], multipleCollection=[MultipleTypeItem(id=2222, value=MultipleType(caseIDM=null, " +
                "ethosCaseReferenceM=2222, leadClaimantM=null, multipleReferenceM=null, clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null, " +
                "claimantRepM=null, respondentRepM=JuanPedro, fileLocM=null, receiptDateM=null, positionTypeM=null, feeGroupReferenceM=null, jurCodesCollectionM=null, " +
                "stateM=null, subMultipleM= , subMultipleTitleM=null, currentPositionM=null, claimantAddressLine1M=null, claimantPostCodeM=null, " +
                "respondentAddressLine1M=null, respondentPostCodeM=null, flag1M=null, flag2M=null, EQPM=null, respondentRepOrgM=null, claimantRepOrgM=null))], " +
                "subMultipleCollection=null, subMultipleDynamicList=null, searchCollectionCount=null, multipleCollectionCount=null, correspondenceType=null, " +
                "correspondenceScotType=null, selectAll=null, scheduleDocName=null, positionType=null, flag1=null, flag2=null, EQP=null, submissionRef=null, " +
                "claimantOrg=null, respondentOrg=null, state=null, flag1Update=null, flag2Update=null, EQPUpdate=null, jurCodesDynamicList=null, outcomeUpdate=null, " +
                "filterCases=null, docMarkUp=null, multipleSource=Manually Created))";
        bulkDetails.getCaseData().setRespondentRep("JuanPedro");
        DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
        DynamicValueType dynamicValueType = new DynamicValueType();
        dynamicValueType.setLabel("11111");
        dynamicValueType.setCode("11111");
        dynamicFixedListType.setValue(dynamicValueType);
        List<DynamicValueType> listItems = new ArrayList<>(Collections.singleton(dynamicValueType));
        dynamicFixedListType.setListItems(listItems);
        bulkDetails.getCaseData().setSubMultipleDynamicList(dynamicFixedListType);
        BulkRequestPayload bulkRequestPayload = bulkSearchService.bulkMidSearchLogic(bulkDetails, false);
        assertEquals(result, bulkRequestPayload.getBulkDetails().toString());
    }

    @Test
    public void midCreateSubMultiple() {
        String result = "BulkDetails(super=GenericCaseDetails(caseId=null, jurisdiction=TRIBUNALS, state=null, caseTypeId=Manchester_Multiple, createdDate=null, " +
                "lastModified=null, dataClassification=null), caseData=BulkData(bulkCaseTitle=null, multipleReference=1111, feeGroupReference=null, claimantSurname=, " +
                "respondentSurname=null, claimantRep=null, respondentRep=JuanPedro, ethosCaseReference=222, clerkResponsible=null, fileLocation=null, " +
                "jurCodesCollection=null, fileLocationV2=null, feeGroupReferenceV2=null, claimantSurnameV2=null, respondentSurnameV2=null, multipleReferenceV2=null, " +
                "clerkResponsibleV2=null, positionTypeV2=null, claimantRepV2=null, respondentRepV2=null, fileLocationGlasgow=null, fileLocationAberdeen=null, " +
                "fileLocationDundee=null, fileLocationEdinburgh=null, managingOffice=null, subMultipleName=null, subMultipleRef=null, caseIdCollection=null, " +
                "searchCollection=null, midSearchCollection=[MidSearchTypeItem(id=2222, value=2222)], multipleCollection=[MultipleTypeItem(id=2222, value=MultipleType" +
                "(caseIDM=null, ethosCaseReferenceM=2222, leadClaimantM=null, multipleReferenceM=null, clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null," +
                " claimantRepM=null, respondentRepM=JuanPedro, fileLocM=null, receiptDateM=null, positionTypeM=null, feeGroupReferenceM=null, jurCodesCollectionM=null," +
                " stateM=null, subMultipleM= , subMultipleTitleM=null, currentPositionM=null, claimantAddressLine1M=null, claimantPostCodeM=null, " +
                "respondentAddressLine1M=null, respondentPostCodeM=null, flag1M=null, flag2M=null, EQPM=null, respondentRepOrgM=null, claimantRepOrgM=null))], " +
                "subMultipleCollection=null, subMultipleDynamicList=null, searchCollectionCount=null, multipleCollectionCount=null, correspondenceType=null, " +
                "correspondenceScotType=null, selectAll=null, scheduleDocName=null, positionType=null, flag1=null, flag2=null, EQP=null, submissionRef=null, " +
                "claimantOrg=null, respondentOrg=null, state=null, flag1Update=null, flag2Update=null, EQPUpdate=null, jurCodesDynamicList=null, outcomeUpdate=null, " +
                "filterCases=null, docMarkUp=null, multipleSource=Manually Created))";
        bulkDetails.getCaseData().setRespondentRep("JuanPedro");
        BulkRequestPayload bulkRequestPayload = bulkSearchService.bulkMidSearchLogic(bulkDetails, true);
        assertEquals(result, bulkRequestPayload.getBulkDetails().toString());
    }

    @Test
    public void midCreateSubMultipleCaseBelongsToOtherSubMultiple() {
        String result = "BulkDetails(super=GenericCaseDetails(caseId=null, jurisdiction=TRIBUNALS, state=null, caseTypeId=Manchester_Multiple, createdDate=null, " +
                "lastModified=null, dataClassification=null), caseData=BulkData(bulkCaseTitle=null, multipleReference=1111, feeGroupReference=null, claimantSurname=, " +
                "respondentSurname=null, claimantRep=null, respondentRep=JuanPedro, ethosCaseReference=222, clerkResponsible=null, fileLocation=null, " +
                "jurCodesCollection=null, fileLocationV2=null, feeGroupReferenceV2=null, claimantSurnameV2=null, respondentSurnameV2=null, multipleReferenceV2=null, " +
                "clerkResponsibleV2=null, positionTypeV2=null, claimantRepV2=null, respondentRepV2=null, fileLocationGlasgow=null, fileLocationAberdeen=null, " +
                "fileLocationDundee=null, fileLocationEdinburgh=null, managingOffice=null, subMultipleName=null, subMultipleRef=null, caseIdCollection=null, " +
                "searchCollection=null, midSearchCollection=[], multipleCollection=[MultipleTypeItem(id=2222, value=MultipleType(caseIDM=null, " +
                "ethosCaseReferenceM=2222, leadClaimantM=null, multipleReferenceM=null, clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null, " +
                "claimantRepM=null, respondentRepM=JuanPedro, fileLocM=null, receiptDateM=null, positionTypeM=null, feeGroupReferenceM=null, jurCodesCollectionM=null, " +
                "stateM=null, subMultipleM=4200001/1, subMultipleTitleM=null, currentPositionM=null, claimantAddressLine1M=null, claimantPostCodeM=null, " +
                "respondentAddressLine1M=null, respondentPostCodeM=null, flag1M=null, flag2M=null, EQPM=null, respondentRepOrgM=null, claimantRepOrgM=null))], " +
                "subMultipleCollection=null, subMultipleDynamicList=null, searchCollectionCount=null, multipleCollectionCount=null, correspondenceType=null, " +
                "correspondenceScotType=null, selectAll=null, scheduleDocName=null, positionType=null, flag1=null, flag2=null, EQP=null, submissionRef=null, " +
                "claimantOrg=null, respondentOrg=null, state=null, flag1Update=null, flag2Update=null, EQPUpdate=null, jurCodesDynamicList=null, outcomeUpdate=null, " +
                "filterCases=null, docMarkUp=null, multipleSource=Manually Created))";
        bulkDetails.getCaseData().setRespondentRep("JuanPedro");
        bulkDetails.getCaseData().getMultipleCollection().get(0).getValue().setSubMultipleM("4200001/1");
        BulkRequestPayload bulkRequestPayload = bulkSearchService.bulkMidSearchLogic(bulkDetails, true);
        assertEquals(result, bulkRequestPayload.getBulkDetails().toString());
    }

    @Test(expected = Exception.class)
    public void searchCasesByFieldsRequestException() {
        List<MidSearchTypeItem> midSearchedListExpected = new ArrayList<>();
        List<MultipleTypeItem> multipleTypeItemToSearchBy = new ArrayList<>();
        List<MidSearchTypeItem> midSearchedList = bulkSearchService.midSearchCasesByFieldsRequest(multipleTypeItemToSearchBy, new BulkDetails(), false);
        assertEquals(midSearchedListExpected, midSearchedList);
    }

    @Test
    public void searchCasesByFieldsNoMatchesCompleteRequest() {
        bulkRequest.getCaseDetails().getCaseData().setMultipleCollection(getMultipleTypeItemList());
        BulkData bulkData = bulkDetails.getCaseData();
        bulkData.setRespondentSurname("Antonio");
        bulkData.setRespondentRep("Mike");
        bulkData.setClaimantRep("Johnson");
        bulkData.setClaimantSurname("Juan");
        bulkData.setPositionType("PositionType");
        bulkData.setFlag1("Flag1");
        bulkData.setFlag2("Flag2");
        bulkData.setEQP("EQP");
        bulkData.setClaimantOrg("ClaimantOrg");
        bulkData.setRespondentOrg("RespondentOrg");
        bulkData.setSubmissionRef("11111111111");
        bulkData.setState("Accepted");
        JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
        JurCodesType jurCodesType = new JurCodesType();
        jurCodesType.setJuridictionCodesList("COM");
        jurCodesTypeItem.setValue(jurCodesType);
        bulkData.setJurCodesCollection(new ArrayList<>(Collections.singleton(jurCodesTypeItem)));
        bulkDetails.setCaseData(bulkData);
        List<MidSearchTypeItem> midSearchedList = bulkSearchService.midSearchCasesByFieldsRequest(getMultipleTypeItemList(), bulkDetails, false);
        assertEquals("[]", midSearchedList.toString());
    }

    private List<MultipleTypeItem> getMultipleTypeItemList() {
        MultipleType multipleType1 = new MultipleType();
        multipleType1.setEthosCaseReferenceM("111");
        multipleType1.setClaimantSurnameM("Pedro");
        multipleType1.setRespondentSurnameM("Pedro");
        MultipleTypeItem multipleTypeItem1 = new MultipleTypeItem();
        multipleTypeItem1.setId("1111");
        multipleTypeItem1.setValue(multipleType1);
        MultipleType multipleType2 = new MultipleType();
        multipleType2.setEthosCaseReferenceM("222");
        multipleType2.setClaimantSurnameM("Pedro");
        multipleType2.setRespondentSurnameM("Pedro");
        MultipleTypeItem multipleTypeItem2 = new MultipleTypeItem();
        multipleTypeItem2.setId("2222");
        multipleTypeItem2.setValue(multipleType2);
        MultipleType multipleType3 = new MultipleType();
        multipleType3.setEthosCaseReferenceM("333");
        multipleType3.setClaimantSurnameM("Pedro3");
        multipleType3.setRespondentSurnameM("Pedro");
        MultipleTypeItem multipleTypeItem3 = new MultipleTypeItem();
        multipleTypeItem3.setId("3333");
        multipleTypeItem3.setValue(multipleType3);
        List<MultipleTypeItem> multipleTypeItemList = new ArrayList<>();
        multipleTypeItemList.add(multipleTypeItem1);
        multipleTypeItemList.add(multipleTypeItem2);
        multipleTypeItemList.add(multipleTypeItem3);
        return multipleTypeItemList;
    }

    private CaseData getCaseData() {
        CaseData caseData = new CaseData();
        caseData.setCaseSource(MANUALLY_CREATED_POSITION);
        caseData.setFeeGroupReference("111122211");
        caseData.setEthosCaseReference("1111");
        caseData.setMultipleReference("123456");
        ClaimantIndType claimantIndType = new ClaimantIndType();
        claimantIndType.setClaimantLastName("Fernandez");
        caseData.setClaimantIndType(claimantIndType);
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName("Mr Respondent");
        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setValue(respondentSumType);
        caseData.setRespondentCollection(new ArrayList<>(Collections.singletonList(respondentSumTypeItem)));
        return caseData;
    }

    private BulkData getSimpleBulkData(BulkData bulkData) {
        CaseType caseType = new CaseType();
        caseType.setEthosCaseReference("1111");
        CaseIdTypeItem caseIdTypeItem = new CaseIdTypeItem();
        caseIdTypeItem.setId("1111");
        caseIdTypeItem.setValue(caseType);
        bulkData.setCaseIdCollection(new ArrayList<>(Collections.singletonList(caseIdTypeItem)));
        MultipleType multipleType = new MultipleType();
        multipleType.setEthosCaseReferenceM("281231");
        multipleType.setLeadClaimantM(YES);
        MultipleTypeItem multipleTypeItem = new MultipleTypeItem();
        multipleTypeItem.setValue(multipleType);
        multipleTypeItem.setId("22222");
        bulkData.setMultipleCollection(new ArrayList<>(Collections.singletonList(multipleTypeItem)));
        bulkData.setMultipleReference("12345");
        return bulkData;
    }

    @Test
    public void caseUpdateRequestElasticSearch() throws IOException {
        List<SubmitEvent> submitEventList = new ArrayList<>(Collections.singletonList(submitEvent));
        when(ccdClient.retrieveCasesElasticSearchForCreation(anyString(), anyString(), anyList(), anyString())).thenReturn(submitEventList);
        bulkDetails.setCaseData(getSimpleBulkData(bulkDetails.getCaseData()));
        BulkCasesPayload bulkCasesPayload = bulkSearchService.bulkCasesRetrievalRequestElasticSearch(bulkDetails, "authToken", false, true);
        assertEquals(submitEventList, bulkCasesPayload.getSubmitEvents());
    }

    @Test
    public void caseUpdateRequestElasticSearchNoFiltered() throws IOException {
        List<SubmitEvent> submitEventList = new ArrayList<>(Collections.singletonList(submitEvent));
        when(ccdClient.retrieveCasesElasticSearchForCreation(anyString(), anyString(), anyList(), anyString()))
                .thenReturn(submitEventList);
        bulkDetails.setCaseData(getSimpleBulkData(bulkDetails.getCaseData()));
        BulkCasesPayload bulkCasesPayload = bulkSearchService.bulkCasesRetrievalRequestElasticSearch(
                bulkDetails, "authToken", false, false);
        assertEquals(submitEventList, bulkCasesPayload.getSubmitEvents());
    }
}