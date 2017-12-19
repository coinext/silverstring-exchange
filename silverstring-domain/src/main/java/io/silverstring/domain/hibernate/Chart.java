package io.silverstring.domain.hibernate;

import io.silverstring.domain.enums.CoinEnum;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@IdClass(ChartPK.class)
public class Chart {

    @Id
    private String dt;
    @Id
    @Enumerated(EnumType.STRING)
    private CoinEnum coin;

    private BigDecimal price;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal volume;
    private BigDecimal adjClose;

    @Transient
    public List<Object> getRow() {
        List<Object> row = new ArrayList<>();
        //log.info("======> {}", dt);
        Timestamp time = Timestamp.valueOf(dt + ":00");
        row.add(time.getTime());
        row.add(open);
        row.add(high);
        row.add(low);
        row.add(close);
        return row;
    }
}
