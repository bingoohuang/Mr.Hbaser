package org.phw.hbaser.valuetype;

import java.text.ParseException;
import java.util.Date;

import org.apache.hadoop.hbase.util.Bytes;
import org.phw.core.exception.AppException;
import org.phw.core.lang.ThreadSafeSimpleDateFormat;

public class MDateValue extends LongValue {
    private static final ThreadSafeSimpleDateFormat MDATE_FMT = new ThreadSafeSimpleDateFormat(
            "yyyy.MM.dd HH:mm:ss.SSS");

    @Override
    public String toStr(byte[] value) {
        final long longValue = Bytes.toLong(value);
        return MDATE_FMT.format(new Date(longValue));
    }

    @Override
    public byte[] toBytes(String value) {
        try {
            final long time = MDATE_FMT.parse(value).getTime();
            return Bytes.toBytes(time);
        }
        catch (ParseException e) {
            throw new AppException(e);
        }
    }

    @Override
    public boolean needQuoted() {
        return true;
    }
}
