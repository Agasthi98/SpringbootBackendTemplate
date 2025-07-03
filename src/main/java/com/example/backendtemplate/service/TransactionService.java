package com.example.backendtemplate.service;

import com.example.backendtemplate.model.request.Transaction.TransactionHistoryRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;

public interface TransactionService {

    BaseDetailsResponse<?> getTransactionHistory(TransactionHistoryRequest request);
}
