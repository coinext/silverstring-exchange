package io.silverstring.domain.hibernate;

import io.silverstring.domain.enums.ActiveEnum;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@IdClass(FingerPrintPK.class)
public class FingerPrint {

    @Id
    private Long userId;

    @Id
    private String hashKey;

    private LocalDateTime regDtm;
    private LocalDateTime delDtm;

    @Enumerated(EnumType.STRING)
    private ActiveEnum active;
}
