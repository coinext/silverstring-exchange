package io.silverstring.core.service;

import com.google.common.collect.Lists;
import io.silverstring.core.annotation.SoftTransational;
import io.silverstring.domain.hibernate.EmailConfirm;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import it.ozimov.springboot.mail.service.exception.CannotSendEmailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class UmsService {

    @Value("${spring.mail.username}")
    public String ADMIN_EMAIL;

    @Value("${base.url}")
    public String BASE_URL;

    private final EmailService emailService;

    @Autowired
    public UmsService(EmailService emailService) {
        this.emailService = emailService;
    }

    @SoftTransational
    public void emailConfirmEmailSend(EmailConfirm emailConfirm) throws UnsupportedEncodingException, CannotSendEmailException {
        final Email email = DefaultEmail.builder()
                .from(new InternetAddress(ADMIN_EMAIL, "Coinext"))
                .to(Lists.newArrayList(new InternetAddress(emailConfirm.getEmail(), emailConfirm.getEmail())))
                .subject("Coinext 회원가입 인증메일입니다.")
                .body("")
                .encoding("UTF-8").build();

        final Map<String, Object> modelObject = new HashMap<>();
        modelObject.put("confirm_url", BASE_URL + "/emailConfirm" + "?hash=" + emailConfirm.getHashEmail() + "&code=" + emailConfirm.getCode());
        emailService.send(email, "common/emailConfirmTemplate", modelObject);
    }

    public void emailSupport(String toEmail, String title, String content) throws UnsupportedEncodingException, CannotSendEmailException {
        final Email email = DefaultEmail.builder()
                .from(new InternetAddress(ADMIN_EMAIL, "Coinext"))
                .to(Lists.newArrayList(new InternetAddress(toEmail, toEmail)))
                .subject(title)
                .body("")
                .encoding("UTF-8").build();

        final Map<String, Object> modelObject = new HashMap<>();
        modelObject.put("title", title);
        modelObject.put("content", content);
        emailService.send(email, "common/supportEmailTemplate", modelObject);
    }
}
