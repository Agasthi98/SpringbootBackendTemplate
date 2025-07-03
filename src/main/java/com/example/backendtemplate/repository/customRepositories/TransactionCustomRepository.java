package com.example.backendtemplate.repository.customRepositories;

import com.example.backendtemplate.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface TransactionCustomRepository {
    Page<Transaction> searchTransactions(
            String type,
            Double minAmount,
            Double maxAmount,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );
}
