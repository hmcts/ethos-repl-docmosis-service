package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.MidSearchTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.MultipleTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.SubMultipleTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.MultipleType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.SubMultipleType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkRequestPayload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.SELECT_ALL_VALUE;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.SELECT_NONE_VALUE;

@RunWith(SpringJUnit4ClassRunner.class)
public class SubMultipleServiceTest {

    @InjectMocks
    private SubMultipleService subMultipleService;
    @Mock
    private SubMultipleReferenceService subMultipleReferenceService;

    private BulkDetails bulkDetails;

    @Before
    public void setUp() {
        BulkData bulkData = new BulkData();
        bulkDetails = new BulkDetails();
        bulkData.setSubMultipleName("SubMultipleNew");

        MidSearchTypeItem midSearchTypeItem = new MidSearchTypeItem();
        midSearchTypeItem.setId("1111");
        midSearchTypeItem.setValue("1111");
        MidSearchTypeItem midSearchTypeItem1 = new MidSearchTypeItem();
        midSearchTypeItem1.setId("2222");
        midSearchTypeItem1.setValue("2222");
        bulkData.setMidSearchCollection(new ArrayList<>(Arrays.asList(midSearchTypeItem, midSearchTypeItem1)));

        MultipleType multipleType = new MultipleType();
        multipleType.setEthosCaseReferenceM("2222");
        multipleType.setSubMultipleM(" ");
        MultipleTypeItem multipleTypeItem = new MultipleTypeItem();
        multipleTypeItem.setId("2222");
        multipleTypeItem.setValue(multipleType);
        bulkData.setMultipleCollection(new ArrayList<>(Collections.singletonList(multipleTypeItem)));

        bulkDetails.setCaseData(bulkData);
    }

    @Test
    public void createSubMultipleLogic() {
        String result = "BulkData(bulkCaseTitle=null, multipleReference=null, feeGroupReference=null, claimantSurname=null, respondentSurname=null, " +
                "claimantRep=null, respondentRep=null, ethosCaseReference=null, clerkResponsible=null, fileLocation=null, jurCodesCollection=null, " +
                "fileLocationV2=null, feeGroupReferenceV2=null, claimantSurnameV2=null, respondentSurnameV2=null, multipleReferenceV2=null, " +
                "clerkResponsibleV2=null, positionTypeV2=null, claimantRepV2=null, respondentRepV2=null, fileLocationGlasgow=null, fileLocationAberdeen=null, " +
                "fileLocationDundee=null, fileLocationEdinburgh=null, managingOffice=null, subMultipleName=null, subMultipleRef=null, caseIdCollection=null, " +
                "searchCollection=null, midSearchCollection=null, " +
                "multipleCollection=[MultipleTypeItem(id=2222, value=MultipleType(caseIDM=null, ethosCaseReferenceM=2222, leadClaimantM=null, " +
                "multipleReferenceM=null, clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null, claimantRepM=null, respondentRepM=null, " +
                "fileLocM=null, receiptDateM=null, positionTypeM=null, feeGroupReferenceM=null, jurCodesCollectionM=null, stateM=null, subMultipleM=null, " +
                "subMultipleTitleM=SubMultipleNew, currentPositionM=null, claimantAddressLine1M=null, claimantPostCodeM=null, respondentAddressLine1M=null, " +
                "respondentPostCodeM=null, flag1M=null, flag2M=null, EQPM=null, respondentRepOrgM=null, claimantRepOrgM=null))], " +
                "subMultipleCollection=[SubMultipleTypeItem(id=null, value=SubMultipleType(subMultipleNameT=SubMultipleNew, subMultipleRefT=null))], " +
                "subMultipleDynamicList=null, searchCollectionCount=null, multipleCollectionCount=null, correspondenceType=null, correspondenceScotType=null, " +
                "selectAll=null, scheduleDocName=null, positionType=null, flag1=null, flag2=null, EQP=null, submissionRef=null, claimantOrg=null, " +
                "respondentOrg=null, state=null, flag1Update=null, flag2Update=null, EQPUpdate=null, jurCodesDynamicList=null, outcomeUpdate=null, " +
                "filterCases=null, docMarkUp=null, caseSource=null)";
        BulkRequestPayload bulkRequestPayload = subMultipleService.createSubMultipleLogic(bulkDetails);
        assertEquals(result, bulkRequestPayload.getBulkDetails().getCaseData().toString());
        assertEquals(1, bulkRequestPayload.getBulkDetails().getCaseData().getSubMultipleCollection().size());
    }

