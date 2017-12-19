package io.silverstring.web.controller.view.funding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequestMapping("/")
public class CloudFundingController {

    @RequestMapping("/funding")
    public ModelAndView funding() {
        ModelAndView mvn = new ModelAndView("funding/index");
        return mvn;
    }
}
