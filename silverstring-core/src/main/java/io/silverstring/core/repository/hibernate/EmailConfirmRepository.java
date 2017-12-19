package io.silverstring.core.repository.hibernate;

import io.silverstring.domain.hibernate.EmailConfirm;
import io.silverstring.domain.hibernate.EmailConfirmPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EmailConfirmRepository extends JpaRepository<EmailConfirm, EmailConfirmPK> {
    EmailConfirm findOneByHashEmail(String hashEmail);
    EmailConfirm findOneByEmail(String email);
}
