package com.example.backendtemplate.repository;

import com.example.backendtemplate.entities.Transaction;
import com.example.backendtemplate.repository.customRepositories.TransactionCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long>, TransactionCustomRepository {


}
