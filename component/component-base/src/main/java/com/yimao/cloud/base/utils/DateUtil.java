package com.yimao.cloud.base.utils;

import com.yimao.cloud.base.exception.BadRequestException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * created by liuhao@yimaokeji.com
 * 2017122017/12/4
 */
public class DateUtil {

    // 默认时间格式化参数
    public static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd";

    public static final String defaultDateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    public static final String MONTH_TIME_FORMAT = "yyyy-MM";

    public static final String DATEFORMAT_01 = "yyyy年MM月dd日";

    public static final String DATEFORMAT_02 = "yyyy年MM月dd日 HH:mm";

    public static final String DATEFORMAT_03 = "yyyy年MM月dd日 HH时mm分ss秒";

    private static Calendar calendar = Calendar.getInstance(Locale.CHINA);

    private static final Integer[] weekDays = { 7, 1, 2, 3, 4, 5, 6 };

    private DateUtil() {
    }

//    private static DateUtil instanceDateUtil = null;
//
//    public static DateUtil getInstance() {
//        if (instanceDateUtil == null) {
//            instanceDateUtil = new DateUtil();
//        }
//        return instanceDateUtil;
//    }

    /**
     * 比较某个日期是否在某个日期区间内
     *
     * @param date
     * @param strDateBegin
     * @param strDateEnd
     * @return
     */
    public static boolean isInDate(Date date, String strDateBegin, String strDateEnd) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String strDate = sdf.format(date);   //2017-04-11
        // 截取当前时间年月日 转成整型
        int tempDate = Integer.parseInt(strDate.split("-")[0] + strDate.split("-")[1] + strDate.split("-")[2]);
        // 截取开始时间年月日 转成整型
        int tempDateBegin = Integer.parseInt(strDateBegin.split("-")[0] + strDateBegin.split("-")[1] + strDateBegin.split("-")[2]);
        // 截取结束时间年月日   转成整型
        int tempDateEnd = Integer.parseInt(strDateEnd.split("-")[0] + strDateEnd.split("-")[1] + strDateEnd.split("-")[2]);

        if ((tempDate >= tempDateBegin && tempDate <= tempDateEnd)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 比较某个时间是否在某个时间段区间内 只精确到分钟
     *
     * @param date
     * @param strDateBegin
     * @param strDateEnd
     * @return
     */
    public static boolean isInDate2(Date date, String strDateBegin, String strDateEnd) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String strDate = sdf.format(date);
        // 截取当前时间时分秒
        int strDateH = Integer.parseInt(strDate.substring(11, 13));
        int strDateM = Integer.parseInt(strDate.substring(14, 16));
        System.out.println(strDate);
//        int strDateS = Integer.parseInt(strDate.substring(17, 19));
//        System.out.println(strDateH+"="+strDateM+"="+strDateS);
        // 截取开始时间时分秒
        int strDateBeginH = Integer.parseInt(strDateBegin.substring(11, 13));
        int strDateBeginM = Integer.parseInt(strDateBegin.substring(14, 16));
//        int strDateBeginS = Integer.parseInt(strDateBegin.substring(17, 19));
//        System.out.println(strDateBeginH+"="+strDateBeginM+"="+strDateBeginS);
        // 截取结束时间时分秒
        int strDateEndH = Integer.parseInt(strDateEnd.substring(11, 13));
        int strDateEndM = Integer.parseInt(strDateEnd.substring(14, 16));
//        int strDateEndS = Integer.parseInt(strDateEnd.substring(17, 19));
//        System.out.println(strDateEndH+"="+strDateEndM+"="+strDateEndS);
        if ((strDateH >= strDateBeginH && strDateH <= strDateEndH)) {
            // 当前时间小时数在开始时间和结束时间小时数之间
            if (strDateH > strDateBeginH && strDateH < strDateEndH) {
                return true;
                // 当前时间小时数等于开始时间小时数，分钟数在开始和结束之间
            } else if (strDateH == strDateBeginH && strDateM >= strDateBeginM
                    && strDateM <= strDateEndM) {
                return true;
                // 当前时间小时数等于开始时间小时数，分钟数等于开始时间分钟数，秒数在开始和结束之间
//            } else if (strDateH == strDateBeginH && strDateM == strDateBeginM && strDateS >= strDateBeginS && strDateS <= strDateEndS) {
            } else if (strDateH == strDateBeginH && strDateM == strDateBeginM) {
                return true;
            }
            // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数小等于结束时间分钟数
            else if (strDateH >= strDateBeginH && strDateH == strDateEndH
                    && strDateM <= strDateEndM) {
                return true;
                // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数等于结束时间分钟数，秒数小等于结束时间秒数
//            } else if (strDateH >= strDateBeginH && strDateH == strDateEndH && strDateM == strDateEndM && strDateS <= strDateEndS) {
            } else if (strDateH >= strDateBeginH && strDateH == strDateEndH && strDateM == strDateEndM) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 获取当天开始的时间
     *
     * @param format 返回的字符串格式
     * @return
     */
    public static String getDayStartTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return sdf.format(cal.getTime());
    }

    /**
     * 获取当天开始的时间
     *
     * @param date
     * @return
     */
    public static Date getDayStartTime(Date date) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取当天结束的时间
     *
     * @param format 返回的字符串格式
     * @return
     */
    public static String getDayEndTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return sdf.format(cal.getTime());
    }

