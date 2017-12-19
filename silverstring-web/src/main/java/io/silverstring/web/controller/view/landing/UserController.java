package io.silverstring.web.controller.view.landing;

import io.silverstring.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/doRegist", method= RequestMethod.POST)
    public String doSignup(
            @RequestParam("email") String email
            , @RequestParam("pwd") String pwd
            , @RequestParam("fingerprint") String fingerprint) {
        try {
            log.info("doRegist : {}", email);
            return userService.doRegist(email, pwd, fingerprint);
        } catch (Exception ex) {
            return "redirect:/regist?msg=invalid";
        }
    }
}
