package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseRetrievalException;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.reference.ReferenceSubmitEvent;
import uk.gov.hmcts.ecm.common.model.reference.types.ClerkType;
import uk.gov.hmcts.ecm.common.model.reference.types.JudgeType;
import uk.gov.hmcts.ecm.common.model.reference.types.VenueType;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("referenceService")
public class ReferenceService {

    private static final String MESSAGE = "Failed to retrieve reference data for case id : ";
    public static final String GOTHAM_REF_DATA_CASE_TYPE_ID = "Gotham_RefData";

    private final CcdClient ccdClient;

    @Autowired
    public ReferenceService(CcdClient ccdClient) {
        this.ccdClient = ccdClient;
    }

    public CaseData fetchHearingVenueRefData(CaseDetails caseDetails, String authToken) {
        CaseData caseData = caseDetails.getCaseData();
        try {
            List<ReferenceSubmitEvent> referenceSubmitEvents = ccdClient.retrieveReferenceDataCases(authToken, GOTHAM_REF_DATA_CASE_TYPE_ID, caseDetails.getJurisdiction());
            if (referenceSubmitEvents != null) {
                log.info("Cases searched: " + referenceSubmitEvents.size());
                List<DynamicValueType> venuesListItems = createDynamicVenueNameFixedList(referenceSubmitEvents);
                bindDynamicVenueNameFixedList(caseData, venuesListItems);
            }
            return caseData;
        } catch (Exception ex) {
            throw new CaseRetrievalException(MESSAGE + caseDetails.getCaseId() + ex.getMessage());
        }
    }

    public CaseData fetchDateListedRefData(CaseDetails caseDetails, String authToken) {
        CaseData caseData = caseDetails.getCaseData();
        try {
            List<ReferenceSubmitEvent> referenceSubmitEvents = ccdClient.retrieveReferenceDataCases(authToken, GOTHAM_REF_DATA_CASE_TYPE_ID, caseDetails.getJurisdiction());
            if (referenceSubmitEvents != null) {
                log.info("Cases searched: " + referenceSubmitEvents.size());

                List<DynamicValueType> venuesListItems = createDynamicVenueNameFixedList(referenceSubmitEvents);
                List<DynamicValueType> clerksListItems = createDynamicClerkNameFixedList(referenceSubmitEvents);
                List<DynamicValueType> judgesListItems = createDynamicJudgeNameFixedList(referenceSubmitEvents);

//                if (!venuesListItems.isEmpty()) { caseData.setHearingVenue(bindDynamicGenericFixedList(venuesListItems)); }
//                if (!clerksListItems.isEmpty()) { caseData.setHearingClerk(bindDynamicGenericFixedList(clerksListItems)); }
//                if (!judgesListItems.isEmpty()) { caseData.setHearingJudge(bindDynamicGenericFixedList(judgesListItems)); }

                // temp store venue name in room name
                if (!venuesListItems.isEmpty()) { caseData.setHearingRoom(bindDynamicGenericFixedList(venuesListItems)); }
                // .......................................................................................................

                bindDynamicVenueNameFixedList(caseData, venuesListItems);
                bindDynamicClerkNameFixedList(caseData, clerksListItems);
                bindDynamicJudgeNameFixedList(caseData, judgesListItems);
            }
            return caseData;
        } catch (Exception ex) {
            throw new CaseRetrievalException(MESSAGE + caseDetails.getCaseId() + ex.getMessage());
        }
    }

    private List<DynamicValueType> createDynamicVenueNameFixedList(List<ReferenceSubmitEvent> referenceSubmitEvents) {
        List<DynamicValueType> listItems = new ArrayList<>();
        for (ReferenceSubmitEvent referenceSubmitEvent : referenceSubmitEvents) {
            if (referenceSubmitEvent.getCaseData().getVenueType() != null) {
                VenueType venueType = referenceSubmitEvent.getCaseData().getVenueType();
                DynamicValueType dynamicValueType = new DynamicValueType();
                dynamicValueType.setCode(venueType.getVenueName());
                dynamicValueType.setLabel(venueType.getVenueName());
                listItems.add(dynamicValueType);
            }
        }
        return listItems;
    }

    private List<DynamicValueType> createDynamicClerkNameFixedList(List<ReferenceSubmitEvent> referenceSubmitEvents) {
        List<DynamicValueType> listItems = new ArrayList<>();
        for (ReferenceSubmitEvent referenceSubmitEvent : referenceSubmitEvents) {
            if (referenceSubmitEvent.getCaseData().getClerkType() != null) {
                ClerkType clerkType = referenceSubmitEvent.getCaseData().getClerkType();
                DynamicValueType dynamicValueType = new DynamicValueType();
                dynamicValueType.setCode(clerkType.getFirstName() + " " + clerkType.getLastName());
                dynamicValueType.setLabel(clerkType.getFirstName() + " " + clerkType.getLastName());
                listItems.add(dynamicValueType);
            }
        }
        return listItems;
    }

