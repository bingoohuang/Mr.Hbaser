package org.phw.core.lang;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.phw.core.exception.AppException;

/**
 * 日期相关操作。
 * @author BingooHuang
 *
 */
public class Dates {
    /**
     * yyyy-MM-dd HH:mm:ss.
     */
    public static final ThreadSafeSimpleDateFormat STD_DATE_FORMAT = new ThreadSafeSimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    /**
     * yyyy-MM-dd HH:mm:ss.SSS.
     */
    public static final ThreadSafeSimpleDateFormat STD_DATE_FORMAT2 = new ThreadSafeSimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 获取当前时间字符串(yyyy-MM-dd HH:mm:ss)。
     * @return 字符串
     */
    public static String now() {
        return STD_DATE_FORMAT.format(Calendar.getInstance().getTime());
    }

    /**
     * 格式化日期。
     * @param date 日期
     * @param pattern 格式
     * @return 字符串
     */
    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 使用标准格式yyyy-MM-dd HH:m:ss格式化日期。
     * @param date Date日期
     * @return String
     */
    public static String format(Date date) {
        return STD_DATE_FORMAT.format(date);
    }

    /**
     * 使用标准格式yyyy-MM-dd HH:m:ss.SSS格式化日期。
     * @param date Date日期
     * @return String
     */
    public static String format2(Date date) {
        return STD_DATE_FORMAT2.format(date);
    }

    /**
     * 使用标准格式yyyy-MM-dd HH:m:ss格式化日期。
     * @param milliSeconds 微秒
     * @return String 格式化日期字符串。
     */
    public static String format(long milliSeconds) {
        return STD_DATE_FORMAT.format(new Date(milliSeconds));
    }

    /**
     * 使用标准格式yyyy-MM-dd HH:m:ss格式化日期。
     * @param milliSeconds 微秒
     * @return String 格式化日期字符串。
     */
    public static String format2(long milliSeconds) {
        return STD_DATE_FORMAT2.format(new Date(milliSeconds));
    }

    /**
     * 解析日期。
     * @param value 字符串
     * @return 日期 
     */
    public static Date parse(String value) {
        try {
            return STD_DATE_FORMAT.parse(value);
        }
        catch (ParseException e) {
            throw new AppException(e);
        }
    }

    /**
     * 从字符串中解析日期。
     * @param value 字符串
     * @param pattern 格式
     * @return 日期
     */
    public static Date parse(String value, String pattern) {
        try {
            Date date = new SimpleDateFormat(pattern).parse(value);
            if (value.equals(format(date, pattern))) {
                return date;
            }
            throw new AppException("Unable to parse date from " + value + " by format " + pattern);
        }
        catch (ParseException e) {
            throw new AppException(e);
        }
    }
}
