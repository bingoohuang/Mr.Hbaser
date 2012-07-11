package org.phw.hbaser.valuetype;

import org.apache.hadoop.hbase.util.Bytes;

public class StringValue implements ValueTypable, ArgumentsAppliable, MatchAware {
    private byte[] constBytes = null;
    private String constString = null;

    @Override
    public String toStr(byte[] value) {
        return Bytes.toString(value);
    }

    @Override
    public byte[] toBytes(String value) {
        return Bytes.toBytes(value);
    }

    @Override
    public boolean needQuoted() {
        return true;
    }

    @Override
    public boolean needEsacaping() {
        return true;
    }

    @Override
    public int getValueLength() {
        return constBytes == null ? -1 : constBytes.length;
    }

    @Override
    public void apply(String[] args) {
        if (args != null && args.length > 0) {
            constBytes = Bytes.toBytes(args[0]);
            constString = args[0];
        }
    }

    @Override
    public boolean match(byte[] value, int offset) {
        if (constBytes == null) {
            return true;
        }

        if (offset + constBytes.length > value.length) {
            return false;
        }

        return Bytes.compareTo(value, offset, constBytes.length,
                constBytes, 0, constBytes.length) == 0;
    }

    @Override
    public boolean match(String value) {
        return constString == null ? true : constString.equals(value);
    }
}
