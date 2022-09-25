package org.rconfalonieri.nzuardi.shootingapp.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

/*
* Class used to contain outlook mail server configuration.
* */

@Component
public class MailSenderImpl{

    //todo credenziali gmail

    @Value("${mail.username}")
    private String username;
    @Value("${mail.password}")
    private String password;

    /* Bean to retrieve information linked to the outlook mail server.
    @Bean
    public JavaMailSender getOutlookMailServer(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("elis-org.mail.protection.outlook.com");
        mailSender.setPort(25);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }*/

    // Bean to retrieve information linked to the Gmail mail server.
    @Bean
    public JavaMailSender getGmailMailServer(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }
}
