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

    public static final String MANCHESTER_BULK_CASE_TYPE_ID = "Manchester_Multiples_Dev";
    public static final String MANCHESTER_CASE_TYPE_ID = "Manchester_Dev";
    public static final String SCOTLAND_BULK_CASE_TYPE_ID = "Scotland_Multiples_Dev";
    public static final String SCOTLAND_CASE_TYPE_ID = "Scotland_Dev";

    public static final String MANCHESTER_USERS_BULK_CASE_TYPE_ID = "Manchester_Multiples";
    public static final String MANCHESTER_USERS_CASE_TYPE_ID = "Manchester";
    public static final String SCOTLAND_USERS_BULK_CASE_TYPE_ID = "Scotland_Multiples";
    public static final String SCOTLAND_USERS_CASE_TYPE_ID = "Scotland";
    public static final String BRISTOL_USERS_BULK_CASE_TYPE_ID = "Bristol_Multiples";
    public static final String BRISTOL_USERS_CASE_TYPE_ID = "Bristol";
    public static final String LEEDS_USERS_BULK_CASE_TYPE_ID = "Leeds_Multiples";
    public static final String LEEDS_USERS_CASE_TYPE_ID = "Leeds";
    public static final String LONDON_CENTRAL_USERS_BULK_CASE_TYPE_ID = "LondonCentral_Multiples";
    public static final String LONDON_CENTRAL_USERS_CASE_TYPE_ID = "LondonCentral";
    public static final String LONDON_EAST_USERS_BULK_CASE_TYPE_ID = "LondonEast_Multiples";
    public static final String LONDON_EAST_USERS_CASE_TYPE_ID = "LondonEast";
    public static final String LONDON_SOUTH_USERS_BULK_CASE_TYPE_ID = "LondonSouth_Multiples";
    public static final String LONDON_SOUTH_USERS_CASE_TYPE_ID = "LondonSouth";
    public static final String MIDLANDS_EAST_USERS_BULK_CASE_TYPE_ID = "MidlandsEast_Multiples";
    public static final String MIDLANDS_EAST_USERS_CASE_TYPE_ID = "MidlandsEast";
    public static final String MIDLANDS_WEST_USERS_BULK_CASE_TYPE_ID = "MidlandsWest_Multiples";
    public static final String MIDLANDS_WEST_USERS_CASE_TYPE_ID = "MidlandsWest";
    public static final String NEWCASTLE_USERS_BULK_CASE_TYPE_ID = "Newcastle_Multiples";
    public static final String NEWCASTLE_USERS_CASE_TYPE_ID = "Newcastle";
    public static final String WALES_USERS_BULK_CASE_TYPE_ID = "Wales_Multiples";
    public static final String WALES_USERS_CASE_TYPE_ID = "Wales";
    public static final String WATFORD_USERS_BULK_CASE_TYPE_ID = "Watford_Multiples";
    public static final String WATFORD_USERS_CASE_TYPE_ID = "Watford";

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

    public static final String MANCHESTER_OFFICE_NUMBER = "32";
    public static final String SCOTLAND_OFFICE_NUMBER = "42";
    public static final String DEFAULT_MANCHESTER_INIT = "0000200";
    public static final String DEFAULT_SCOTLAND_INIT = "0000210";
    public static final String DEFAULT_MAX_REF = "9999999";
}
