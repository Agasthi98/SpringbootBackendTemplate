package com.example.backendtemplate.model.request.Transaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequest {
    private String type;
    private String status;
    private String amount;
    private String fee;
    private String description;
} 