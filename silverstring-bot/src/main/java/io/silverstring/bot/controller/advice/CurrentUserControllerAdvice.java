package io.silverstring.bot.controller.advice;

import io.silverstring.core.repository.hibernate.UserRepository;
import io.silverstring.core.service.CoinService;
import io.silverstring.domain.dto.CoinDTO;
import io.silverstring.domain.dto.CurrentUserDTO;
import io.silverstring.domain.hibernate.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@Slf4j
@ControllerAdvice(basePackages = "io.silverstring.bot.controller")
@Order(1)
public class CurrentUserControllerAdvice {

    final UserRepository userRepository;
    final CoinService coinService;

    @Autowired
    public CurrentUserControllerAdvice(UserRepository userRepository, CoinService coinService) {
        this.userRepository = userRepository;
        this.coinService = coinService;
    }

    @ModelAttribute("user")
    public User getCurrentUser(Authentication authentication) {
        return (authentication == null) ? null : ((CurrentUserDTO) authentication.getPrincipal()).getUser();
    }

    @ModelAttribute("version")
    public long getVersion(Authentication authentication) {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        return timestamp.getTime();
    }
}
