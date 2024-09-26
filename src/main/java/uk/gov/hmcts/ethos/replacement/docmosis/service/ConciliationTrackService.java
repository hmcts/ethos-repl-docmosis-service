package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;

import java.util.Arrays;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_FAST_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_NO_CONCILIATION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_OPEN_TRACK;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CONCILIATION_TRACK_STANDARD_TRACK;

@Service("conciliationTrackService")
public class ConciliationTrackService {

    private static final List<String> JUR_CODE_CONCILIATION_TRACK_OP = Arrays.asList(
            "DAG", "DDA", "DRB", "DSO", "EQP", "GRA", "MAT", "PID", "RRD", "SXD", "VIC");
    private static final List<String> JUR_CODE_CONCILIATION_TRACK_ST = Arrays.asList(
            "ADG", "APA", "AWR", "DOD", "FCT", "FLW", "FTE", "FT1", "FPI", "FWP", "FWS",
            "HSD", "HSR", "IRF", "MWD", "PAC", "PLD", "PTE", "RTR(ST)", "SUN",
            "TPE", "TT", "TUE", "TUI", "TUM", "TUR", "TUS", "TXC(ST)", "UDC", "UDL", "UIA", "WTR");
    private static final List<String> JUR_CODE_CONCILIATION_TRACK_SH = Arrays.asList(
            "BOC", "FML", "FPA", "FTC", "FTO", "FTP", "FTR", "FTS", "FTU", "PAY", "RPT", "TIP", "WA", "WTR(AL)");
    private static final List<String> JUR_CODE_CONCILIATION_TRACK_NO = Arrays.asList(
            "ADT", "ADT(ST)", "CCP", "COM", "EAP", "HAS", "ISV", "LEV ", "LSO", "MWA",
            "NNA", "PEN",  "RPT(S)", "RTR", "TXC", "WTA");

    public void populateConciliationTrackForJurisdiction(CaseData caseData) {
        if (CollectionUtils.isNotEmpty(caseData.getJurCodesCollection())) {
            switch (checkConciliationTrack(caseData)) {
                case "OP":
                    caseData.setConciliationTrack(CONCILIATION_TRACK_OPEN_TRACK);
                    break;
                case "ST":
                    caseData.setConciliationTrack(CONCILIATION_TRACK_STANDARD_TRACK);
                    break;
                case "SH":
                    caseData.setConciliationTrack(CONCILIATION_TRACK_FAST_TRACK);
                    break;
                case "NO":
                    caseData.setConciliationTrack(CONCILIATION_TRACK_NO_CONCILIATION);
                    break;
                default:
                    caseData.setConciliationTrack(null);
            }
        } else {
            caseData.setConciliationTrack(null);
        }
    }

    private String checkConciliationTrack(CaseData caseData) {
        boolean isConTrackSt = false;
        boolean isConTrackSh = false;
        boolean isConTrackNo = false;

        for (JurCodesTypeItem jurCodesTypeItem : caseData.getJurCodesCollection()) {
            if (JUR_CODE_CONCILIATION_TRACK_OP.contains(jurCodesTypeItem.getValue().getJuridictionCodesList())) {
                return "OP";
            } else if (JUR_CODE_CONCILIATION_TRACK_ST.contains(jurCodesTypeItem.getValue().getJuridictionCodesList())) {
                isConTrackSt = true;
            } else if (!isConTrackSt
                    && JUR_CODE_CONCILIATION_TRACK_SH.contains(jurCodesTypeItem.getValue().getJuridictionCodesList())) {
                isConTrackSh = true;
            } else if (!isConTrackSh && !isConTrackSt
                    && JUR_CODE_CONCILIATION_TRACK_NO.contains(jurCodesTypeItem.getValue().getJuridictionCodesList())) {
                isConTrackNo = true;
            }
        }

        if (isConTrackSt) {
            return "ST";
        } else if (isConTrackSh) {
            return "SH";
        } else if (isConTrackNo) {
            return "NO";
        } else {
            return "";
        }
    }

}
