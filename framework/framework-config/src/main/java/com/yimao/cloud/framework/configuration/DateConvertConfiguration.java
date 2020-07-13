package com.yimao.cloud.framework.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Spring MVC String转Date类型全局转换器
 *
 * @author Zhang Bo
 * @date 2018/11/27.
 */
@Component
public class DateConvertConfiguration implements Converter<String, Date> {

    private static final String format1 = "yyyy-MM";
    private static final String format2 = "yyyy-MM-dd";
    private static final String format3 = "yyyy-MM-dd HH:mm";
    private static final String format4 = "yyyy-MM-dd HH:mm:ss";

    /**
     * Spring MVC String转Date类型全局转换器
     *
     * @param source
     * @return
     */
    @Override
    public Date convert(String source) {
        String value = source.trim();
        if ("".equals(value)) {
            return null;
        }
        if (source.matches("^\\d{4}-\\d{1,2}$")) {
            return parseDate(source, format1);
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            return parseDate(source, format2);
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$")) {
            return parseDate(source, format3);
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return parseDate(source, format4);
        } else {
            throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
        }
    }

    /**
     * @param dateStr 字符类型的日期
     * @param format  转换格式
     * @return
     */
    private Date parseDate(String dateStr, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.parse(dateStr);
        } catch (Exception e) {

        }
        return null;
    }

}
