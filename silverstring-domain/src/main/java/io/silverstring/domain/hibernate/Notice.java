package io.silverstring.domain.hibernate;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class Notice {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String content;
    private String url;

    private LocalDateTime regDtm;
    private LocalDateTime delDtm;
}