    private List<DynamicValueType> createDynamicJudgeNameFixedList(List<ReferenceSubmitEvent> referenceSubmitEvents) {
        List<DynamicValueType> listItems = new ArrayList<>();
        for (ReferenceSubmitEvent referenceSubmitEvent : referenceSubmitEvents) {
            if (referenceSubmitEvent.getCaseData().getJudgeType() != null) {
                JudgeType judgeType = referenceSubmitEvent.getCaseData().getJudgeType();
                DynamicValueType dynamicValueType = new DynamicValueType();
                dynamicValueType.setCode(judgeType.getJudgeDisplayName());
                dynamicValueType.setLabel(judgeType.getJudgeDisplayName());
                listItems.add(dynamicValueType);
            }
        }
        return listItems;
    }

    private DynamicFixedListType bindDynamicGenericFixedList(List<DynamicValueType> listItems) {
        DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
        dynamicFixedListType.setValue(listItems.get(0));
        dynamicFixedListType.setListItems(listItems);
        return dynamicFixedListType;
    }

    private void bindDynamicVenueNameFixedList(CaseData caseData, List<DynamicValueType> listItems) {
        if (!listItems.isEmpty()) {
            if (caseData.getHearingVenue() != null) {
                caseData.getHearingVenue().setListItems(listItems);
            } else {
                DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
                dynamicFixedListType.setValue(listItems.get(0));
                dynamicFixedListType.setListItems(listItems);
                caseData.setHearingVenue(dynamicFixedListType);
            }
        }
    }

    private void bindDynamicClerkNameFixedList(CaseData caseData, List<DynamicValueType> listItems) {
        if (!listItems.isEmpty()) {
            if (caseData.getHearingClerk() != null) {
                caseData.getHearingClerk().setListItems(listItems);
            } else {
                DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
                dynamicFixedListType.setValue(listItems.get(0));
                dynamicFixedListType.setListItems(listItems);
                caseData.setHearingClerk(dynamicFixedListType);
            }
        }
    }

    private void bindDynamicJudgeNameFixedList(CaseData caseData, List<DynamicValueType> listItems) {
        if (!listItems.isEmpty()) {
            if (caseData.getHearingJudge() != null) {
                caseData.getHearingJudge().setListItems(listItems);
            } else {
                DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
                dynamicFixedListType.setValue(listItems.get(0));
                dynamicFixedListType.setListItems(listItems);
                caseData.setHearingJudge(dynamicFixedListType);
            }
        }
    }

    public CaseData fetchHearingJudgeNameRefDataForComplexType(CaseDetails caseDetails, String authToken) {
        CaseData caseData = caseDetails.getCaseData();
        try {
            List<ReferenceSubmitEvent> referenceSubmitEvents = ccdClient.retrieveReferenceDataCases(authToken, GOTHAM_REF_DATA_CASE_TYPE_ID, caseDetails.getJurisdiction());
            if (referenceSubmitEvents != null) {
                log.info("Cases searched: " + referenceSubmitEvents.size());
                List<DynamicValueType> listItems = createDynamicJudgeNameFixedList(referenceSubmitEvents);
                if (!listItems.isEmpty()) {
                    if (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty()) {
                        List<HearingTypeItem> hearingCollection =  caseData.getHearingCollection();
                        for (HearingTypeItem hearingTypeItem : hearingCollection) {
                            HearingType hearingType = hearingTypeItem.getValue();
                            if (hearingType.getHearingDateCollection() != null && !hearingType.getHearingDateCollection().isEmpty()) {
                                List<DateListedTypeItem> hearingDateCollection = hearingType.getHearingDateCollection();
                                for (DateListedTypeItem dateListedTypeItem : hearingDateCollection) {
                                    bindDynamicJudgeNameFixedListForComplexType(dateListedTypeItem.getValue(), listItems);
                                }
                            }
                        }
                    }
                }
            }
            return caseData;
        } catch (Exception ex) {
            throw new CaseRetrievalException(MESSAGE + caseDetails.getCaseId() + ex.getMessage());
        }
    }

    private void bindDynamicJudgeNameFixedListForComplexType(DateListedType dateListedType, List<DynamicValueType> listItems) {
        if (dateListedType.getHearingJudgeName() != null) {
            //dateListedType.getHearingJudgeName().setListItems(listItems);
        } else {
            DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
            dynamicFixedListType.setValue(listItems.get(0));
            dynamicFixedListType.setListItems(listItems);
            //dateListedType.setHearingJudgeName(dynamicFixedListType);
        }
        //Default dynamic list
        //dateListedType.getHearingJudgeName().setValue(listItems.get(3));
    }

}