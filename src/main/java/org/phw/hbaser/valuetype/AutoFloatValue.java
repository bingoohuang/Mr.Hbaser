package org.phw.hbaser.valuetype;

import org.apache.hadoop.hbase.util.Bytes;

public class AutoFloatValue extends DoubleValue {

    @Override
    public String toStr(byte[] value) {
        switch (value.length) {
        case Bytes.SIZEOF_DOUBLE:
            return "" + Bytes.toDouble(value);
        case Bytes.SIZEOF_FLOAT:
            return "" + Bytes.toFloat(value);
        default:
            break;
        }

        return Bytes.toString(value);
    }

}
