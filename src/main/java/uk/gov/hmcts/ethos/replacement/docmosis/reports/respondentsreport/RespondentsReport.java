package uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;

public class RespondentsReport {
    public ListingData generateReportData(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {

        if (CollectionUtils.isNotEmpty(submitEvents)) {
            executeReport(listingDetails, submitEvents);
        }

        listingDetails.getCaseData().clearReportFields();
        return listingDetails.getCaseData();
    }

    private void executeReport(ListingDetails listingDetails, List<SubmitEvent> submitEvents) {
        var noOfCasesWithMoreThanOneRespondents = 0;
        var noOfCasesWithMoreThanOneRespondentsAndRepresented = 0;
        var representativeWithMoreThanOneRespondent = 0;
        var respondentReportData = new RespondentsReportData();
       List<RespondentsReportDetail> respondentsReportDetailList = new ArrayList<>();
        RespondentsReportDetail respondentsReportDetail;
        for (SubmitEvent submitEvent : submitEvents) {
            var caseData = submitEvent.getCaseData();
            if (CollectionUtils.isNotEmpty(caseData.getRespondentCollection())
                    && caseData.getRespondentCollection().size() > 1) {
                noOfCasesWithMoreThanOneRespondents = noOfCasesWithMoreThanOneRespondents + 1;
                respondentsReportDetail = getReportDetail(caseData);
                if (respondentsReportDetail != null) {
                    respondentsReportDetailList.add(respondentsReportDetail);
                }
                if (CollectionUtils.isNotEmpty(caseData.getRepCollection())) {
                    noOfCasesWithMoreThanOneRespondentsAndRepresented = noOfCasesWithMoreThanOneRespondentsAndRepresented + 1;
                    for (RepresentedTypeRItem rep : caseData.getRepCollection()) {
                        if (rep.getValue().getDynamicRespRepName().getListItems().size() > 1) {
                            representativeWithMoreThanOneRespondent = representativeWithMoreThanOneRespondent + 1;
                            break;
                        }
                    }
                }

            }
        }
        respondentReportData.setTotalCasesWithMoreThanOneRespondent(String.valueOf(noOfCasesWithMoreThanOneRespondents));
        respondentReportData.setTotalCasesWithMoreThanOneRespondentAndRepresented(String.valueOf(noOfCasesWithMoreThanOneRespondentsAndRepresented));
        respondentReportData.setTotalCasesWithRepresentativesWithMoreThanOneRespondent(String.valueOf(representativeWithMoreThanOneRespondent));
        respondentReportData.setRespondentsReportDetails(respondentsReportDetailList);
    }

    private RespondentsReportDetail getReportDetail(CaseData caseData) {
        var respondentsReportDetail = new RespondentsReportDetail();
       List<RespondentData> respondentDataList = new ArrayList<>();
        respondentsReportDetail.setCaseNumber(caseData.getEthosCaseReference());
        for(RespondentSumTypeItem resp : caseData.getRespondentCollection()) {
            var respondentData = new RespondentData();
            String respondentName = resp.getValue().getRespondentName();
            respondentData.setRespondentName(respondentName);
            var representativeName = "N/A";
            var representingMoreThanOneRespondent = "N/A";
            Optional<RepresentedTypeRItem> rep = caseData.getRepCollection().stream().filter(
                    a -> respondentName.equals(a.getValue().getRespRepName())).findFirst();
            if(rep.isPresent()) {
                representativeName = rep.get().getValue().getNameOfRepresentative();
                if (rep.get().getValue().getDynamicRespRepName().getListItems().size() > 1) {
                    representingMoreThanOneRespondent = "YES";
                } else {
                    representingMoreThanOneRespondent = "NO";
                }
            }
            respondentData.setRepresentativeName(representativeName);
            respondentData.setRepresentativeHasMoreThanOneRespondent(representingMoreThanOneRespondent);
            respondentDataList.add(respondentData);
        }
        respondentsReportDetail.setRespondentDataList(respondentDataList);
        return  respondentsReportDetail;
    }
}
