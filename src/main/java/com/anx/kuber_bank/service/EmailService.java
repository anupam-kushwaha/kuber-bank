package com.anx.kuber_bank.service;

import com.anx.kuber_bank.dto.EmailDetails;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendEmailAlerts(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails);
}
