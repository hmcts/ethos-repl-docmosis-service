package uk.gov.hmcts.ethos.replacement.docmosis.model.helper;

import java.time.format.DateTimeFormatter;

public class Constants {

    public static final DateTimeFormatter OLD_DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static final DateTimeFormatter NEW_DATE_PATTERN = DateTimeFormatter.ofPattern("d MMMM yyyy");
    public static final DateTimeFormatter NEW_DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm");
    public static final String NEW_LINE = "\",\n";
    public static final String OUTPUT_FILE_NAME = "document.docx";

    public static final String PRE_DEFAULT_XLSX_FILE_PATH = "preDefaultValues.xlsx";
    public static final String POST_DEFAULT_XLSX_FILE_PATH = "postDefaultValues.xlsx";

    public static final String MANCHESTER_CASE_TYPE_ID = "Manchester_Dev";
    public static final String MANCHESTER_USERS_CASE_TYPE_ID = "Manchester";
    public static final String GLASGOW_CASE_TYPE_ID = "Glasgow_Dev";
    public static final String MANCHESTER_BULK_CASE_TYPE_ID = "Manchester_Multiples_Dev";
    public static final String MANCHESTER_USERS_BULK_CASE_TYPE_ID = "Manchester_Multiples";

    public static final String PENDING_STATE = "Pending";
    public static final String SUBMITTED_STATE = "Submitted";
    public static final String ACCEPTED_STATE = "Accepted";
    public static final String REJECTED_STATE = "Rejected";

    public static final String CREATION_EVENT_TRIGGER_ID = "initiateCase";
    public static final String UPDATE_EVENT_TRIGGER_ID = "amendCaseDetails";
    public static final String UPDATE_EVENT_TRIGGER_ID_BULK = "amendCaseDetailsBulk";
    public static final String PRE_ACCEPT_CASE_TRIGGER_ID_BULK = "preAcceptanceCase";
    public static final String UPDATE_BULK_EVENT_TRIGGER_ID = "updateBulkAction";

    public static final String GLASGOW_OFFICE = "Glasgow";
    public static final String ABERDEEN_OFFICE = "Aberdeen";
    public static final String DUNDEE_OFFICE = "Dundee";
    public static final String EDINBURGH_OFFICE = "Edinburgh";

}
