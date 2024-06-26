package com.shop.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


import java.io.UnsupportedEncodingException;

public interface MailServiceInter {
    MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException;

    String createKey();

    String sendSimpleMessage(String to) throws MessagingException, UnsupportedEncodingException;
}
