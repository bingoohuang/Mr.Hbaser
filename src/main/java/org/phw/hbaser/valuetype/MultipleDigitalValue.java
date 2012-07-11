package org.phw.hbaser.valuetype;

import org.phw.core.lang.Strings;

public abstract class MultipleDigitalValue implements ValueTypable {

    protected abstract String toStr2(byte[] value, int offset);

    protected abstract byte[] toBytes2(String str);

    @Override
    public String toStr(byte[] value) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length; i += getValueLength()) {
            if (sb.length() > 0) {
                sb.append('-');
            }
            sb.append(toStr2(value, i));
        }

        return sb.toString();
    }

    @Override
    public byte[] toBytes(String value) {
        String[] parts = Strings.split(value, "-", true);
        byte[] result = new byte[parts.length * getValueLength()];

        for (int i = 0; i < parts.length; ++i) {
            System.arraycopy(toBytes2(parts[i]), 0, result, getValueLength() * i, getValueLength());
        }

        return result;
    }

    @Override
    public boolean needQuoted() {
        return true;
    }

    @Override
    public boolean needEsacaping() {
        return false;
    }
}
