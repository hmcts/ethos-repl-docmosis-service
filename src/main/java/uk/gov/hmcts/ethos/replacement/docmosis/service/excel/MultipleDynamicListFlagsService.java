package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper.SELECT_ALL;

@Slf4j
@Service("multipleDynamicListFlagsService")
public class MultipleDynamicListFlagsService {

    private final ExcelReadingService excelReadingService;

    @Autowired
    public MultipleDynamicListFlagsService(ExcelReadingService excelReadingService) {
        this.excelReadingService = excelReadingService;
    }

    public void populateDynamicListFlagsLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Read excel to populate dynamic list flags logic");

        TreeMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleDetails),
                        errors,
                        multipleDetails.getCaseData(),
                        FilterExcelType.DL_FLAGS);

        log.info("MultipleObjectsKeySet: " + multipleObjects.keySet());
        log.info("MultipleObjectsValues: " + multipleObjects.values());

        log.info("Populates the dynamic list with flags from Excel");

        populateDynamicList(multipleDetails.getCaseData().getFlag1(), getDynamicList(multipleObjects, HEADER_3));
        populateDynamicList(multipleDetails.getCaseData().getFlag2(), getDynamicList(multipleObjects, HEADER_4));
        populateDynamicList(multipleDetails.getCaseData().getFlag3(), getDynamicList(multipleObjects, HEADER_5));
        populateDynamicList(multipleDetails.getCaseData().getFlag4(), getDynamicList(multipleObjects, HEADER_6));

        log.info("FLAG1 : " + multipleDetails.getCaseData().getFlag1());

    }

    private List<DynamicValueType> getDynamicList(TreeMap<String, Object> multipleObjects, String key) {

        Set<String> values = (Set<String>) multipleObjects.get(key);
        List<DynamicValueType> listItems = new ArrayList<>();

        if (values != null && !values.isEmpty()) {

            for (String flag : values) {

                if (!flag.isEmpty()) {
                    DynamicValueType dynamicValueType = new DynamicValueType();
                    dynamicValueType.setCode(flag);
                    dynamicValueType.setLabel(flag);
                    listItems.add(dynamicValueType);
                }
            }
        }

        return listItems;
    }

    private void populateDynamicList(DynamicFixedListType dynamicListFlag, List<DynamicValueType> listItems) {

        if (dynamicListFlag != null) {
            dynamicListFlag.setListItems(listItems);

        } else {

            DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
            dynamicFixedListType.setListItems(listItems);
            dynamicListFlag = dynamicFixedListType;
        }

        //Default dynamic list
        DynamicValueType dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode(SELECT_ALL);
        dynamicValueType.setLabel(SELECT_ALL);
        dynamicListFlag.setValue(dynamicValueType);

    }

}
