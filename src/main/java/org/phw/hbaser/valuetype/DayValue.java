package org.phw.hbaser.valuetype;

import java.text.ParseException;
import java.util.Date;

import org.apache.hadoop.hbase.util.Bytes;
import org.phw.core.exception.AppException;
import org.phw.core.lang.ThreadSafeSimpleDateFormat;

public class DayValue extends DateValue {
    private static final ThreadSafeSimpleDateFormat DAY_FMT = new ThreadSafeSimpleDateFormat(
            "yyyy.MM.dd");

    @Override
    public String toStr(byte[] value) {
        final long longValue = Bytes.toLong(value);
        return DAY_FMT.format(new Date(longValue));
    }

    @Override
    public byte[] toBytes(String value) {
        try {
            final long time = DAY_FMT.parse(value).getTime();
            return Bytes.toBytes(time);
        }
        catch (ParseException e) {
            throw new AppException(e);
        }
    }
}
