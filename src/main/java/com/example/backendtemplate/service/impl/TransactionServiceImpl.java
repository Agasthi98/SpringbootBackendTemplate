package com.example.backendtemplate.service.impl;

import com.example.backendtemplate.model.request.Transaction.TransactionHistoryRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.service.TransactionService;
import org.springframework.stereotype.Service;
import com.example.backendtemplate.model.response.Transction.TransactionHistoryResponse;
import com.example.backendtemplate.repository.TransactionRepository;
import com.example.backendtemplate.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.backendtemplate.entities.Transaction;
import com.example.backendtemplate.model.request.Transaction.TransactionRequest;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public BaseDetailsResponse<TransactionHistoryResponse> getTransactionHistory(TransactionHistoryRequest request) {
        int pageNo = 0;
        int pageSize = 10;
        try {
            pageNo = Integer.parseInt(request.getPageNo());
        } catch (Exception ignored) {}
        try {
            pageSize = Integer.parseInt(request.getPageSize());
        } catch (Exception ignored) {}
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Double minAmount = null;
        Double maxAmount = null;
        if (request.getAmountFilter() != null) {
            try {
                minAmount = request.getAmountFilter().getMinAmount() != null ? Double.valueOf(request.getAmountFilter().getMinAmount()) : null;
            } catch (Exception ignored) {}
            try {
                maxAmount = request.getAmountFilter().getMaxAmount() != null ? Double.valueOf(request.getAmountFilter().getMaxAmount()) : null;
            } catch (Exception ignored) {}
        }

        // Date filters can be added here if needed (currently not in request)
        Page<Transaction> page = transactionRepository.searchTransactions(
            request.getType() != null ? request.getType() : "",
            minAmount,
            maxAmount,
            request.getDateType(), // <-- add this line
            pageable
        );

        TransactionHistoryResponse response = TransactionHistoryResponse.builder()
            .transactions(page.getContent())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .build();

        return BaseDetailsResponse.<TransactionHistoryResponse>builder()
            .code(ResponseUtil.SUCCESS_CODE)
            .title(ResponseUtil.SUCCESS)
            .message("Transaction history fetched successfully")
            .data(response)
            .build();
    }

    @Override
    public BaseDetailsResponse<?> addTransaction(TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setType(request.getType());
        transaction.setStatus(request.getStatus());
        transaction.setAmount(request.getAmount());
        transaction.setFee(request.getFee());
        transaction.setDescription(request.getDescription());
        transactionRepository.save(transaction);
        return BaseDetailsResponse.builder()
            .code(ResponseUtil.SUCCESS_CODE)
            .title(ResponseUtil.SUCCESS)
            .message("Transaction added successfully")
            .data(null)
            .build();
    }
}
