package io.silverstring.domain.hibernate;

import io.silverstring.domain.enums.ActiveEnum;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@IdClass(EmailConfirmPK.class)
public class EmailConfirm {

    @Id
    private String hashEmail;

    @Id
    private String code;
    private String email;

    @Enumerated(EnumType.STRING)
    private ActiveEnum sendYn;

    private LocalDateTime regDtm;
}
