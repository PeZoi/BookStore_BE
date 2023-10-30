package com.example.web_bookstore_be.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    public void sendMessage(String from, String to, String subject, String message) throws MessagingException;
}
