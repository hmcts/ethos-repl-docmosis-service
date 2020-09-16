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

        log.info("Create multiple reference number");

        multipleDetails.getCaseData().setMultipleReference(generateMultipleRef(multipleDetails));

        log.info("Add data to the multiple");

        addDataToMultiple(multipleDetails.getCaseData());

        log.info("Add state to the multiple");

        addStateToMultiple(multipleDetails.getCaseData());

        log.info("Get lead case markUp and add to the collection case Ids");

        getLeadMarkUpAndAddLeadToCaseIds(userToken, multipleDetails);

        List<String> ethosCaseRefCollection = MultiplesHelper.getCaseIds(multipleDetails.getCaseData());

        log.info("Create the EXCEL");

        excelDocManagementService.generateAndUploadExcel(ethosCaseRefCollection, userToken, multipleDetails.getCaseData());

        if (!multipleDetails.getCaseData().getMultipleSource().equals(ET1_ONLINE_CASE_SOURCE)) {

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

        if (!isNullOrEmpty(multipleData.getLeadCase())) {

            MultiplesHelper.addLeadToCaseIds(multipleData, multipleData.getLeadCase());

            multipleHelperService.addLeadMarkUp(userToken, multipleDetails.getCaseTypeId(), multipleData, multipleData.getLeadCase());

        }
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
                    createUpdatesBusSender,
                    String.valueOf(ethosCaseRefCollection.size()));

        } else {

            log.info("Empty case ref collection");

        }
    }

}
