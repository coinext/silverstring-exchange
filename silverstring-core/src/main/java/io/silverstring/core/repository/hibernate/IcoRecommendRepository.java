package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.hibernate.IcoRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IcoRecommendRepository extends JpaRepository<IcoRecommend, Long> {
}
