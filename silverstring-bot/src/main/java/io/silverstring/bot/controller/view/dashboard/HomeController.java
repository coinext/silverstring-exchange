package io.silverstring.bot.controller.view.dashboard;

import io.silverstring.core.service.*;
import io.silverstring.domain.hibernate.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Slf4j
@Controller
@RequestMapping("/")
public class HomeController {

    private final NoticeService noticeService;
    private final NewsService newsService;
    private final WalletService walletService;
    private final CoinService coinService;
    private final LevelService levelService;
    private final IcoRecommendService icoRecommendService;

    @Autowired
    public HomeController(NoticeService noticeService, NewsService newsService, WalletService walletService, CoinService coinService, LevelService levelService, IcoRecommendService icoRecommendService) {
        this.noticeService = noticeService;
        this.newsService = newsService;
        this.walletService = walletService;
        this.coinService = coinService;
        this.levelService = levelService;
        this.icoRecommendService = icoRecommendService;
    }

    @RequestMapping("/dashboard")
    public ModelAndView home(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/home");
        return mvn;
    }

    @RequestMapping("/connect_setting")
    public ModelAndView connectSEtting(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/connect_setting");
        return mvn;
    }

    @RequestMapping("/report")
    public ModelAndView report(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/report");
        return mvn;
    }

    @RequestMapping("/trade_setting")
    public ModelAndView tradeSetting(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/trade_setting");
        return mvn;
    }
}
