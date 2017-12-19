package io.silverstring.domain.hibernate;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Coupon {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String content;
    private String imgUrl;
    private LocalDateTime regDtm;
    private LocalDateTime delDtm;
    private Long expireHr;
    private Long underKrw;
}
