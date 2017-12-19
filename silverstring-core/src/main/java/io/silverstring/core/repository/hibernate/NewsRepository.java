package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.hibernate.News;
import io.silverstring.domain.hibernate.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    Page<News> findAllByDelDtmIsNullOrderByRegDtmDesc(Pageable pageable);
}
