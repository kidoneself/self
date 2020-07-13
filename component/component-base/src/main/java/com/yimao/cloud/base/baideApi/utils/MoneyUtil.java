package com.yimao.cloud.base.baideApi.utils;


import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;

public class MoneyUtil {
    public MoneyUtil() {
    }

    public static double getDoubleMoney(int money) {
        BigDecimal b1 = new BigDecimal(money);
        return b1.divide(new BigDecimal(100)).doubleValue();
    }

    public static int getIntegerMoney(double money) {
        BigDecimal b1 = new BigDecimal(money);
        return b1.multiply(new BigDecimal(100)).intValue();
    }

    public static int add(int... moneys) {
        if (!ObjectUtils.isEmpty(moneys) && moneys.length > 0) {
            BigDecimal b1 = new BigDecimal(0);
            int[] var2 = moneys;
            int var3 = moneys.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                double money = (double)var2[var4];
                b1 = b1.add(new BigDecimal(money));
            }

            return b1.intValue();
        } else {
            return 0;
        }
    }
}
