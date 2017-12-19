package io.silverstring.bot.service;

import io.silverstring.core.service.ActionLogService;
import io.silverstring.core.service.FingerPrintService;
import io.silverstring.core.service.UserService;
import io.silverstring.domain.dto.CurrentUserDTO;
import io.silverstring.domain.enums.RoleEnum;
import io.silverstring.domain.enums.TagEnum;
import io.silverstring.domain.hibernate.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service
public class CurrentUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final FingerPrintService fingerPrintService;
    private final ActionLogService actionLogService;

    @Autowired
    public CurrentUserDetailsService(UserService userService, FingerPrintService fingerPrintService, ActionLogService actionLogService) {
        this.userService = userService;
        this.fingerPrintService = fingerPrintService;
        this.actionLogService = actionLogService;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Authenticating user with loginId={}", email);
        User user = userService.getActiveUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email=%s was not found", email)));

        if (!RoleEnum.ADMIN.equals(user.getRole())) {
            throw new UsernameNotFoundException("접근허용되지 USER입니다.");
        }


        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String fingerprint = attr.getRequest().getParameter("fingerprint");

        if (!fingerPrintService.avaliableDeviceAccess(user.getId(), fingerprint)) {
            //throw new UsernameNotFoundException("접근허용되지 않은 디바이스입니다.");
        }
        //action logging
        actionLogService.log(user.getId(), TagEnum.LOGIN);
        return new CurrentUserDTO(user);
    }
}
