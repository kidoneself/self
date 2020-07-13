package com.yimao.cloud.out.utils;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class ObjectUtil implements Serializable {
    private static final long serialVersionUID = -1370885507957292970L;

    public ObjectUtil() {
    }

    public static boolean isNull(Object o) {
        return null == o;
    }

    public static boolean isEmpty(Object o) {
        if (isNull(o)) {
            return true;
        } else {
            return isString(o) ? ((String)o).trim().equals("") : false;
        }
    }

    public static boolean isChar(Object o) {
        return o instanceof Character;
    }

    public static boolean isString(Object o) {
        return o instanceof String;
    }

    public static boolean isInteger(Object o) {
        return o instanceof Integer;
    }

    public static boolean isLong(Object o) {
        return o instanceof Long;
    }

    public static boolean isDouble(Object o) {
        return o instanceof Double;
    }

    public static boolean isFloat(Object o) {
        return o instanceof Float;
    }

    public static boolean isMap(Object o) {
        return o instanceof Map;
    }

    public static boolean isDate(Object o) {
        return o instanceof Date;
    }

    public static boolean isSqlDate(Object o) {
        return o instanceof java.sql.Date;
    }
}
