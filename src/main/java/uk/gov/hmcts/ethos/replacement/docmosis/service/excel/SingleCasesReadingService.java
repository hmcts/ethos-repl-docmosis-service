package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.schedule.SchedulePayloadEvent;

import java.util.*;

@Slf4j
@Service("singleCasesReadingService")
public class SingleCasesReadingService {

    private final CcdClient ccdClient;

    @Autowired
    public SingleCasesReadingService(CcdClient ccdClient) {
        this.ccdClient = ccdClient;
    }

    public SubmitEvent retrieveSingleCase(String userToken, String multipleCaseTypeId, String caseId) {

        List<SubmitEvent> submitEvents = retrieveSingleCases(userToken,
                multipleCaseTypeId,
                new ArrayList<>(Collections.singletonList(caseId)));

        return submitEvents.isEmpty() ? null : submitEvents.get(0);
    }

    public List<SubmitEvent> retrieveSingleCases(String userToken, String multipleCaseTypeId, List<String> caseIds) {

        List<SubmitEvent> submitEvents = new ArrayList<>();

        try {
            submitEvents = ccdClient.retrieveCasesElasticSearch(userToken,
                    UtilHelper.getCaseTypeId(multipleCaseTypeId),
                    caseIds);

        } catch (Exception ex) {

            log.error("Error retrieving single cases");

            log.error(ex.getMessage());

        }

        return submitEvents;

    }

    public HashSet<SchedulePayloadEvent> retrieveScheduleCases(String userToken, String multipleCaseTypeId, List<String> caseIds) {

        HashSet<SchedulePayloadEvent> schedulePayloadEvents = new HashSet<>();

        try {
            schedulePayloadEvents = new HashSet<>(ccdClient.retrieveCasesElasticSearchSchedule(userToken,
                    UtilHelper.getCaseTypeId(multipleCaseTypeId),
                    caseIds));

            log.info("SchedulePayloadEventsList: " + schedulePayloadEvents);

        } catch (Exception ex) {

            log.error("Error retrieving schedule cases");

            log.error(ex.getMessage(), ex);

        }

        return schedulePayloadEvents;

    }

}
