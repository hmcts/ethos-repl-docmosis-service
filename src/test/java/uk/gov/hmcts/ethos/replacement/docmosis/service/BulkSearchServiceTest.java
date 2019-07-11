package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkRequest;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.MultipleTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.SearchTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.MultipleType;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public class BulkSearchServiceTest {

    @InjectMocks
    private BulkSearchService bulkSearchService;
    @Mock
    private CcdClient ccdClient;
    private BulkRequest bulkRequest;
    private BulkDetails bulkDetails;

    @Before
    public void setUp() {
        bulkRequest = new BulkRequest();
        bulkDetails = new BulkDetails();
        BulkData bulkData = new BulkData();
        bulkData.setMultipleReference("1111");
        bulkData.setClaimantSurname("");
        bulkData.setEthosCaseReference("222");
        bulkDetails.setJurisdiction("TRIBUNALS");
        bulkDetails.setCaseData(bulkData);
        bulkRequest.setCaseDetails(bulkDetails);

        bulkSearchService = new BulkSearchService(ccdClient);
    }

    @Test
    public void bulkSearchLogic() {
        String result = "BulkDetails(caseId=null, jurisdiction=TRIBUNALS, state=null, caseData=BulkData(bulkCaseTitle=null, " +
                "multipleReference=1111, feeGroupReference=null, claimantSurname=null, respondentSurname=null, claimantRep=null, " +
                "respondentRep=null, ethosCaseReference=null, clerkResponsible=null, fileLocation=null, jurCodesCollection=null, " +
                "fileLocationV2=null, feeGroupReferenceV2=null, claimantSurnameV2=null, respondentSurnameV2=null, multipleReferenceV2=null, " +
                "clerkResponsibleV2=null, positionTypeV2=null, claimantRepV2=null, respondentRepV2=null, caseIdCollection=null, " +
                "searchCollection=[], multipleCollection=null, searchCollectionCount=0, multipleCollectionCount=null), " +
                "caseTypeId=null, createdDate=null, lastModified=null, dataClassification=null)";
        BulkDetails bulkDetails1 = bulkSearchService.bulkSearchLogic(bulkDetails);
        assertEquals(result, bulkDetails1.toString());
    }

    @Test(expected = Exception.class)
    public void searchCasesByFieldsRequestException() {
        List<SearchTypeItem> searchTypeItemListExpected = new ArrayList<>();
        List<SearchTypeItem> searchTypeItemList = bulkSearchService.searchCasesByFieldsRequest(new BulkDetails());
        assertEquals(searchTypeItemListExpected, searchTypeItemList);
    }

    @Test
    public void searchCasesByFieldsRequest() {
        List<SearchTypeItem> searchTypeItemListExpected = new ArrayList<>();
        List<SearchTypeItem> searchTypeItemList = bulkSearchService.searchCasesByFieldsRequest(bulkDetails);
        assertEquals(searchTypeItemListExpected, searchTypeItemList);
    }

    @Test
    public void searchCasesByFieldsCompleteRequest() {
        bulkRequest.getCaseDetails().getCaseData().setMultipleCollection(getMultipleTypeItemList());
        List<SearchTypeItem> searchTypeItemList = bulkSearchService.searchCasesByFieldsRequest(bulkDetails);
        assertEquals("[SearchTypeItem(id=2222, value=SearchType(caseIDS=null, ethosCaseReferenceS=222, " +
                "leadClaimantS=null, clerkRespS=null, claimantSurnameS=Pedro, respondentSurnameS=Pedro, claimantRepS=null, " +
                "respondentRepS=null, fileLocS=null, receiptDateS=null, acasOfficeS=null, positionTypeS=null, " +
                "feeGroupReferenceS=null, jurCodesCollectionS=null, stateS=null))]", searchTypeItemList.toString());
    }

    @Test
    public void searchCasesByFieldsNoMatchesCompleteRequest() {
        bulkRequest.getCaseDetails().getCaseData().setMultipleCollection(getMultipleTypeItemList());
        BulkData bulkData = bulkDetails.getCaseData();
        bulkData.setRespondentSurname("Antonio");
        bulkData.setRespondentRep("Mike");
        bulkData.setClaimantRep("Johnson");
        bulkData.setClaimantSurname("Juan");
        bulkDetails.setCaseData(bulkData);
        List<SearchTypeItem> searchTypeItemList = bulkSearchService.searchCasesByFieldsRequest(bulkDetails);
        assertEquals("[]", searchTypeItemList.toString());
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
}