package io.silverstring.domain.util;

import java.math.BigDecimal;

public class DataUtil {

    public static String rtrim(String str){
        int len = str.length();
        while ((0 < len) && (str.charAt(len-1) <= '0'))
        {
            len--;
        }
        return str.substring(0, len);
    }

    public static String decimal(BigDecimal val) {
        String[] _val = val.toPlainString().split("\\.");
        if (_val.length == 2) {
            return _val[0] + "." + ("".equals(DataUtil.rtrim(_val[1])) ? "0" : DataUtil.rtrim(_val[1]));
        }
        return val.toPlainString();
    }
}
