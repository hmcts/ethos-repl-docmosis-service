package uk.gov.hmcts.ethos.replacement.docmosis.service.multiples.bulkaddsingles;

import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;

import java.util.List;

interface SingleCasesImporter {
    List<String> importCases(MultipleData multipleData, String authToken) throws ImportException;
}
