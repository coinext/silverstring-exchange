package io.silverstring.web.controller.view.dashboard;

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
    public ModelAndView home(@ModelAttribute User user, @RequestParam(value = "selectionCoin", defaultValue = "BITCOIN") String coin) {
        ModelAndView mvn = new ModelAndView("dashboard/home");
        mvn.addObject("selectionCoin", coin);
        mvn.addObject("notices", noticeService.getTopNNotices(3));
        mvn.addObject("newsList", newsService.getTopNNews(3));
        mvn.addObject("currentCoinInfos", coinService.getAllCoinAvgPrices());
        mvn.addObject("recommendIcoSites", icoRecommendService.getAll());
        return mvn;
    }

    @RequestMapping("/deposit_manage")
    public ModelAndView depositManage(@ModelAttribute User user, @RequestParam(value = "selectionCoin", defaultValue = "BITCOIN") String coin) {
        ModelAndView mvn = new ModelAndView("dashboard/deposit_manage");
        mvn.addObject("selectionCoin", coin);
        mvn.addObject("walletInfos", walletService.getMyWallets(user.getId(), user.getLevel()));
        return mvn;
    }

    @RequestMapping("/withdrawal_manage")
    public ModelAndView withdrawalManage(@ModelAttribute User user, @RequestParam(value = "selectionCoin", defaultValue = "BITCOIN") String coin) {
        ModelAndView mvn = new ModelAndView("dashboard/withdrawal_manage");
        mvn.addObject("selectionCoin", coin);
        mvn.addObject("walletInfos", walletService.getMyWallets(user.getId(), user.getLevel()));
        return mvn;
    }

    @RequestMapping("/myinfo_manage")
    public ModelAndView myInfoManage(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/myinfo_manage");
        return mvn;
    }

    @RequestMapping("/auth_manage")
    public ModelAndView authManage(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/auth_manage");
        mvn.addObject("levels", levelService.getAllGroups());
        mvn.addObject("walletInfos", walletService.getMyWallets(user.getId(), user.getLevel()));
        return mvn;
    }

    @RequestMapping("/access_manage")
    public ModelAndView accessManage(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/access_manage");
        return mvn;
    }

    @RequestMapping("/level_manage")
    public ModelAndView levelManage(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/level_manage");
        return mvn;
    }

    @RequestMapping("/simple_trade")
    public ModelAndView simpleTrade(@ModelAttribute User user, @RequestParam(value = "selectionCoin", defaultValue = "BITCOIN") String coin) {
        ModelAndView mvn = new ModelAndView("dashboard/simple_trade");
        mvn.addObject("selectionCoin", coin);
        mvn.addObject("coinInfo", coinService.getActiveCoin(CoinEnum.valueOf(coin)));
        mvn.addObject("walletInfos", walletService.getMyWallets(user.getId(), user.getLevel()));
        mvn.addObject("krwWallet", walletService.get(user.getId(), new Coin(CoinEnum.KRW)));
        mvn.addObject("wallet", walletService.get(user.getId(), new Coin(coin)));
        return mvn;
    }

    @RequestMapping("/trade")
    public ModelAndView trade(@ModelAttribute User user, @RequestParam(value = "selectionCoin", defaultValue = "BITCOIN") String coin) {
        ModelAndView mvn = new ModelAndView("dashboard/trade");
        mvn.addObject("selectionCoin", coin);
        mvn.addObject("coinInfo", coinService.getActiveCoin(CoinEnum.valueOf(coin)));
        mvn.addObject("walletInfos", walletService.getMyWallets(user.getId(), user.getLevel()));
        mvn.addObject("krwWallet", walletService.get(user.getId(), new Coin(CoinEnum.KRW)));
        mvn.addObject("wallet", walletService.get(user.getId(), new Coin(coin)));
        return mvn;
    }


    @RequestMapping("/assets_manage")
    public ModelAndView assetsManage(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/assets_manage");
        mvn.addObject("walletInfos", walletService.getMyWallets(user.getId(), user.getLevel()));
        mvn.addObject("krwWallet", walletService.get(user.getId(), new Coin(CoinEnum.KRW)));
        return mvn;
    }

    @RequestMapping("/trade_history")
    public ModelAndView tradeHistory(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/trade_history");
        return mvn;
    }

    @RequestMapping("/support_manage")
    public ModelAndView supportManage(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/support_manage");
        return mvn;
    }

    @RequestMapping("/faq_manage")
    public ModelAndView faqManage(@ModelAttribute User user) {
        ModelAndView mvn = new ModelAndView("dashboard/faq_manage");
        return mvn;
    }
}
