package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ecm.common.model.multiples.types.MoveCasesType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.FilterExcelType;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.HEADER_2;
import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.HEADER_3;
import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.HEADER_4;
import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.HEADER_5;
import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.HEADER_6;
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

        var multipleData = multipleDetails.getCaseData();

        SortedMap<String, Object> multipleObjects =
                excelReadingService.readExcel(
                        userToken,
                        MultiplesHelper.getExcelBinaryUrl(multipleData),
                        errors,
                        multipleData,
                        FilterExcelType.DL_FLAGS);

        log.info("Populates the dynamic list with flags from Excel");

        multipleData.setSubMultiple(populateDynamicList(multipleData.getSubMultiple(),
                getDynamicList(multipleObjects, HEADER_2)));
        multipleData.setFlag1(populateDynamicList(multipleData.getFlag1(), getDynamicList(multipleObjects, HEADER_3)));
        multipleData.setFlag2(populateDynamicList(multipleData.getFlag2(), getDynamicList(multipleObjects, HEADER_4)));
        multipleData.setFlag3(populateDynamicList(multipleData.getFlag3(), getDynamicList(multipleObjects, HEADER_5)));
        multipleData.setFlag4(populateDynamicList(multipleData.getFlag4(), getDynamicList(multipleObjects, HEADER_6)));

        log.info("Pass the current multiple reference to the UI");
        multipleData.setMoveCases(populateCurrentMultipleRef(multipleData.getMultipleReference()));

    }

    private List<DynamicValueType> getDynamicList(SortedMap<String, Object> multipleObjects, String key) {

        Set<String> values = (Set<String>) multipleObjects.get(key);
        List<DynamicValueType> listItems = new ArrayList<>();

        listItems.add(Helper.getDynamicValue(SELECT_ALL));

        if (values != null && !values.isEmpty()) {

            for (String flag : values) {

                if (!flag.isEmpty()) {

                    listItems.add(Helper.getDynamicValue(flag));
                }
            }
        }

        return listItems;
    }

    private DynamicFixedListType populateDynamicList(DynamicFixedListType dynamicListFlag,
                                                     List<DynamicValueType> listItems) {

        if (dynamicListFlag != null) {
            dynamicListFlag.setListItems(listItems);
            dynamicListFlag.setValue(Helper.getDynamicValue(SELECT_ALL));
            return dynamicListFlag;
        } else {
            var dynamicFixedListType = new DynamicFixedListType();
            dynamicFixedListType.setListItems(listItems);
            dynamicFixedListType.setValue(Helper.getDynamicValue(SELECT_ALL));
            return dynamicFixedListType;
        }

    }

    private MoveCasesType populateCurrentMultipleRef(String currentMultipleRef) {

        var moveCasesType = new MoveCasesType();

        moveCasesType.setUpdatedMultipleRef(currentMultipleRef);

        return moveCasesType;
    }
}
