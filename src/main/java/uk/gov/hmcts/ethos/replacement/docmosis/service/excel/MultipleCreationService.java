package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ET1_ONLINE_CASE_SOURCE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANUALLY_CREATED_POSITION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIGRATION_CASE_SOURCE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OPEN_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

@Slf4j
@RequiredArgsConstructor
@Service("multipleCreationService")
public class MultipleCreationService {

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    private final ExcelDocManagementService excelDocManagementService;
    private final MultipleReferenceService multipleReferenceService;
    private final MultipleHelperService multipleHelperService;
    private final SubMultipleUpdateService subMultipleUpdateService;
    private final MultipleTransferService multipleTransferService;

    public void bulkCreationLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Add data to the multiple");

        addDataToMultiple(multipleDetails.getCaseData());

        log.info("Add state to the multiple");

        multipleDetails.getCaseData().setState(OPEN_STATE);

        log.info("Check if creation is coming from Case Transfer");
        multipleTransferService.populateDataIfComingFromCT(userToken, multipleDetails, errors);

        log.info("Get lead case link and add to the collection case Ids");
        getLeadMarkUpAndAddLeadToCaseIds(userToken, multipleDetails);

        if (!multipleDetails.getCaseData().getMultipleSource().equals(ET1_ONLINE_CASE_SOURCE)
                && !multipleDetails.getCaseData().getMultipleSource().equals(MIGRATION_CASE_SOURCE)) {

            log.info("Multiple Creation UI");
            multipleCreationUI(userToken, multipleDetails, errors);

        } else {

            multipleCreationET1OnlineMigration(userToken, multipleDetails);

        }

        log.info("Clearing the payload");

