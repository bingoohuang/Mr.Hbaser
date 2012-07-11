package org.phw.hbaser.valuetype;

import org.apache.hadoop.hbase.util.Bytes;

public class MultipleLongValue extends MultipleDigitalValue {

    @Override
    protected String toStr2(byte[] value, int offset) {
        return "" + Bytes.toLong(value, offset);
    }

    @Override
    protected byte[] toBytes2(String str) {
        return Bytes.toBytes(Long.valueOf(str));
    }

    @Override
    public int getValueLength() {
        return Bytes.SIZEOF_LONG;
    }
}
