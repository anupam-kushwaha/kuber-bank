package com.anx.kuber_bank.service;

import com.anx.kuber_bank.dto.BankResponse;
import com.anx.kuber_bank.dto.EmailDetails;
import com.anx.kuber_bank.entity.Transaction;
import com.anx.kuber_bank.entity.User;
import com.anx.kuber_bank.repository.TransactionRepository;
import com.anx.kuber_bank.repository.UserRepository;
import com.anx.kuber_bank.utils.PdfGeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.anx.kuber_bank.utils.AccountUtils.*;

@Slf4j
@Service
public class BankStatementService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PdfGeneratorUtil pdfGeneratorUtil;
    @Autowired
    private EmailService emailService;
    /**
     * Get the list of transaction with a date range for the given account number
     * now we want to create a pdf with these transaction data
     * and send this pdf as an attachment in the email in the generate the bank statement api
     */

    public List<Transaction> getStatement(String accountNumber, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        return transactionRepository.findAll().stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().isAfter(start.atStartOfDay()))
                .filter(transaction -> transaction.getCreatedAt().isBefore(end.atTime(23,59, 59))).toList();
    }

    public BankResponse generateBankStatement(String accountNumber, String startDate, String endDate) {
        User accountUser = userRepository.findByAccountNumber(accountNumber);
        if (accountUser != null) {
            List<Transaction> transactionList = getStatement(accountNumber, startDate, endDate);
            Map<String, Object> data = new HashMap<>();
            data.put("transactions", transactionList);
            String fullName = accountUser.getFirstName() + " " + accountUser.getLastName() + " "+ accountUser.getOtherName();
            data.put("accountName", fullName);
            data.put("address", accountUser.getAddress());
            data.put("accountNumber", accountUser.getAccountNumber());
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            Date today = new Date();
            data.put("todayDate", sdf.format(today));
            data.put("accountBalance", accountUser.getAccountBalance());
            String statementRange;
            try {
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                Date sdate = sdf2.parse(startDate);
                Date eDate = sdf2.parse(endDate);
               statementRange = sdf.format(sdate) + " to "+ sdf.format(eDate);
            } catch (ParseException e) {
                System.out.println("in catch block while parsing date");
                log.info("Error while : ", e);
               statementRange = startDate + " to " + endDate;
            }
            data.put("statementRage", statementRange);
            File bankStatement = pdfGeneratorUtil.generatePDF("", "bank_statement", data);
            emailService.sendEmailWithAttachment(EmailDetails.builder()
                            .recipient(accountUser.getEmail())
                            .subject("Bank Statement from "+ statementRange)
                            .messageBody("Dear "+ fullName + ",\n" + "Your bank statement for the requested date range has been generated. Please refer to the attachment of this mail.")
                            .attachment(bankStatement.getAbsolutePath())
                    .build());
            return BankResponse.builder()
                    .responseCode(BANK_STATEMENT_GEN_SUCCESS_CODE)
                    .responseMessage(BANK_STATEMENT_GEN_SUCCESS_MESSAGE)
                    .build();
        }
        return BankResponse.builder()
                .responseCode(ACCOUNT_NOT_EXISTS_CODE)
                .responseMessage(ACCOUNT_NOT_EXISTS_MESSAGE)
                .accountInfo(null)
                .build();
    }
}
