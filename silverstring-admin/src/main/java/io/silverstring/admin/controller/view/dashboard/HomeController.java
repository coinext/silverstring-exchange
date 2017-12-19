package io.silverstring.admin.controller.view.dashboard;

import io.silverstring.core.service.*;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        mvn.addObject("notices", noticeService.getTopNNotices(3));
        mvn.addObject("newsList", newsService.getTopNNews(3));
        mvn.addObject("currentCoinInfos", coinService.getAllCoinAvgPrices());
        mvn.addObject("recommendIcoSites", icoRecommendService.getAll());
        return mvn;
    }

    @RequestMapping("/notice_manage")
    public ModelAndView notice_manage(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/notice_manage");
        return mvn;
    }

    @RequestMapping("/news_manage")
    public ModelAndView news_manage(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/news_manage");
        return mvn;
    }

    @RequestMapping("/wallet_manage")
    public ModelAndView walletManage(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/wallet_manage");
        return mvn;
    }

    @RequestMapping("/deposit_manage")
    public ModelAndView depositManage(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/deposit_manage");
        return mvn;
    }

    @RequestMapping("/withdrawal_manage")
    public ModelAndView withdrawalManage(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/withdrawal_manage");
        return mvn;
    }

    @RequestMapping("/level_manage")
    public ModelAndView levelManage(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/level_manage");
        return mvn;
    }

    @RequestMapping("/doc_manage")
    public ModelAndView docManage(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/doc_manage");
        return mvn;
    }

    @RequestMapping("/coupon_manage")
    public ModelAndView couponManage(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/coupon_manage");
        return mvn;
    }

    @RequestMapping("/support_manage")
    public ModelAndView supportManage(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/support_manage");
        return mvn;
    }
}
