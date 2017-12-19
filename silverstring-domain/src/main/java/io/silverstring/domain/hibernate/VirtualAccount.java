package io.silverstring.domain.hibernate;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;


@Data
@Entity
public class VirtualAccount {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;
    private String account;
    private String bankName;
    private String bankCode;
    private String recvCorpNm;
    private LocalDateTime regDtm;
    private LocalDateTime allocDtm;
}
