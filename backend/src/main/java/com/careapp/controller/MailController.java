package com.careapp.controller;

import com.careapp.service.impl.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mail")
public class MailController {
    private final EmailService emailService;
    public MailController(EmailService emailService) { this.emailService = emailService; }

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestParam String to) throws Exception {
        emailService.sendText(to, "CareTrack Test", "Hello from CareTrack via SendGrid!");
        return ResponseEntity.ok(Map.of("status","sent"));
    }
}
