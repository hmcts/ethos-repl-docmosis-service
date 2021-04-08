package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.*;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static org.junit.Assert.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

public class ListingHelperTest {

    private ListingDetails listingDetails;
    private ListingDetails listingDetails2;
    private ListingDetails listingDetails3;
    private UserDetails userDetails;

    @Before
    public void setUp() throws Exception {
        listingDetails = generateListingDetails("listingDetailsTest1.json");
        listingDetails2 = generateListingDetails("listingDetailsTest2.json");
        listingDetails3 = generateListingDetails("listingDetailsTest3.json");
        userDetails = HelperTest.getUserDetails();
    }

    private ListingDetails generateListingDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, ListingDetails.class);
    }

    @Test
    public void buildCaseCauseListByRoom() {
        String expected = "{\n"
                + "\"accessKey\":\"\",\n"
                + "\"templateName\":\"EM-TRB-SCO-ENG-00214.docx\",\n"
                + "\"outputName\":\"document.docx\",\n"
                + "\"data\":{\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"Office_name\":\"Manchester\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Listed_date\":\"12 October 2020\",\n"
                + "\"Clerk\":\"Mike Jordan\",\n"
                + "\"listing_date\":[\n"
                + "{\"date\":\"11 February 2020\",\n"
                + "\"location\":[\n"
                + "{\"Hearing_room\":\"Tribunal 2\",\n"
                + "\"listing\":[\n"
                + "{\"Judge\":\"Ms AM Judge\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Hearing\",\n"
                + "\"Jurisdictions\":\"ADG, DCD\",\n"
                + "\"Hearing_date\":\"11 February 2020\",\n"
                + "\"Hearing_date_time\":\"11 February 2020 at 00:00\",\n"
                + "\"Hearing_time\":\"00:00\",\n"
                + "\"Hearing_duration\":\"12 Days\",\n"
                + "\"Hearing_clerk\":\"Andrew Pearl\",\n"
                + "\"Claimant\":\"Mr s sdfs\",\n"
                + "\"claimant_town\":\"claimantTown1\",\n"
                + "\"claimant_representative\":\"Rep2\",\n"
                + "\"Respondent\":\"sdf2\",\n"
                + "\"resp_others\":\"Mark Taylor\\nTony Jones\",\n"
                + "\"respondent_town\":\"respondentTown1\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Hearing_room\":\"Tribunal 2\",\n"
                + "\"Hearing_dayofdays\":\"2 of 3\",\n"
                + "\"Hearing_panel\":\"\",\n"
                + "\"Hearing_notes\":\"Notes2\",\n"
                + "\"respondent_representative\":\"Org2\"}]\n"
                + "}],\n"
                + "},\n"
                + "{\"date\":\"12 October 2020\",\n"
                + "\"location\":[\n"
                + "{\"Hearing_room\":\"* Not Allocated\",\n"
                + "\"listing\":[\n"
                + "{\"Judge\":\"Ms AM Aspden\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Hearing\",\n"
                + "\"Jurisdictions\":\"ADG, COM\",\n"
                + "\"Hearing_date\":\"12 October 2020\",\n"
                + "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n"
                + "\"Hearing_time\":\"00:00\",\n"
                + "\"Hearing_duration\":\"12 Days\",\n"
                + "\"Hearing_clerk\":\"Anne Fox\",\n"
                + "\"Claimant\":\"Mr s sdfs\",\n"
                + "\"claimant_town\":\"claimantTown\",\n"
                + "\"claimant_representative\":\"Rep\",\n"
                + "\"Respondent\":\"sdf\",\n"
                + "\"resp_others\":\"Mark Taylor\\nTony Jones\\nSteve Thomas\",\n"
                + "\"respondent_town\":\"respondentTown\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Hearing_room\":\"\",\n"
                + "\"Hearing_dayofdays\":\"1 of 3\",\n"
                + "\"Hearing_panel\":\"Panel\",\n"
                + "\"Hearing_notes\":\"Notes with  -  new line\",\n"
                + "\"respondent_representative\":\"Org\"}]\n"
                + "}],\n"
                + "},\n"
                + "{\"date\":\"14 December 2020\",\n"
                + "\"location\":[\n"
                + "{\"Hearing_room\":\"Tribunal 4\",\n"
                + "\"listing\":[\n"
                + "{\"Judge\":\"Another Judge\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Preliminary Hearing (CM)\",\n"
                + "\"Jurisdictions\":\"ADG, COM\",\n"
                + "\"Hearing_date\":\"14 December 2020\",\n"
                + "\"Hearing_date_time\":\"14 December 2020 at 00:00\",\n"
                + "\"Hearing_time\":\"00:00\",\n"
                + "\"Hearing_duration\":\"3 Minutes\",\n"
                + "\"Hearing_clerk\":\"Juan Fox\",\n"
                + "\"Claimant\":\"Mr s Dominguez\",\n"
                + "\"claimant_town\":\"\",\n"
                + "\"claimant_representative\":\"Representative\",\n"
                + "\"Respondent\":\"Respondent\",\n"
                + "\"resp_others\":\"\",\n"
                + "\"respondent_town\":\"\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Hearing_room\":\"Tribunal 4\",\n"
                + "\"Hearing_dayofdays\":\"2 of 3\",\n"
                + "\"Hearing_panel\":\"Panel\",\n"
                + "\"Hearing_notes\":\"Notes4\",\n"
                + "\"respondent_representative\":\"Organization\"},\n"
                + "{\"Judge\":\"Judge For Tribunal4\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Preliminary Hearing (CM)\",\n"
                + "\"Jurisdictions\":\"ADG, COM\",\n"
                + "\"Hearing_date\":\"14 December 2020\",\n"
                + "\"Hearing_date_time\":\"14 December 2020 at 01:00\",\n"
                + "\"Hearing_time\":\"01:00\",\n"
                + "\"Hearing_duration\":\"12 Minutes\",\n"
                + "\"Hearing_clerk\":\"Anne Fox\",\n"
                + "\"Claimant\":\"Mr s sdfs\",\n"
                + "\"claimant_town\":\"claimantTown2\",\n"
                + "\"claimant_representative\":\"Rep\",\n"
                + "\"Respondent\":\"sdf\",\n"
                + "\"resp_others\":\"Mark Taylor\",\n"
                + "\"respondent_town\":\"respondentTown2\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Hearing_room\":\"Tribunal 4\",\n"
                + "\"Hearing_dayofdays\":\"2 of 3\",\n"
                + "\"Hearing_panel\":\"\",\n"
                + "\"Hearing_notes\":\"Notes3\",\n"
                + "\"respondent_representative\":\"Org\"}]\n"
                + "}],\n"
                + "}],\n"
                + "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n"
                + "}\n"
                + "}\n";
        listingDetails.getCaseData().getListingCollection().get(0).getValue().setHearingNotes("Notes with \n new line");
        assertEquals(expected, ListingHelper.buildListingDocumentContent(listingDetails.getCaseData(),
                "", PUBLIC_CASE_CAUSE_LIST_ROOM_TEMPLATE, userDetails,
                MANCHESTER_LISTING_CASE_TYPE_ID).toString());
    }

    @Test
    public void buildCaseCauseListWithNoDocument() {
        String expected = "{\n"
                + "\"accessKey\":\"\",\n"
                + "\"templateName\":\".docx\",\n"
                + "\"outputName\":\"document.docx\",\n"
                + "\"data\":{\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"Office_name\":\"London Central\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Listed_date\":\"12 October 2020\",\n"
                + "\"Clerk\":\"Mike Jordan\",\n"
                + "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n"
                + "}\n"
                + "}\n";
        assertEquals(expected, ListingHelper.buildListingDocumentContent(listingDetails.getCaseData(),
                "", "", userDetails, LONDON_CENTRAL_LISTING_CASE_TYPE_ID).toString());
    }

    @Test
    public void buildCaseCauseListWithNoDocumentAndRangeAndScotland() {
        String expected = "{\n"
                + "\"accessKey\":\"\",\n"
                + "\"templateName\":\".docx\",\n"
                + "\"outputName\":\"document.docx\",\n"
                + "\"data\":{\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:schmcts.png]\",\n"
                + "\"Office_name\":\"Scotland\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Listed_date_from\":\"2 January 2020\",\n"
                + "\"Listed_date_to\":\"1 March 2020\",\n"
                + "\"Clerk\":\"Mike Jordan\",\n"
                + "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n"
                + "}\n"
                + "}\n";
        listingDetails.getCaseData().setHearingDateType(RANGE_HEARING_DATE_TYPE);
        listingDetails.getCaseData().setListingDateFrom("2020-01-02");
        listingDetails.getCaseData().setListingDateTo("2020-03-01");
        assertEquals(expected, ListingHelper.buildListingDocumentContent(listingDetails.getCaseData(),
                "", "", userDetails, SCOTLAND_LISTING_CASE_TYPE_ID).toString());
    }

    @Test
    public void buildCaseCauseList() {
        String expected = "{\n"
                + "\"accessKey\":\"\",\n"
                + "\"templateName\":\"EM-TRB-SCO-ENG-00212.docx\",\n"
                + "\"outputName\":\"document.docx\",\n"
                + "\"data\":{\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"Office_name\":\"Manchester\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Listed_date\":\"12 October 2020\",\n"
                + "\"Clerk\":\"Mike Jordan\",\n"
                + "\"listing_date\":[\n"
                + "{\"date\":\"11 February 2020\",\n"
                + "\"case_total\":\"1\",\n"
                + "\"listing\":[\n"
                + "{\"Judge\":\"Ms AM Judge\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Hearing\",\n"
                + "\"Jurisdictions\":\"ADG, DCD\",\n"
                + "\"Hearing_date\":\"11 February 2020\",\n"
                + "\"Hearing_date_time\":\"11 February 2020 at 00:00\",\n"
                + "\"Hearing_time\":\"00:00\",\n"
                + "\"Hearing_duration\":\"12 Days\",\n"
                + "\"Hearing_clerk\":\"Andrew Pearl\",\n"
                + "\"Claimant\":\"Mr s sdfs\",\n"
                + "\"claimant_town\":\"claimantTown1\",\n"
                + "\"claimant_representative\":\"Rep2\",\n"
                + "\"Respondent\":\"sdf2\",\n"
                + "\"resp_others\":\"Mark Taylor\\nTony Jones\",\n"
                + "\"respondent_town\":\"respondentTown1\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Hearing_room\":\"Tribunal 2\",\n"
                + "\"Hearing_dayofdays\":\"2 of 3\",\n"
                + "\"Hearing_panel\":\"\",\n"
                + "\"Hearing_notes\":\"Notes2\",\n"
                + "\"respondent_representative\":\"Org2\"}]\n"
                + "},\n"
                + "{\"date\":\"12 October 2020\",\n"
                + "\"case_total\":\"1\",\n"
                + "\"listing\":[\n"
                + "{\"Judge\":\"Ms AM Aspden\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Hearing\",\n"
                + "\"Jurisdictions\":\"ADG, COM\",\n"
                + "\"Hearing_date\":\"12 October 2020\",\n"
                + "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n"
                + "\"Hearing_time\":\"00:00\",\n"
                + "\"Hearing_duration\":\"12 Days\",\n"
                + "\"Hearing_clerk\":\"Anne Fox\",\n"
                + "\"Claimant\":\"Mr s sdfs\",\n"
                + "\"claimant_town\":\"claimantTown\",\n"
                + "\"claimant_representative\":\"Rep\",\n"
                + "\"Respondent\":\"sdf\",\n"
                + "\"resp_others\":\"Mark Taylor\\nTony Jones\\nSteve Thomas\",\n"
                + "\"respondent_town\":\"respondentTown\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Hearing_room\":\"\",\n"
                + "\"Hearing_dayofdays\":\"1 of 3\",\n"
                + "\"Hearing_panel\":\"Panel\",\n"
                + "\"Hearing_notes\":\"Notes1\",\n"
                + "\"respondent_representative\":\"Org\"}]\n"
                + "},\n"
                + "{\"date\":\"14 December 2020\",\n"
                + "\"case_total\":\"2\",\n"
                + "\"listing\":[\n"
                + "{\"Judge\":\"Another Judge\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Preliminary Hearing (CM)\",\n"
                + "\"Jurisdictions\":\"ADG, COM\",\n"
                + "\"Hearing_date\":\"14 December 2020\",\n"
                + "\"Hearing_date_time\":\"14 December 2020 at 00:00\",\n"
                + "\"Hearing_time\":\"00:00\",\n"
                + "\"Hearing_duration\":\"3 Minutes\",\n"
                + "\"Hearing_clerk\":\"Juan Fox\",\n"
                + "\"Claimant\":\"Mr s Dominguez\",\n"
                + "\"claimant_town\":\"\",\n"
                + "\"claimant_representative\":\"Representative\",\n"
                + "\"Respondent\":\"Respondent\",\n"
                + "\"resp_others\":\"\",\n"
                + "\"respondent_town\":\"\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Hearing_room\":\"Tribunal 4\",\n"
                + "\"Hearing_dayofdays\":\"2 of 3\",\n"
                + "\"Hearing_panel\":\"Panel\",\n"
                + "\"Hearing_notes\":\"Notes4\",\n"
                + "\"respondent_representative\":\"Organization\"},\n"
                + "{\"Judge\":\"Judge For Tribunal4\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Preliminary Hearing (CM)\",\n"
                + "\"Jurisdictions\":\"ADG, COM\",\n"
                + "\"Hearing_date\":\"14 December 2020\",\n"
                + "\"Hearing_date_time\":\"14 December 2020 at 01:00\",\n"
                + "\"Hearing_time\":\"01:00\",\n"
                + "\"Hearing_duration\":\"12 Minutes\",\n"
                + "\"Hearing_clerk\":\"Anne Fox\",\n"
                + "\"Claimant\":\"Mr s sdfs\",\n"
                + "\"claimant_town\":\"claimantTown2\",\n"
                + "\"claimant_representative\":\"Rep\",\n"
                + "\"Respondent\":\"sdf\",\n"
                + "\"resp_others\":\"Mark Taylor\",\n"
                + "\"respondent_town\":\"respondentTown2\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Hearing_room\":\"Tribunal 4\",\n"
                + "\"Hearing_dayofdays\":\"2 of 3\",\n"
                + "\"Hearing_panel\":\"\",\n"
                + "\"Hearing_notes\":\"Notes3\",\n"
                + "\"respondent_representative\":\"Org\"}]\n"
                + "}],\n"
                + "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n"
                + "}\n"
                + "}\n";
        assertEquals(expected, ListingHelper.buildListingDocumentContent(listingDetails.getCaseData(), "",
                PUBLIC_CASE_CAUSE_LIST_TEMPLATE, userDetails, MANCHESTER_LISTING_CASE_TYPE_ID).toString());
    }

    @Test
    public void buildCaseCauseList2() {
        String expected = "{\n"
                + "\"accessKey\":\"\",\n"
                + "\"templateName\":\"EM-TRB-SCO-ENG-00212.docx\",\n"
                + "\"outputName\":\"document.docx\",\n"
                + "\"data\":{\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"Office_name\":\"Manchester\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Listed_date\":\"12 October 2020\",\n"
                + "\"Clerk\":\"Mike Jordan\",\n"
                + "\"listing_date\":[\n"
                + "{\"date\":\"16 January 2020\",\n"
                + "\"case_total\":\"1\",\n"
                + "\"listing\":[\n"
                + "{\"Judge\":\"\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Preliminary Hearing (CM)\",\n"
                + "\"Jurisdictions\":\"ADG, COM\",\n"
                + "\"Hearing_date\":\"16 January 2020\",\n"
                + "\"Hearing_date_time\":\"16 January 2020 at 00:00\",\n"
                + "\"Hearing_time\":\"00:00\",\n"
                + "\"Hearing_duration\":\"12 Minutes\",\n"
                + "\"Hearing_clerk\":\"Anne Fox\",\n"
                + "\"Claimant\":\"Mr s sdfs\",\n"
                + "\"claimant_town\":\"claimantTown2\",\n"
                + "\"claimant_representative\":\"Rep\",\n"
                + "\"Respondent\":\"sdf\",\n"
                + "\"resp_others\":\"Mark Taylor\",\n"
                + "\"respondent_town\":\"respondentTown2\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Hearing_room\":\"Tribunal 4\",\n"
                + "\"Hearing_dayofdays\":\"2 of 3\",\n"
                + "\"Hearing_panel\":\"\",\n"
                + "\"Hearing_notes\":\"Notes3\",\n"
                + "\"respondent_representative\":\"Org\"}]\n"
                + "},\n"
                + "{\"date\":\"12 October 2020\",\n"
                + "\"case_total\":\"1\",\n"
                + "\"listing\":[\n"
                + "{\"Judge\":\"\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Hearing\",\n"
                + "\"Jurisdictions\":\"ADG, COM\",\n"
                + "\"Hearing_date\":\"12 October 2020\",\n"
                + "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n"
                + "\"Hearing_time\":\"00:00\",\n"
                + "\"Hearing_duration\":\"12 Days\",\n"
                + "\"Hearing_clerk\":\"Anne Fox\",\n"
                + "\"Claimant\":\"Mr s sdfs\",\n"
                + "\"claimant_town\":\"claimantTown\",\n"
                + "\"claimant_representative\":\"Rep\",\n"
                + "\"Respondent\":\"sdf\",\n"
                + "\"resp_others\":\"Mark Taylor\\nTony Jones\\nSteve Thomas\",\n"
                + "\"respondent_town\":\"respondentTown\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Hearing_room\":\"Tribunal 2\",\n"
                + "\"Hearing_dayofdays\":\"1 of 3\",\n"
                + "\"Hearing_panel\":\"Panel\",\n"
                + "\"Hearing_notes\":\"Notes1\",\n"
                + "\"respondent_representative\":\"Org\"}]\n"
                + "},\n"
                + "{\"date\":\"14 October 2020\",\n"
                + "\"case_total\":\"1\",\n"
                + "\"listing\":[\n"
                + "{\"Judge\":\"\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Hearing\",\n"
                + "\"Jurisdictions\":\"ADG, DCD\",\n"
                + "\"Hearing_date\":\"14 October 2020\",\n"
                + "\"Hearing_date_time\":\"14 October 2020 at 00:00\",\n"
                + "\"Hearing_time\":\"00:00\",\n"
                + "\"Hearing_duration\":\"12 Days\",\n"
                + "\"Hearing_clerk\":\"Andrew Pearl\",\n"
                + "\"Claimant\":\"Mr s sdfs\",\n"
                + "\"claimant_town\":\"claimantTown1\",\n"
                + "\"claimant_representative\":\"Rep2\",\n"
                + "\"Respondent\":\"sdf2\",\n"
                + "\"resp_others\":\"Mark Taylor\\nTony Jones\",\n"
                + "\"respondent_town\":\"respondentTown1\",\n"
                + "\"Hearing_location\":\"Leeds\",\n"
                + "\"Hearing_room\":\"Tribunal 2\",\n"
                + "\"Hearing_dayofdays\":\"2 of 3\",\n"
                + "\"Hearing_panel\":\"\",\n"
                + "\"Hearing_notes\":\"Notes2\",\n"
                + "\"respondent_representative\":\"Org2\"}]\n"
                + "}],\n"
                + "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n"
                + "}\n"
                + "}\n";
        assertEquals(expected, ListingHelper.buildListingDocumentContent(listingDetails2.getCaseData(), "",
                PUBLIC_CASE_CAUSE_LIST_TEMPLATE, userDetails, MANCHESTER_LISTING_CASE_TYPE_ID).toString());
    }

    @Test
    public void buildCaseCauseIt56() {
        String expected = "{\n"
                + "\"accessKey\":\"\",\n"
                + "\"templateName\":\"EM-TRB-SCO-ENG-00210.docx\",\n"
                + "\"outputName\":\"document.docx\",\n"
                + "\"data\":{\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"Office_name\":\"Manchester\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Listed_date\":\"12 October 2020\",\n"
                + "\"Clerk\":\"Mike Jordan\",\n"
                + "\"listing\":[\n"
                + "{\"Judge\":\"\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Preliminary Hearing (CM)\",\n"
                + "\"Jurisdictions\":\"ADG, COM\",\n"
                + "\"Hearing_date\":\"16 January 2020\",\n"
                + "\"Hearing_date_time\":\"16 January 2020 at 00:00\",\n"
                + "\"Hearing_time\":\"00:00\",\n"
                + "\"Hearing_duration\":\"12 Minutes\",\n"
                + "\"Hearing_clerk\":\"Anne Fox\",\n"
                + "\"Claimant\":\"Mr s sdfs\",\n"
                + "\"claimant_town\":\"claimantTown2\",\n"
                + "\"claimant_representative\":\"Rep\",\n"
                + "\"Respondent\":\"sdf\",\n"
                + "\"resp_others\":\"Mark Taylor\",\n"
                + "\"respondent_town\":\"respondentTown2\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Hearing_room\":\"Tribunal 4\",\n"
                + "\"Hearing_dayofdays\":\"2 of 3\",\n"
                + "\"Hearing_panel\":\"\",\n"
                + "\"Hearing_notes\":\"Notes3\",\n"
                + "\"respondent_representative\":\"Org\"},\n"
                + "{\"Judge\":\"\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Hearing\",\n"
                + "\"Jurisdictions\":\"ADG, COM\",\n"
                + "\"Hearing_date\":\"12 October 2020\",\n"
                + "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n"
                + "\"Hearing_time\":\"00:00\",\n"
                + "\"Hearing_duration\":\"12 Days\",\n"
                + "\"Hearing_clerk\":\"Anne Fox\",\n"
                + "\"Claimant\":\"Mr s sdfs\",\n"
                + "\"claimant_town\":\"claimantTown\",\n"
                + "\"claimant_representative\":\"Rep\",\n"
                + "\"Respondent\":\"sdf\",\n"
                + "\"resp_others\":\"Mark Taylor\\nTony Jones\\nSteve Thomas\",\n"
                + "\"respondent_town\":\"respondentTown\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Hearing_room\":\"Tribunal 2\",\n"
                + "\"Hearing_dayofdays\":\"1 of 3\",\n"
                + "\"Hearing_panel\":\"Panel\",\n"
                + "\"Hearing_notes\":\"Notes1\",\n"
                + "\"respondent_representative\":\"Org\"},\n"
                + "{\"Judge\":\"\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Hearing\",\n"
                + "\"Jurisdictions\":\"ADG, DCD\",\n"
                + "\"Hearing_date\":\"14 October 2020\",\n"
                + "\"Hearing_date_time\":\"14 October 2020 at 00:00\",\n"
                + "\"Hearing_time\":\"00:00\",\n"
                + "\"Hearing_duration\":\"12 Days\",\n"
                + "\"Hearing_clerk\":\"Andrew Pearl\",\n"
                + "\"Claimant\":\"Mr s sdfs\",\n"
                + "\"claimant_town\":\"claimantTown1\",\n"
                + "\"claimant_representative\":\"Rep2\",\n"
                + "\"Respondent\":\"sdf2\",\n"
                + "\"resp_others\":\"Mark Taylor\\nTony Jones\",\n"
                + "\"respondent_town\":\"respondentTown1\",\n"
                + "\"Hearing_location\":\"Leeds\",\n"
                + "\"Hearing_room\":\"Tribunal 2\",\n"
                + "\"Hearing_dayofdays\":\"2 of 3\",\n"
                + "\"Hearing_panel\":\"\",\n"
                + "\"Hearing_notes\":\"Notes2\",\n"
                + "\"respondent_representative\":\"Org2\"}],\n"
                + "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n"
                + "}\n"
                + "}\n";
        assertEquals(expected, ListingHelper.buildListingDocumentContent(listingDetails2.getCaseData(), "",
                IT56_TEMPLATE, userDetails, MANCHESTER_LISTING_CASE_TYPE_ID).toString());
    }

    @Test
    public void buildCaseCauseListPressList() {
        String expected = "{\n"
                + "\"accessKey\":\"\",\n"
                + "\"templateName\":\"EM-TRB-SCO-ENG-00217.docx\",\n"
                + "\"outputName\":\"document.docx\",\n"
                + "\"data\":{\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"Office_name\":\"Manchester\",\n"
                + "\"Hearing_location\":\"Glasgow COET\",\n"
                + "\"Listed_date\":\"12 October 2020\",\n"
                + "\"Clerk\":\"Mike Jordan\",\n"
                + "\"location\":[\n"
                + "{\"Hearing_venue\":\"Manchester\",\n"
                + "\"listing\":[\n"
                + "{\"Judge\":\"\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Preliminary Hearing (CM)\",\n"
                + "\"Jurisdictions\":\"ADG, COM\",\n"
                + "\"Hearing_date\":\"16 January 2020\",\n"
                + "\"Hearing_date_time\":\"16 January 2020 at 00:00\",\n"
                + "\"Hearing_time\":\"00:00\",\n"
                + "\"Hearing_duration\":\"12 Minutes\",\n"
                + "\"Hearing_clerk\":\"Anne Fox\",\n"
                + "\"Claimant\":\"Mr s sdfs\",\n"
                + "\"claimant_town\":\"claimantTown2\",\n"
                + "\"claimant_representative\":\"Rep\",\n"
                + "\"Respondent\":\"sdf\",\n"
                + "\"resp_others\":\"Mark Taylor\",\n"
                + "\"respondent_town\":\"respondentTown2\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Hearing_room\":\"Tribunal 4\",\n"
                + "\"Hearing_dayofdays\":\"2 of 3\",\n"
                + "\"Hearing_panel\":\"\",\n"
                + "\"Hearing_notes\":\"Notes3\",\n"
                + "\"respondent_representative\":\"Org\"},\n"
                + "{\"Judge\":\"\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Hearing\",\n"
                + "\"Jurisdictions\":\"ADG, DCD\",\n"
                + "\"Hearing_date\":\"16 January 2020\",\n"
                + "\"Hearing_date_time\":\"16 January 2020 at 02:00\",\n"
                + "\"Hearing_time\":\"02:00\",\n"
                + "\"Hearing_duration\":\"12 Days\",\n"
                + "\"Hearing_clerk\":\"Andrew Pearl\",\n"
                + "\"Claimant\":\"Mr s sdfs\",\n"
                + "\"claimant_town\":\"claimantTown1\",\n"
                + "\"claimant_representative\":\"Rep2\",\n"
                + "\"Respondent\":\"sdf2\",\n"
                + "\"resp_others\":\"Mark Taylor\\nTony Jones\",\n"
                + "\"respondent_town\":\"respondentTown1\",\n"
                + "\"Hearing_location\":\"Manchester\",\n"
                + "\"Hearing_room\":\"Tribunal 2\",\n"
                + "\"Hearing_dayofdays\":\"2 of 3\",\n"
                + "\"Hearing_panel\":\"\",\n"
                + "\"Hearing_notes\":\"Notes2\",\n"
                + "\"respondent_representative\":\"Org2\"}]\n"
                + "},\n"
                + "{\"Hearing_venue\":\"Not_Allocated\",\n"
                + "\"listing\":[\n"
                + "{\"Judge\":\"\",\n"
                + "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n"
                + "\"Court_addressLine2\":\"Alexandra House\",\n"
                + "\"Court_addressLine3\":\"14-22 The Parsonage\",\n"
                + "\"Court_town\":\"Manchester\",\n"
                + "\"Court_county\":\"\",\n"
                + "\"Court_postCode\":\"M3 2JA\",\n"
                + "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, "
                + "Manchester, M3 2JA\",\n"
                + "\"Court_telephone\":\"03577131270\",\n"
                + "\"Court_fax\":\"07577126570\",\n"
                + "\"Court_DX\":\"123456\",\n"
                + "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n"
                + "\"listing_logo\":\"[userImage:enhmcts.png]\",\n"
                + "\"ERMember\":\" \",\n"
                + "\"EEMember\":\" \",\n"
                + "\"Case_No\":\"1112\",\n"
                + "\"Hearing_type\":\"Hearing\",\n"
                + "\"Jurisdictions\":\"ADG, COM\",\n"
                + "\"Hearing_date\":\"16 January 2020\",\n"
                + "\"Hearing_date_time\":\"16 January 2020 at 03:00\",\n"
                + "\"Hearing_time\":\"03:00\",\n"
                + "\"Hearing_duration\":\"12 Days\",\n"
                + "\"Hearing_clerk\":\"Anne Fox\",\n"
                + "\"Claimant\":\"Mr s sdfs\",\n"
                + "\"claimant_town\":\"claimantTown\",\n"
                + "\"claimant_representative\":\"Rep\",\n"
                + "\"Respondent\":\"sdf\",\n"
                + "\"resp_others\":\"Mark Taylor\\nTony Jones\\nSteve Thomas\",\n"
                + "\"respondent_town\":\"respondentTown\",\n"
                + "\"Hearing_location\":\"\",\n"
                + "\"Hearing_room\":\"Tribunal 2\",\n"
                + "\"Hearing_dayofdays\":\"1 of 3\",\n"
                + "\"Hearing_panel\":\"Panel\",\n"
                + "\"Hearing_notes\":\"Notes1\",\n"
                + "\"respondent_representative\":\"Org\"}]\n"
                + "}],\n"
                + "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n"
                + "}\n"
                + "}\n";
        assertEquals(expected, ListingHelper.buildListingDocumentContent(listingDetails3.getCaseData(), "",
                PRESS_LIST_CAUSE_LIST_SINGLE_TEMPLATE, userDetails, MANCHESTER_LISTING_CASE_TYPE_ID).toString());
    }

    @Test
    public void getListingCaseTypeId() {
        assertEquals(MANCHESTER_DEV_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(MANCHESTER_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(MANCHESTER_USERS_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(MANCHESTER_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(MANCHESTER_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID));
        assertEquals(LEEDS_DEV_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(LEEDS_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(LEEDS_USERS_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(LEEDS_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(LEEDS_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID));
        assertEquals(SCOTLAND_DEV_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(SCOTLAND_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(SCOTLAND_USERS_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(SCOTLAND_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(SCOTLAND_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(SCOTLAND_LISTING_CASE_TYPE_ID));
        assertEquals(BRISTOL_DEV_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(BRISTOL_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(BRISTOL_USERS_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(BRISTOL_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(BRISTOL_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(BRISTOL_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_CENTRAL_DEV_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(LONDON_CENTRAL_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_CENTRAL_USERS_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(LONDON_CENTRAL_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_CENTRAL_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(LONDON_CENTRAL_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_EAST_DEV_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(LONDON_EAST_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_EAST_USERS_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(LONDON_EAST_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_EAST_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(LONDON_EAST_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_SOUTH_DEV_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(LONDON_SOUTH_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_SOUTH_USERS_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(LONDON_SOUTH_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_SOUTH_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(LONDON_SOUTH_LISTING_CASE_TYPE_ID));
        assertEquals(MIDLANDS_EAST_DEV_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(MIDLANDS_EAST_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(MIDLANDS_EAST_USERS_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(MIDLANDS_EAST_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(MIDLANDS_EAST_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(MIDLANDS_EAST_LISTING_CASE_TYPE_ID));
        assertEquals(MIDLANDS_WEST_DEV_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(MIDLANDS_WEST_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(MIDLANDS_WEST_USERS_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(MIDLANDS_WEST_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(MIDLANDS_WEST_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(MIDLANDS_WEST_LISTING_CASE_TYPE_ID));
        assertEquals(NEWCASTLE_DEV_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(NEWCASTLE_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(NEWCASTLE_USERS_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(NEWCASTLE_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(NEWCASTLE_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID));
        assertEquals(WALES_DEV_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(WALES_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(WALES_USERS_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(WALES_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(WALES_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(WALES_LISTING_CASE_TYPE_ID));
        assertEquals(WATFORD_DEV_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(WATFORD_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(WATFORD_USERS_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(WATFORD_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(WATFORD_CASE_TYPE_ID,
                UtilHelper.getListingCaseTypeId(WATFORD_LISTING_CASE_TYPE_ID));
    }

    @Test
    public void getListingTypeFromSubmitData() {
        CaseData caseData = new CaseData();
        ClaimantIndType claimantIndType = new ClaimantIndType();
        claimantIndType.setClaimantLastName("Rodriguez");
        caseData.setClaimantIndType(claimantIndType);
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName("Juan Pedro");
        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setValue(respondentSumType);
        caseData.setRespondentCollection(new ArrayList<>(Collections.singleton(respondentSumTypeItem)));
        HearingType hearingType = new HearingType();
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        DateListedType dateListedType = new DateListedType();
        dateListedType.setHearingClerk("Clerk");
        dateListedType.setHearingRoomKirkawall("Tribunal 4");
        dateListedType.setHearingEdinburgh("EdinburghVenue");
        dateListedType.setHearingVenueDay("Edinburgh");
        dateListedType.setListedDate("2019-12-12T12:11:00.000");
        dateListedTypeItem.setId("123");
        dateListedTypeItem.setValue(dateListedType);
        hearingType.setHearingDateCollection(new ArrayList<>(Collections.singleton(dateListedTypeItem)));
        hearingType.setHearingVenue(ABERDEEN_OFFICE);
        hearingType.setHearingEstLengthNum("2");
        hearingType.setHearingEstLengthNumType("hours");
        String expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, "
                + "causeListVenue=EdinburghVenue, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , "
                + "hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , "
                + "respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, "
                + "respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData,
                hearingType, dateListedType, 1, 3).toString());

        dateListedType.setHearingRoomStranraer("Tribunal 5");
        dateListedType.setHearingEdinburgh(null);
        dateListedType.setHearingVenueDay(DUNDEE_OFFICE);
        dateListedType.setHearingDundee("DundeeVenue");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=DundeeVenue, "
                + "elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , "
                + "hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , "
                + "respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 5, "
                + "respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData,
                hearingType, dateListedType, 1, 3).toString());

        dateListedType.setHearingRoomCambeltown("Tribunal 6");
        dateListedType.setHearingDundee(null);
        dateListedType.setHearingVenueDay(GLASGOW_OFFICE);
        dateListedType.setHearingGlasgow("GlasgowVenue");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=GlasgowVenue, "
                + "elmoCaseReference=null, jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , "
                + "hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , "
                + "respondent=Juan Pedro, respondentTown= , respondentRepresentative= , estHearingLength=2 hours, "
                + "hearingPanel= , hearingRoom=Tribunal 6, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData,
                hearingType, dateListedType, 1, 3).toString());

        dateListedType.setHearingRoomCambeltown("Tribunal 7");
        dateListedType.setHearingGlasgow(null);
        dateListedType.setHearingVenueDay(EDINBURGH_OFFICE);
        dateListedType.setHearingEdinburgh("EdinburghVenue");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=EdinburghVenue, "
                + "elmoCaseReference=null, jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , "
                + "hearingEEMember= , hearingERMember= , hearingClerk=Clerk, hearingDay=2 of 3, "
                + "claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, "
                + "respondentTown= , respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , "
                + "hearingRoom=Tribunal 7, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData,
                hearingType, dateListedType, 1, 3).toString());

        CaseData caseDataRule50 = new CaseData();
        RestrictedReportingType restrictedReportingType = new RestrictedReportingType();
        restrictedReportingType.setRule503b(YES);
        caseDataRule50.setRestrictedReporting(restrictedReportingType);
        ListingData listingDataPublic = listingDetails.getCaseData();
        listingDataPublic.setHearingDocETCL(HEARING_ETCL_PUBLIC);
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=EdinburghVenue, "
                + "elmoCaseReference=null, jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , "
                + "hearingEEMember= , hearingERMember= , hearingClerk=Clerk, hearingDay=2 of 3, claimantName= , "
                + "claimantTown= , claimantRepresentative= , respondent= , respondentTown= , "
                + "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 7, "
                + "respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDataPublic, caseDataRule50,
                hearingType, dateListedType, 1, 3).toString());
        ListingData listingDataPressList = listingDetails.getCaseData();
        listingDataPressList.setHearingDocETCL(HEARING_ETCL_PRESS_LIST);
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=EdinburghVenue, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Order made pursuant to Rule 50, claimantTown= , claimantRepresentative= , " +
                "respondent=Order made pursuant to Rule 50, respondentTown= , respondentRepresentative= , estHearingLength=2 hours, " +
                "hearingPanel= , hearingRoom=Tribunal 7, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDataPressList, caseDataRule50, hearingType, dateListedType, 1, 3).toString());

        dateListedType.setHearingVenueDay("ManchesterVenue");
        dateListedType.setHearingRoomKirkawall(null);
        dateListedType.setHearingRoomStranraer(null);
        dateListedType.setHearingRoomCambeltown(null);

        dateListedType.setHearingRoomM("Tribunal M");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=ManchesterVenue, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal M, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomM(null);

        dateListedType.setHearingRoomL("Tribunal L");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=ManchesterVenue, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal L, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomL(null);

        dateListedType.setHearingRoomCM("Tribunal CM");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=ManchesterVenue, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal CM, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomCM(null);

        dateListedType.setHearingRoomCC("Tribunal CC");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=ManchesterVenue, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal CC, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomCC(null);

        dateListedType.setHearingRoomCrownCourt("Tribunal Crown Court");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=ManchesterVenue, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal Crown Court, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomCrownCourt(null);

        dateListedType.setHearingRoomKendal("Tribunal Kendal");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=ManchesterVenue, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal Kendal, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomKendal(null);

        dateListedType.setHearingRoomMinshullSt("Tribunal Minshull St");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=ManchesterVenue, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal Minshull St, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomMinshullSt(null);

        dateListedType.setHearingRoomMancMagistrate("Tribunal Manc Magistrate");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=ManchesterVenue, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal Manc Magistrate, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomMancMagistrate(null);

        dateListedType.setHearingVenueDay(null);
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue= , elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom= , respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomMancMagistrate(null);
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
    public void getMatchingDateBetween() {
        String dateToSearchFrom = "2020-01-11";
        String dateToSearchTo = "2020-01-20";
        String dateToSearch = "2020-01-15";
        boolean isBetween = ListingHelper.getMatchingDateBetween(dateToSearchFrom, dateToSearchTo, dateToSearch, true);
        assertTrue(isBetween);

        dateToSearchFrom = "2020-01-11";
        dateToSearchTo = "2020-01-20";
        dateToSearch = "2020-01-25";
        isBetween = ListingHelper.getMatchingDateBetween(dateToSearchFrom, dateToSearchTo, dateToSearch, true);
        assertFalse(isBetween);

        dateToSearchFrom = "2020-01-11";
        dateToSearchTo = "";
        dateToSearch = "2020-01-11";
        boolean isEqual = ListingHelper.getMatchingDateBetween(dateToSearchFrom, dateToSearchTo, dateToSearch, false);
        assertTrue(isEqual);

        dateToSearchFrom = "2020-01-11";
        dateToSearchTo = "";
        dateToSearch = "2020-01-12";
        isEqual = ListingHelper.getMatchingDateBetween(dateToSearchFrom, dateToSearchTo, dateToSearch, false);
        assertFalse(isEqual);
    }

    @Test
    public void getListingDocName() {
        ListingData listingData = new ListingData();
        listingData.setHearingDocType(HEARING_DOC_ETCL);
        listingData.setHearingDocETCL(HEARING_ETCL_STAFF);
        listingData.setRoomOrNoRoom(NO);
        assertEquals(STAFF_CASE_CAUSE_LIST_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setRoomOrNoRoom(YES);
        assertEquals(STAFF_CASE_CAUSE_LIST_ROOM_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setHearingDocETCL(HEARING_ETCL_PUBLIC);
        listingData.setRoomOrNoRoom(NO);
        assertEquals(PUBLIC_CASE_CAUSE_LIST_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setRoomOrNoRoom(YES);
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
        listingData.setHearingDocType(BROUGHT_FORWARD_REPORT);
        assertEquals("EM-TRB-SCO-ENG-00218", ListingHelper.getListingDocName(listingData));
        listingData.setHearingDocType(CLAIMS_ACCEPTED_REPORT);
        assertEquals("EM-TRB-SCO-ENG-00219", ListingHelper.getListingDocName(listingData));
        listingData.setHearingDocType(LIVE_CASELOAD_REPORT);
        assertEquals("EM-TRB-SCO-ENG-00220", ListingHelper.getListingDocName(listingData));
        listingData.setHearingDocType(CASES_COMPLETED_REPORT);
        assertEquals("EM-TRB-SCO-ENG-00221", ListingHelper.getListingDocName(listingData));
        listingData.setHearingDocType("");
        assertEquals("No document found", ListingHelper.getListingDocName(listingData));
    }

    @Test
    public void getRespondentOthersWithLineBreaksForMultipleRespondents() {
        String expected = "Mark Taylor\\nTony Jones\\nSteve Thomas";

        String actual = ListingHelper.getRespondentOthersWithLineBreaks(listingDetails.getCaseData().getListingCollection().get(0).getValue());

        assertEquals(expected, actual);
    }

    @Test
    public void getRespondentOthersWithLineBreaksForTwoRespondents() {
        String expected = "Mark Taylor\\nTony Jones";

        String actual = ListingHelper.getRespondentOthersWithLineBreaks(listingDetails.getCaseData().getListingCollection().get(1).getValue());

        assertEquals(expected, actual);
    }

    @Test
    public void getRespondentOthersWithLineBreaksForSingleRespondent() {
        String expected = "Mark Taylor";

        String actual = ListingHelper.getRespondentOthersWithLineBreaks(listingDetails.getCaseData().getListingCollection().get(2).getValue());

        assertEquals(expected, actual);
    }

    @Test
    public void getRespondentOthersWithLineBreaksForNoRespondents() {
        String expected = "";

        String actual = ListingHelper.getRespondentOthersWithLineBreaks(listingDetails.getCaseData().getListingCollection().get(3).getValue());

        assertEquals(expected, actual);
    }
}