package com.anx.kuber_bank.service;

import com.anx.kuber_bank.config.JwtTokenProvider;
import com.anx.kuber_bank.constants.AccountConstants;
import com.anx.kuber_bank.dto.*;
import com.anx.kuber_bank.entity.Role;
import com.anx.kuber_bank.entity.User;
import com.anx.kuber_bank.repository.UserRepository;
import com.anx.kuber_bank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

import static com.anx.kuber_bank.utils.AccountUtils.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired UserRepository userRepository;

    @Autowired EmailService emailService;

    @Autowired TransactionService transactionService;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired AuthenticationManager authenticationManager;
    @Autowired JwtTokenProvider jwtTokenProvider;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        // check if user exists in the db
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        // saving a new user in db
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .alternatePhoneNumber(userRequest.getAlternatePhoneNumber())
                .status(AccountConstants.AccountStatus.ACTIVE.name())
                .role(Role.valueOf("ROLE_ADMIN"))
                .build();

        User savedUser = userRepository.save(newUser);
        emailService.sendEmailAlerts(EmailDetails.builder()
                        .recipient(savedUser.getEmail())
                        .subject("Account Creation!")
                        .messageBody(("Congratulations! Your account has been successfully created. \n Your account details:\n" +
                        "Account Name : "+ savedUser.getFirstName() + " " + savedUser.getLastName() + " "+ savedUser.getOtherName()
                                + "\n Account Number : " + savedUser.getAccountNumber()))
                .build());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " "+ savedUser.getOtherName())
                        .build())
                .build();
    }

    public LoginResponse login(LoginDto loginDto) {
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(), loginDto.getPassword()
                )
        );

        EmailDetails loginAlert = EmailDetails.builder()
                .recipient(loginDto.getEmail())
                .subject("Login Alert!")
                .messageBody("Dear " + AccountUtils.getFullNameFromEmail(loginDto.getEmail()) + ",\n" +
                        "You have successfully logged in to your account on " + new Date() + ". If this was not you, please contact support immediately.")
                .build();
        emailService.sendEmailAlerts(loginAlert);

        return LoginResponse.builder()
                .responseCode(LOGIN_SUCCESS_CODE)
                .responseMessage(LOGIN_SUCCESS_MESSAGE)
                .token(jwtTokenProvider.generateToken(authentication))
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        if (!checkAccountExists(enquiryRequest.getAccountNumber())) return BankResponse.builder()
                .accountInfo(null)
                .responseCode(ACCOUNT_NOT_EXISTS_CODE)
                .responseMessage(ACCOUNT_NOT_EXISTS_MESSAGE)
                .build();
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(ACCOUNT_EXISTS_CODE)
                .responseMessage(ACCOUNT_EXISTS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .accountNumber(foundUser.getAccountNumber())
                        .build())
                .build();
    }

    private boolean checkAccountExists(String accountNumber) {
        return userRepository.existsByAccountNumber(accountNumber);
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        if (!checkAccountExists(enquiryRequest.getAccountNumber())) {
            return ACCOUNT_NOT_EXISTS_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditDebitRequest) {
        // checking if account exist
        if (!checkAccountExists(creditDebitRequest.getAccountNumber())) return BankResponse.builder()
                .accountInfo(null)
                .responseCode(ACCOUNT_NOT_EXISTS_CODE)
                .responseMessage(ACCOUNT_NOT_EXISTS_MESSAGE)
                .build();

        User userToCredit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitRequest.getAmount()));
        userRepository.save(userToCredit);

        // Save the transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .transactionType(AccountConstants.TransactionType.CREDIT.name())
                .accountNumber(userToCredit.getAccountNumber())
                .amount(creditDebitRequest.getAmount())
                .accountBalance(userToCredit.getAccountBalance())
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(ACCOUNT_CREDITED_CODE)
                .responseMessage(ACCOUNT_CREDITED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " "+ userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(creditDebitRequest.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest creditDebitRequest) {
        if (!checkAccountExists(creditDebitRequest.getAccountNumber())) return BankResponse.builder()
                .accountInfo(null)
                .responseCode(ACCOUNT_NOT_EXISTS_CODE)
                .responseMessage(ACCOUNT_NOT_EXISTS_MESSAGE)
                .build();

        // check if the amount we want to withdraw is not greater than the account balance
        User userToDebit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
        if (userToDebit.getAccountBalance().compareTo(creditDebitRequest.getAmount()) < 0) {
            return BankResponse.builder()
                    .responseCode(INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        } else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(creditDebitRequest.getAmount()));
            userRepository.save(userToDebit);

            // Save the transaction
            TransactionDto transactionDto = TransactionDto.builder()
                    .transactionType(AccountConstants.TransactionType.DEBIT.name())
                    .accountNumber(userToDebit.getAccountNumber())
                    .amount(creditDebitRequest.getAmount())
                    .accountBalance(userToDebit.getAccountBalance())
                    .build();
            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
                    .responseCode(ACCOUNT_DEBITED_CODE)
                    .responseMessage(ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(creditDebitRequest.getAccountNumber())
                            .accountBalance(userToDebit.getAccountBalance())
                            .accountName(userToDebit.getFirstName() + " "+ userToDebit.getLastName() + " "+ userToDebit.getOtherName())
                            .build())
                    .build();
        }
    }

    @Override
    public BankResponse transfer(TransferRequest transferRequest) {
        // check if both the account exists
        // check if the source account have sufficient balance
        // then debit from source and credit to destination

        if (!checkAccountExists(transferRequest.getSourceAccountNumber())) return BankResponse.builder()
                .accountInfo(null)
                .responseCode(SOURCE_ACCOUNT_NOT_EXISTS_CODE)
                .responseMessage(SOURCE_ACCOUNT_NOT_EXISTS_MESSAGE)
                .build();

        if (!checkAccountExists(transferRequest.getDestinationAccountNumber())) return BankResponse.builder()
                .accountInfo(null)
                .responseCode(DESTINATION_ACCOUNT_NOT_EXISTS_CODE)
                .responseMessage(DESTINATION_ACCOUNT_NOT_EXISTS_MESSAGE)
                .build();

        // check if the amount we want to withdraw is not greater than the account balance
        User sourceAccountUser = userRepository.findByAccountNumber(transferRequest.getSourceAccountNumber());
        if (sourceAccountUser.getAccountBalance().compareTo(transferRequest.getAmount()) < 0) {
            return BankResponse.builder()
                    .responseCode(INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(transferRequest.getAmount()));
        userRepository.save(sourceAccountUser);
        String sourceUserName = sourceAccountUser.getFirstName() + " "+ sourceAccountUser.getLastName() +" "+ sourceAccountUser.getOtherName();
        String maskedSourceAc =  maskAccountNumber(transferRequest.getSourceAccountNumber());
        EmailDetails debitEmail = EmailDetails.builder()
                .recipient(sourceAccountUser.getEmail())
                .subject("DEBIT ALERT!")
                .messageBody("Dear "+ sourceUserName + ",\n"+ "Your A/C "+ maskedSourceAc + " Debited INR "+ transferRequest.getAmount() + " on "+ new Date() + ". Your available account balance is : "+ sourceAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlerts(debitEmail);


        // Save the transaction
        TransactionDto sourceTransactionDto = TransactionDto.builder()
                .transactionType(AccountConstants.TransactionType.DEBIT.name())
                .accountNumber(sourceAccountUser.getAccountNumber())
                .amount(transferRequest.getAmount())
                .accountBalance(sourceAccountUser.getAccountBalance())
                .build();
        transactionService.saveTransaction(sourceTransactionDto);


        User destinationAccountUser = userRepository.findByAccountNumber(transferRequest.getDestinationAccountNumber());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(transferRequest.getAmount()));
        userRepository.save(destinationAccountUser);
        String destinationUserName = destinationAccountUser.getFirstName() + " "+ destinationAccountUser.getLastName() + " "+ destinationAccountUser.getOtherName();
        String maskedDestinationAc = maskAccountNumber(transferRequest.getDestinationAccountNumber());
        EmailDetails creditAlert = EmailDetails.builder()
                .recipient(destinationAccountUser.getEmail())
                .subject("CREDIT ALERT")
                .messageBody("Dear "+ destinationUserName + ",\n" + "Your A/C "+ maskedDestinationAc + " Credit INR "+ transferRequest.getAmount() + " on "+ new Date() +". Your available account balance is : "+ destinationAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlerts(creditAlert);


        // Save the transaction
        TransactionDto destinationTransactionDto = TransactionDto.builder()
                .transactionType(AccountConstants.TransactionType.CREDIT.name())
                .accountNumber(destinationAccountUser.getAccountNumber())
                .amount(transferRequest.getAmount())
                .accountBalance(destinationAccountUser.getAccountBalance())
                .build();
        transactionService.saveTransaction(destinationTransactionDto);

        return BankResponse.builder()
                .responseCode(TRANSFER_SUCCESS_CODE)
                .responseMessage(TRANSFER_SUCCESS_MESSAGE)
                .build();
    }

    private static String maskAccountNumber(String sourceAccountNumber) {
        int numberOfDigitsToKeep = sourceAccountNumber.length() - 4;
        String maskAc = sourceAccountNumber.substring(0, numberOfDigitsToKeep).replaceAll("[0-9]", "X");
        return maskAc + sourceAccountNumber.substring(numberOfDigitsToKeep);
    }
}