    @Test
    public void createSubMultipleLogicWithErrors() {
        String result = "[No cases have been found]";
        bulkDetails.getCaseData().setMidSearchCollection(null);
        BulkRequestPayload bulkRequestPayload = subMultipleService.createSubMultipleLogic(bulkDetails);
        assertEquals(result, bulkRequestPayload.getErrors().toString());
    }

    @Test
    public void createSubMultipleLogicWithPreviousSubMultiples() {
        bulkDetails.getCaseData().setSubMultipleCollection(createSubMultiples());
        BulkRequestPayload bulkRequestPayload = subMultipleService.createSubMultipleLogic(bulkDetails);
        assertEquals(2, bulkRequestPayload.getBulkDetails().getCaseData().getSubMultipleCollection().size());
    }

    @Test
    public void populateSubMultipleDynamicListLogic() {
        String result = "DynamicFixedListType(value=DynamicValueType(code=1234567, label=SubMultiple1), " +
                "listItems=[DynamicValueType(code=1234567, label=SubMultiple1)])";
        bulkDetails.getCaseData().setSubMultipleCollection(createSubMultiples());
        BulkRequestPayload bulkRequestPayload = subMultipleService.populateSubMultipleDynamicListLogic(bulkDetails);
        assertEquals(result, bulkRequestPayload.getBulkDetails().getCaseData().getSubMultipleDynamicList().toString());
    }

    @Test
    public void populateSubMultipleDynamicListLogicWithPreviousDynamicList() {
        String result = "DynamicFixedListType(value=DynamicValueType(code=1234567, label=SubMultiple1), " +
                "listItems=[DynamicValueType(code=1234567, label=SubMultiple1)])";
        bulkDetails.getCaseData().setSubMultipleDynamicList(createDynamicFixedListType());
        bulkDetails.getCaseData().setSubMultipleCollection(createSubMultiples());
        BulkRequestPayload bulkRequestPayload = subMultipleService.populateSubMultipleDynamicListLogic(bulkDetails);
        assertEquals(result, bulkRequestPayload.getBulkDetails().getCaseData().getSubMultipleDynamicList().toString());
    }

    @Test
    public void populateSubMultipleDynamicListLogicWithErrors() {
        String result = "[No sub multiples have been found]";
        BulkRequestPayload bulkRequestPayload = subMultipleService.populateSubMultipleDynamicListLogic(bulkDetails);
        assertEquals(result, bulkRequestPayload.getErrors().toString());
    }

    @Test
    public void populateFilterDefaultedAllDynamicListLogic() {
        String result = "DynamicFixedListType(value=DynamicValueType(code=999999, label=Select All), " +
                "listItems=[DynamicValueType(code=999999, label=Select All), DynamicValueType(code=1234567, label=SubMultiple1)])";
        bulkDetails.getCaseData().setSubMultipleCollection(createSubMultiples());
        BulkRequestPayload bulkRequestPayload = subMultipleService.populateFilterDefaultedDynamicListLogic(bulkDetails, SELECT_ALL_VALUE);
        assertEquals(result, bulkRequestPayload.getBulkDetails().getCaseData().getSubMultipleDynamicList().toString());
    }

    @Test
    public void populateFilterDefaultedNoneDynamicListLogic() {
        String result = "DynamicFixedListType(value=DynamicValueType(code=999999, label=None), " +
                "listItems=[DynamicValueType(code=999999, label=None), DynamicValueType(code=1234567, label=SubMultiple1)])";
        bulkDetails.getCaseData().setSubMultipleCollection(createSubMultiples());
        BulkRequestPayload bulkRequestPayload = subMultipleService.populateFilterDefaultedDynamicListLogic(bulkDetails, SELECT_NONE_VALUE);
        assertEquals(result, bulkRequestPayload.getBulkDetails().getCaseData().getSubMultipleDynamicList().toString());
    }

