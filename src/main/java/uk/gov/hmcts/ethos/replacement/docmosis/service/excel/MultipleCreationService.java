package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ecm.common.model.multiples.items.CaseMultipleTypeItem;
import uk.gov.hmcts.ecm.common.model.multiples.items.SubMultipleTypeItem;
import uk.gov.hmcts.ecm.common.model.multiples.types.MultipleObjectType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.MultipleReferenceService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
@Service("multipleCreationService")
public class MultipleCreationService {

    private final ExcelDocManagementService excelDocManagementService;
    private final MultipleReferenceService multipleReferenceService;
    private final MultipleHelperService multipleHelperService;
    private final SubMultipleUpdateService subMultipleUpdateService;

    @Autowired
    public MultipleCreationService(ExcelDocManagementService excelDocManagementService,
                                   MultipleReferenceService multipleReferenceService,
                                   MultipleHelperService multipleHelperService,
                                   SubMultipleUpdateService subMultipleUpdateService) {
        this.excelDocManagementService = excelDocManagementService;
        this.multipleReferenceService = multipleReferenceService;
        this.multipleHelperService = multipleHelperService;
        this.subMultipleUpdateService = subMultipleUpdateService;
    }

    public void bulkCreationLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Add data to the multiple");

        addDataToMultiple(multipleDetails.getCaseData());

        log.info("Add state to the multiple");

        addStateToMultiple(multipleDetails.getCaseData());

        log.info("Get lead case link and add to the collection case Ids");

        getLeadMarkUpAndAddLeadToCaseIds(userToken, multipleDetails);

        if (!multipleDetails.getCaseData().getMultipleSource().equals(ET1_ONLINE_CASE_SOURCE)
                && !multipleDetails.getCaseData().getMultipleSource().equals(MIGRATION_CASE_SOURCE)) {

            log.info("Multiple Creation UI");

            multipleCreationUI(userToken, multipleDetails, errors);

        } else {

            log.info("Multiple Creation ET1Online or Migration");

            multipleCreationET1OnlineMigration(userToken, multipleDetails);

        }

        log.info("Clearing the payload");

