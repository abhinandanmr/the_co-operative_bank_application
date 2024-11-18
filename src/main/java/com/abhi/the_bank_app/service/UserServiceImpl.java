package com.abhi.the_bank_app.service;

import com.abhi.the_bank_app.dto.*;
import com.abhi.the_bank_app.entity.User;
import com.abhi.the_bank_app.repository.UserRepository;
import com.abhi.the_bank_app.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // This method handles account creation. It first checks if the email is already registered.
    // If not, it creates a new account and sends a welcome email to the user.
    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        // Check if a user with the same email already exists in the system
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        // Create a new user entity and assign all necessary fields from the request
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber()) // Generate a unique account number
                .accountBalance(BigDecimal.ZERO)  // Account starts with zero balance
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        // Save the new user in the database
        User saveUser = userRepository.save(newUser);

        // Send a welcome email with account details to the user
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(saveUser.getEmail())
                .subject("Account creation")
                .messageBody(
                        "Congratulations! Your account has been successfully created.\n\n" +
                                "Here are your account details:\n" +
                                "------------------------------------\n" +
                                "Account Name: " + saveUser.getFirstName() + " " + saveUser.getLastName() + "\n" +
                                "Account Number: " + saveUser.getAccountNumber() + "\n" +
                                "------------------------------------\n" +
                                "Thank you for choosing our services!"
                )
                .build();

        emailService.sendEmailAlert(emailDetails);

        // Return the success response with account information
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(saveUser.getAccountBalance())
                        .accountNumber(saveUser.getAccountNumber())
                        .accountName(saveUser.getFirstName() + " " + saveUser.getLastName() + " " + saveUser.getOtherName())
                        .build())
                .build();
    }

    // This method checks the balance of a given account number and returns the balance if found.
    // If the account doesn't exist, an error message is returned.
    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {

        // Check if the account number exists in the database
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());

        // If the account doesn't exist, return an error response
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        // If the account exists, retrieve the account details and return the balance
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName())
                        .build())
                .build();
    }

    // This method returns the full name of the account holder based on the provided account number.
    // If the account doesn't exist, it returns a message saying the account doesn't exist.
    @Override
    public String nameEnquiry(EnquiryRequest request) {

        // Check if the account exists in the database
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());

        // If the account doesn't exist, return a message saying so
        if (!isAccountExist) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }

        // If the account exists, return the account holder's name
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName();
    }

    // This method handles crediting a specific amount to an account. It checks if the account exists,
    // and if so, it adds the specified amount to the balance.
    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {

        // First, check if the account exists in the database
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        // Fetch the user and add the specified amount to their balance
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        // Return a response with the updated balance information
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITEd_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITEd_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName())
                        .build())
                .build();
    }

    // This method debits a specified amount from an account. It checks if the account exists and if
    // the account has enough balance. If so, it deducts the amount; otherwise, it returns an error.
    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {

        // Check if the account exists in the database
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        // Fetch the user details
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());

        // Check if the account has sufficient balance
        if (userToDebit.getAccountBalance().compareTo(request.getAmount()) < 0) {
            return BankResponse.builder()
                    .responseCode("006") // Insufficient balance error code
                    .responseMessage("Insufficient account balance!")
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(userToDebit.getAccountBalance())
                            .accountNumber(request.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName())
                            .build())
                    .build();
        }

        // Deduct the amount from the account balance
        userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(userToDebit);

        // Return a response with the updated balance
        return BankResponse.builder()
                .responseCode("007") // Debit success code
                .responseMessage("Account debited successfully!")
                .accountInfo(AccountInfo.builder()
                        .accountBalance(userToDebit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName())
                        .build())
                .build();
    }

    // This method handles transferring funds from one account to another.
    // It checks that both the source and destination accounts exist, and ensures the source account has enough balance.
    // If everything checks out, it debits the source account and credits the destination account.
    @Override
    public BankResponse transfer(TransferRequest request) {

        // Check if the source account exists
        boolean isSourceAccountExist = userRepository.existsByAccountNumber(request.getSourceAccountNumber());
        if (!isSourceAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage("Source account does not exist!")
                    .accountInfo(null)
                    .build();
        }

        // Check if the destination account exists
        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isDestinationAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage("Destination account does not exist!")
                    .accountInfo(null)
                    .build();
        }

        // Get details of the source account
        User sourceAccount = userRepository.findByAccountNumber(request.getSourceAccountNumber());

        // Check if the source account has enough balance for the transfer
        if (sourceAccount.getAccountBalance().compareTo(request.getAmount()) < 0) {
            return BankResponse.builder()
                    .responseCode("006") // Insufficient balance error code
                    .responseMessage("Insufficient balance in source account!")
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(sourceAccount.getAccountBalance())
                            .accountNumber(sourceAccount.getAccountNumber())
                            .accountName(sourceAccount.getFirstName() + " " + sourceAccount.getLastName())
                            .build())
                    .build();
        }

        // Get the destination account details
        User destinationAccount = userRepository.findByAccountNumber(request.getDestinationAccountNumber());

        // Debit the source account
        sourceAccount.setAccountBalance(sourceAccount.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sourceAccount);

        // Credit the destination account
        destinationAccount.setAccountBalance(destinationAccount.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationAccount);

        // Return the updated account information after the transfer
        return BankResponse.builder()
                .responseCode("008") // Transfer success code
                .responseMessage("Transfer successful!")
                .accountInfo(AccountInfo.builder()
                        .accountBalance(sourceAccount.getAccountBalance())
                        .accountNumber(sourceAccount.getAccountNumber())
                        .accountName(sourceAccount.getFirstName() + " " + sourceAccount.getLastName())
                        .build())
                .build();
    }
}