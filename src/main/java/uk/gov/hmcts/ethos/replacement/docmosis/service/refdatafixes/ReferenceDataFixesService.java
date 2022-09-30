package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.elasticsearch.common.Strings;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.CaseDataEntity;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.CaseEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.CaseDataRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.CaseEventRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.AdminData;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.AdminDetails;

@Slf4j
@RequiredArgsConstructor
@Service("referenceDataFixesService")
public class ReferenceDataFixesService {
    private static final String CASES_SEARCHED = "Cases searched: ";
    private static final String MESSAGE = "Failed to retrieve reference data for case id : ";
    private static final String CLAIM_SERVED_DATE = "claimServedDate";
    public static final String GENERATE_CORRESPONDENCE = "generateCorrespondence";
    private static final Clock UTC_CLOCK = Clock.systemUTC();
    private String description = "";
    private String summary = "";
    private final CcdClient ccdClient;
    private final CaseDataRepository caseDataRepository;
    private final CaseEventRepository caseEventRepository;

    public AdminData updateJudgesItcoReferences(AdminDetails adminDetails, String authToken, RefDataFixesCcdDataSource dataSource) {

        AdminData adminData = adminDetails.getCaseData();
        String existingJudgeCode = adminData.getExistingJudgeCode();
        String requiredJudgeCode = adminData.getRequiredJudgeCode();
        String caseTypeId = adminData.getTribunalOffice();
        List<String> dates = getDateRangeForSearch(adminDetails, OLD_DATE_TIME_PATTERN);
        String dateFrom = dates.get(0);
        String dateTo = dates.get(1);
        try {
            List<SubmitEvent> submitEvents = dataSource.getDataForJudges(caseTypeId, dateFrom, dateTo, ccdClient);
            if (CollectionUtils.isNotEmpty(submitEvents)) {
                log.info(CASES_SEARCHED + submitEvents.size());
                for (SubmitEvent submitEvent : submitEvents) {
                    CaseData caseData = submitEvent.getCaseData();
                    setJudgeName(caseData, existingJudgeCode, requiredJudgeCode);
                    CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, caseTypeId,
                            adminDetails.getJurisdiction(), String.valueOf(submitEvent.getCaseId()));
                    ccdClient.submitEventForCase(authToken, caseData, caseTypeId,
                            adminDetails.getJurisdiction(), returnedRequest, String.valueOf(submitEvent.getCaseId()));
                }
                log.info(String.format(
                        "Existing Judge's code in all cases from %s to %s is " +
                                "updated to required judge's code", dateFrom, dateTo));
            }
            return adminData;
        } catch (Exception ex) {
            log.error(MESSAGE + adminDetails.getCaseId(), ex);
            return null;
        }
    }

    private List<String> getDateRangeForSearch(AdminDetails adminDetails, DateTimeFormatter pattern) {
        var refDataFixesData = adminDetails.getCaseData();
        boolean isRangeHearingDateType = refDataFixesData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE);
        String dateFrom;
        String dateTo;
        if (!isRangeHearingDateType) {
            dateFrom = LocalDate.parse(refDataFixesData.getDate(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().format(pattern);
            dateTo = LocalDate.parse(refDataFixesData.getDate(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().plusDays(1).minusSeconds(1).format(pattern);
        } else {
            dateFrom = LocalDate.parse(refDataFixesData.getDateFrom(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().format(pattern);
            dateTo = LocalDate.parse(refDataFixesData.getDateTo(), OLD_DATE_TIME_PATTERN2)
                    .atStartOfDay().plusDays(1).minusSeconds(1).format(pattern);
        }
        return List.of(dateFrom, dateTo);
    }

    private void setJudgeName(CaseData caseData, String existingJudgeCode, String requiredJudgeCode) {
        for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
            HearingType hearingType = hearingTypeItem.getValue();
            if (!Strings.isNullOrEmpty(hearingType.getJudge()) && hearingType.getJudge().equals(existingJudgeCode)) {
                hearingType.setJudge(requiredJudgeCode);
                log.info(String.format("Judge's code in Case with ethosReference %s is updated", caseData.getEthosCaseReference()));
            }
        }
    }

    /**
     * This method does not return anything. Initializes AdminData to null values
     * to not show any existing values for update of judge codes
     *
     * @param  adminData  AdminData which is a generic data type for most of the
     *                    methods which holds judge code, dates
     *                    and tribunal office
     */
    public void initAdminData(AdminData adminData) {
        adminData.setDate(null);
        adminData.setDateFrom(null);
        adminData.setDateTo(null);
        adminData.setHearingDateType(null);
        adminData.setExistingJudgeCode(null);
        adminData.setRequiredJudgeCode(null);
    }

    public AdminData insertClaimServedDateNew(AdminDetails adminDetails, RefDataFixesCcdDataSource dataSource, String authToken) {
        AdminData adminData = adminDetails.getCaseData();
        String caseTypeId = adminData.getTribunalOffice();
        List<String> dates = getDateRangeForSearch(adminDetails, OLD_DATE_TIME_PATTERN);
        String dateFrom = dates.get(0);
        String dateTo = dates.get(1);
        try {
            List<SubmitEvent> submitEvents = dataSource.getDataForInsertClaimDate(caseTypeId, dateFrom, dateTo, ccdClient);
            if (CollectionUtils.isNotEmpty(submitEvents)) {
                log.info(CASES_SEARCHED + submitEvents.size());
                for (SubmitEvent submitEvent : submitEvents) {
                    CaseData caseData = submitEvent.getCaseData();
                    //submitEvent.getCaseId()
                    CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, caseTypeId,
                            adminDetails.getJurisdiction(), String.valueOf(submitEvent.getCaseId()));
                    ccdClient.submitEventForCase(authToken, caseData, caseTypeId,
                            adminDetails.getJurisdiction(), returnedRequest, String.valueOf(submitEvent.getCaseId()));
                }
                log.info(String.format(
                        "Existing Judge's code in all cases from %s to %s is " +
                                "updated to required judge's code", dateFrom, dateTo));
            }
            return adminData;
        } catch (Exception ex) {
            log.error(MESSAGE + adminDetails.getCaseId(), ex);
            return null;
        }
    }



    public AdminData insertClaimServedDate(AdminDetails adminDetails) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            List<String> dates = getDateRangeForSearch(adminDetails, formatter);
            AdminData adminData =  adminDetails.getCaseData();
            List<CaseDataEntity> caseDataEntities = caseDataRepository.findCasesByCreationDateAndCaseType(
                    Timestamp.valueOf(dates.get(0)),
                    Timestamp.valueOf(dates.get(1)),
                    adminData.getTribunalOffice());
            for (CaseDataEntity caseDataEntity : caseDataEntities) {
              CaseEvent ce =  caseEventRepository.findFirstCaseEventByEventIdAndState(GENERATE_CORRESPONDENCE,
                      "Accepted", caseDataEntity.getId());
              if (ce != null && ce.getCreatedDate() != null) {
                  addFieldToPayload(caseDataEntity.getReference(), ce.getCreatedDate());
              }
            }

            return adminData;
        } catch (Exception ex) {
            log.error(ex.toString());
            return adminDetails.getCaseData();
        }
    }

    private void addFieldToPayload(Long r, LocalDateTime eventDate) throws ParseException, JSONException {
        description = "Adding ClaimServedDate to case";
        summary = "Adding ClaimServedDate to case";
        Optional<CaseDataEntity> caseDataEntityOpt = caseDataRepository.findCaseDataByReference(r);
        if (caseDataEntityOpt.isEmpty()) {
            log.error(r + "not found");
            return;
        }

        CaseDataEntity caseDataEntity = caseDataEntityOpt.get();
        JSONParser parser = new JSONParser(caseDataEntity.getData());
        JSONObject caseData = (JSONObject) parser.parse();

        //JsonNode caseData = caseDataEntity.getData();
        var caseReference = caseDataEntity.getReference();
        JSONParser parser2 = new JSONParser(caseDataEntity.getDataClassification());
        JSONObject dataClassification = (JSONObject) parser2.parse();

        //JsonNode dataClassification = caseDataEntity.getDataClassification();
        if (!caseData.toString().contains(CLAIM_SERVED_DATE)) {
            log.info("Adding field to case " + caseReference);
            (caseData).put(CLAIM_SERVED_DATE, eventDate.toLocalDate().toString());
            dataClassification.put(CLAIM_SERVED_DATE, "PUBLIC");
            caseDataEntity.setData(caseData.toString());
            caseDataEntity.setDataClassification(dataClassification.toString());
            if (!caseData.get(CLAIM_SERVED_DATE).equals(eventDate.toLocalDate().toString())) {
                log.info("Data has not been inserted correctly");
                return;
            }
            log.info("Data has been inserted correctly");
                findLatestEventAndCreateNew(caseDataEntity);
                caseDataRepository.save(caseDataEntity);

        } else {
            log.info(CLAIM_SERVED_DATE + " already exists for case " + caseReference);
        }
    }

    private void findLatestEventAndCreateNew(CaseDataEntity caseDataEntity) {
        List<CaseEvent> latestEventsSingletonList = caseEventRepository
                .findLatestEvents(caseDataEntity.getId(), PageRequest.of(0, 1));
        if (CollectionUtils.isEmpty(latestEventsSingletonList)) {
            log.error("Cannot find latest event for case " + caseDataEntity.getReference());
            return;
        }
        log.info("Creating new history event for case " + caseDataEntity.getReference() );
        CaseEvent newHistoryRecord = createHistoryEvent(caseDataEntity, latestEventsSingletonList.get(0));
        caseEventRepository.save(newHistoryRecord);
        log.info(caseDataEntity.getReference() + " updated from admin event");
    }

    private CaseEvent createHistoryEvent(CaseDataEntity caseDataEntity, CaseEvent caseEventEntity) {
        return CaseEvent.builder()
                .eventId("fixCaseAPI")
                .eventName("Update to case data")
                .caseDataId(caseDataEntity.getId())
                .caseTypeId(caseEventEntity.getCaseTypeId())
                .caseTypeVersion(caseEventEntity.getCaseTypeVersion())
                .createdDate(LocalDateTime.now(UTC_CLOCK))
                .data(caseDataEntity.getData())
                .dataClassification(caseDataEntity.getDataClassification())
                .stateId(caseEventEntity.getStateId())
                .stateName(caseEventEntity.getStateName())
                .userId("123456")
                .description(description)
                .summary(summary)
                .userFirstName("ECM")
                .userLastName("Local Dev (Stub)")
                .securityClassification(caseEventEntity.getSecurityClassification())
                .build();

    }
}
