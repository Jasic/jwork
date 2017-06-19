package org.jasic.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期时间处理工具类
 *
 * @author
 */
public class DateTimeUtil {

    // 日志处理
    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtil.class);

    // 时区名称
    public static String zoneName = "GMT+8";

    // 默认格式化字符串
    public static final String DEFAULT_FORMAT_STRING = "yyyyMMddHHmmss";

    public static void sleep(int sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(long milSec) {
        try {
            Thread.sleep(milSec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 字符串转换为日期
     *
     * @param dateStr
     * @param formatStr
     * @return
     */
    public static Date parseDate(String dateStr, String formatStr) {
        if (StringUtils.isEmpty(dateStr) || StringUtils.isEmpty(formatStr)) {
            return null;
        }

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(zoneName));
            return simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            logger.info("字符串转换为日期异常: ", e);
        }

        return null;
    }

    /**
     * 默认格式的转换字符串为日期
     *
     * @param dateStr
     * @return
     */
    public static Date parseDate(String dateStr) {
        return parseDate(dateStr, DEFAULT_FORMAT_STRING);
    }

    /**
     * 日期格式化为字符串
     *
     * @param date
     * @param formatStr
     * @return
     */
    public static String formatDate(Date date, String formatStr) {
        if (date == null) {
            return null;
        }

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(zoneName));
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            logger.info("字符串转换为日期异常: ", e);
        }

        return null;
    }

    /**
     * 默认格式的日期格式化为字符串
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        return formatDate(date, DEFAULT_FORMAT_STRING);
    }

    /**
     * 返回年份
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    /**
     * 返回月份
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 返回周
     *
     * @param date 日期
     * @return 返回周
     */
    public static int getWeek(java.util.Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.get(java.util.Calendar.DAY_OF_WEEK);
    }

    /**
     * 返回日份
     *
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 返回小时
     *
     * @param date
     * @return
     */
    public static int getHour(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 返回分钟
     *
     * @param date
     * @return
     */
    public static int getMinute(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MINUTE);
    }

    /**
     * 返回秒钟
     *
     * @param date
     * @return
     */
    public static int getSecond(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.SECOND);
    }

    /**
     * 返回毫秒
     *
     * @param date
     * @return
     */
    public static long getMillis(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis();
    }

    /**
     * 返回字符型日期
     *
     * @param date
     * @return
     */
    public static String getDate(Date date) {
        return formatDate(date, "yyyyMMdd");
    }

    /**
     * 返回字符型时间
     *
     * @param date
     * @return
     */
    public static String getTime(Date date) {
        return formatDate(date, "HHmmss");
    }

    /**
     * 返回字符型日期时间
     *
     * @param date
     * @return
     */
    public static String getDateTime(Date date) {
        return formatDate(date, DEFAULT_FORMAT_STRING);
    }

    /**
     * 日期相加
     *
     * @param date
     * @param day
     * @return
     */
    public static Date addDate(Date date, int day) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(getMillis(date) + ((long) day) * 24 * 3600 * 1000);
        return c.getTime();
    }

    /**
     * 日期相减
     *
     * @param date
     * @param date1
     * @return
     */
    public static int diffDateForDay(Date date, Date date1) {
        return (int) ((getMillis(date) - getMillis(date1)) / (24 * 3600 * 1000));
    }

    /**
     * 日期相减
     *
     * @param date
     * @param date1
     * @return
     */
    public static int diffDateForHour(Date date, Date date1) {
        return (int) ((getMillis(date) - getMillis(date1)) / (3600 * 1000));
    }

    /**
     * 日期相减
     *
     * @param date
     * @param date1
     * @return
     */
    public static int diffDateForMinute(Date date, Date date1) {
        return (int) ((getMillis(date) - getMillis(date1)) / (60 * 1000));
    }

    /**
     * 日期相减
     *
     * @param date
     * @param date1
     * @return
     */
    public static int diffDateForSecond(Date date, Date date1) {
        return (int) ((getMillis(date) - getMillis(date1)) / 1000);
    }

}
