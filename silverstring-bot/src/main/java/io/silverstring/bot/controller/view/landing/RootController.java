package io.silverstring.bot.controller.view.landing;

import io.silverstring.core.service.UserService;
import io.silverstring.domain.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Slf4j
@Controller
@RequestMapping("/")
public class RootController {

    final UserService userService;

    @Autowired
    public RootController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public ModelAndView root() {
        ModelAndView mvn = new ModelAndView("landing/index");
        return mvn;
    }

    @RequestMapping("/login")
    public ModelAndView login(@RequestParam(name = "msg", defaultValue = "none") String msg) {
        ModelAndView mvn = new ModelAndView("landing/login");
        mvn.addObject("msg", msg);
        return mvn;
    }

    @RequestMapping("/logout")
    public ModelAndView logout() {
        ModelAndView mvn = new ModelAndView("landing/logout");
        return mvn;
    }
}
