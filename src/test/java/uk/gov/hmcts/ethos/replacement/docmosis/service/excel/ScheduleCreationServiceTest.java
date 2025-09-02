package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.helper.SchedulePayload;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertNotNull;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LIST_CASES_CONFIG;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_SCHEDULE_CONFIG;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_SCHEDULE_DETAILED_CONFIG;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesScheduleHelper.NOT_ALLOCATED;

@RunWith(SpringJUnit4ClassRunner.class)
public class ScheduleCreationServiceTest {

    @InjectMocks
    private ScheduleCreationService scheduleCreationService;

    private TreeMap<String, Object> multipleObjectsFlags;
    private TreeMap<String, Object> multipleObjectsSubMultiple;
    private MultipleDetails multipleDetails;
    private List<SchedulePayload> schedulePayloads;

    @Before
    public void setUp() {
        multipleObjectsFlags = MultipleUtil.getMultipleObjectsFlags();
        multipleObjectsSubMultiple = MultipleUtil.getMultipleObjectsSubMultiple();
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        schedulePayloads = getSchedulePayload();
    }

    @Test
    public void writeSchedule() {
        multipleDetails.getCaseData().setScheduleDocName(MULTIPLE_SCHEDULE_CONFIG);
        assertNotNull(scheduleCreationService.writeSchedule(
                multipleDetails.getCaseData(),
                schedulePayloads,
                multipleObjectsFlags));
    }

    @Test
    public void writeScheduleDetailed() {
        multipleDetails.getCaseData().setScheduleDocName(MULTIPLE_SCHEDULE_DETAILED_CONFIG);
        assertNotNull(scheduleCreationService.writeSchedule(
                multipleDetails.getCaseData(),
                schedulePayloads,
                multipleObjectsFlags));
    }

    @Test
    public void writeScheduleSubMultiplesNoAllocated() {
        multipleObjectsSubMultiple.put(NOT_ALLOCATED, new ArrayList<>(Collections.singletonList("245002/2020")));
        multipleDetails.getCaseData().setScheduleDocName(LIST_CASES_CONFIG);
        assertNotNull(scheduleCreationService.writeSchedule(
                multipleDetails.getCaseData(),
                schedulePayloads,
                multipleObjectsSubMultiple));
    }

    private List<SchedulePayload> getSchedulePayload() {

        SchedulePayload schedulePayload1 = SchedulePayload.builder()
                .ethosCaseRef("245000/2020")
                .claimantName("Mr Claimant1")
                .claimantAddressLine1("Address1 claimant1")
                .claimantAddressLine2("Address2 claimant1")
                .claimantAddressLine3("Address3 claimant1")
                .claimantTown("Town claimant1")
                .claimantPostCode("Postcode claimant1")
                .respondentName("Mr RespondentName1")
                .respondentAddressLine1("Address1 respondent1")
                .respondentAddressLine2("Address2 respondent1")
                .respondentAddressLine3("Address3 respondent1")
                .respondentTown("Town respondent1")
                .respondentPostCode("PostCode respondent1")
                .positionType("PositionType1")
                .build();

        SchedulePayload schedulePayload2 = SchedulePayload.builder()
                .ethosCaseRef("245002/2020")
                .claimantName("Mr Claimant2")
                .claimantAddressLine1("Address claimant2")
                .claimantPostCode("Postcode claimant2")
                .respondentName("Mr RespondentName2")
                .respondentAddressLine1("Address respondent2 Long Address2")
                .respondentPostCode("PostCode respondent2")
                .positionType("PositionType2")
                .build();

        SchedulePayload schedulePayload3 = SchedulePayload.builder()
                .ethosCaseRef("245003/2020")
                .claimantName("Mr Claimant3")
                .claimantAddressLine1("Address claimant3")
                .claimantPostCode("Postcode claimant3")
                .respondentName("Mr RespondentName3")
                .respondentAddressLine1("Address respondent3 Long Address3")
                .respondentPostCode("PostCode respondent3")
                .positionType("PositionType3")
                .build();
        return new ArrayList<>(Arrays.asList(schedulePayload1, schedulePayload2, schedulePayload3));
    }

}