    /**
     * 获取当天结束的时间
     *
     * @param date 日期
     * @return
     */
    public static Date getDayEndTime(Date date) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * 按指定格式，格式化指定的日期，并以字符串形式返回。
     */
    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(defaultDateTimeFormat);
        return formatter.format(date);
    }


    /**
     * 格式化当前时间并以date形式。
     *
     * @param pattern 构造DateFormat的格式化串等.如果参数为空，默认格式化参数是"yyyy-MM-dd"
     * @return 当前时间的字符串形式
     */
    public final static Date formatCurrentTime2Date(String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String date_str = formatter.format(new Date());
        Date date = null;
        try {
            date = formatter.parse(date_str);
        } catch (Exception e) {
        }
        return date;
    }

    /**
     * 格式化当前时间并以字符串形式。
     *
     * @param pattern 构造DateFormat的格式化串等.如果参数为空，默认格式化参数是"yyyy-MM-dd"
     * @return 当前时间的字符串形式
     */
    public final static String formatCurrentTime(String pattern) {
        return transferDateToString(new Date(), pattern);
    }

    /**
     * 采用默认格式，格式化指定的日期，并以字符串形式返回。
     *
     * @param formatDate 要格式化为字符串类型的日期。
     * @return 格式化后的字符串类型的日期。采用默认格式化参数"yyyy-MM-dd"
     */
    public final static String transferDateToString(Date formatDate) {
        return transferDateToString(formatDate, null);
    }

    /**
     * 按指定格式，格式化指定的日期，并以字符串形式返回。
     *
     * @param formatDate 要格式化为字符串类型的日期。
     * @param pattern    构造DateFormat的格式化串，如果参数为空，默认格式化参数是"yyyy-MM-dd"
     * @return 格式化后的字符串类型的日期。
     */
    public final static String transferDateToString(Date formatDate, String pattern) {
        if (formatDate == null) {
            throw new IllegalArgumentException("日期对象参数不能为空");
        }
        pattern = isEmpty(pattern) ? DEFAULT_TIME_FORMAT : pattern;
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);

        return formatter.format(formatDate);
    }

    /**
     * 采用默认格式，将日期格式字符串转换成java.util.Date对象。
     *
     * @param strDate 日期格式字符串
     * @return 转换后的java.util.Date对象。日期字符串的格式是默认格式"yyyy-MM-dd"。
     */
    public final static Date transferStringToDate(String strDate) {
        return transferStringToDate(strDate, null);
    }

    /**
     * 将日期格式字符串转换成java.util.Date对象。
     *
     * @param strDate 日期格式字符串
     * @param pattern 构造DateFormat的格式化串，如果参数为空，默认格式化参数是"yyyy-MM-dd"
     * @return 转换后的java.util.Date对象。如果指定的日期格式字符串不能被解析,返回 null
     */
    public final static Date transferStringToDate(String strDate, String pattern) {
        if (strDate == null) {
            throw new IllegalArgumentException("日期格式字符串不能为空");
        }
        pattern = isEmpty(pattern) ? DEFAULT_TIME_FORMAT : pattern;
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        try {
            return formatter.parse(strDate);
        } catch (Exception e) {
            throw new RuntimeException("日期字符串格式错误", e);
        }
    }

    /**
     * @param strDate
     * @param pattern
     * @return String  返回类型
     * @throws
     * @Title: transferStringToDateStr
     * @Description: 根据指定日期格式及日期字符串，取得日期字符串
     */
    public final static String transferStringToDateStr(String strDate, String pattern) {
        if (strDate == null) {
            throw new IllegalArgumentException("日期格式字符串不能为空");
        }
        pattern = isEmpty(pattern) ? DEFAULT_TIME_FORMAT : pattern;
        SimpleDateFormat md = new SimpleDateFormat(pattern);
        Date d = null;
        try {
            d = md.parse(strDate);
            md.applyPattern("yyyy/MM/dd");
            d = md.parse(strDate);
        } catch (Exception e1) {
            e1.printStackTrace();
            throw new RuntimeException("日期字符串格式错误", e1);
        }
        md.applyPattern(pattern);
        strDate = md.format(d);
        return strDate;
    }

    /**
     * 将时间字符串按照指定的格式转换成想要的格式
     *
     * @param strDate
     * @param formPattern
     * @param toPattern
     * @return
     */
    public static String transferDateString(String strDate, String formPattern, String toPattern) {
        if (StringUtil.isEmpty(strDate)) {
            throw new IllegalArgumentException("日期格式字符串不能为空");
        }
        if (StringUtil.isEmpty(formPattern)) {
            throw new IllegalArgumentException("日期没有指定原格式");
        }
        if (StringUtil.isEmpty(toPattern)) {
            throw new IllegalArgumentException("日期没有指定转化后的格式");
        }
        SimpleDateFormat from = new SimpleDateFormat(formPattern);
        SimpleDateFormat to = new SimpleDateFormat(toPattern);
        try {
            return to.format(from.parse(strDate));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("日期字符串格式错误", e);
        }
    }

    /**
     * @param value
     * @return String  返回类型
     * @throws
     * @Title: formatDate
     */
    public final static String formatDate(String value) {
        if (value == null) {
            throw new IllegalArgumentException("日期格式字符串不能为空");
        }
        SimpleDateFormat md = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = md.parse(value);
            md.applyPattern("yyyy/MM/dd");
            d = md.parse(value);
        } catch (Exception e1) {
            e1.printStackTrace();
            throw new RuntimeException("日期字符串格式错误!", e1);
        }
        md.applyPattern("yyyy-MM-dd");
        value = md.format(d);
        return value;
    }

    /**
     * 获得当前年
     *
     * @return 获得当前年，比如现在是2011年4月10日，那么返回2011。
     */
    public static int getCurrentYear() {
        return getTimeField(Calendar.getInstance().getTime(), Calendar.YEAR);
    }

    /**
     * 获得当前月
     *
     * @return 获得当前月，比如现在是2011年4月10日，那么返回4。
     */
    public static int getCurrentMonth() {
        return getTimeField(Calendar.getInstance().getTime(), Calendar.MONTH);
    }

    /**
     * 返回当前时间是当月的第几天
     *
     * @return
     */
    public static int getCurrentDay() {
        return getTimeField(Calendar.getInstance().getTime(), Calendar.DAY_OF_MONTH);
    }

    /**
     * 返回指定时间对象的指定字段值
     *
     * @param date  时间对象
     * @param field 日历字段值：比如Calendar.YEAR、Calendar.MONTH等
     * @return
     */
    public static int getTimeField(Date date, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return getTimeField(cal, field);
    }

    /**
     * 返回指定时间对象的指定字段值
     *
     * @param cal   日历对象
     * @param field 日历字段值：比如Calendar.YEAR、Calendar.MONTH等
     * @return
     */
    public static int getTimeField(Calendar cal, int field) {
        int fieldValue = cal.get(field);
        fieldValue = field == Calendar.MONTH ? (fieldValue + 1) : fieldValue;
        return fieldValue;
    }

