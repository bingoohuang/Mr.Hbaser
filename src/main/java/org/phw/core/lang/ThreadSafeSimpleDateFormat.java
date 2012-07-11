package org.phw.core.lang;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 线程安全的日期格式类。
 * @author BingooHuang
 *
 */
public class ThreadSafeSimpleDateFormat {
    private DateFormat df;

    /**
     * 构造函数.
     * @param format 格式
     */
    public ThreadSafeSimpleDateFormat(String format) {
        df = new SimpleDateFormat(format);
    }

    /**
     * 格式化。
     * @param date 日期
     * @return 字符串
     */
    public synchronized String format(Date date) {
        return df.format(date);
    }

    /**
     * 解析日期。
     * @param string 字符串
     * @return 日期
     * @throws ParseException ParseException
     */
    public synchronized Date parse(String string) throws ParseException {
        return df.parse(string);
    }
}
