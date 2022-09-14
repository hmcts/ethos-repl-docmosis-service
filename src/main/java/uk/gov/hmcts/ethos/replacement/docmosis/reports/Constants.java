package uk.gov.hmcts.ethos.replacement.docmosis.reports;

import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.Set;

public final class Constants {
    public static final String NO_CHANGE_IN_CURRENT_POSITION_REPORT = "No Change In Current Position";
    public static final String RESPONDENTS_REPORT = "Respondents";
    public static final String ECC_REPORT = "ECC";

    public static final String REPORT_OFFICE = "\"Report_Office\":\"";
    public static final String TOTAL_CASES = "\"Total_Cases\":\"";
    public static final String REPORT_DATE = "\"Report_Date\":\"";
    public static final String TOTAL_SINGLE = "\"Total_Single\":\"";
    public static final String TOTAL_MULTIPLE = "\"Total_Multiple\":\"";
    public static final String REPORT_DETAILS_SINGLE = "reportDetailsSingle";
    public static final String REPORT_DETAILS_MULTIPLE = "reportDetailsMultiple";

    public static final Set<DayOfWeek> WEEKEND_DAYS_LIST = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    private Constants() {
    }
}
