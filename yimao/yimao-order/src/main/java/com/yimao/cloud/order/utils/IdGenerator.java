package com.yimao.cloud.order.utils;

import com.yimao.cloud.base.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class IdGenerator {
    private static SimpleDateFormat idDateFormate = new SimpleDateFormat("yyMMddHHmm");
    private static SimpleDateFormat idDayFormate = new SimpleDateFormat("yyyyMMdd");
    private static Date idlastUpdate = null;
    private static Long idIndexValue = 0L;

    public IdGenerator() {
    }

    private static String randomStr() {
        return (new Random()).nextInt(999) + 100 + "";
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String hexId() {
        return Long.toHexString(System.currentTimeMillis()) + randomStr();
    }

    public static String digitId() {
        return System.currentTimeMillis() + randomStr();
    }

    public static synchronized String localSafeId() {
        int addIndex = (new Random()).nextInt(10) + 1;
        Date date = new Date();
        if (idlastUpdate == null) {
            idlastUpdate = date;
            idIndexValue = 0L;
        }

        if (!DateUtil.isToday(idlastUpdate.getTime())) {
            idlastUpdate = date;
            idIndexValue = 0L;
        }

        idIndexValue = idIndexValue + (long)addIndex;

        String endStr;
        for(endStr = idIndexValue + ""; endStr.length() < 6; endStr = "0" + endStr) {
            ;
        }

        return idDateFormate.format(date) + endStr;
    }

    public static synchronized String localSafeWorkOrderId() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return simpleDateFormat.format(new Date()) + ((new Random()).nextInt(9000) + 1000);
    }

    public static synchronized String radixId() {
        return Long.toString(new Long(localSafeId()), 36);
    }
}
