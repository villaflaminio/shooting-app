package org.rconfalonieri.nzuardi.shootingapp.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.rconfalonieri.nzuardi.shootingapp.model.dto.MailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.activation.URLDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Configuration freeMarkerConfiguration;

    @Value("${mail.username}")
    private String username;

    //todo mail per recupero password

    // Methods that sends an email using Freemarker specified template.
    public MailResponse sendEmail(String to, String subject, Map<String, Object> model, String ftlFileName) {
        MailResponse response = new MailResponse();
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            // Set mediaType
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            // Get the correct template into resources/mail-templates
            Template t = freeMarkerConfiguration.getTemplate(ftlFileName + ".ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
            helper.setTo(to);

            // Set up the email
            helper.setText(html, true);
            helper.setSubject(subject);
            helper.setFrom("Shooting App Customer Service <" + username + ">");
            helper.addInline("logo.png", new ClassPathResource("mail-templates/images/3275432.png"));
            helper.addInline("facebook.png", new ClassPathResource("mail-templates/images/facebook2x.png"));
            helper.addInline("instagram.png", new ClassPathResource("mail-templates/images/instagram2x.png"));
            helper.addInline("twitter.png", new ClassPathResource("mail-templates/images/twitter2x.png"));
            helper.addInline("logo-background.png", new ClassPathResource("mail-templates/images/logo-no-background.png"));

            javaMailSender.send(message);

            response.setMessage(ftlFileName + " | Mail sent to : " + to);
            response.setStatus(Boolean.TRUE);

        } catch (MessagingException | IOException | TemplateException e) {
            response.setMessage(ftlFileName + "Mail Sending failure : "+e.getMessage());
            response.setStatus(Boolean.FALSE);
        }

        return response;
    }
}












