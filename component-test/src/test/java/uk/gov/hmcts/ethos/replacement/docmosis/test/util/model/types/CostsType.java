package uk.gov.hmcts.ethos.replacement.docmosis.test.util.model.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CostsType {

    @JsonProperty("costs_question")
    private String costsQuestion;
    @JsonProperty("costs_awarded_to")
    private String costsAwardedTo;
    @JsonProperty("costs_amount")
    private String costsAmount;
    @JsonProperty("costs_expenses_awarded_to")
    private String costsExpensesAwardedTo;
    @JsonProperty("costs_expenses_awarded_against")
    private String costExpensesAwardedAgainst;
    @JsonProperty("costs_expenses_awarded_amount")
    private String costsExpensesAwardedAmount;
    @JsonProperty("preparation_of_time_awarded_to")
    private String preparationOfTimeAwardedTo;
    @JsonProperty("preparation_of_time_awarded_against")
    private String preparationOfTimeAwardedAgainst;
    @JsonProperty("preparation_of_time_amount_awarded")
    private String preparationOfTimeAmountAwarded;
    @JsonProperty("wasted_cost_awarded_to")
    private String wastedCostAwardedTo;
    @JsonProperty("wasted_cost_awarded_against")
    private String wastedCostAwardedAgainst;
    @JsonProperty("wasted_cost_amount_awarded")
    private String wastedCostAmountAwarded;
}