    @Test
    public void deleteSubMultipleLogic() {
        String result = "BulkData(bulkCaseTitle=null, multipleReference=null, feeGroupReference=null, claimantSurname=null, respondentSurname=null, " +
                "claimantRep=null, respondentRep=null, ethosCaseReference=null, clerkResponsible=null, fileLocation=null, jurCodesCollection=null, " +
                "fileLocationV2=null, feeGroupReferenceV2=null, claimantSurnameV2=null, respondentSurnameV2=null, multipleReferenceV2=null, " +
                "clerkResponsibleV2=null, positionTypeV2=null, claimantRepV2=null, respondentRepV2=null, fileLocationGlasgow=null, fileLocationAberdeen=null, " +
                "fileLocationDundee=null, fileLocationEdinburgh=null, managingOffice=null, subMultipleName=SubMultipleNew, subMultipleRef=null, " +
                "caseIdCollection=null, searchCollection=null, " +
                "midSearchCollection=[MidSearchTypeItem(id=1111, value=1111), MidSearchTypeItem(id=2222, value=2222)], " +
                "multipleCollection=[MultipleTypeItem(id=2222, value=MultipleType(caseIDM=null, ethosCaseReferenceM=2222, leadClaimantM=null, " +
                "multipleReferenceM=null, clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null, claimantRepM=null, respondentRepM=null, " +
                "fileLocM=null, receiptDateM=null, positionTypeM=null, feeGroupReferenceM=null, jurCodesCollectionM=null, stateM=null, subMultipleM= , " +
                "subMultipleTitleM=null, currentPositionM=null, claimantAddressLine1M=null, claimantPostCodeM=null, respondentAddressLine1M=null, " +
                "respondentPostCodeM=null, flag1M=null, flag2M=null, EQPM=null, respondentRepOrgM=null, claimantRepOrgM=null)), " +
                "MultipleTypeItem(id=3333, value=MultipleType(caseIDM=null, ethosCaseReferenceM=3333, leadClaimantM=null, multipleReferenceM=null, " +
                "clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null, claimantRepM=null, respondentRepM=null, fileLocM=null, receiptDateM=null, " +
                "positionTypeM=null, feeGroupReferenceM=null, jurCodesCollectionM=null, stateM=null, subMultipleM= , subMultipleTitleM= , " +
                "currentPositionM=null, claimantAddressLine1M=null, claimantPostCodeM=null, respondentAddressLine1M=null, respondentPostCodeM=null, " +
                "flag1M=null, flag2M=null, EQPM=null, respondentRepOrgM=null, claimantRepOrgM=null))], " +
                "subMultipleCollection=[SubMultipleTypeItem(id=1234567, value=SubMultipleType(subMultipleNameT=SubMultiple1, subMultipleRefT=1234567))], " +
                "subMultipleDynamicList=null, searchCollectionCount=null, multipleCollectionCount=null, correspondenceType=null, correspondenceScotType=null, " +
                "selectAll=null, scheduleDocName=null, positionType=null, flag1=null, flag2=null, EQP=null, submissionRef=null, claimantOrg=null, " +
                "respondentOrg=null, state=null, flag1Update=null, flag2Update=null, EQPUpdate=null, jurCodesDynamicList=null, outcomeUpdate=null, " +
                "filterCases=null, docMarkUp=null, caseSource=null)";
        MultipleType multipleType = new MultipleType();
        multipleType.setEthosCaseReferenceM("3333");
        multipleType.setSubMultipleM("1111");
        MultipleTypeItem multipleTypeItem = new MultipleTypeItem();
        multipleTypeItem.setId("3333");
        multipleTypeItem.setValue(multipleType);
        bulkDetails.getCaseData().getMultipleCollection().add(multipleTypeItem);
        bulkDetails.getCaseData().setSubMultipleDynamicList(createDynamicFixedListType());
        bulkDetails.getCaseData().setSubMultipleCollection(createTwoSubMultiples());
        BulkRequestPayload bulkRequestPayload = subMultipleService.deleteSubMultipleLogic(bulkDetails);
        assertEquals(result, bulkRequestPayload.getBulkDetails().getCaseData().toString());
    }

