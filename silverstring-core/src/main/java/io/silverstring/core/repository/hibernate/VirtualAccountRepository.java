package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.hibernate.VirtualAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VirtualAccountRepository extends JpaRepository<VirtualAccount, Long> {
    Page<VirtualAccount> findAllByAllocDtmIsNull(Pageable pageable);
}
