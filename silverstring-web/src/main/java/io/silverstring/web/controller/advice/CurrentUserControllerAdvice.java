package io.silverstring.web.controller.advice;

import io.silverstring.core.repository.hibernate.UserRepository;
import io.silverstring.core.service.CoinService;
import io.silverstring.domain.dto.CoinDTO;
import io.silverstring.domain.dto.CurrentUserDTO;
import io.silverstring.domain.hibernate.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice(basePackages = "io.silverstring.web.controller")
@Order(1)
public class CurrentUserControllerAdvice {

    final UserRepository userRepository;
    final CoinService coinService;
    final Environment environment;

    @Autowired
    public CurrentUserControllerAdvice(UserRepository userRepository, CoinService coinService, Environment environment) {
        this.userRepository = userRepository;
        this.coinService = coinService;
        this.environment = environment;
    }

    @ModelAttribute("user")
    public User getCurrentUser(Authentication authentication) {
        return (authentication == null) ? null : ((CurrentUserDTO) authentication.getPrincipal()).getUser();
    }

    @ModelAttribute("version")
    public long getVersion() {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        return timestamp.getTime();
    }

    @ModelAttribute("baseurl")
    public String getBaseUrl() {
        return environment.getProperty("base.url");
    }

    @ModelAttribute("coins")
    public CoinDTO.ResInfo getCoins() {
        return coinService.getCoins();
    }
}
