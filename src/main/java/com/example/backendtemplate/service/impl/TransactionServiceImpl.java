package com.example.backendtemplate.service.impl;

import com.example.backendtemplate.model.request.Transaction.TransactionHistoryRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.service.TransactionService;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Override
    public BaseDetailsResponse<?> getTransactionHistory(TransactionHistoryRequest request) {
        return null;
    }
}
