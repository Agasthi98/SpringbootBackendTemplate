package com.example.backendtemplate.model.request.Transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AmountFilter {
    @JsonProperty("min_amount")
    private String minAmount;
    @JsonProperty("max_amount")
    private String maxAmount;
}
