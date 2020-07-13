package com.yimao.cloud.base.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.enums.ProductActivityStatusEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.vo.product.ProductActivityVO;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/***
 * 计算活动开始时间
 * @author zhangbaobao
 *
 */
public class CalcActivityStartTimeUtil {

    private static final String TIME_FORMAT = "yyyy-MM-dd";

    /***
     * 计算活动开始时间
     * @param pa
     */
    public static void calcActivityStartTime(ProductActivityVO pa) {
        if (pa.getStatus() == ProductActivityStatusEnum.TERMINATED.value) {
            return;
        }
        Date now = new Date();
        Date startDate = pa.getStartTime();
        Date endDate = pa.getEndTime();
        if (startDate == null || endDate == null) {
            throw new BadRequestException("活动时间配置错误。");
        }
        pa.setCountdownTime(0L);
        if (pa.getCycle() == null || !pa.getCycle()) {
            //非周期重复活动
            if (now.before(startDate)) {
                pa.setStatus(ProductActivityStatusEnum.NOT_STARTED.value);
                //活动开始倒计时
                pa.setCountdownTime(startDate.getTime() - now.getTime());
            } else if (now.after(endDate)) {
                pa.setStatus(ProductActivityStatusEnum.OVER.value);
            } else {
                pa.setStatus(ProductActivityStatusEnum.PROCESSING.value);
                //活动结束倒计时
                pa.setCountdownTime(endDate.getTime() - now.getTime());
            }
        } else {
            //周期重复活动
            String cycleTime = pa.getCycleTime();
            JSONObject json = JSON.parseObject(cycleTime);
            String suffix1 = json.getString("startTime");
            String suffix2 = json.getString("endTime");
            String prefix = getDayShortTime(now);
            if (pa.getCycleType() == 1) {
                //每天
                prefix = getDayShortTime(now);
            } else if (pa.getCycleType() == 2) {
                //每周几
                prefix = getWeekDayShortTime(json.getInteger("weekDay"));
            } else if (pa.getCycleType() == 3) {
                //每月几号
                prefix = getMonthDayShortTime(json.getInteger("day"));
            }
            Date startTime = DateUtil.transferStringToDate(prefix + " " + suffix1, DateUtil.defaultDateTimeFormat);
            Date endTime = DateUtil.transferStringToDate(prefix + " " + suffix2, DateUtil.defaultDateTimeFormat);

            if (startTime == null || endTime == null) {
                throw new BadRequestException("活动时间配置错误。");
            }
            if (now.before(startTime)) {
                pa.setStatus(ProductActivityStatusEnum.NOT_STARTED.value);
                //活动开始倒计时
                pa.setCountdownTime(startTime.getTime() - now.getTime());
            } else if (now.after(endTime)) {
                //如果当前时间晚于当前活动结束时间，则获取下一次活动开始时间结束时间
                //下次活动开始时间
                startTime = getNextTime(startTime, pa.getCycleType());
                endTime = getNextTime(endTime, pa.getCycleType());
                if (startTime == null || endTime == null) {
                    throw new BadRequestException("活动时间配置错误。");
                }
                if (startTime.after(endDate)) {
                    pa.setStatus(ProductActivityStatusEnum.OVER.value);
                } else {
                    pa.setStatus(ProductActivityStatusEnum.NOT_STARTED.value);
                    //活动开始倒计时
                    pa.setCountdownTime(startTime.getTime() - now.getTime());
                }
            } else {
                pa.setStatus(ProductActivityStatusEnum.PROCESSING.value);
                //活动结束倒计时
                pa.setCountdownTime(endTime.getTime() - now.getTime());
            }
            pa.setStartTime(startTime);
            pa.setEndTime(endTime);
        }
    }


    /**
     * 获取当天的时间（格式：yyyy-MM-dd）
     */
    public static String getDayShortTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
        return formatter.format(date);
    }

    /**
     * 获取当周星期几的时间（格式：yyyy-MM-dd）
     * weekDay取值：1(星期一),2,3,4,5,6,7(星期六)
     */
    public static String getWeekDayShortTime(int weekDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_WEEK, weekDay);
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
        return formatter.format(calendar.getTime());
    }

    /**
     * 获取当月几号的时间（格式：yyyy-MM-dd）
     */
    public static String getMonthDayShortTime(int monthDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, monthDay);
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
        return formatter.format(calendar.getTime());
    }

    public static Date getNextTime(Date time, int cycleType) {
        if (cycleType == 1) {
            //每天
            return DateUtil.dayAfter(time, 1);
        } else if (cycleType == 2) {
            //每周几
            return DateUtil.dayAfter(time, 7);
        } else if (cycleType == 3) {
            //每月几号
            return DateUtil.monthAfter(time, 1);
        }
        return null;
    }
}
