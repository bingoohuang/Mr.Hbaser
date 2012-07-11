package org.phw.hbaser.valuetype;

import org.apache.hadoop.hbase.util.Bytes;

public class AutoNumberValue extends LongValue {

    @Override
    public String toStr(byte[] value) {
        switch (value.length) {
        case Bytes.SIZEOF_LONG:
            return "" + Bytes.toLong(value);
        case Bytes.SIZEOF_INT:
            return "" + Bytes.toInt(value);
        case Bytes.SIZEOF_SHORT:
            return "" + Bytes.toShort(value);
        default:
            break;
        }

        return Bytes.toString(value);
    }

}
