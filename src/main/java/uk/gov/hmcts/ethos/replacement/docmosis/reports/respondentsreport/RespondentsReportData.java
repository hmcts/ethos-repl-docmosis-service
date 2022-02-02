package uk.gov.hmcts.ethos.replacement.docmosis.reports.respondentsreport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

@Slf4j
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RespondentsReportData extends ListingData {

    @JsonIgnore
    public String office;

    @JsonIgnore
    public String totalCasesWithMoreThanOneRespondent;

    @JsonIgnore
    private String totalCasesWithMoreThanOneRespondentAndRepresented;

    @JsonIgnore
    private String totalCasesWithRepresentativesWithMoreThanOneRespondent;

    @JsonIgnore
    private List<RespondentsReportDetail> respondentsReportDetails = new ArrayList<>();

}

