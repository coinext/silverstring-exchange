package io.silverstring.domain.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public class ChartDTO {
    private String dt;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;

    public ChartDTO(String dt, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close) {
        this.dt = dt;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }

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
