package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.enums.ActiveEnum;
import io.silverstring.domain.hibernate.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findOneByEmail(String email);
    User findOneByEmailAndDelDtmIsNullAndActive(String email, ActiveEnum active);
    User findOneByEmailAndPwdAndDelDtmIsNullAndActive(String email, String pwd, ActiveEnum active);
    List<User> findAllByDelDtmIsNullAndActive(ActiveEnum active);
}
