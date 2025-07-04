package com.example.backendtemplate.service;

import com.example.backendtemplate.model.request.Transaction.TransactionHistoryRequest;
import com.example.backendtemplate.model.request.Transaction.TransactionRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.model.response.Transction.TransactionHistoryResponse;

public interface TransactionService {

    BaseDetailsResponse<TransactionHistoryResponse> getTransactionHistory(TransactionHistoryRequest request);

    BaseDetailsResponse<?> addTransaction(TransactionRequest request);
}
