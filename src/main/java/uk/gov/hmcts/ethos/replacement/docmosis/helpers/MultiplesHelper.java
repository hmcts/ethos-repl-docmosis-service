package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MultiplesHelper {

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
        return multipleDetails.getCaseData().getDocumentCollection().get(0).getValue().getUploadedDocument().getDocumentBinaryUrl();
    }

}
