package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.enums.CategoryEnum;
import io.silverstring.domain.enums.StatusEnum;
import io.silverstring.domain.hibernate.ManualTransaction;
import io.silverstring.domain.hibernate.ManualTransactionPK;
import io.silverstring.domain.hibernate.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ManualTransactionRepository extends JpaRepository<ManualTransaction, ManualTransactionPK> {
    List<ManualTransaction> findAllByCategoryAndStatus(CategoryEnum cotegory, StatusEnum status);
    Page<ManualTransaction> findAllByCategoryOrderByRegDtDesc(CategoryEnum cotegory, Pageable pageable);
    ManualTransaction findByIdAndUserId(String id, Long userId);
}
