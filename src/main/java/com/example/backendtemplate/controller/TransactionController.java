package com.example.backendtemplate.controller;

import com.example.backendtemplate.model.request.Transaction.TransactionHistoryRequest;
import com.example.backendtemplate.model.request.Transaction.TransactionRequest;
import com.example.backendtemplate.model.response.BaseDetailsResponse;
import com.example.backendtemplate.model.response.DefaultResponse;
import com.example.backendtemplate.model.response.Transction.TransactionHistoryResponse;
import com.example.backendtemplate.service.TransactionService;
import com.example.backendtemplate.util.ReturnResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/get/transaction-history")
    public ResponseEntity<DefaultResponse> getTransactionHistory(@RequestBody TransactionHistoryRequest request){
        BaseDetailsResponse<TransactionHistoryResponse> response = transactionService.getTransactionHistory(request);

        return ReturnResponseUtil.returnResponse(response);
    }

    @PostMapping("/add") 
    public ResponseEntity<DefaultResponse> addTransaction(@RequestBody TransactionRequest request) {
        BaseDetailsResponse<?> response = transactionService.addTransaction(request);
        return ReturnResponseUtil.returnResponse(response);
    }

    
    
}
