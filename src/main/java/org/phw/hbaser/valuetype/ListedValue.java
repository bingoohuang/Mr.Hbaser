package org.phw.hbaser.valuetype;

import java.util.ArrayList;

import org.apache.hadoop.hbase.util.Bytes;
import org.phw.core.lang.Strings;

public class ListedValue implements ValueTypable, MatchAware {
    private ArrayList<ValueTypable> listVT = new ArrayList<ValueTypable>();
    private String type;
    private boolean hasVariableValueTypeable;
    private int fixedLength;

    public ListedValue(String type) {
        this.type = type;
    }

    public void addValueTypable(ValueTypable vt) {
        if (vt.getValueLength() <= 0) {
            hasVariableValueTypeable = true;
        }

        listVT.add(vt);
    }

    public ValueTypable get(int index) {
        return listVT.get(index);
    }

    public int getListSize() {
        return listVT.size();
    }

    @Override
    public String toStr(byte[] value) {
        StringBuilder sb = new StringBuilder();
        int offset = 0;
        for (ValueTypable vt : listVT) {
            int valueLength = vt.getValueLength();
            if (valueLength <= 0) {
                valueLength = value.length - getOtherFixedLength(vt);
            }
            byte[] b1 = new byte[valueLength];
            System.arraycopy(value, offset, b1, 0, valueLength);
            if (offset > 0) {
                sb.append('-');
            }

            sb.append(vt.toStr(b1));
            offset += valueLength;
        }
        return sb.toString();
    }

    private int getOtherFixedLength(ValueTypable vt) {
        int count = 0;
        for (ValueTypable vt1 : listVT) {
            count += vt1 == vt ? 0 : vt1.getValueLength();
        }

        return count;
    }

    @Override
    public byte[] toBytes(String value) {
        String[] ss = Strings.split(value, "-", true);
        if (ss.length != listVT.size()) {
            return null;
        }

        byte[] ret = null;
        for (int i = 0; i < ss.length; ++i) {
            ValueTypable vt = listVT.get(i);
            byte[] bytes = vt.toBytes(ss[i]);
            ret = ret == null ? bytes : Bytes.add(ret, bytes);
        }

        return ret;
    }

    @Override
    public boolean needQuoted() {
        return true;
    }

    @Override
    public int getValueLength() {
        int total = 0;
        for (ValueTypable vt : listVT) {
            total += vt.getValueLength();
        }
        return total;
    }

    @Override
    public boolean needEsacaping() {
        return false;
    }

    @Override
    public boolean match(byte[] value, int offset) {
        if (!hasVariableValueTypeable && value.length != getValueLength()
                || hasVariableValueTypeable && value.length < fixedLength) {
            return false;
        }

        int offset1 = 0;
        for (ValueTypable vt : listVT) {
            if (!(vt instanceof MatchAware)) {
                return false;
            }

            MatchAware ma = (MatchAware) vt;
            if (!ma.match(value, offset1)) {
                return false;
            }

            offset1 += vt.getValueLength() <= 0 ? value.length - fixedLength : vt.getValueLength();
        }

        return true;
    }

    @Override
    public boolean match(String value) {
        String[] parts = Strings.split(value, "-", false);
        if (parts.length != getListSize()) {
            return false;
        }

        for (int i = 0; i < parts.length; ++i) {
            ValueTypable vt = listVT.get(i);
            if (!(vt instanceof MatchAware)) {
                return false;
            }

            MatchAware ma = (MatchAware) vt;
            if (!ma.match(parts[i])) {
                return false;
            }
        }

        return true;
    }

    public void afterPropertiesSet() {
        int count = 0;
        fixedLength = 0;
        for (ValueTypable vt : listVT) {
            if (vt.getValueLength() <= 0) {
                ++count;
            }
            else {
                fixedLength += vt.getValueLength();
            }
        }
        if (count > 1) {
            throw new RuntimeException(type + " contains more than one unfixed length's type");
        }
    }
}
