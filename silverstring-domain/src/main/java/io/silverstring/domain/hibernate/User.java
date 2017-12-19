package io.silverstring.domain.hibernate;

import io.silverstring.domain.enums.ActiveEnum;
import io.silverstring.domain.enums.LevelEnum;
import io.silverstring.domain.enums.RoleEnum;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
public class User implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    private String email;
    private String pwd;

    @Enumerated(EnumType.STRING)
    private LevelEnum level;

    @Enumerated(EnumType.STRING)
    private ActiveEnum active;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    private String otpHash;

    private LocalDateTime regDtm;
    private LocalDateTime delDtm;
}
