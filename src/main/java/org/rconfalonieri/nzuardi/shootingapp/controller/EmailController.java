package org.rconfalonieri.nzuardi.shootingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.rconfalonieri.nzuardi.shootingapp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/email")
@RestController
@Tag(name = "Email")
public class EmailController {

    @Autowired
    EmailService emailService;

    @Operation(summary = "sendSetPassword", description = "send set password email")
    @GetMapping("/sendSetPassword")
    public ResponseEntity<?> sendSetPassword() {
        Map<String, Object> model = new HashMap<>();
        model.put("name", "Raffaele");
        model.put("indirizzo", "www.shootingapp.it/setPassword");
        return ResponseEntity.ok(emailService.sendEmail("raffaeleconfalonieri@gmail.com", "Shooting App | Imposta la tua password", model, "setPassword"));
    }
}