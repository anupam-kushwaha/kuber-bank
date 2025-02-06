package com.anx.kuber_bank.utils;

import lombok.Getter;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXIST_CODE = "AC001";
    public static final String ACCOUNT_EXIST_MESSAGE = "This user already has an account created!";
    public static final String ACCOUNT_CREATION_SUCCESS = "AC0002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created!";

    public static final String ACCOUNT_NOT_EXISTS_CODE = "AC0003";
    public static final String ACCOUNT_NOT_EXISTS_MESSAGE = "Account number does not exists.";

    public static final String ACCOUNT_EXISTS_CODE = "AC0004";
    public static final String ACCOUNT_EXISTS_MESSAGE = "Account number exists.";

    public static final String ACCOUNT_CREDITED_CODE = "AC0005";
    public static final String ACCOUNT_CREDITED_MESSAGE = "Account credited successfully.";

    public static final String INSUFFICIENT_BALANCE_CODE = "AC0006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Account balance insufficient.";

    public static final String ACCOUNT_DEBITED_CODE = "AC0007";
    public static final String ACCOUNT_DEBITED_MESSAGE = "Account balance debited.";

    public static final String SOURCE_ACCOUNT_NOT_EXISTS_CODE = "AC0008";
    public static final String SOURCE_ACCOUNT_NOT_EXISTS_MESSAGE = "Source account does not exists. Transfer cancelled. Please enter correct account number.";

    public static final String DESTINATION_ACCOUNT_NOT_EXISTS_CODE = "AC0009";
    public static final String DESTINATION_ACCOUNT_NOT_EXISTS_MESSAGE = "Destination account does not exists. Transfer cancelled. Please enter correct account number.";

    public static final String TRANSFER_SUCCESS_CODE = "AC0010";
    public static final String TRANSFER_SUCCESS_MESSAGE = "Transfer successfully completed!!";

    /**
     *  current year + random six digits number
     */

    public static String generateAccountNumber() {
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;
        int rnd = (int) (Math.random() * (max - min + 1) + min);
        return currentYear.toString().concat(String.valueOf(rnd));
    }
}
