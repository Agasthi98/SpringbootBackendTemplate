package com.example.backendtemplate.service.impl;

import com.example.backendtemplate.model.request.Transaction.TransactionHistoryRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.service.TransactionService;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;
import com.example.backendtemplate.model.response.Transction.TransactionHistoryResponse;
import com.example.backendtemplate.repository.TransactionRepository;
import com.example.backendtemplate.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.backendtemplate.entities.Transaction;
import com.example.backendtemplate.enums.FilterTypes;
import com.example.backendtemplate.enums.TranTypes;
import com.example.backendtemplate.model.request.Transaction.TransactionRequest;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;

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
        } catch (Exception ignored) {
        }
        try {
            pageSize = Integer.parseInt(request.getPageSize());
        } catch (Exception ignored) {
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Double minAmount = null;
        Double maxAmount = null;
        if (ObjectUtils.isEmpty(request.getAmountFilter())) {
            try {
                minAmount = request.getAmountFilter().getMinAmount() != null ? Double.valueOf(request.getAmountFilter().getMinAmount()) : null;
            } catch (Exception ignored) {
            }
            try {
                maxAmount = request.getAmountFilter().getMaxAmount() != null ? Double.valueOf(request.getAmountFilter().getMaxAmount()) : null;
            } catch (Exception ignored) {
            }
        }

        // Map type from full name to code if needed
        String type = request.getType();
        if (!StringUtils.isEmpty(type)) {
            if (type.equals(FilterTypes.FT.name())) {
                type = TranTypes.FUND_TRANSFER.name();
            } else if (type.equals(FilterTypes.BP.name())) {
                type = TranTypes.BILL_PAYMENT.name();
            }
        }else{
            type = null; // If type is empty, we can set it to null or handle it as needed
        }
        // Date filters can be added here if needed (currently not in request)
        Page<Transaction> page = transactionRepository.searchTransactions(
                type,
                minAmount,
                maxAmount,
                request.getDateType(),
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
        transaction.setAmount(new BigDecimal(request.getAmount().trim()));
        transaction.setFee(new BigDecimal(request.getFee().trim()));
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
