package uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.common.Strings;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseRetrievalException;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ethos.replacement.docmosis.reports.ReportParams;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.RefDataFixesData;
import uk.gov.hmcts.ethos.replacement.docmosis.service.refdatafixes.refData.RefDataFixesDetails;

@Slf4j
@RequiredArgsConstructor
@Service("referenceDataFixesService")
public class ReferenceDataFixesService {
    private static final String CASES_SEARCHED = "Cases searched: ";
    private static final String MESSAGE = "Failed to retrieve reference data for case id : ";
    private final CcdClient ccdClient;

    public RefDataFixesData updateJudgesItcoReferences(RefDataFixesDetails refDataFixesDetails, String authToken) {

        var refDataFixesData = refDataFixesDetails.getCaseData();
        ReportParams params = new ReportParams(
                refDataFixesDetails.getCaseTypeId(),
                refDataFixesData.getListingDateFrom(),
                refDataFixesData.getListingDateTo());
        try {
            RefDataFixesCcdDataSource dataSource = new RefDataFixesCcdDataSource(authToken, ccdClient);
            List<SubmitEvent> submitEvents = dataSource.getData(params);
            List<JudgeCodes> judges = getJudges();
            if (CollectionUtils.isNotEmpty(submitEvents)) {
                log.info(CASES_SEARCHED + submitEvents.size());
                for (SubmitEvent submitEvent : submitEvents) {
                    CaseData caseData = submitEvent.getCaseData();
                    for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                        HearingType hearingType = hearingTypeItem.getValue();
                        hearingType.setJudge(updateJudgeName(
                                judges,
                                hearingType.getJudge(),
                                Strings.split(refDataFixesDetails.getCaseTypeId(), "_")[0]));
                    }
                    CCDRequest returnedRequest = ccdClient.startEventForCase(authToken, Strings.split(refDataFixesDetails.getCaseTypeId(), "_")[0],
                            refDataFixesDetails.getJurisdiction(), String.valueOf(submitEvent.getCaseId()));
                    ccdClient.submitEventForCase(authToken, caseData, Strings.split(refDataFixesDetails.getCaseTypeId(), "_")[0],
                            refDataFixesDetails.getJurisdiction(), returnedRequest, String.valueOf(submitEvent.getCaseId()));
                }
            }
            return refDataFixesData;
        } catch (Exception ex) {
            throw new CaseRetrievalException(MESSAGE + refDataFixesDetails.getCaseId() + ex.getMessage());
        }
    }

    private String updateJudgeName(List<JudgeCodes> judges, String judgeCode, String office) {
      Optional<JudgeCodes> t = judges.stream().filter(i -> i.existingJudgeCode.equals(judgeCode) && i.office.equals(office)).findFirst();
        if (t.isPresent()) {
           return t.get().requiredJudgeCode;
        }
        return judgeCode;
    }

    private List<JudgeCodes> getJudges() throws Exception {
        try {

            String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                    .getResource("Judges.json")).toURI())));
            ObjectMapper mapper = new ObjectMapper();

            return Arrays.asList(mapper.readValue(json, JudgeCodes[].class));
        } catch (Exception ex) {
            throw new Exception(MESSAGE + ex.toString());
        }
    }


    public RefDataFixesData insertClaimServedDate(RefDataFixesDetails refDataFixesDetails, String authToken) {

        var refDataFixesData = refDataFixesDetails.getCaseData();
        ReportParams params = new ReportParams(
                refDataFixesDetails.getCaseTypeId(),
                refDataFixesData.getListingDateFrom(),
                refDataFixesData.getListingDateTo());
        try {
            RefDataFixesCcdDataSource dataSource = new RefDataFixesCcdDataSource(authToken, ccdClient);
            List<SubmitEvent> submitEvents = dataSource.getData(params);
            if (CollectionUtils.isNotEmpty(submitEvents)) {
                log.info(CASES_SEARCHED + submitEvents.size());
                for (SubmitEvent submitEvent : submitEvents) {
                    CaseData caseData = submitEvent.getCaseData();
                }

            }
            return refDataFixesData;
        } catch (Exception ex) {
            throw new CaseRetrievalException(MESSAGE + refDataFixesDetails.getCaseId() + ex.getMessage());
        }
    }

//    public RefDataFixesData insertClaimServedDate(RefDataFixesDetails refDataFixesDetails) {
//        RefDataFixesData refDataFixesData = refDataFixesDetails.getCaseData();
//        claimServedDateFixRepository.addClaimServedDate(Date.valueOf(refDataFixesData.getListingDateFrom()),
//                Date.valueOf(refDataFixesData.getListingDateTo()),refDataFixesDetails.getCaseTypeId());
//        return refDataFixesData;
//    }

}
