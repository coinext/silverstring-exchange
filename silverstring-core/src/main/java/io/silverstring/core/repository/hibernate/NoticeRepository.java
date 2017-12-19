package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.hibernate.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findAllByDelDtmIsNullOrderByRegDtmDesc(Pageable pageable);
}