        multipleDetails.getCaseData().setCaseIdCollection(new ArrayList<>());

    }

    private void multipleCreationUI(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        MultipleData multipleData = multipleDetails.getCaseData();

        log.info("Filter duplicated and empty caseIds");

        multipleData.setCaseIdCollection(MultiplesHelper.filterDuplicatedAndEmptyCaseIds(multipleData));

        List<String> ethosCaseRefCollection = MultiplesHelper.getCaseIds(multipleData);

        log.info("Create multiple reference number");

        multipleData.setMultipleReference(generateMultipleRef(multipleDetails));

        log.info("Create the EXCEL");

        excelDocManagementService.generateAndUploadExcel(ethosCaseRefCollection, userToken, multipleData);

        log.info("Send updates to single cases");

        sendUpdatesToSingles(userToken, multipleDetails, errors, ethosCaseRefCollection);

    }

    private void multipleCreationET1OnlineMigration(String userToken, MultipleDetails multipleDetails) {

        if (multipleDetails.getCaseData().getMultipleSource().equals(MIGRATION_CASE_SOURCE)) {

            log.info("Multiple Creation Migration Logic");

            List<MultipleObject> multipleObjectList = new ArrayList<>();

            HashSet<String> subMultipleNames = new HashSet<>();

            multipleCreationMigrationLogic(multipleDetails, multipleObjectList, subMultipleNames);

            log.info("Generating the excel document for Migration");

            excelDocManagementService.writeAndUploadExcelDocument(
                    multipleObjectList,
                    userToken,
                    multipleDetails.getCaseData(),
                    new ArrayList<>(subMultipleNames));

        } else {

            log.info("Generating the excel document for ET1 Online");

            excelDocManagementService.writeAndUploadExcelDocument(
                    MultiplesHelper.getCaseIds(multipleDetails.getCaseData()),
                    userToken,
                    multipleDetails.getCaseData(),
                    new ArrayList<>());

        }

        log.info("Resetting creation fields");

        multipleDetails.getCaseData().setCaseMultipleCollection(null);

    }

    private void multipleCreationMigrationLogic(MultipleDetails multipleDetails,
                                                           List<MultipleObject> multipleObjectList,
                                                           HashSet<String> subMultipleNames) {

        List<CaseMultipleTypeItem> caseMultipleTypeItemList = multipleDetails.getCaseData().getCaseMultipleCollection();

        List<CaseIdTypeItem> caseIdCollection = new ArrayList<>();

        HashSet<SubMultipleTypeItem> subMultipleTypeItems = new HashSet<>();

        if (caseMultipleTypeItemList != null) {

            for (CaseMultipleTypeItem caseMultipleTypeItem : caseMultipleTypeItemList) {

                log.info("Adding the new subMultiple name to the collection");

                MultipleObjectType multipleObjectType = caseMultipleTypeItem.getValue();

                if (!multipleObjectType.getSubMultiple().trim().isEmpty()
                        && !subMultipleNames.contains(multipleObjectType.getSubMultiple())) {

                    subMultipleNames.add(multipleObjectType.getSubMultiple());

                    log.info("Generating subMultiple type: " + multipleObjectType.getSubMultiple());

                    subMultipleTypeItems.add(
                            subMultipleUpdateService.createSubMultipleTypeItemWithReference(
                                    multipleDetails, multipleObjectType.getSubMultiple()));

                }

                log.info("Creating multipleObject from the collection");

                multipleObjectList.add(generateMultipleObjectFromMultipleObjectType(multipleObjectType));

                log.info("Creating a new caseTypeItem and add to the caseIdCollection");

                caseIdCollection.add(MultiplesHelper.createCaseIdTypeItem(multipleObjectType.getEthosCaseRef()));

            }

        }

        log.info("Adding the new caseIdCollection and subMultipleCollection coming from Migration");

        multipleDetails.getCaseData().setCaseIdCollection(caseIdCollection);

        multipleDetails.getCaseData().setSubMultipleCollection(new ArrayList<>(subMultipleTypeItems));

    }

    private MultipleObject generateMultipleObjectFromMultipleObjectType(MultipleObjectType multipleObjectType) {

        return MultipleObject.builder()
                .ethosCaseRef(multipleObjectType.getEthosCaseRef())
                .subMultiple(multipleObjectType.getSubMultiple())
                .flag1(multipleObjectType.getFlag1())
                .flag2(multipleObjectType.getFlag2())
                .flag3(multipleObjectType.getFlag3())
                .flag4(multipleObjectType.getFlag4())
                .build();

    }

    private String generateMultipleRef(MultipleDetails multipleDetails) {

        MultipleData multipleData = multipleDetails.getCaseData();

        if (multipleData.getMultipleReference() == null
                || multipleData.getMultipleReference().trim().equals("")) {

            log.info("Case Type: " + multipleDetails.getCaseTypeId());

            return multipleReferenceService.createReference(multipleDetails.getCaseTypeId(), 1);

        } else {

            return multipleData.getMultipleReference();

        }
    }

    private void addDataToMultiple(MultipleData multipleData) {

        if (multipleData.getMultipleSource() == null
                || multipleData.getMultipleSource().trim().equals("")) {

            multipleData.setMultipleSource(MANUALLY_CREATED_POSITION);

        }

    }

    private void addStateToMultiple(MultipleData multipleData) {

        if (!multipleData.getMultipleSource().equals(ET1_ONLINE_CASE_SOURCE)
                && !multipleData.getMultipleSource().equals(MIGRATION_CASE_SOURCE)
                && !multipleData.getCaseIdCollection().isEmpty()) {

            multipleData.setState(UPDATING_STATE);

        } else {

            multipleData.setState(OPEN_STATE);

        }
    }

    private void getLeadMarkUpAndAddLeadToCaseIds(String userToken, MultipleDetails multipleDetails) {

        MultipleData multipleData = multipleDetails.getCaseData();

        String leadCase;

        if (!isNullOrEmpty(multipleData.getLeadCase())) {

            log.info("Adding lead case introduced by user: " + multipleData.getLeadCase());

            MultiplesHelper.addLeadToCaseIds(multipleData, multipleData.getLeadCase());

            leadCase = multipleData.getLeadCase();

        } else {

            log.info("Getting lead case from the case ids collection");

            leadCase = MultiplesHelper.getLeadFromCaseIds(multipleData);

        }

        multipleHelperService.addLeadMarkUp(userToken, multipleDetails.getCaseTypeId(), multipleData, leadCase, "");

    }

    private void sendUpdatesToSingles(String userToken, MultipleDetails multipleDetails,
                                      List<String> errors, List<String> ethosCaseRefCollection) {

        log.info("Ethos case ref collection: " + ethosCaseRefCollection);

        if (!ethosCaseRefCollection.isEmpty()) {

            multipleHelperService.sendCreationUpdatesToSinglesWithConfirmation(userToken,
                    multipleDetails, ethosCaseRefCollection, errors);

        } else {

            log.info("Empty case ref collection");

        }
    }

}
