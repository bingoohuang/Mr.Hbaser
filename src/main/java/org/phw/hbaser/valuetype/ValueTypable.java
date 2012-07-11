package org.phw.hbaser.valuetype;

public interface ValueTypable {
    String toStr(byte[] value);

    byte[] toBytes(String value);

    int getValueLength();

    boolean needQuoted();

    boolean needEsacaping();

}
