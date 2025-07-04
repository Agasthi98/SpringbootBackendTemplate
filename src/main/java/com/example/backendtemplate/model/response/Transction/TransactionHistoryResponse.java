package com.example.backendtemplate.model.response.Transction;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import com.example.backendtemplate.entities.Transaction;

@Setter
@Getter
@Builder
public class TransactionHistoryResponse {
    private List<Transaction> transactions;
    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private int pageSize;
}
