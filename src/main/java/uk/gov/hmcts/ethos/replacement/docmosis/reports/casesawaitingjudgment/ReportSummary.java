package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ReportSummary {
    private final String office;
    private final String user;
    private final LocalDate reportRunDate;
    private final Map<String, Integer> positionTypes = new HashMap<>();
}