    @Test
    public void deleteSubMultipleLogicWithErrors() {
        String result = "[No sub multiples have been found]";
        BulkRequestPayload bulkRequestPayload = subMultipleService.deleteSubMultipleLogic(bulkDetails);
        assertEquals(result, bulkRequestPayload.getErrors().toString());
    }

    @Test
    public void bulkMidUpdateLogic() {
        String result = "[MidSearchTypeItem(id=123456, value=3333)]";
        MultipleType multipleType = new MultipleType();
        multipleType.setEthosCaseReferenceM("3333");
        multipleType.setSubMultipleM("1111");
        MultipleTypeItem multipleTypeItem = new MultipleTypeItem();
        multipleTypeItem.setId("123456");
        multipleTypeItem.setValue(multipleType);
        bulkDetails.getCaseData().getMultipleCollection().add(multipleTypeItem);
        bulkDetails.getCaseData().setSubMultipleDynamicList(createDynamicFixedListType());
        bulkDetails.getCaseData().setSubMultipleCollection(createTwoSubMultiples());
        BulkRequestPayload bulkRequestPayload = subMultipleService.bulkMidUpdateLogic(bulkDetails);
        assertEquals(result, bulkRequestPayload.getBulkDetails().getCaseData().getMidSearchCollection().toString());
        assertEquals("SubMultiple2", bulkRequestPayload.getBulkDetails().getCaseData().getSubMultipleName());
        assertEquals("1111", bulkRequestPayload.getBulkDetails().getCaseData().getSubMultipleRef());
    }

    @Test
    public void bulkMidUpdateLogicWithErrors() {
        String result = "[No sub multiples have been found]";
        BulkRequestPayload bulkRequestPayload = subMultipleService.bulkMidUpdateLogic(bulkDetails);
        assertEquals(result, bulkRequestPayload.getErrors().toString());
    }

    @Test
    public void updateSubMultipleLogic() {
        String result = "BulkData(bulkCaseTitle=null, multipleReference=null, feeGroupReference=null, claimantSurname=null, respondentSurname=null, " +
                "claimantRep=null, respondentRep=null, ethosCaseReference=null, clerkResponsible=null, fileLocation=null, jurCodesCollection=null, " +
                "fileLocationV2=null, feeGroupReferenceV2=null, claimantSurnameV2=null, respondentSurnameV2=null, multipleReferenceV2=null, " +
                "clerkResponsibleV2=null, positionTypeV2=null, claimantRepV2=null, respondentRepV2=null, fileLocationGlasgow=null, fileLocationAberdeen=null, " +
                "fileLocationDundee=null, fileLocationEdinburgh=null, managingOffice=null, subMultipleName=null, subMultipleRef=null, " +
                "caseIdCollection=null, searchCollection=null, midSearchCollection=null, " +
                "multipleCollection=[MultipleTypeItem(id=2222, value=MultipleType(caseIDM=null, ethosCaseReferenceM=2222, leadClaimantM=null, " +
                "multipleReferenceM=null, clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null, claimantRepM=null, respondentRepM=null, " +
                "fileLocM=null, receiptDateM=null, positionTypeM=null, feeGroupReferenceM=null, jurCodesCollectionM=null, stateM=null, subMultipleM=1111, " +
                "subMultipleTitleM=NewSubMultipleName, currentPositionM=null, claimantAddressLine1M=null, claimantPostCodeM=null, respondentAddressLine1M=null, " +
                "respondentPostCodeM=null, flag1M=null, flag2M=null, EQPM=null, respondentRepOrgM=null, claimantRepOrgM=null)), " +
                "MultipleTypeItem(id=123456, value=MultipleType(caseIDM=null, ethosCaseReferenceM=3333, leadClaimantM=null, multipleReferenceM=null, " +
                "clerkRespM=null, claimantSurnameM=null, respondentSurnameM=null, claimantRepM=null, respondentRepM=null, fileLocM=null, receiptDateM=null, " +
                "positionTypeM=null, feeGroupReferenceM=null, jurCodesCollectionM=null, stateM=null, subMultipleM= , subMultipleTitleM= , currentPositionM=null, " +
                "claimantAddressLine1M=null, claimantPostCodeM=null, respondentAddressLine1M=null, respondentPostCodeM=null, flag1M=null, flag2M=null, " +
                "EQPM=null, respondentRepOrgM=null, claimantRepOrgM=null))], " +
                "subMultipleCollection=[SubMultipleTypeItem(id=1234567, value=SubMultipleType(subMultipleNameT=SubMultiple1, subMultipleRefT=1234567)), " +
                "SubMultipleTypeItem(id=1111, value=SubMultipleType(subMultipleNameT=NewSubMultipleName, subMultipleRefT=1111))], subMultipleDynamicList=null, " +
                "searchCollectionCount=null, multipleCollectionCount=null, correspondenceType=null, correspondenceScotType=null, selectAll=null, " +
                "scheduleDocName=null, positionType=null, flag1=null, flag2=null, EQP=null, submissionRef=null, claimantOrg=null, respondentOrg=null, " +
                "state=null, flag1Update=null, flag2Update=null, EQPUpdate=null, jurCodesDynamicList=null, outcomeUpdate=null, filterCases=null, docMarkUp=null, " +
                "caseSource=null)";
        bulkDetails.getCaseData().setSubMultipleRef("1111");
        bulkDetails.getCaseData().setSubMultipleName("NewSubMultipleName");
        MultipleType multipleType = new MultipleType();
        multipleType.setEthosCaseReferenceM("3333");
        multipleType.setSubMultipleM("1111");
        MultipleTypeItem multipleTypeItem = new MultipleTypeItem();
        multipleTypeItem.setId("123456");
        multipleTypeItem.setValue(multipleType);
        bulkDetails.getCaseData().getMultipleCollection().add(multipleTypeItem);
        bulkDetails.getCaseData().setSubMultipleDynamicList(createDynamicFixedListType());
        bulkDetails.getCaseData().setSubMultipleCollection(createTwoSubMultiples());
        BulkRequestPayload bulkRequestPayload = subMultipleService.updateSubMultipleLogic(bulkDetails);
        assertEquals(result, bulkRequestPayload.getBulkDetails().getCaseData().toString());
    }

