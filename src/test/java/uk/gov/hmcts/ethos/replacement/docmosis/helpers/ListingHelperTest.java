package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ethos.replacement.docmosis.idam.models.UserDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.DateListedType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.HearingType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.ListingData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.listing.ListingDetails;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static org.junit.Assert.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

public class ListingHelperTest {

    private ListingDetails listingDetails;
    private UserDetails userDetails;

    @Before
    public void setUp() throws Exception {
        listingDetails = generateListingDetails("listingDetailsTest1.json");
        userDetails = new UserDetails("1", "example@hotmail.com", "Mike", "Jordan", new ArrayList<>());
    }

    private ListingDetails generateListingDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, ListingDetails.class);
    }

    @Test
    public void buildCaseCauseListByRoom() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-SCO-ENG-00214.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"Listed_date\":\"12 October 2020\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"location\":[\n" +
                "{\"Hearing_room\":\"Tribunal 2\",\n" +
                "\"listing\":[\n" +
                "{\"Judge\":\"Ms AM Aspden\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"ERMember\":\" \",\n" +
                "\"EEMember\":\" \",\n" +
                "\"Case_No\":\"1112\",\n" +
                "\"Hearing_type\":\"Hearing\",\n" +
                "\"Jurisdictions\":\"ADG, COM\",\n" +
                "\"Hearing_date\":\"12 October 2020\",\n" +
                "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n" +
                "\"Hearing_time\":\"00:00\",\n" +
                "\"Hearing_duration\":\"12 Days\",\n" +
                "\"Hearing_clerk\":\"Anne Fox\",\n" +
                "\"Claimant\":\"Mr s sdfs\",\n" +
                "\"claimant_town\":\"claimantTown\",\n" +
                "\"claimant_representative\":\"Rep\",\n" +
                "\"Respondent\":\"sdf\",\n" +
                "\"resp_others\":\"null\",\n" +
                "\"respondent_town\":\"respondentTown\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Hearing_room\":\"Tribunal 2\",\n" +
                "\"Hearing_dayofdays\":\"1 of 3\",\n" +
                "\"Hearing_panel\":\"Panel\",\n" +
                "\"Hearing_notes\":\"Notes1\",\n" +
                "\"respondent_representative\":\"Org\"},\n" +
                "{\"Judge\":\"Ms AM Judge\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"ERMember\":\" \",\n" +
                "\"EEMember\":\" \",\n" +
                "\"Case_No\":\"1112\",\n" +
                "\"Hearing_type\":\"Hearing\",\n" +
                "\"Jurisdictions\":\"ADG, DCD\",\n" +
                "\"Hearing_date\":\"12 October 2020\",\n" +
                "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n" +
                "\"Hearing_time\":\"00:00\",\n" +
                "\"Hearing_duration\":\"12 Days\",\n" +
                "\"Hearing_clerk\":\"Andrew Pearl\",\n" +
                "\"Claimant\":\"Mr s sdfs\",\n" +
                "\"claimant_town\":\"claimantTown1\",\n" +
                "\"claimant_representative\":\"Rep2\",\n" +
                "\"Respondent\":\"sdf2\",\n" +
                "\"resp_others\":\"null\",\n" +
                "\"respondent_town\":\"respondentTown1\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Hearing_room\":\"Tribunal 2\",\n" +
                "\"Hearing_dayofdays\":\"2 of 3\",\n" +
                "\"Hearing_panel\":\"null\",\n" +
                "\"Hearing_notes\":\"Notes2\",\n" +
                "\"respondent_representative\":\"Org2\"}]\n" +
                "},\n" +
                "{\"Hearing_room\":\"Tribunal 4\",\n" +
                "\"listing\":[\n" +
                "{\"Judge\":\"Judge For Tribunal4\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"ERMember\":\" \",\n" +
                "\"EEMember\":\" \",\n" +
                "\"Case_No\":\"1112\",\n" +
                "\"Hearing_type\":\"Preliminary Hearing (CM)\",\n" +
                "\"Jurisdictions\":\"ADG, COM\",\n" +
                "\"Hearing_date\":\"12 October 2020\",\n" +
                "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n" +
                "\"Hearing_time\":\"00:00\",\n" +
                "\"Hearing_duration\":\"12 Minutes\",\n" +
                "\"Hearing_clerk\":\"Anne Fox\",\n" +
                "\"Claimant\":\"Mr s sdfs\",\n" +
                "\"claimant_town\":\"claimantTown2\",\n" +
                "\"claimant_representative\":\"Rep\",\n" +
                "\"Respondent\":\"sdf\",\n" +
                "\"resp_others\":\"null\",\n" +
                "\"respondent_town\":\"respondentTown2\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Hearing_room\":\"Tribunal 4\",\n" +
                "\"Hearing_dayofdays\":\"2 of 3\",\n" +
                "\"Hearing_panel\":\"null\",\n" +
                "\"Hearing_notes\":\"Notes3\",\n" +
                "\"respondent_representative\":\"Org\"},\n" +
                "{\"Judge\":\"Another Judge\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"ERMember\":\" \",\n" +
                "\"EEMember\":\" \",\n" +
                "\"Case_No\":\"1112\",\n" +
                "\"Hearing_type\":\"Preliminary Hearing (CM)\",\n" +
                "\"Jurisdictions\":\"ADG, COM\",\n" +
                "\"Hearing_date\":\"12 October 2020\",\n" +
                "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n" +
                "\"Hearing_time\":\"00:00\",\n" +
                "\"Hearing_duration\":\"3 Minutes\",\n" +
                "\"Hearing_clerk\":\"Juan Fox\",\n" +
                "\"Claimant\":\"Mr s Dominguez\",\n" +
                "\"claimant_town\":\"null\",\n" +
                "\"claimant_representative\":\"Representative\",\n" +
                "\"Respondent\":\"Respondent\",\n" +
                "\"resp_others\":\"null\",\n" +
                "\"respondent_town\":\"null\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Hearing_room\":\"Tribunal 4\",\n" +
                "\"Hearing_dayofdays\":\"2 of 3\",\n" +
                "\"Hearing_panel\":\"Panel\",\n" +
                "\"Hearing_notes\":\"Notes4\",\n" +
                "\"respondent_representative\":\"Organization\"}]\n" +
                "}],\n" +
                "\"case_total\":\"1\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\"\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, ListingHelper.buildListingDocumentContent(listingDetails.getCaseData(), "", PUBLIC_CASE_CAUSE_LIST_ROOM_TEMPLATE, userDetails, MANCHESTER_LISTING_CASE_TYPE_ID).toString());
    }

    @Test
    public void buildCaseCauseListWithNoDocument() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\".docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"Listed_date\":\"12 October 2020\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"case_total\":\"1\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\"\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, ListingHelper.buildListingDocumentContent(listingDetails.getCaseData(), "", "", userDetails, MANCHESTER_LISTING_CASE_TYPE_ID).toString());
    }

    @Test
    public void buildCaseCauseListWithNoDocumentAndRangeAndScotland() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\".docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:schmcts.png]\",\n" +
                "\"Listed_date\":\"12 October 2020\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Listed_date_from\":\"2 January 2020\",\n" +
                "\"Listed_date_to\":\"1 March 2020\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"case_total\":\"1\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\"\n" +
                "}\n" +
                "}\n";
        listingDetails.getCaseData().setHearingDateType(RANGE_HEARING_DATE_TYPE);
        listingDetails.getCaseData().setListingDateFrom("2020-01-02");
        listingDetails.getCaseData().setListingDateTo("2020-03-01");
        assertEquals(expected, ListingHelper.buildListingDocumentContent(listingDetails.getCaseData(), "", "", userDetails, SCOTLAND_LISTING_CASE_TYPE_ID).toString());
    }

    @Test
    public void buildCaseCauseList() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-SCO-ENG-00212.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"Listed_date\":\"12 October 2020\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"listing\":[\n" +
                "{\"Judge\":\"Ms AM Aspden\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"ERMember\":\" \",\n" +
                "\"EEMember\":\" \",\n" +
                "\"Case_No\":\"1112\",\n" +
                "\"Hearing_type\":\"Hearing\",\n" +
                "\"Jurisdictions\":\"ADG, COM\",\n" +
                "\"Hearing_date\":\"12 October 2020\",\n" +
                "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n" +
                "\"Hearing_time\":\"00:00\",\n" +
                "\"Hearing_duration\":\"12 Days\",\n" +
                "\"Hearing_clerk\":\"Anne Fox\",\n" +
                "\"Claimant\":\"Mr s sdfs\",\n" +
                "\"claimant_town\":\"claimantTown\",\n" +
                "\"claimant_representative\":\"Rep\",\n" +
                "\"Respondent\":\"sdf\",\n" +
                "\"resp_others\":\"null\",\n" +
                "\"respondent_town\":\"respondentTown\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Hearing_room\":\"Tribunal 2\",\n" +
                "\"Hearing_dayofdays\":\"1 of 3\",\n" +
                "\"Hearing_panel\":\"Panel\",\n" +
                "\"Hearing_notes\":\"Notes1\",\n" +
                "\"respondent_representative\":\"Org\"},\n" +
                "{\"Judge\":\"Ms AM Judge\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"ERMember\":\" \",\n" +
                "\"EEMember\":\" \",\n" +
                "\"Case_No\":\"1112\",\n" +
                "\"Hearing_type\":\"Hearing\",\n" +
                "\"Jurisdictions\":\"ADG, DCD\",\n" +
                "\"Hearing_date\":\"12 October 2020\",\n" +
                "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n" +
                "\"Hearing_time\":\"00:00\",\n" +
                "\"Hearing_duration\":\"12 Days\",\n" +
                "\"Hearing_clerk\":\"Andrew Pearl\",\n" +
                "\"Claimant\":\"Mr s sdfs\",\n" +
                "\"claimant_town\":\"claimantTown1\",\n" +
                "\"claimant_representative\":\"Rep2\",\n" +
                "\"Respondent\":\"sdf2\",\n" +
                "\"resp_others\":\"null\",\n" +
                "\"respondent_town\":\"respondentTown1\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Hearing_room\":\"Tribunal 2\",\n" +
                "\"Hearing_dayofdays\":\"2 of 3\",\n" +
                "\"Hearing_panel\":\"null\",\n" +
                "\"Hearing_notes\":\"Notes2\",\n" +
                "\"respondent_representative\":\"Org2\"},\n" +
                "{\"Judge\":\"Judge For Tribunal4\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"ERMember\":\" \",\n" +
                "\"EEMember\":\" \",\n" +
                "\"Case_No\":\"1112\",\n" +
                "\"Hearing_type\":\"Preliminary Hearing (CM)\",\n" +
                "\"Jurisdictions\":\"ADG, COM\",\n" +
                "\"Hearing_date\":\"12 October 2020\",\n" +
                "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n" +
                "\"Hearing_time\":\"00:00\",\n" +
                "\"Hearing_duration\":\"12 Minutes\",\n" +
                "\"Hearing_clerk\":\"Anne Fox\",\n" +
                "\"Claimant\":\"Mr s sdfs\",\n" +
                "\"claimant_town\":\"claimantTown2\",\n" +
                "\"claimant_representative\":\"Rep\",\n" +
                "\"Respondent\":\"sdf\",\n" +
                "\"resp_others\":\"null\",\n" +
                "\"respondent_town\":\"respondentTown2\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Hearing_room\":\"Tribunal 4\",\n" +
                "\"Hearing_dayofdays\":\"2 of 3\",\n" +
                "\"Hearing_panel\":\"null\",\n" +
                "\"Hearing_notes\":\"Notes3\",\n" +
                "\"respondent_representative\":\"Org\"},\n" +
                "{\"Judge\":\"Another Judge\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"ERMember\":\" \",\n" +
                "\"EEMember\":\" \",\n" +
                "\"Case_No\":\"1112\",\n" +
                "\"Hearing_type\":\"Preliminary Hearing (CM)\",\n" +
                "\"Jurisdictions\":\"ADG, COM\",\n" +
                "\"Hearing_date\":\"12 October 2020\",\n" +
                "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n" +
                "\"Hearing_time\":\"00:00\",\n" +
                "\"Hearing_duration\":\"3 Minutes\",\n" +
                "\"Hearing_clerk\":\"Juan Fox\",\n" +
                "\"Claimant\":\"Mr s Dominguez\",\n" +
                "\"claimant_town\":\"null\",\n" +
                "\"claimant_representative\":\"Representative\",\n" +
                "\"Respondent\":\"Respondent\",\n" +
                "\"resp_others\":\"null\",\n" +
                "\"respondent_town\":\"null\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Hearing_room\":\"Tribunal 4\",\n" +
                "\"Hearing_dayofdays\":\"2 of 3\",\n" +
                "\"Hearing_panel\":\"Panel\",\n" +
                "\"Hearing_notes\":\"Notes4\",\n" +
                "\"respondent_representative\":\"Organization\"}],\n" +
                "\"case_total\":\"1\",\n" +
                "\"Today_date\":\"" + Helper.formatCurrentDate(LocalDate.now()) + "\"\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, ListingHelper.buildListingDocumentContent(listingDetails.getCaseData(), "", PUBLIC_CASE_CAUSE_LIST_TEMPLATE, userDetails, MANCHESTER_LISTING_CASE_TYPE_ID).toString());
    }

    @Test
    public void getCaseTypeId() {
        assertEquals(MANCHESTER_USERS_CASE_TYPE_ID, ListingHelper.getCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID));
        assertEquals(LEEDS_USERS_CASE_TYPE_ID, ListingHelper.getCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID));
        assertEquals(SCOTLAND_USERS_CASE_TYPE_ID, ListingHelper.getCaseTypeId("OTHERS"));
    }

    @Test
    public void getListingTypeFromSubmitData() {
        SubmitEvent submitEvent = new SubmitEvent();
        submitEvent.setCaseData(new CaseData());
        submitEvent.setCaseId(1);
        HearingType hearingType = new HearingType();
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        DateListedType dateListedType = new DateListedType();
        dateListedType.setHearingClerk("Clerk");
        dateListedType.setHearingRoomKirkawall("Tribunal 4");
        dateListedType.setHearingEdinburgh("Edinburgh");
        dateListedType.setHearingVenueDay("Other");
        dateListedType.setListedDate("2019-12-12T12:11:00.000");
        dateListedTypeItem.setId("123");
        dateListedTypeItem.setValue(dateListedType);
        hearingType.setHearingDateCollection(new ArrayList<>(Collections.singleton(dateListedTypeItem)));
        hearingType.setHearingVenue("Aberdeen");
        hearingType.setHearingEstLengthNum("2");
        hearingType.setHearingEstLengthNumType("hours");
        String expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Edinburgh, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , clerkResponsible= , " +
                "hearingDay=2 of 3, claimantName= , claimantTown= , claimantRepresentative= , respondent= , respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromSubmitData(submitEvent, hearingType, dateListedType, 1, 3).toString());

        dateListedType.setHearingRoomStranraer("Tribunal 5");
        dateListedType.setHearingEdinburgh(null);
        dateListedType.setHearingDundee("Dundee");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Dundee, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , clerkResponsible= , " +
                "hearingDay=2 of 3, claimantName= , claimantTown= , claimantRepresentative= , respondent= , respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 5, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromSubmitData(submitEvent, hearingType, dateListedType, 1, 3).toString());

        dateListedType.setHearingRoomCambeltown("Tribunal 6");
        dateListedType.setHearingDundee(null);
        dateListedType.setHearingGlasgow("Glasgow");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Glasgow, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , clerkResponsible= , " +
                "hearingDay=2 of 3, claimantName= , claimantTown= , claimantRepresentative= , respondent= , respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 6, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromSubmitData(submitEvent, hearingType, dateListedType, 1, 3).toString());

        dateListedType.setHearingRoomCambeltown("Tribunal 7");
        dateListedType.setHearingGlasgow(null);
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Other, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , clerkResponsible= , " +
                "hearingDay=2 of 3, claimantName= , claimantTown= , claimantRepresentative= , respondent= , respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 7, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromSubmitData(submitEvent, hearingType, dateListedType, 1, 3).toString());
    }

    @Test
    public void getListingDateBetween() {
        String dateToSearchFrom = "2020-01-02";
        String dateToSearchTo = "2020-03-01";
        String dateToSearch = "2020-01-02T00:00:00.000";
        boolean isBetween = ListingHelper.getListingDateBetween(dateToSearchFrom, dateToSearchTo, dateToSearch);
        assertTrue(isBetween);

        dateToSearchFrom = "2020-01-02";
        dateToSearchTo = "";
        dateToSearch = "2020-01-02T10:00:00.000";
        boolean isEqual = ListingHelper.getListingDateBetween(dateToSearchFrom, dateToSearchTo, dateToSearch);
        assertTrue(isEqual);

        dateToSearchFrom = "2020-01-02";
        dateToSearchTo = "";
        dateToSearch = "2020-01-03T10:00:00.000";
        isEqual = ListingHelper.getListingDateBetween(dateToSearchFrom, dateToSearchTo, dateToSearch);
        assertFalse(isEqual);
    }

    @Test
    public void getListingDocName() {
        ListingData listingData = new ListingData();
        listingData.setHearingDocType(HEARING_DOC_ETCL);
        listingData.setHearingDocETCL(HEARING_ETCL_STAFF);
        listingData.setRoomOrNoRoom("No");
        assertEquals(STAFF_CASE_CAUSE_LIST_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setRoomOrNoRoom("Yes");
        assertEquals(STAFF_CASE_CAUSE_LIST_ROOM_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setHearingDocETCL(HEARING_ETCL_PUBLIC);
        listingData.setRoomOrNoRoom("No");
        assertEquals(PUBLIC_CASE_CAUSE_LIST_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setRoomOrNoRoom("Yes");
        assertEquals(PUBLIC_CASE_CAUSE_LIST_ROOM_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setHearingDocETCL(HEARING_ETCL_PRESS_LIST);
        listingData.setHearingDateType(RANGE_HEARING_DATE_TYPE);
        assertEquals(PRESS_LIST_CAUSE_LIST_RANGE_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        assertEquals(PRESS_LIST_CAUSE_LIST_SINGLE_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setHearingDocType(HEARING_DOC_IT56);
        assertEquals(IT56_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setHearingDocType(HEARING_DOC_IT57);
        assertEquals(IT57_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setHearingDocType("");
        assertEquals("No document found", ListingHelper.getListingDocName(listingData));
    }
}