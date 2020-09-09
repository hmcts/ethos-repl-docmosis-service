package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.*;

@Slf4j
public class MultiplesHelper {

    public static List<String> HEADERS = new ArrayList<>(Arrays.asList(HEADER_1, HEADER_2, HEADER_3, HEADER_4, HEADER_5, HEADER_6));
    public static String SELECT_ALL = "All";

    public static List<String> getCaseIds(MultipleData multipleData) {

        if (multipleData.getCaseIdCollection() != null
                && !multipleData.getCaseIdCollection().isEmpty()) {

            return multipleData.getCaseIdCollection().stream()
                    .filter(key -> key.getId() != null && !key.getId().equals("null"))
                    .map(caseId -> caseId.getValue().getEthosCaseReference())
                    .distinct()
                    .collect(Collectors.toList());

        } else {

            return new ArrayList<>();

        }
    }

    public static String getExcelBinaryUrl(MultipleDetails multipleDetails) {
        return multipleDetails.getCaseData().getCaseImporterFile().getUploadedDocument().getDocumentBinaryUrl();
    }

    public static void resetMidFields(MultipleData multipleData) {

        multipleData.setFlag1(null);
        multipleData.setFlag2(null);
        multipleData.setFlag3(null);
        multipleData.setFlag4(null);

        multipleData.setManagingOffice(null);
        multipleData.setFileLocation(null);
        multipleData.setFileLocationGlasgow(null);
        multipleData.setFileLocationAberdeen(null);
        multipleData.setFileLocationDundee(null);
        multipleData.setFileLocationEdinburgh(null);
        multipleData.setClerkResponsible(null);
        multipleData.setPositionType(null);
        multipleData.setReceiptDate(null);
        multipleData.setHearingStage(null);

    }

    public static DynamicValueType getDynamicValue(String value) {

        DynamicValueType dynamicValueType = new DynamicValueType();
        dynamicValueType.setCode(value);
        dynamicValueType.setLabel(value);

        return dynamicValueType;

    }
}
