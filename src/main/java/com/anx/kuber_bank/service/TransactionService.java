package com.anx.kuber_bank.service;

import com.anx.kuber_bank.dto.TransactionDto;
import com.anx.kuber_bank.entity.Transaction;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
