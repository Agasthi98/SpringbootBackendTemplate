package com.example.backendtemplate.model.request.Transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionHistoryRequest {

    private String type;
    @JsonProperty("date_type")
    private String dateType;
    @JsonProperty("amount_filter")
    private AmountFilter amountFilter;
    @JsonProperty("is_date")
    private boolean isDate;
    @JsonProperty("is_amount")
    private boolean isAMount;
    @JsonProperty("page_no")
    private String pageNo;
    @JsonProperty("page_size")
    private String pageSize;
}
