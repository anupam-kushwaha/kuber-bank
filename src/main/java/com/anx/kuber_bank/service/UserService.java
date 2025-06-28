package com.anx.kuber_bank.service;

import com.anx.kuber_bank.dto.*;
import org.springframework.stereotype.Service;

public interface UserService {
   BankResponse createAccount(UserRequest userRequest);
   BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
   String nameEnquiry(EnquiryRequest enquiryRequest);
   BankResponse creditAccount(CreditDebitRequest creditDebitRequest);
   BankResponse debitAccount(CreditDebitRequest creditDebitRequest);
   BankResponse transfer(TransferRequest transferRequest);
   LoginResponse login(LoginDto loginDto);
}
