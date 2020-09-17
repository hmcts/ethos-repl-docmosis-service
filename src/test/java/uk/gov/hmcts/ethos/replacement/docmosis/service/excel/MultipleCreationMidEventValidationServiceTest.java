package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.types.CaseType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ET1_ONLINE_CASE_SOURCE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SUBMITTED_STATE;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleCreationMidEventValidationServiceTest {

    @Mock
    private SingleCasesReadingService singleCasesReadingService;
    @InjectMocks
    private MultipleCreationMidEventValidationService multipleCreationMidEventValidationService;

    private MultipleDetails multipleDetails;
    private List<String> errors;
    private String userToken;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        errors = new ArrayList<>();
        userToken = "authString";
    }

    @Test
    public void multipleCreationMidEventValidationService() {

        when(singleCasesReadingService.retrieveSingleCases(userToken,
                multipleDetails.getCaseTypeId(),
                MultiplesHelper.getCaseIds(multipleDetails.getCaseData())))
                .thenReturn(new ArrayList<>());

        createCaseIdCollection(multipleDetails.getCaseData(), 40);

        multipleCreationMidEventValidationService.multipleCreationValidationLogic(
                userToken,
                multipleDetails,
                errors);

        assertEquals(0, errors.size());

    }

    @Test
    public void multipleCreationMidEventValidationServiceMaxSize() {

        createCaseIdCollection(multipleDetails.getCaseData(), 60);

        multipleCreationMidEventValidationService.multipleCreationValidationLogic(
                userToken,
                multipleDetails,
                errors);

        assertEquals(1, errors.size());
        assertEquals("Three are 60 cases in the multiple. The limit is 50.", errors.get(0));

    }

    @Test
    public void multipleCreationMidEventValidationServiceWrongStateAndMultipleError() {

        when(singleCasesReadingService.retrieveSingleCases(userToken,
                multipleDetails.getCaseTypeId(),
                MultiplesHelper.getCaseIds(multipleDetails.getCaseData())))
                .thenReturn(getSubmitEvents());

        multipleCreationMidEventValidationService.multipleCreationValidationLogic(
                userToken,
                multipleDetails,
                errors);

        assertEquals(2, errors.size());
        assertEquals("[245000/2020] cases have not been Accepted.", errors.get(0));
        assertEquals("[245000/2020] cases belong already to a different multiple", errors.get(1));

    }

    @Test
    public void multipleCreationMidEventValidationServiceET1OnlineCase() {

        multipleDetails.getCaseData().setMultipleSource(ET1_ONLINE_CASE_SOURCE);

        multipleCreationMidEventValidationService.multipleCreationValidationLogic(
                userToken,
                multipleDetails,
                errors);

        assertEquals(0, errors.size());

    }

    private void createCaseIdCollection(MultipleData multipleData, int numberCases) {

        List<CaseIdTypeItem> caseIdCollection = new ArrayList<>();

        for (int i = 0 ; i < numberCases ; i++) {

            caseIdCollection.add(createCaseIdType(String.valueOf(i)));

        }

        multipleData.setCaseIdCollection(caseIdCollection);

    }

    private CaseIdTypeItem createCaseIdType(String ethosCaseReference) {

        CaseType caseType = new CaseType();
        caseType.setEthosCaseReference(ethosCaseReference);

        CaseIdTypeItem caseIdTypeItem = new CaseIdTypeItem();
        caseIdTypeItem.setId(ethosCaseReference);
        caseIdTypeItem.setValue(caseType);

        return caseIdTypeItem;

    }

    private static List<SubmitEvent> getSubmitEvents() {

        CaseData caseData = new CaseData();
        caseData.setState(SUBMITTED_STATE);
        caseData.setEthosCaseReference("245000/2020");
        caseData.setMultipleReference("245000");

        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setCaseData(caseData);
        submitEvent.setState(SUBMITTED_STATE);
        submitEvent.setCaseId(1232121232);

        CaseData caseData1 = new CaseData();
        caseData1.setState(SUBMITTED_STATE);
        caseData1.setEthosCaseReference("245001/2020");

        SubmitEvent submitEvent1 = new SubmitEvent();
        submitEvent1.setCaseData(caseData1);
        submitEvent1.setState(SUBMITTED_STATE);
        submitEvent1.setCaseId(1232121233);

        return new ArrayList<>(Arrays.asList(submitEvent, submitEvent1));

    }
}