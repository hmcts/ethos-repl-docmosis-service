package uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport;

import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeR;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ecm.common.model.reports.respondentsreport.RespondentsReportCaseData;
import uk.gov.hmcts.ecm.common.model.reports.respondentsreport.RespondentsReportSubmitEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class RespondentsReportCaseDataBuilder {
    private final RespondentsReportCaseData caseData = new RespondentsReportCaseData();


    public void withNoRespondents() {
        caseData.setRespondentCollection(null);
    }

    public RespondentSumTypeItem getRespondent(String respName) {
        RespondentSumTypeItem item = new RespondentSumTypeItem();
        RespondentSumType type = new RespondentSumType();
        type.setRespondentName(respName);
        item.setId(UUID.randomUUID().toString());
        item.setValue(type);
        return item;
    }

    public RepresentedTypeRItem getRepresentative(String respName, String repName) {
        RepresentedTypeRItem item = new RepresentedTypeRItem();
        RepresentedTypeR type = new RepresentedTypeR();
        type.setRespRepName(respName);
        type.setNameOfRepresentative(repName);
        item.setId(UUID.randomUUID().toString());
        item.setValue(type);
        return item;
    }

    public void withOneRespondent() {
        caseData.setRespondentCollection(Collections.singletonList(getRespondent("Resp")));
    }

    public void withMoreThanOneRespondents() {
        RespondentSumTypeItem item1 = getRespondent("Resp1");
        RespondentSumTypeItem item2 = getRespondent("Resp2");
        caseData.setRespondentCollection(Arrays.asList(item1, item2));
    }

    public void withMoreThan1RespondentsRepresented() {
        RespondentSumTypeItem item1 = getRespondent("Resp1");
        RespondentSumTypeItem item2 = getRespondent("Resp2");
        RepresentedTypeRItem rItem1 = getRepresentative("Resp1", "Rep1");
        RepresentedTypeRItem rItem2 = getRepresentative("Resp2", "Rep1");

        caseData.setRepCollection(Arrays.asList(rItem1, rItem2));
        caseData.setRespondentCollection(Arrays.asList(item1, item2));
    }

    public RespondentsReportSubmitEvent buildAsSubmitEvent() {
        var submitEvent = new RespondentsReportSubmitEvent();
        caseData.setEthosCaseReference("111");
        submitEvent.setCaseData(caseData);
        return submitEvent;
    }

}
