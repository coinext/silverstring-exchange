package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.enums.ActiveEnum;
import io.silverstring.domain.hibernate.FingerPrint;
import io.silverstring.domain.hibernate.FingerPrintPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FingerPrintRepository extends JpaRepository<FingerPrint, FingerPrintPK> {
   FingerPrint findOneByUserIdAndHashKeyAndDelDtmIsNull(Long userId, String hashKey);
   List<FingerPrint> findAllByUserIdAndDelDtmIsNullAndActive(Long userId, ActiveEnum active);
}
