package com.abhi.the_bank_app.service;

import com.abhi.the_bank_app.dto.EmailDetails;

public interface EmailService {

    void sendEmailAlert(EmailDetails emailDetails);
}
