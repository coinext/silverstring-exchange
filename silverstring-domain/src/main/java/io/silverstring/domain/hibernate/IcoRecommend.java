package io.silverstring.domain.hibernate;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class IcoRecommend {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String url;
    private String content;
    private String email;
    private String imgUrl;
}
