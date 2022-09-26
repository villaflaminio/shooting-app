package org.rconfalonieri.nzuardi.shootingapp.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.rconfalonieri.nzuardi.shootingapp.model.dto.MailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.activation.URLDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

            String qrCode = "iVBORw0KGgoAAAANSUhEUgAAAV4AAAFeAQMAAAD35eVZAAAABlBMVEUAAAD///+l2Z/dAAAAAWJLR0QB/wIt3gAAAeFJREFUaN7t2juSgzAQBNB2ERDqCByFo4mjcRQdgVABRW8wM9hrez8J0m5VK6LgOZqSNGoZ/P3YISwsLCwsLPw38AYbE+tAIq9IFQC5+ocs3B6PJMl12kYeQGbBuN9IrhNJUrgL3gHkdbJS5hWpDke8uQl3xawAuGAivYLC3bHPLxaMJH8qt/C1ONY6DAdIFtjD9wuj8KU4uoJt3G9WOH/4roUQvhSfY4O3asDA5yHcFPtsWpG43wgA8IkGjDsg3AGTO6Jttq4gShnHHOFe2HsAxwe4zLTmjcLtsb8piSR9J7LTDSDcCZO+74wkgflTSnO86QqEL8e+snkUg7xOJA8Ac0l25BRujkke4AIAwwFgvtfV55dwc2yhJeYSuKQKM3H2FG6PfbtJ58pWo5RVuA8+P0WkbMd/LoD1CVm4Od7iquX+cB7/X7sC4SbYUv1lLql6DoBIkuOOTLg13iLV9/mFiSQ9nHnp64Sb4HP4r1gQhXvb1wk3wA85v2E+BP58biGE2+C4EWONoMywV1C4C44L/diJgIH3wP+L23/hJjjCmbOmb3cr4aaY3GHxvudjXsos3AFbvexPYlyAVGH3lYlfbkDC1+KzKwAAZJZUBxKYrWcT7oB/N4SFhYWFhYX/Gf4AT8YBIH5wdHUAAAAASUVORK5CYII=";

            // Convert Base64 String to File
            ClassPathResource cpr = new ClassPathResource("mail-templates/images/qr-code.png");
            Files.write(Paths.get(cpr.getURL().toString().substring(6,cpr.getURL().toString().length()-1)), Base64.decode(qrCode.getBytes()));

            helper.addInline("qr-code.png", new File(cpr.getURL().toString().substring(6,cpr.getURL().toString().length()-1)));
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












