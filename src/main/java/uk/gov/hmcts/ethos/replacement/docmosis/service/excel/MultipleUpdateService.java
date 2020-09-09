package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.PersistentQHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.service.UserService;
import uk.gov.hmcts.ethos.replacement.docmosis.servicebus.CreateUpdatesBusSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.OPEN_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.UPDATING_STATE;

@Slf4j
@Service("multipleUpdateService")
public class MultipleUpdateService {

    private final CreateUpdatesBusSender createUpdatesBusSender;
    private final UserService userService;
    private final ExcelReadingService excelReadingService;

    @Autowired
    public MultipleUpdateService(CreateUpdatesBusSender createUpdatesBusSender,
                                 UserService userService,
                                 ExcelReadingService excelReadingService) {
        this.createUpdatesBusSender = createUpdatesBusSender;
        this.userService = userService;
        this.excelReadingService = excelReadingService;
    }

    public void bulkUpdateLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Read excel to update logic");

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleDetails),
                        errors,
                        multipleDetails.getCaseData(),
                        FilterExcelType.FLAGS);

        log.info("MultipleObjectsKeySet: " + multipleObjects.keySet());
        log.info("MultipleObjectsValues: " + multipleObjects.values());

        log.info("Add state to the multiple");

        addStateToMultiple(multipleDetails.getCaseData(), multipleObjects.keySet());

        log.info("Send updates to single cases");

        sendUpdatesToSingles(userToken, multipleDetails, errors, multipleObjects);

        log.info("Resetting mid fields");

        MultiplesHelper.resetMidFields(multipleDetails.getCaseData());

    }

    private void sendUpdatesToSingles(String userToken, MultipleDetails multipleDetails,
                                      List<String> errors, TreeMap<String, Object> multipleObjects) {

        List<String> multipleObjectsFiltered = new ArrayList<>(multipleObjects.keySet());
        MultipleData multipleData = multipleDetails.getCaseData();
        String username = userService.getUserDetails(userToken).getEmail();

        PersistentQHelper.sendSingleUpdatesPersistentQ(multipleDetails,
                username,
                multipleObjectsFiltered,
                PersistentQHelper.getUpdateDataModel(multipleDetails.getCaseData()),
                errors,
                multipleData.getMultipleReference(),
                createUpdatesBusSender,
                String.valueOf(multipleObjectsFiltered.size()));

    }

    private void addStateToMultiple(MultipleData multipleData, Set<String> caseFiltered) {

        if (!caseFiltered.isEmpty()) {

            multipleData.setState(UPDATING_STATE);

        } else {

            multipleData.setState(OPEN_STATE);

        }
    }
}
