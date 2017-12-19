package io.silverstring.core.service.batch;

import io.silverstring.core.annotation.HardTransational;
import io.silverstring.core.repository.hibernate.UserRepository;
import io.silverstring.core.repository.hibernate.WalletRepository;
import io.silverstring.domain.hibernate.User;
import io.silverstring.domain.hibernate.Wallet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;


@Slf4j
@Service
public class ResetWithdrawalLimitTotalBalanceBatchService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Autowired
    public ResetWithdrawalLimitTotalBalanceBatchService(WalletRepository walletRepository, UserRepository userRepository, EntityManager entityManager) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @HardTransational
    public void dailyReset() {
        List<User> users = userRepository.findAll();
        if (users == null) return;

        for (User user : users) {
            List<Wallet> wallets = walletRepository.findAllByUserId(user.getId());
            if (wallets == null) continue;

            for (Wallet wallet : wallets) {
                wallet.setTodayWithdrawalTotalBalance(BigDecimal.ZERO);
            }

            entityManager.flush();
        }
    }
}