//    /**
//     *
//     *返回当前年指定月有多少天
//     *
//     * @param month
//     *            月份，从1开始，即1表示1月，2表示2月
//     * @return 当前年指定月的总天数
//     */
//    public static int getLastDayOfMonth(int month) {
//        if (month < 1 || month > 12) {
//            return -1;
//        }
//        int retn = 0;
//        if (month == 2 && isLeapYear()) {
//            retn = 29;
//        } else {
//            retn = dayArray[month - 1];
//        }
//        return retn;
//    }

    /**
     *返回指定年的指定月有多少天
     *
     * @param year
     *            年份
     * @param month
     *            月份，从1开始，即1表示1月，2表示2月
     * @return
     */
//    public static int getLastDayOfMonth(int year, int month) {
//        if (month < 1 || month > 12) {
//            return -1;
//        }
//        int retn = 0;
//        if (month == 2 && isLeapYear(year)) {
//            retn = 29;
//        } else {
//            retn = dayArray[month - 1];
//        }
//        return retn;
//    }


    /**
     * 判断当前年是否是闰年
     *
     * @return 是闰年返回true，否则返回false
     */
    public static boolean isLeapYear() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        return isLeapYear(year);
    }

    /**
     * 判断指定的年是否是闰年
     *
     * @param year
     * @return 是闰年返回true，否则返回false
     */
    public static boolean isLeapYear(int year) {
        if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 从当前年月日往前倒推指定年。比如，现在是2011-05-18，倒推5年，就是2006-05-18。
     * 如果当前是闰年2月29日，倒推后的年是平年2月，没有29日，则取28日。
     *
     * @param curDate
     * @param backYearNum
     * @return
     */
    public static Date backYears(Date curDate, int backYearNum) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        int curYearNum = cal.get(Calendar.YEAR);
        int curDayofmonth = cal.get(Calendar.DAY_OF_MONTH);

        //当前年是闰年是2月29日，但到推后的年不是闰年。
        if (isLeapYear(curYearNum) && cal.get(Calendar.MONTH) == Calendar.FEBRUARY
                && curDayofmonth == 29 && !isLeapYear(curYearNum - backYearNum)) {
            cal.set(Calendar.DAY_OF_MONTH, 28);
        }
        cal.set(Calendar.YEAR, curYearNum - backYearNum);
        return new Date(cal.getTimeInMillis());
    }

    /**
     * 从给定日期开始前进/后推指定天。
     *
     * @param curDate    参照时间点。
     * @param addDaysNum 要前进/后推的天数。 正数表示往后推，负数表示前推
     * @return
     */
    public static Date addDays(Date curDate, int addDaysNum) {
        long curDateMills = curDate.getTime();
        int addDaysMills = addDaysNum * 24 * 3600 * 1000;
        return new Date(curDateMills + addDaysMills);
    }

    /**
     * 从给定的时间开始，前进/后退几个月
     *
     * @param curDate
     * @param addMonthsNum 前进/后退月数
     * @param retFormat    返回的时间格式
     * @return
     */
    public static String addMonths(Date curDate, int addMonthsNum, String retFormat) {
        long curDateMills = curDate.getTime();
        long addMonthsMills = addMonthsNum * 30 * 24 * 3600 * 1000L;
        return transferDateToString(new Date(curDateMills + addMonthsMills), retFormat);
    }

    public static String currtntDateBackMonths(int backMonthsNum) {
        return backMonths(new Date(), backMonthsNum, defaultDateTimeFormat);
    }

    public static String currtntDateBackMonths(int backMonthsNum, String retFormat) {
        return backMonths(new Date(), backMonthsNum, retFormat);
    }

    public static String backMonths(Date curDate, int backMonthsNum, String retFormat) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - backMonthsNum);
        return transferDateToString(cal.getTime(), retFormat);
    }

    /*
     * 判断字符串是否为空
     *
     * @param value
     *
     * @return 如果是空返回true，否则返回false
     */
    private static boolean isEmpty(String value) {
        return value == null || value.trim().equals("");
    }

    /**
     * 根据当前日期时间生产带有前缀的日期时间序列。
     *
     * @param prefix 前缀
     * @return 前缀的时间序列
     */
    public static String getTimeSecondSequence(String prefix) {
        String random = String.valueOf((int) (Math.random() * 10000));
        StringBuilder result = new StringBuilder();
        result.append(prefix);
        result.append(transferDateToString(new Date(), "yyyyMMddHHmmss"));
        for (int i = random.length(); i < 4; i++)
            result.append("0");
        result.append(random);
        return result.toString();
    }

    /**
     * 获取当天的开始时间
     *
     * @return
     */
    public static Date getCurrentDayBeginTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当天的结束时间
     *
     * @return
     */
    public static Date getCurrentDayEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 获取当周的开始时间
     *
     * @return
     */
    public static Date getCurrentWeekBeginTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当周的结束时间
     *
     * @return
     */
    public static Date getCurrentWeekEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_WEEK, 7);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 获取当月的开始时间
     *
     * @return
     */
    public static Date getCurrentMonthBeginTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当月的结束时间
     *
     * @return
     */
    public static Date getCurrentMonthEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 获取当月的结束时间
     */
    public static Date getMonthEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 获取当年的开始时间
     *
     * @return
     */
    public static Date getCurrentYearBeginTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当年的结束时间
     *
     * @return
     */
    public static Date getCurrentYearEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 获取当年的结束时间
     */
    public static Date getYearEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * JWT过期时间设置在凌晨，使用概率比较低的时间点，避免用户正在使用token过期，数据丢失
     *
     * @param expire 单位：秒
     */
    public static Date JWTTokenExpirationTime(Date date, int expire) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // cal.set(Calendar.HOUR_OF_DAY, 3);
        // cal.set(Calendar.MINUTE, 0);
        // cal.set(Calendar.SECOND, 0);
        // cal.set(Calendar.MILLISECOND, 0);
        //expire秒之后的时间
        cal.add(Calendar.SECOND, expire);
        return cal.getTime();
    }

    /**
     * 根据指定的格式化，获取当前时间字符串
     *
     * @param formatter 格式化
     * @return 时间字符串
     */
    public static String getCurrentTimeStr(String formatter) {
        Date localDate = new Date();
        SimpleDateFormat sf = new SimpleDateFormat(formatter);
        return sf.format(localDate);
    }

    /**
     * 获取当前年份
     *
     * @return 年 如2013
     */
    public static int getCurrYearStr() {
        Calendar localCalendar = Calendar.getInstance();
        return localCalendar.get(Calendar.YEAR);
    }

    /**
     * 获取当前月开始的时间
     *
     * @param month  月份
     * @param format 返回的时间格式
     * @return
     */
    public static String getMonthBeginTime(int month, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(Calendar.MONTH, month - 1);
        localCalendar.set(Calendar.DAY_OF_MONTH, 1);
        localCalendar.set(Calendar.HOUR_OF_DAY, 0);
        localCalendar.set(Calendar.MINUTE, 0);
        localCalendar.set(Calendar.SECOND, 0);
        localCalendar.set(Calendar.MILLISECOND, 0);
        return sdf.format(localCalendar.getTime());
    }

    /**
     * 获取传入月分开始的时间
     *
     * @param monthStr    月份
     * @param afterFormat 返回的时间格式
     * @return
     */
    public static String getMonthBeginTime(String monthStr, String monthFormat, String afterFormat) {
        SimpleDateFormat monthSdf = new SimpleDateFormat(monthFormat);
        SimpleDateFormat sdf = new SimpleDateFormat(afterFormat);
        Calendar localCalendar = Calendar.getInstance();
        try {
            localCalendar.setTime(monthSdf.parse(monthStr));
        } catch (Exception e) {
        }
        localCalendar.set(Calendar.DAY_OF_MONTH, 1);
        localCalendar.set(Calendar.HOUR_OF_DAY, 0);
        localCalendar.set(Calendar.MINUTE, 0);
        localCalendar.set(Calendar.SECOND, 0);
        localCalendar.set(Calendar.MILLISECOND, 0);
        return sdf.format(localCalendar.getTime());
    }

    public static String getCurrentMonthBeginTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTime(new Date());
        localCalendar.set(Calendar.DAY_OF_MONTH, 1);
        localCalendar.set(Calendar.HOUR_OF_DAY, 0);
        localCalendar.set(Calendar.MINUTE, 0);
        localCalendar.set(Calendar.SECOND, 0);
        localCalendar.set(Calendar.MILLISECOND, 0);
        return sdf.format(localCalendar.getTime());
    }

    /**
     * @param date
     * @return 获取传入参数时间的当前月的第一天 返回格式 yyyy-MM-dd HH:mm:ss
     * @author xzp
     */
    public static String getMonthBeginTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return simpleDateFormat.format(calendar.getTime());
    }
    /**
     * 获取当前月结束时间
     * @param month 月份
     * @param format 返回的时间格式
     * @return
     */
