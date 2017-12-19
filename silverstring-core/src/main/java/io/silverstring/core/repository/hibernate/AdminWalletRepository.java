package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.enums.CoinEnum;
import io.silverstring.domain.enums.WalletType;
import io.silverstring.domain.hibernate.AdminWallet;
import io.silverstring.domain.hibernate.AdminWalletPK;
import io.silverstring.domain.hibernate.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AdminWalletRepository extends JpaRepository<AdminWallet, AdminWalletPK> {
    Optional<AdminWallet> findOneByCoinNameAndType(CoinEnum coinName, WalletType type);
}
