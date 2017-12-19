package io.silverstring.admin.service;

import io.silverstring.core.repository.hibernate.ManualTransactionRepository;
import io.silverstring.core.repository.hibernate.UserRepository;
import io.silverstring.core.repository.hibernate.WalletRepository;
import io.silverstring.domain.dto.DashboardDTO;
import io.silverstring.domain.enums.ActiveEnum;
import io.silverstring.domain.enums.CategoryEnum;
import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.StatusEnum;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.ManualTransaction;
import io.silverstring.domain.hibernate.User;
import io.silverstring.domain.hibernate.Wallet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@Service
public class DashboardService {

    final UserRepository userRepository;
    final WalletRepository walletRepository;
    final ManualTransactionRepository manualTransactionRepository;

    public DashboardService(UserRepository userRepository, WalletRepository walletRepository, ManualTransactionRepository manualTransactionRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.manualTransactionRepository = manualTransactionRepository;
    }

    public DashboardDTO.ResInfo getDashboardInfo() {
        List<User> users = userRepository.findAllByDelDtmIsNullAndActive(ActiveEnum.Y);

        List<Wallet> krwWallets = walletRepository.findAllByCoin(new Coin(CoinEnum.KRW));
        BigDecimal totoalKrwTotalBal = BigDecimal.ZERO;
        for (Wallet wallet : krwWallets) {
            totoalKrwTotalBal = totoalKrwTotalBal.add(wallet.getTotalBalance());
        }

        List<ManualTransaction> depositTransactions = manualTransactionRepository.findAllByCategoryAndStatus(CategoryEnum.receive, StatusEnum.PENDING);
        List<ManualTransaction> withdrawalTransactions = manualTransactionRepository.findAllByCategoryAndStatus(CategoryEnum.send, StatusEnum.PENDING);

        return DashboardDTO.ResInfo.builder()
                .userTotalCnt(users.size())
                .totoalKrwTotalBal(totoalKrwTotalBal.longValue())
                .depositWaitCnt(depositTransactions.size())
                .withdrawalWaitCnt(withdrawalTransactions.size())
                .build();
    }
}
