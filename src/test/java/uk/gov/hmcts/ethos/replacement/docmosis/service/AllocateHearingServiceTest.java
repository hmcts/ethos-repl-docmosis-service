package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.Document;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

class AllocateHearingServiceTest {

    private AllocateHearingService allocateHearingService;
    private CaseData caseData;

    @BeforeEach
    void setUp() {
        allocateHearingService = new AllocateHearingService();
        caseData = new CaseData();
    }

    @Test
    void initialiseAllocateHearing_shouldPopulateHearingSelectionList() {
        // Given
        HearingTypeItem hearing1 = createHearing("1", "Manchester", "2024-01-15T10:00:00.000");
        HearingTypeItem hearing2 = createHearing("2", "Leeds", "2024-02-20T14:00:00.000");
        caseData.setHearingCollection(List.of(hearing1, hearing2));

        // When
        allocateHearingService.initialiseAllocateHearing(caseData);

        // Then
        DynamicFixedListType hearingList = caseData.getSelectedHearingNumberForUpdate();
        assertNotNull(hearingList);
        assertEquals(2, hearingList.getListItems().size());
        assertEquals("1", hearingList.getListItems().getFirst().getCode());
        assertEquals("1 : Hearing - Manchester - 15 Jan 2024", hearingList.getListItems().getFirst().getLabel());
        assertEquals("2", hearingList.getListItems().get(1).getCode());
        assertEquals("2 : Hearing - Leeds - 20 Feb 2024", hearingList.getListItems().get(1).getLabel());
    }

    @Test
    void populateHearingDetails_shouldThrowExceptionWhenNoHearingSelected() {
        // Given
        caseData.setSelectedHearingNumberForUpdate(null);

        // When & Then
        Exception exception = assertThrows(NullPointerException.class, () ->
            allocateHearingService.populateHearingDetails(caseData)
        );
        assertEquals("selectedHearingNumberForUpdate cannot be null for case null", exception.getMessage());
    }

    @Test
    void populateHearingDetails_shouldPopulateSelectedHearing() {
        // Given
        HearingTypeItem hearing1 = createHearing("1", "Manchester", "2024-01-15T10:00:00.000");
        HearingTypeItem hearing2 = createHearing("2", "Leeds", "2024-02-20T14:00:00.000");
        caseData.setHearingCollection(List.of(hearing1, hearing2));

        DynamicFixedListType selectedHearing = new DynamicFixedListType();
        DynamicValueType dynamicValue = new DynamicValueType();
        dynamicValue.setCode("2");
        dynamicValue.setLabel("Hearing 2");
        selectedHearing.setValue(dynamicValue);
        caseData.setSelectedHearingNumberForUpdate(selectedHearing);

        // When
        allocateHearingService.populateHearingDetails(caseData);

        // Then
        assertNotNull(caseData.getHearingsCollectionForUpdate());
        assertEquals(1, caseData.getHearingsCollectionForUpdate().size());
        assertEquals(hearing2, caseData.getHearingsCollectionForUpdate().getFirst());
        assertNull(caseData.getHearingsCollectionForUpdate().getFirst().getValue().getDoesHearingNotesDocExist());
    }

    @Test
    void populateHearingDetails_shouldSetFlagWhenHearingNotesExist() {
        // Given
        HearingTypeItem hearing1 = createHearing("1", "Manchester", "2024-01-15T10:00:00.000");
        HearingTypeItem hearing2 = createHearing("2", "Leeds", "2024-02-20T14:00:00.000");
        hearing2.getValue().setHearingNotesDocument(new Document());
        caseData.setHearingCollection(List.of(hearing1, hearing2));

        DynamicFixedListType selectedHearing = new DynamicFixedListType();
        DynamicValueType dynamicValue = new DynamicValueType();
        dynamicValue.setCode("2");
        dynamicValue.setLabel("Hearing 2");
        selectedHearing.setValue(dynamicValue);
        caseData.setSelectedHearingNumberForUpdate(selectedHearing);

        // When
        allocateHearingService.populateHearingDetails(caseData);

        // Then
        assertNotNull(caseData.getHearingsCollectionForUpdate());
        assertEquals(1, caseData.getHearingsCollectionForUpdate().size());
        assertEquals(hearing2, caseData.getHearingsCollectionForUpdate().getFirst());
        assertEquals(YES, caseData.getHearingsCollectionForUpdate().getFirst().getValue()
            .getDoesHearingNotesDocExist());
    }

    @Test
    void populateHearingDetails_shouldHandleNoMatchingHearing() {
        // Given
        HearingTypeItem hearing1 = createHearing("1", "Manchester", "2024-01-15T10:00:00.000");
        caseData.setHearingCollection(List.of(hearing1));

        DynamicFixedListType selectedHearing = new DynamicFixedListType();
        DynamicValueType dynamicValue = new DynamicValueType();
        dynamicValue.setCode("99");
        dynamicValue.setLabel("Non-existent Hearing");
        selectedHearing.setValue(dynamicValue);
        caseData.setSelectedHearingNumberForUpdate(selectedHearing);

        // When
        allocateHearingService.populateHearingDetails(caseData);

        // Then,
        // When no matching hearing is found, the collection is not set (remains null or empty depending on
        // CaseData initialization)
        // We check that either it's null or it's empty
        var updateCollection = caseData.getHearingsCollectionForUpdate();
        if (updateCollection != null) {
            assertEquals(0, updateCollection.size());
        }
    }

    private HearingTypeItem createHearing(String number, String venue, String listedDate) {
        HearingType hearingType = new HearingType();
        hearingType.setHearingNumber(number);
        hearingType.setHearingType("Hearing");
        hearingType.setHearingVenue(venue);
        hearingType.setHearingSitAlone("Sit Alone");
        hearingType.setHearingEstLengthNum("1");
        hearingType.setHearingEstLengthNumType("Days");

        // Create a hearing date - required by DynamicListHelper
        DateListedType dateListedType = new DateListedType();
        dateListedType.setListedDate(listedDate);
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        dateListedTypeItem.setValue(dateListedType);
        hearingType.setHearingDateCollection(List.of(dateListedTypeItem));

        HearingTypeItem hearingTypeItem = new HearingTypeItem();
        hearingTypeItem.setValue(hearingType);
        hearingTypeItem.setId(number);
        return hearingTypeItem;
    }
}