//    public static String getMonthEndTime(int month,String format){
//        SimpleDateFormat sdf = new SimpleDateFormat(format);
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.MONTH, month-1);
//        cal.set(Calendar.DAY_OF_MONTH, getLastDayOfMonth(month));
//        cal.set(Calendar.HOUR_OF_DAY, 23);
//        cal.set(Calendar.MINUTE, 59);
//        cal.set(Calendar.SECOND, 59);
//        cal.set(Calendar.MILLISECOND, 999);
//        return sdf.format(cal.getTime());
//    }

    /**
     * 获取当前月结束时间
     *
     * @return
     */
//    public static String getMonthEndTime(String monthStr,String monthFormat,String afterFormat){
//        SimpleDateFormat monthSdf = new SimpleDateFormat(monthFormat);
//        SimpleDateFormat sdf = new SimpleDateFormat(afterFormat);
//        Calendar cal = Calendar.getInstance();
//        try {
//            Date month = monthSdf.parse(monthStr);
//            cal.setTime(month);
//            cal.set(Calendar.DAY_OF_MONTH, getLastDayOfMonth(month));
//            cal.set(Calendar.HOUR_OF_DAY, 23);
//            cal.set(Calendar.MINUTE, 59);
//            cal.set(Calendar.SECOND, 59);
//            cal.set(Calendar.MILLISECOND, 999);
//        } catch (Exception e) {
//        }
//        return sdf.format(cal.getTime());
//    }
//    public static String getCurrentMonthEndTime(String format) {
//        SimpleDateFormat sdf = new SimpleDateFormat(format);
//        Calendar cal = Calendar.getInstance();
//        Date month = new Date();
//        cal.setTime(month);
//        cal.set(Calendar.DAY_OF_MONTH, getLastDayOfMonth(month));
//        cal.set(Calendar.HOUR_OF_DAY, 23);
//        cal.set(Calendar.MINUTE, 59);
//        cal.set(Calendar.SECOND, 59);
//        cal.set(Calendar.MILLISECOND, 999);
//        return sdf.format(cal.getTime());
//    }

    /**
     * @param dateStr
     * @param pattern
     * @return 获取传入参数时间的当前月的最后一天 返回格式 yyyy-MM-dd HH:mm:ss
     * @author xzp
     */
    public static String getCurrentMonthEndTime(String dateStr, String pattern) {
        SimpleDateFormat sourceDateFormat = new SimpleDateFormat(pattern);
        SimpleDateFormat destDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = sourceDateFormat.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        date = calendar.getTime();
        return destDateFormat.format(date);
    }

    /**
     * @param date
     * @return 获取传入参数时间的当前月的最后一天 返回格式 yyyy-MM-dd HH:mm:ss
     * @author xzp
     */
    public static String getCurrentMonthEndTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String getDurationTime(String startTime, String endTime, String pattern) {
        Date start = transferStringToDate(startTime, pattern);
        Date end = transferStringToDate(endTime, pattern);
        long duration = end.getTime() - start.getTime();
        long day = duration / 1000 / 60 / 60 / 24;
        long hour = duration / 1000 / 60 / 60 % 24;
        long minute = duration / 1000 / 60 % 60;
        String retStr = "";
        if (day != 0) {
            retStr += day + "天";
        }
        if (hour != 0) {
            retStr += hour + "小时";
        }
        if (minute != 0) {
            retStr += minute + "分钟";
        }
        return retStr;
    }

    public static int getDifferenceTime(String startTime, String endTime, String pattern) {
        Date start = transferStringToDate(startTime, pattern);
        Date end = transferStringToDate(endTime, pattern);
        long duration = end.getTime() - start.getTime();
        return Integer.parseInt(String.valueOf(duration / 1000));
    }

    public static List<String> getDurationMonth(String startTime, String endTime, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            List<String> list = new ArrayList<String>();
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(start);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(end);
            while (cal2.compareTo(cal) != -1) {
                list.add(sdf.format(cal.getTime()));
                cal.set(Calendar.MONTH, cal.get(cal.MONTH) + 1);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取天
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static int getTimeDay(String dateStr, String pattern) {
        Date date = transferStringToDate(dateStr, pattern);
        return getTimeField(date, Calendar.DATE);
    }

    /**
     * 获取月份
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static int getTimeMonth(String dateStr, String pattern) {
        Date date = transferStringToDate(dateStr, pattern);
        return getTimeField(date, Calendar.MONTH);
    }

    public static Date yearAfter(Date date, int year) {
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, year);
        return calendar.getTime();
    }

    public static Date monthAfter(Date date, int month) {
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    public static Date dayAfter(Date date, int day) {
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, day);
        return calendar.getTime();
    }

    public static Date hourAfter(Date date, int hour) {
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        return calendar.getTime();
    }

    public static Date minuteAfter(Date date, int minute) {
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    public static String dateStrFormat(String dateStr, String srcPattern) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(srcPattern);
            Date temp = formatter.parse(dateStr);
            formatter.applyPattern(defaultDateTimeFormat);
            return formatter.format(temp);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("日期字符串格式转换异常。", e);
        }
    }

    /**
     * 获取本周的开始时间，到周末的结束时间
     *
     * @return
     */
    public static List<String> dateFromSunDay() {
        try {
            List<String> stringList = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat(defaultDateTimeFormat, Locale.CHINA);
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            calendar.setFirstDayOfWeek(Calendar.MONDAY);

            calendar.setTimeInMillis(System.currentTimeMillis());

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            stringList.add(sdf.format(calendar.getTime()));

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);

            stringList.add(sdf.format(calendar.getTime()));
            return stringList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("日期字符串格式转换异常。", e);
        }
    }

    /**
     * 获取当周的开始时间
     *
     * @return
     */
    public static String getSunDayBeginTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(defaultDateTimeFormat, Locale.CHINA);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return sdf.format(calendar.getTime());
    }

    /**
     * 获取当周的结束时间
     *
     * @return
     */
    public static String getSunDayEndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(defaultDateTimeFormat, Locale.CHINA);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return sdf.format(calendar.getTime());
    }

    /**
     * 获取当前的开始时间
     *
     * @param date
     * @return
     */
    public static String getCurrentYearBeginTime(Date date, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 获取当前的结束时间
     *
     * @param date
     * @return
     */
    public static String getCurrentYearEndTime(Date date, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * @param dateString
     * @param format
     * @return java.util.Date
     * @description 日期字符串转date
     */
    public static Date stringToDate(String dateString, String format) {
        if (StringUtil.isEmpty(dateString)) {
            return null;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(format);

            try {
                return sdf.parse(dateString);
            } catch (ParseException var4) {
                return null;
            }
        }
    }

    /**
     * @param dateString
     * @return java.util.Date
     * @description 日期字符串转date
     */
    public static Date stringToDate(String dateString) {
        if (StringUtil.isEmpty(dateString)) {
            return null;
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return sdf.parse(dateString);
            } catch (Exception var3) {
                try {
                    Long time = Long.parseLong(dateString);
                    return new Date(time);
                } catch (Exception var2) {
                    var2.printStackTrace();
                    return null;
                }
            }
        }
    }

    public static Date dayNumAddTime(Date date, int days) throws Exception {
        long dayMills = days * 24 * 3600 * 1000;
        long nowMills = date.getTime();
        long total = dayMills + nowMills;
        System.out.println("totalMills =" + dayMills + nowMills);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        date.setTime(total);
        String str = simpleDateFormat.format(date);
        System.out.println("str=" + str);
        return simpleDateFormat.parse(str);
    }

    /**
     * @param date
     * @param pattern
     * @return java.lang.String
     * @description date日期转字符串
     */
    public static String getDateToString(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        return (new SimpleDateFormat(pattern)).format(date);
    }

//    public static void main(String[] args)throws Exception {
//        //String currentYearBeginTime = getCurrentYearEndTime(new Date(), DateUtil.defaultDateTimeFormat);
//        //System.out.println(currentYearBeginTime);
//        Date date = dayNumAddTime(new Date(),2);
//
//    }

    /**
     * 有效期
     *
     * @param limitType 有效期类型 0-无限期 3-年 2-月 1-日
     * @param limitNum  值
     * @return date
     */
    public static Date createValidEndTime(Integer limitType, Integer limitNum) {
        switch (limitType) {
            //年
            case 3:
                return DateUtil.yearAfter(DateUtil.getDayEndTime(new Date()), limitNum);
            case 2:
                return DateUtil.monthAfter(DateUtil.getDayEndTime(new Date()), limitNum);
            case 1:
                return DateUtil.dayAfter(DateUtil.getDayEndTime(new Date()), limitNum);
            default:
                return null;
        }
    }

    /***
     * 功能描述: 判断是否是今天
     *
     * @param: [time]
     * @auther: liu yi
     * @date: 2019/3/28 17:17
     * @return: boolean
     */
    public static boolean isToday(Long time) {
        if (time == null) {
            return false;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(time);
            Date d = c.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(new Date()).equals(sdf.format(d));
        }
    }

    /***
     * 功能描述:获取时间差
     *
     * @param: [startTime, endTime]
     * @auther: liu yi
     * @date: 2019/3/28 17:17
     * @return: long
     */
    public static long getSeconds(Date startTime, Date endTime) {
        return endTime.getTime() - startTime.getTime() > 0L ? (endTime.getTime() - startTime.getTime()) / 1000L : 0L;
    }

    public static String getDateStr(Date startTime, Date endTime) {
        if (endTime != null) {
            long millors = (endTime.getTime() - startTime.getTime()) / 1000L;
            if (millors != 0L) {
                String str = getDateStr(millors);
                if (millors < 0L) {
                    return "-" + str;
                }

                return str;
            }
        }

        return "0天0时0分0秒";
    }

    private static String getDateStr(long millors) {
        if (millors <= 0L) {
            millors = -millors;
        }

        int day = (int) (millors / 86400L);
        int hour = (int) (millors - (long) (86400 * day)) / 3600;
        int minute = (int) (millors - (long) (86400 * day) - (long) (3600 * hour)) / 60;
        int seconds = (int) (millors - (long) (86400 * day) - (long) (3600 * hour) - (long) (minute * 60));
        return day + "天" + hour + "时" + minute + "分" + seconds + "秒";
    }

    /**
     * 获取两个日期之间相差的秒数
     *
     * @param beginTime 开始日期
     * @param endTime   结束日期
     */
    public static long betweenSeconds(Date beginTime, Date endTime) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(beginTime);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(endTime);
        long time1 = cal1.getTimeInMillis();
        long time2 = cal2.getTimeInMillis();
        return (time2 - time1) / 1000;
    }

    /**
     * 获取两个日期之间相差的分钟数
     *
     * @param beginTime 开始日期
     * @param endTime   结束日期
     */
    public static double betweenMinutes(Date beginTime, Date endTime) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(beginTime);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(endTime);
        double time1 = (double) cal1.getTimeInMillis();
        double time2 = (double) cal2.getTimeInMillis();
        return (time2 - time1) / 60000.0D;
    }

    /**
     * 获取两个日期之间相差的小时数
     *
     * @param beginTime 开始日期
     * @param endTime   结束日期
     */
    public static double betweenHours(Date beginTime, Date endTime) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(beginTime);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(endTime);
        double time1 = (double) cal1.getTimeInMillis();
        double time2 = (double) cal2.getTimeInMillis();
        return (time2 - time1) / 3600000.0D;
    }

    /**
     * 获取两个日期之间相差的天数
     *
     * @param beginTime 开始日期
     * @param endTime   结束日期
     */
    public static int betweenDays(Date beginTime, Date endTime) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(beginTime);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(endTime);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 < year2) {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                    //闰年
                    timeDistance += 366;
                } else {
                    //不是闰年
                    timeDistance += 365;
                }
            }
            return timeDistance + day2 - day1;
        } else {
            return day2 - day1;
        }
    }

    /**
     * 获取两个日期之间相差的月数
     *
     * @param beginTime 开始日期
     * @param endTime   结束日期
     */
    public static int betweenMonths(Date beginTime, Date endTime) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(beginTime);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(endTime);
        int month1 = cal1.get(Calendar.MONTH);
        int month2 = cal2.get(Calendar.MONTH);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 < year2) {
            return (year2 - year1) * 12 + month2 - month1;
        } else {
            return month2 - month1;
        }
    }

    /**
     * 获取当前时间前一天的结束时间
     *
     * @author hhf
     * @date 2019/5/27
     */
    public static Date getBeforeEndDay() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /***
     * 获取当前日期的年
     * @param date
     * @return
     */
    public static int getYearByDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        return year;
    }

    /***
     * 获取当前日期的月
     * @param date
     * @return
     */
    public static int getMonthByDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        return month + 1;
    }

    /***
     * 获取当前日期的日
     * @param date
     * @return
     */
    public static int getDayByDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        return day;
    }

    /****
     * 获取指定日期的某个月 ,返回yyyy-MM
     * @param date 指定日期
     * @param changeMonth 指定日期的前 几个月/后几个月,前几个月用-
     * @return
     */
    public static String getChangeMonthByDate(Date date, int changeMonth) {
        SimpleDateFormat sdf = new SimpleDateFormat(MONTH_TIME_FORMAT);
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, changeMonth);
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /***
     * 根据日期计算是周几
     * @param date
     * @return
     */
    public static Integer getWeekOfDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

    /***
     * 获取日期的下周周几
     * @param date
     * @param day
     * @return
     */
    public static Date getNextWeekDay(Date date,int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 7-day);
		return cal.getTime();
	}


    /***
     * 获取日期的某月的某一天
     * @param date
     * @param day
     * @return
     */
    public final static Date getOtherMonthDay(Date date,int num,int day) {
    	date.setDate(day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, num);
        date = calendar.getTime();
        return date;

    }

    /****
     * 获取指定日期的某天 ,返回yyyy-MM-dd
     * @param date 指定日期
     * @param changeMonth 指定日期的前 几个月/后几个月,前几个月用-
     * @return
     */
    public static String getChangeDayByDate(Date date, int day) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, day);
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 校验两个时间是否在同一天
     *
     * @param d1
     * @param d2
     * @return true-是同一天；false-不是同一天
     */
    public static boolean checkIsSameDate(Date d1, Date d2) {
        if (d1 == null && d2 == null) {
            throw new BadRequestException("时间参数为必传！");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String d1Str = sdf.format(d1);
        String d2Str = sdf.format(d2);
        return d1Str.equals(d2Str);
    }

}