        clearingMultipleCreationPayload(multipleDetails);

    }

    private void multipleCreationUI(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        var multipleData = multipleDetails.getCaseData();

        multipleData.setPreAcceptDone(YES);

        log.info("Filter duplicated and empty caseIds");

        multipleData.setCaseIdCollection(MultiplesHelper.filterDuplicatedAndEmptyCaseIds(multipleData));

        log.info("Create multiple reference number");

        multipleData.setMultipleReference(generateMultipleRef(multipleDetails));

        log.info("Create the EXCEL");

        List<String> ethosCaseRefCollection = MultiplesHelper.getCaseIds(multipleData);

        excelDocManagementService.generateAndUploadExcel(ethosCaseRefCollection, userToken, multipleDetails);

        log.info("Send updates to single cases");

        sendUpdatesToSingles(userToken, multipleDetails, errors, ethosCaseRefCollection);

    }

    private void multipleCreationET1OnlineMigration(String userToken, MultipleDetails multipleDetails) {

        if (multipleDetails.getCaseData().getMultipleSource().equals(MIGRATION_CASE_SOURCE)) {

            log.info("Multiple Creation Migration Logic");

            if (multipleDetails.getCaseData().getPreAcceptDone() == null) {
                multipleDetails.getCaseData().setPreAcceptDone(YES);
            }

            List<MultipleObject> multipleObjectList = new ArrayList<>();

            HashSet<String> subMultipleNames = new HashSet<>();

            multipleCreationMigrationLogic(multipleDetails, multipleObjectList, subMultipleNames);

            log.info("Generating the excel document for Migration");

            excelDocManagementService.writeAndUploadExcelDocument(
                    multipleObjectList,
                    userToken,
                    multipleDetails,
                    new ArrayList<>(subMultipleNames));

        } else {
            if (multipleDetails.getCaseData().getPreAcceptDone() == null) {
                multipleDetails.getCaseData().setPreAcceptDone(NO);
            }

            log.info("Generating the excel document for ET1 Online");

            excelDocManagementService.writeAndUploadExcelDocument(
                    MultiplesHelper.getCaseIds(multipleDetails.getCaseData()),
                    userToken,
                    multipleDetails,
                    new ArrayList<>());

        }

        log.info("Resetting creation fields");

        multipleDetails.getCaseData().setCaseMultipleCollection(null);

    }

    private void multipleCreationMigrationLogic(MultipleDetails multipleDetails,
                                                           List<MultipleObject> multipleObjectList,
                                                           HashSet<String> subMultipleNames) {

        List<CaseMultipleTypeItem> caseMultipleTypeItemList = multipleDetails.getCaseData().getCaseMultipleCollection();

        HashSet<SubMultipleTypeItem> subMultipleTypeItems = new HashSet<>();

        if (caseMultipleTypeItemList != null) {

            for (CaseMultipleTypeItem caseMultipleTypeItem : caseMultipleTypeItemList) {

                var multipleObjectType = caseMultipleTypeItem.getValue();

                if (multipleObjectType.getSubMultiple() != null
                        && !multipleObjectType.getSubMultiple().trim().isEmpty()
                        && !subMultipleNames.contains(multipleObjectType.getSubMultiple())) {

                    subMultipleNames.add(multipleObjectType.getSubMultiple());

                    log.info("Generating subMultiple type: " + multipleObjectType.getSubMultiple());

                    subMultipleTypeItems.add(
                            subMultipleUpdateService.createSubMultipleTypeItemWithReference(
                                    multipleDetails, multipleObjectType.getSubMultiple()));

                }

                multipleObjectList.add(generateMultipleObjectFromMultipleObjectType(multipleObjectType));

            }

        }

        log.info("Adding the subMultipleCollection coming from Migration");

        multipleDetails.getCaseData().setSubMultipleCollection(new ArrayList<>(subMultipleTypeItems));

    }

    private MultipleObject generateMultipleObjectFromMultipleObjectType(MultipleObjectType multipleObjectType) {

        return MultipleObject.builder()
                .ethosCaseRef(multipleObjectType.getEthosCaseRef())
                .subMultiple(multipleObjectType.getSubMultiple() != null ? multipleObjectType.getSubMultiple() : "")
                .flag1(multipleObjectType.getFlag1() != null ? multipleObjectType.getFlag1() : "")
                .flag2(multipleObjectType.getFlag2() != null ? multipleObjectType.getFlag2() : "")
                .flag3(multipleObjectType.getFlag3() != null ? multipleObjectType.getFlag3() : "")
                .flag4(multipleObjectType.getFlag4() != null ? multipleObjectType.getFlag4() : "")
                .build();

    }

    private String generateMultipleRef(MultipleDetails multipleDetails) {

        var multipleData = multipleDetails.getCaseData();

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

    private void getLeadMarkUpAndAddLeadToCaseIds(String userToken, MultipleDetails multipleDetails) {

        var multipleData = multipleDetails.getCaseData();

        String leadCase;

        if (!isNullOrEmpty(multipleData.getLeadCase())) {

            log.info("Pre-existing Lead case: Adding lead case introduced by user: " + multipleData.getLeadCase());

            MultiplesHelper.addLeadToCaseIds(multipleData, multipleData.getLeadCase());

            leadCase = multipleData.getLeadCase();

        } else {

            if (multipleDetails.getCaseData().getMultipleSource().equals(MIGRATION_CASE_SOURCE)) {

                log.info("No Lead case - Migration Source: Getting lead case from caseMultipleCollection");

                leadCase = MultiplesHelper.getLeadFromCaseMultipleCollection(multipleData);

            } else {

                log.info("No Lead case - to set New : Getting lead case from the case ids collection");

                leadCase = MultiplesHelper.getLeadFromCaseIds(multipleData);

            }

        }

        multipleHelperService.addLeadMarkUp(userToken, multipleDetails.getCaseTypeId(),
                multipleData, leadCase, "");

    }

    private void sendUpdatesToSingles(String userToken, MultipleDetails multipleDetails,
                                      List<String> errors, List<String> ethosCaseRefCollection) {

        log.info("Ethos case ref collection: " + ethosCaseRefCollection);

        var refMarkup = MultiplesHelper.generateMarkUp(ccdGatewayBaseUrl, multipleDetails.getCaseId(),
                multipleDetails.getCaseData().getMultipleReference());

        if (!ethosCaseRefCollection.isEmpty()) {

            multipleHelperService.sendCreationUpdatesToSinglesWithoutConfirmation(userToken,
                    multipleDetails.getCaseTypeId(),
                    multipleDetails.getJurisdiction(),
                    multipleDetails.getCaseData(),
                    errors,
                    ethosCaseRefCollection,
                    ethosCaseRefCollection.get(0),
                    refMarkup);

        } else {

            log.info("Empty case ref collection");

        }
    }

    private void clearingMultipleCreationPayload(MultipleDetails multipleDetails) {

        multipleDetails.getCaseData().setCaseIdCollection(null);

        if (multipleDetails.getCaseData().getMultipleSource().equals(MIGRATION_CASE_SOURCE)
                && multipleDetails.getCaseData().getLinkedMultipleCT() != null) {

            multipleDetails.getCaseData().setMultipleSource(MANUALLY_CREATED_POSITION);

        }

    }

}
