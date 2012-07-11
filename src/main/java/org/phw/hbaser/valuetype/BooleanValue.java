package org.phw.hbaser.valuetype;

import org.apache.hadoop.hbase.util.Bytes;
import org.phw.core.lang.Converts;

public class BooleanValue implements ValueTypable, MatchAware {

    @Override
    public String toStr(byte[] value) {
        return "" + Bytes.toBoolean(value);
    }

    @Override
    public byte[] toBytes(String value) {
        return Bytes.toBytes(Boolean.valueOf(value));
    }

    @Override
    public boolean needQuoted() {
        return false;
    }

    @Override
    public boolean needEsacaping() {
        return false;
    }

    @Override
    public int getValueLength() {
        return Bytes.SIZEOF_BOOLEAN;
    }

    @Override
    public boolean match(byte[] value, int offset) {
        return true;
    }

    @Override
    public boolean match(String value) {
        return Converts.convert(value, boolean.class) != null;
    }
}
