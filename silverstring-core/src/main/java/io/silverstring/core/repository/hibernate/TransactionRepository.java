package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.enums.CategoryEnum;
import io.silverstring.domain.enums.StatusEnum;
import io.silverstring.domain.hibernate.Coin;
import io.silverstring.domain.hibernate.Transaction;
import io.silverstring.domain.hibernate.TransactionPK;
import org.apache.tomcat.jni.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, TransactionPK> {
    Page<Transaction> findAllByCoinAndCategoryAndStatus(Coin coin, CategoryEnum category, StatusEnum status, Pageable pageable);
    Page<Transaction> findAllByUserIdAndCoinAndCategoryOrderByRegDtDesc(Long userId, Coin coin, CategoryEnum category, Pageable pageable);
    Transaction findOneByCoinAndTxIdAndStatus(Coin coin, String txId, StatusEnum Status);
    Transaction findOneByCoinAndTxId(Coin coin, String txId);
}
