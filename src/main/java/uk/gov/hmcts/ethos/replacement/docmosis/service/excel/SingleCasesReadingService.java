package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.labels.LabelPayloadEvent;
import uk.gov.hmcts.ecm.common.model.schedule.SchedulePayloadEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service("singleCasesReadingService")
public class SingleCasesReadingService {

    private final CcdClient ccdClient;

    @Autowired
    public SingleCasesReadingService(CcdClient ccdClient) {
        this.ccdClient = ccdClient;
    }

    public SubmitEvent retrieveSingleCase(String userToken, String multipleCaseTypeId, String caseId,
                                          String multipleSource) {

        List<SubmitEvent> submitEvents = retrieveSingleCases(userToken,
                multipleCaseTypeId,
                new ArrayList<>(Collections.singletonList(caseId)),
                multipleSource);

        return submitEvents.isEmpty() ? null : submitEvents.get(0);
    }

    public List<SubmitEvent> retrieveSingleCases(String userToken, String multipleCaseTypeId, List<String> caseIds,
                                                 String multipleSource) {

        List<SubmitEvent> submitEvents = new ArrayList<>();

        try {
            submitEvents = ccdClient.retrieveCasesElasticSearchForCreation(userToken,
                    UtilHelper.getCaseTypeId(multipleCaseTypeId),
                    caseIds,
                    multipleSource);

        } catch (Exception ex) {

            log.error("Error retrieving single cases");

            log.error(ex.getMessage());

        }

        return submitEvents;

    }

    public List<LabelPayloadEvent> retrieveLabelCases(String userToken, String multipleCaseTypeId,
                                                      List<String> caseIds) {

        List<LabelPayloadEvent> labelEvents = new ArrayList<>();

        try {
            labelEvents = ccdClient.retrieveCasesElasticSearchLabels(userToken,
                    UtilHelper.getCaseTypeId(multipleCaseTypeId),
                    caseIds);

        } catch (Exception ex) {

            log.error("Error retrieving label cases");

            log.error(ex.getMessage());

        }

        return labelEvents;

    }

    public HashSet<SchedulePayloadEvent> retrieveScheduleCases(String userToken, String multipleCaseTypeId,
                                                               List<String> caseIds) {

        HashSet<SchedulePayloadEvent> schedulePayloadEvents = new HashSet<>();

        try {
            schedulePayloadEvents = new HashSet<>(ccdClient.retrieveCasesElasticSearchSchedule(userToken,
                    UtilHelper.getCaseTypeId(multipleCaseTypeId),
                    caseIds));

        } catch (Exception ex) {

            log.error("Error retrieving schedule cases");

            log.error(ex.getMessage(), ex);

        }

        return schedulePayloadEvents;

    }

}
