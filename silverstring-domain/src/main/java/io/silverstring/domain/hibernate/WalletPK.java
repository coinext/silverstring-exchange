package io.silverstring.domain.hibernate;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

@Data
public class WalletPK implements Serializable {
    @Id
    private Long id;
    @Id
    private Long userId;
}
