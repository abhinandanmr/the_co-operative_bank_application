package com.abhi.the_bank_app.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXIST_CODE="001";
    public static final String ACCOUNT_EXIST_MESSAGE="This user already has an account created";
    public static final String ACCOUNT_CREATION_SUCCESS="002";
    public static final String ACCOUNT_CREATION_MESSAGE="Account has been created successfully!";
    public static final String ACCOUNT_NOT_EXIST_CODE="003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE="User provided account not exist!";
    public static final String ACCOUNT_FOUND_CODE="004";
    public static final String ACCOUNT_FOUND_MESSAGE="User Account Found";
    public static final String ACCOUNT_CREDITEd_SUCCESS="005";
    public static final String ACCOUNT_CREDITEd_SUCCESS_MESSAGE="User Account Credited success";

    public static String generateAccountNumber() {
        // Get the current year as a string
        String year = String.valueOf(Year.now());

        // Generate a random six-digit number
        int min = 100000;
        int max = 999999;
        int randNumber = (int) (Math.random() * (max - min + 1)) + min;

        // Concatenate the year and random number
        return year + randNumber;
    }

}