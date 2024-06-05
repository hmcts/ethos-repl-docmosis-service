package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.MultipleReferenceService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleCreationServiceTest {

    @Mock
    private ExcelDocManagementService excelDocManagementService;
    @Mock
    private MultipleReferenceService multipleReferenceService;
    @Mock
    private MultipleHelperService multipleHelperService;
    @Mock
    private SubMultipleUpdateService subMultipleUpdateService;
    @Mock
    private MultipleTransferService multipleTransferService;

    @InjectMocks
    private MultipleCreationService multipleCreationService;

    private MultipleDetails multipleDetails;
    private List<String> ethosCaseRefCollection;
    private String userToken;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        ethosCaseRefCollection = MultiplesHelper.getCaseIds(multipleDetails.getCaseData());
        //Adding lead to the case id collection
        ethosCaseRefCollection.add(0, "21006/2020");
        userToken = "authString";
    }

    @Test
    public void bulkCreationLogic() {
        multipleCreationService.bulkCreationLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(ethosCaseRefCollection,
                userToken,
                multipleDetails);
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void bulkCreationLogicWithMultipleReference() {
        multipleDetails.getCaseData().setMultipleReference("2100001");
        multipleCreationService.bulkCreationLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(ethosCaseRefCollection,
                userToken,
                multipleDetails);
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void bulkCreationLogicETOnline() {
        multipleDetails.getCaseData().setMultipleSource(ET1_ONLINE_CASE_SOURCE);
        multipleCreationService.bulkCreationLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).writeAndUploadExcelDocument(ethosCaseRefCollection,
                userToken,
                multipleDetails,
                new ArrayList<>());
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void bulkCreationLogicETOnlinePreAcceptDone() {
        multipleDetails.getCaseData().setMultipleSource(ET1_ONLINE_CASE_SOURCE);
        multipleDetails.getCaseData().setPreAcceptDone(YES);
        multipleCreationService.bulkCreationLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).writeAndUploadExcelDocument(ethosCaseRefCollection,
                userToken,
                multipleDetails,
                new ArrayList<>());
        verifyNoMoreInteractions(excelDocManagementService);
        assertEquals(YES, multipleDetails.getCaseData().getPreAcceptDone());
    }

    @Test
    public void bulkCreationLogicETOnlinePreAcceptDoneNull() {
        multipleDetails.getCaseData().setMultipleSource(ET1_ONLINE_CASE_SOURCE);
        multipleDetails.getCaseData().setPreAcceptDone(null);
        multipleCreationService.bulkCreationLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).writeAndUploadExcelDocument(ethosCaseRefCollection,
                userToken,
                multipleDetails,
                new ArrayList<>());
        verifyNoMoreInteractions(excelDocManagementService);
        assertEquals(NO, multipleDetails.getCaseData().getPreAcceptDone());
    }

    @Test
    public void bulkCreationLogicMigration() {
        multipleDetails.getCaseData().setLeadCase("");
        multipleDetails.getCaseData().setCaseIdCollection(new ArrayList<>());
        multipleDetails.getCaseData().setMultipleSource(MIGRATION_CASE_SOURCE);
        multipleDetails.getCaseData().setCaseMultipleCollection(MultipleUtil.getCaseMultipleCollection());
        multipleCreationService.bulkCreationLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).writeAndUploadExcelDocument(
                MultipleUtil.getCaseMultipleObjectCollection(),
                userToken,
                multipleDetails,
                new ArrayList<>(Arrays.asList("Sub3", "Sub2", "Sub1")));
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void bulkCreationLogicMigrationPreAcceptDoneNull() {
        multipleDetails.getCaseData().setLeadCase("");
        multipleDetails.getCaseData().setCaseIdCollection(new ArrayList<>());
        multipleDetails.getCaseData().setMultipleSource(MIGRATION_CASE_SOURCE);
        multipleDetails.getCaseData().setPreAcceptDone(null);
        multipleDetails.getCaseData().setCaseMultipleCollection(MultipleUtil.getCaseMultipleCollection());
        multipleCreationService.bulkCreationLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).writeAndUploadExcelDocument(
                MultipleUtil.getCaseMultipleObjectCollection(),
                userToken,
                multipleDetails,
                new ArrayList<>(Arrays.asList("Sub3", "Sub2", "Sub1")));
        verifyNoMoreInteractions(excelDocManagementService);
        assertEquals(YES, multipleDetails.getCaseData().getPreAcceptDone());
    }

    @Test
    public void bulkCreationLogicMigrationPreAcceptDone() {
        multipleDetails.getCaseData().setLeadCase("");
        multipleDetails.getCaseData().setCaseIdCollection(new ArrayList<>());
        multipleDetails.getCaseData().setMultipleSource(MIGRATION_CASE_SOURCE);
        multipleDetails.getCaseData().setPreAcceptDone(YES);
        multipleDetails.getCaseData().setCaseMultipleCollection(MultipleUtil.getCaseMultipleCollection());
        multipleCreationService.bulkCreationLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).writeAndUploadExcelDocument(
                MultipleUtil.getCaseMultipleObjectCollection(),
                userToken,
                multipleDetails,
                new ArrayList<>(Arrays.asList("Sub3", "Sub2", "Sub1")));
        verifyNoMoreInteractions(excelDocManagementService);
        assertEquals(YES, multipleDetails.getCaseData().getPreAcceptDone());
    }

    @Test
    public void bulkCreationLogicMigrationEmptyCaseMultipleCollection() {
        multipleDetails.getCaseData().setLeadCase("");
        multipleDetails.getCaseData().setCaseIdCollection(new ArrayList<>());
        multipleDetails.getCaseData().setMultipleSource(MIGRATION_CASE_SOURCE);
        multipleDetails.getCaseData().setCaseMultipleCollection(new ArrayList<>());
        multipleCreationService.bulkCreationLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).writeAndUploadExcelDocument(
                new ArrayList<>(),
                userToken,
                multipleDetails,
                new ArrayList<>());
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void bulkCreationLogicEmptyCaseIdCollection() {
        multipleDetails.getCaseData().setCaseIdCollection(new ArrayList<>());
        multipleDetails.getCaseData().setLeadCase(null);
        multipleCreationService.bulkCreationLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(new ArrayList<>(),
                userToken,
                multipleDetails);
        verifyNoMoreInteractions(excelDocManagementService);
    }

    @Test
    public void bulkCreationLogicWithNullMultipleRef() {
        multipleDetails.getCaseData().setMultipleReference(null);
        multipleCreationService.bulkCreationLogic(userToken,
                multipleDetails,
                new ArrayList<>());
        verify(excelDocManagementService, times(1)).generateAndUploadExcel(ethosCaseRefCollection,
                userToken,
                multipleDetails);
        verifyNoMoreInteractions(excelDocManagementService);
        verify(multipleReferenceService, times(1)).createReference(
                multipleDetails.getCaseTypeId(),
                1);
        verifyNoMoreInteractions(multipleReferenceService);
    }

}