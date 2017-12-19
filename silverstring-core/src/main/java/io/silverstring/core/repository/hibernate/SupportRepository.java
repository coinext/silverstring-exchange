package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.hibernate.Support;
import io.silverstring.domain.hibernate.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SupportRepository extends JpaRepository<Support, Long> {
    Support findOneByIdAndParentIdNotNull(Long id);
    Page<Support> findAll(Pageable pageable);
    Page<Support> findAllByUserOrderByIdDescParentIdDescRegDtmDesc(User user, Pageable pageable);
}
