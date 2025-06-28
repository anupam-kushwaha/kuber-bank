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

        @PostMapping(value = "/login")
        public LoginResponse login(@RequestBody LoginDto loginDto) {
                return userService.login(loginDto);
        }
}
