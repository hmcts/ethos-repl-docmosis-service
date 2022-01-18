package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_FAST_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_NO_CONCILIATION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_OPEN_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_STANDARD_TRACK;

class ConciliationTrackServiceTest {

    private ConciliationTrackService conciliationTrackService;
    private CaseDetails baseCaseDetails;

    @BeforeEach
    public void setUp() throws Exception {
        conciliationTrackService = new ConciliationTrackService();
        baseCaseDetails = generateCaseDetails("caseDetailsTest1.json");
    }

    private CaseDetails generateCaseDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, CaseDetails.class);
    }

    private List<JurCodesTypeItem> getJurCodesTypeItems(String... codes) {
        List<JurCodesTypeItem> jurCodesTypeItemList = new ArrayList<>();
        for (String jurCode : codes) {
            JurCodesType jurCodesType = new JurCodesType();
            jurCodesType.setJuridictionCodesList(jurCode);
            JurCodesTypeItem jurCodesTypeItem = new JurCodesTypeItem();
            jurCodesTypeItem.setValue(jurCodesType);
            jurCodesTypeItemList.add(jurCodesTypeItem);
        }
        return jurCodesTypeItemList;
    }

    @Test
    void populateConciliationTrack_OpOnly_ReturnOp() {
        CaseData caseData = baseCaseDetails.getCaseData();
        List<JurCodesTypeItem> jurCodesTypeItems = getJurCodesTypeItems("DAG", "EQP", "SXD"); // OP OP OP
        caseData.setJurCodesCollection(jurCodesTypeItems);
        conciliationTrackService.populateConciliationTrackForJurisdiction(caseData);
        Assertions.assertEquals(CONCILIATION_TRACK_OPEN_TRACK, caseData.getConciliationTrack());
    }

    @Test
    void populateConciliationTrack_OpMix_ReturnOp() {
        CaseData caseData = baseCaseDetails.getCaseData();
        List<JurCodesTypeItem> jurCodesTypeItems = getJurCodesTypeItems("LEV", "PID", "RPT", "FLW"); // No OP SH ST
        caseData.setJurCodesCollection(jurCodesTypeItems);
        conciliationTrackService.populateConciliationTrackForJurisdiction(caseData);
        Assertions.assertEquals(CONCILIATION_TRACK_OPEN_TRACK, caseData.getConciliationTrack());
    }

    @Test
    void populateConciliationTrack_StOnly_ReturnSt() {
        CaseData caseData = baseCaseDetails.getCaseData();
        List<JurCodesTypeItem> jurCodesTypeItems = getJurCodesTypeItems("APA", "FWS", "TUS"); // ST ST ST
        caseData.setJurCodesCollection(jurCodesTypeItems);
        conciliationTrackService.populateConciliationTrackForJurisdiction(caseData);
        Assertions.assertEquals(CONCILIATION_TRACK_STANDARD_TRACK, caseData.getConciliationTrack());
    }

    @Test
    void populateConciliationTrack_StMix_ReturnSt() {
        CaseData caseData = baseCaseDetails.getCaseData();
        List<JurCodesTypeItem> jurCodesTypeItems = getJurCodesTypeItems("MWA", "RTR(ST)", "PAY"); // No ST SH
        caseData.setJurCodesCollection(jurCodesTypeItems);
        conciliationTrackService.populateConciliationTrackForJurisdiction(caseData);
        Assertions.assertEquals(CONCILIATION_TRACK_STANDARD_TRACK, caseData.getConciliationTrack());
    }

    @Test
    void populateConciliationTrack_ShOnly_ReturnSh() {
        CaseData caseData = baseCaseDetails.getCaseData();
        List<JurCodesTypeItem> jurCodesTypeItems = getJurCodesTypeItems("BOC", "FTO", "PAY"); // SH SH SH
        caseData.setJurCodesCollection(jurCodesTypeItems);
        conciliationTrackService.populateConciliationTrackForJurisdiction(caseData);
        Assertions.assertEquals(CONCILIATION_TRACK_FAST_TRACK, caseData.getConciliationTrack());
    }

    @Test
    void populateConciliationTrack_ShMix_ReturnSh() {
        CaseData caseData = baseCaseDetails.getCaseData();
        List<JurCodesTypeItem> jurCodesTypeItems = getJurCodesTypeItems("HAS", "FTU", "ISV"); // No SH No
        caseData.setJurCodesCollection(jurCodesTypeItems);
        conciliationTrackService.populateConciliationTrackForJurisdiction(caseData);
        Assertions.assertEquals(CONCILIATION_TRACK_FAST_TRACK, caseData.getConciliationTrack());
    }

    @Test
    void populateConciliationTrack_NoOnly_ReturnNo() {
        CaseData caseData = baseCaseDetails.getCaseData();
        List<JurCodesTypeItem> jurCodesTypeItems = getJurCodesTypeItems("ADT(ST)", "RPT(S)", "RTR"); // No No No
        caseData.setJurCodesCollection(jurCodesTypeItems);
        conciliationTrackService.populateConciliationTrackForJurisdiction(caseData);
        Assertions.assertEquals(CONCILIATION_TRACK_NO_CONCILIATION, caseData.getConciliationTrack());
    }

    @Test
    void populateConciliationTrack_NoMix_ReturnNo() {
        CaseData caseData = baseCaseDetails.getCaseData();
        List<JurCodesTypeItem> jurCodesTypeItems = getJurCodesTypeItems("CCP", "TBA", "WTA"); // No Null No
        caseData.setJurCodesCollection(jurCodesTypeItems);
        conciliationTrackService.populateConciliationTrackForJurisdiction(caseData);
        Assertions.assertEquals(CONCILIATION_TRACK_NO_CONCILIATION, caseData.getConciliationTrack());
    }

    @Test
    void populateConciliationTrack_TbaOnly_ReturnNull() {
        CaseData caseData = baseCaseDetails.getCaseData();
        List<JurCodesTypeItem> jurCodesTypeItems = getJurCodesTypeItems("TBA"); // Null
        caseData.setJurCodesCollection(jurCodesTypeItems);
        conciliationTrackService.populateConciliationTrackForJurisdiction(caseData);
        Assertions.assertNull(caseData.getConciliationTrack());
    }

    @Test
    void populateConciliationTrack_NoJurCode_ReturnNull() {
        CaseData caseData = baseCaseDetails.getCaseData();
        List<JurCodesTypeItem> jurCodesTypeItems = getJurCodesTypeItems();
        caseData.setJurCodesCollection(jurCodesTypeItems);
        conciliationTrackService.populateConciliationTrackForJurisdiction(caseData);
        Assertions.assertNull(caseData.getConciliationTrack());
    }

}