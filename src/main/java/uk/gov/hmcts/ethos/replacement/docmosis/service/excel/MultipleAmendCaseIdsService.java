package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleObject;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service("multipleAmendCaseIdsService")
public class MultipleAmendCaseIdsService {

    private final MultipleHelperService multipleHelperService;

    @Autowired
    public MultipleAmendCaseIdsService(MultipleHelperService multipleHelperService) {
        this.multipleHelperService = multipleHelperService;
    }

    public List<MultipleObject> bulkAmendCaseIdsLogic(String userToken, MultipleDetails multipleDetails,
                                                      List<String> errors, TreeMap<String, Object> multipleObjects) {

        List<String> newEthosCaseRefCollection = MultiplesHelper.getCaseIds(multipleDetails.getCaseData());

        log.info("Calculate union new and old cases");

        List<String> unionLists = concatNewAndOldCases(multipleObjects, newEthosCaseRefCollection);

        if (!newEthosCaseRefCollection.isEmpty()) {

            log.info("Send updates to single cases");

            multipleHelperService.sendUpdatesToSinglesLogic(userToken, multipleDetails, errors, unionLists.get(0),
                    multipleObjects, newEthosCaseRefCollection);

        }

        log.info("Create a new Excel");

        return generateMultipleObjects(unionLists, multipleObjects);

    }

    private List<String> concatNewAndOldCases(TreeMap<String, Object> multipleObjects, List<String> newEthosCaseRefCollection) {

        log.info("EthosCaseRefCollection: " + newEthosCaseRefCollection);

        return Stream.concat(newEthosCaseRefCollection.stream(), multipleObjects.keySet().stream())
                .distinct().collect(Collectors.toList());

    }

    private List<MultipleObject> generateMultipleObjects(List<String> unionLists, TreeMap<String, Object> multipleObjects) {

        List<MultipleObject> multipleObjectList = new ArrayList<>();

        for (String ethosCaseRef : unionLists) {

                MultipleObject multipleObject;

                if (multipleObjects.containsKey(ethosCaseRef)) {

                    multipleObject = (MultipleObject)multipleObjects.get(ethosCaseRef);

                } else {

                    multipleObject = MultiplesHelper.createMultipleObject(ethosCaseRef, "");

                }

                multipleObjectList.add(multipleObject);

        }

        return multipleObjectList;
    }

}
