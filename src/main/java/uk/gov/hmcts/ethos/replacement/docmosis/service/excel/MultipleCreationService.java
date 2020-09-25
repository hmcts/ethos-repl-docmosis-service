package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.PersistentQHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.MultipleReferenceService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
@Service("multipleCreationService")
public class MultipleCreationService {

    private final CreateUpdatesBusSender createUpdatesBusSender;
    private final UserService userService;
    private final ExcelDocManagementService excelDocManagementService;
    private final MultipleReferenceService multipleReferenceService;
    private final MultipleHelperService multipleHelperService;

    @Autowired
    public MultipleCreationService(CreateUpdatesBusSender createUpdatesBusSender,
                                   UserService userService,
                                   ExcelDocManagementService excelDocManagementService,
                                   MultipleReferenceService multipleReferenceService,
                                   MultipleHelperService multipleHelperService) {
        this.createUpdatesBusSender = createUpdatesBusSender;
        this.userService = userService;
        this.excelDocManagementService = excelDocManagementService;
        this.multipleReferenceService = multipleReferenceService;
        this.multipleHelperService = multipleHelperService;
    }

    public void bulkCreationLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        MultipleData multipleData = multipleDetails.getCaseData();

        log.info("Add data to the multiple");

        addDataToMultiple(multipleData);

        log.info("Add state to the multiple");

        addStateToMultiple(multipleData);

        log.info("Get lead case link and add to the collection case Ids");

        getLeadMarkUpAndAddLeadToCaseIds(userToken, multipleDetails);

        log.info("Filter duplicated and empty caseIds");

        multipleData.setCaseIdCollection(MultiplesHelper.filterDuplicatedAndEmptyCaseIds(multipleData));

        List<String> ethosCaseRefCollection = MultiplesHelper.getCaseIds(multipleData);

        log.info("Create the EXCEL");

        excelDocManagementService.generateAndUploadExcel(ethosCaseRefCollection, userToken, multipleData);

        log.info("Create multiple reference number");

        multipleData.setMultipleReference(generateMultipleRef(multipleDetails));

        if (!multipleData.getMultipleSource().equals(ET1_ONLINE_CASE_SOURCE)) {

            log.info("Send updates to single cases");

            sendUpdatesToSingles(userToken, multipleDetails, errors, ethosCaseRefCollection);

        }
    }

    private String generateMultipleRef(MultipleDetails multipleDetails) {

        MultipleData multipleData = multipleDetails.getCaseData();

        if (multipleData.getMultipleReference() == null
                || multipleData.getMultipleReference().trim().equals("")) {

            log.info("Case Type: " + multipleDetails.getCaseTypeId());

            return multipleReferenceService.createReference(multipleDetails.getCaseTypeId()+"s", 1);

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

        MultipleData multipleData = multipleDetails.getCaseData();

        log.info("Ethos case ref collection: " + ethosCaseRefCollection);

        if (!ethosCaseRefCollection.isEmpty()) {

            String username = userService.getUserDetails(userToken).getEmail();
            PersistentQHelper.sendSingleUpdatesPersistentQ(multipleDetails.getCaseTypeId(),
                    multipleDetails.getJurisdiction(),
                    username,
                    ethosCaseRefCollection,
                    PersistentQHelper.getCreationDataModel(ethosCaseRefCollection.get(0),
                            multipleData.getMultipleReference()),
                    errors,
                    multipleData.getMultipleReference(),
                    YES,
                    createUpdatesBusSender,
                    String.valueOf(ethosCaseRefCollection.size()));

        } else {

            log.info("Empty case ref collection");

        }
    }

}
