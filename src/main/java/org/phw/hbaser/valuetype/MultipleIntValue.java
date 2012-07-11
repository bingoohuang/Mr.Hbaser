package org.phw.hbaser.valuetype;

import org.apache.hadoop.hbase.util.Bytes;

public class MultipleIntValue extends MultipleDigitalValue {

    @Override
    protected String toStr2(byte[] value, int offset) {
        return "" + Bytes.toInt(value, offset);
    }

    @Override
    protected byte[] toBytes2(String str) {
        return Bytes.toBytes(Integer.valueOf(str));
    }

    @Override
    public int getValueLength() {
        return Bytes.SIZEOF_INT;
    }
}
