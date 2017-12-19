package io.silverstring.domain.hibernate;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

@Data
public class FingerPrintPK implements Serializable {
    @Id
    private Long userId;

    @Id
    private String hashKey;

}
