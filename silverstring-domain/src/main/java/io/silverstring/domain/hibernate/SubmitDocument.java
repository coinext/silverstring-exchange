package io.silverstring.domain.hibernate;

import io.silverstring.domain.enums.StatusEnum;
import lombok.Data;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class SubmitDocument {

    @Id
    private Long userId;

    @OneToOne
    @JoinColumn(name="userId")
    @Lazy
    private User user;

    private String levelIdcardUrlHash;
    private String levelDocUrlHash;
    private LocalDateTime completeDtm;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    private LocalDateTime regDtm;
    private String reason;
}
