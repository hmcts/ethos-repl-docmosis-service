package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

/**
 * Enum to decide which kind of filter is needed to read the excel.
 *
 * ALL          -> TreeMap <String, MultipleObject> multipleObjectsALL   (String = ethosCaseReference)
 * FLAGS        -> TreeMap <String, String> multipleObjectsFlags         (String = ethosCaseReference)
 * SUB_MULTIPLE -> TreeMap <String, List<String>> multipleObjectsSubM    (String = subMultiple, List<String> = List<ethosCaseReference>)
 *
 */
public enum FilterExcelType {
    ALL,
    FLAGS,
    SUB_MULTIPLE
}
