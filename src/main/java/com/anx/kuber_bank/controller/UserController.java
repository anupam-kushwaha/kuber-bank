package com.anx.kuber_bank.controller;

import com.anx.kuber_bank.dto.*;
import com.anx.kuber_bank.service.UserService;
import com.anx.kuber_bank.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Account Management APIs")
@RestController
@RequestMapping("/api/user/")
public class UserController {
        @Autowired UserService userService;

        @PostMapping(value = "/create")
        public BankResponse accountCreation(@RequestBody UserRequest userRequest) {
                return userService.createAccount(userRequest);
        }

        @GetMapping(value = "/balanceEnquiry")
        public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
                return userService.balanceEnquiry(enquiryRequest);
        }

        @GetMapping(value = "/nameEnquiry")
        public String nameEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
                return userService.nameEnquiry(enquiryRequest);
        }

        @PostMapping(value = "/creditAccount")
        public BankResponse creditAccount(@RequestBody CreditDebitRequest creditDebitRequest) {
                return userService.creditAccount(creditDebitRequest);
        }

        @PostMapping(value = "/debitAccount")
        public BankResponse debitAccount(@RequestBody CreditDebitRequest creditDebitRequest) {
                return userService.debitAccount(creditDebitRequest);
        }

        @PostMapping(value = "/transfer")
        public BankResponse transfer(@RequestBody TransferRequest transferRequest) {
                return userService.transfer(transferRequest);
        }
}
