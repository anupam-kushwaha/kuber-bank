package com.anx.kuber_bank.controller;

import com.anx.kuber_bank.dto.BankResponse;
import com.anx.kuber_bank.entity.Transaction;
import com.anx.kuber_bank.service.BankStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping(value = "/api/transaction/")
public class TransactionController {

    @Autowired private BankStatementService bankStatementService;

    @GetMapping(value = "/bankStatement")
    public List<Transaction> getBankStatement(@RequestParam String accountNumber, @RequestParam String startDate, @RequestParam String endDate) {
        return bankStatementService.getStatement(accountNumber, startDate, endDate);
    }

    @GetMapping(value = "/emailBankStatement")
    public BankResponse emailBankStatement(@RequestParam String accountNumber, @RequestParam String startDate, @RequestParam String endDate) {
        return bankStatementService.emailBankStatement(accountNumber, startDate, endDate);
    }

    @GetMapping(value = "/generateBankStatement", produces = "application/pdf")
    public ResponseEntity<Object> generateBankStatement(@RequestParam String accountNumber, @RequestParam String startDate, @RequestParam String endDate) {
        return bankStatementService.generateBankStatement(accountNumber, startDate, endDate);
    }
}
