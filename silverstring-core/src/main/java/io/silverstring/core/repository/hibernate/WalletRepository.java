package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.Wallet;
import io.silverstring.domain.hibernate.WalletPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface WalletRepository extends JpaRepository<Wallet, WalletPK> {
    Wallet findOneByCoinAndDepositDvcd(Coin coin, String depositDvcd);
    Wallet findOneByCoinAndAddress(Coin coin, String address);
    List<Wallet> findAllByUserId(Long userId);
    Wallet findOneByCoinAndUserId(Coin coin, Long userId);
    Optional<Wallet> findOneByUserIdAndCoin(Long userId, Coin coin);
    List<Wallet> findAllByCoin(Coin coin);
    Page<Wallet> findAllByOrderByRegDtDesc(Pageable pageable);
}
