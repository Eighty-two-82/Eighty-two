package com.careapp.service.impl;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${sendgrid.api-key:}")
    private String apiKey;

    // 纯文本
    public void sendText(String to, String subject, String text) throws Exception {
        Email from = new Email("caretrack3@gmail.com", "CareTrack"); // 用已验证的 Single Sender
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", text);

        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(apiKey);
        Request req = new Request();
        req.setMethod(Method.POST);
        req.setEndpoint("mail/send");
        req.setBody(mail.build());
        Response resp = sg.api(req);

        if (resp.getStatusCode() < 200 || resp.getStatusCode() >= 300) {
            throw new RuntimeException("SendGrid error: " + resp.getStatusCode() + " - " + resp.getBody());
        }
    }

    // HTML 示例（可选）
    public void sendHtml(String to, String subject, String html) throws Exception {
        Email from = new Email("caretrack3@gmail.com", "CareTrack");
        Email toEmail = new Email(to);
        Content content = new Content("text/html", html);

        Mail mail = new Mail(from, subject, toEmail, content);
        SendGrid sg = new SendGrid(apiKey);
        Request req = new Request();
        req.setMethod(Method.POST);
        req.setEndpoint("mail/send");
        req.setBody(mail.build());
        Response resp = sg.api(req);

        if (resp.getStatusCode() < 200 || resp.getStatusCode() >= 300) {
            throw new RuntimeException("SendGrid error: " + resp.getStatusCode() + " - " + resp.getBody());
        }
    }
}
