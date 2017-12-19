package io.silverstring.domain.hibernate;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

@Data
public class TransactionPK implements Serializable {

    @Id
    private String id;

    @Id
    private Long userId;
}
