package com.anx.kuber_bank.service;

import com.anx.kuber_bank.constants.AccountConstants;
import com.anx.kuber_bank.dto.TransactionDto;
import com.anx.kuber_bank.entity.Transaction;
import com.anx.kuber_bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;
    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .accountBalance(transactionDto.getAccountBalance())
                .status(AccountConstants.TransactionStatus.SUCCESS.name())
                .build();
        transactionRepository.save(transaction);
        System.out.println("Transaction saved successfully.");
    }
}