    private DynamicFixedListType createDynamicFixedListType() {
        DynamicValueType dynamicValueType = new DynamicValueType();
        dynamicValueType.setLabel("Label1");
        dynamicValueType.setCode("1111");
        DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
        List<DynamicValueType> dynamicValueTypes = new ArrayList<>(Collections.singleton(dynamicValueType));
        dynamicFixedListType.setListItems(dynamicValueTypes);
        dynamicFixedListType.setValue(dynamicValueType);
        return dynamicFixedListType;
    }

    private List<SubMultipleTypeItem> createSubMultiples() {
        SubMultipleType subMultipleType = new SubMultipleType();
        subMultipleType.setSubMultipleNameT("SubMultiple1");
        subMultipleType.setSubMultipleRefT("1234567");
        SubMultipleTypeItem subMultipleTypeItem = new SubMultipleTypeItem();
        subMultipleTypeItem.setId(subMultipleType.getSubMultipleRefT());
        subMultipleTypeItem.setValue(subMultipleType);
        return new ArrayList<>(Collections.singletonList(subMultipleTypeItem));
    }

    private List<SubMultipleTypeItem> createTwoSubMultiples() {
        SubMultipleType subMultipleType = new SubMultipleType();
        subMultipleType.setSubMultipleNameT("SubMultiple1");
        subMultipleType.setSubMultipleRefT("1234567");
        SubMultipleTypeItem subMultipleTypeItem = new SubMultipleTypeItem();
        subMultipleTypeItem.setId(subMultipleType.getSubMultipleRefT());
        subMultipleTypeItem.setValue(subMultipleType);
        SubMultipleType subMultipleType1 = new SubMultipleType();
        subMultipleType1.setSubMultipleNameT("SubMultiple2");
        subMultipleType1.setSubMultipleRefT("1111");
        SubMultipleTypeItem subMultipleTypeItem1 = new SubMultipleTypeItem();
        subMultipleTypeItem1.setId(subMultipleType1.getSubMultipleRefT());
        subMultipleTypeItem1.setValue(subMultipleType1);
        return new ArrayList<>(Arrays.asList(subMultipleTypeItem, subMultipleTypeItem1));
    }

}