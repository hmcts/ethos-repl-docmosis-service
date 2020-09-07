package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.ecm.common.model.multiples.MultipleConstants.*;

@Slf4j
public class MultiplesHelper {

    public static List<String> HEADERS = new ArrayList<>(Arrays.asList(HEADER_1, HEADER_2, HEADER_3, HEADER_4, HEADER_5));
    public static DateTimeFormatter DATE_TIME_USER_FRIENDLY_PATTERN = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

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

}
