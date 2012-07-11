package org.phw.hbaser.valuetype;

import org.phw.core.lang.Codec;

public class Base64Value extends ArgumentsApply {

    @Override
    public String toStr(byte[] value) {
        return Codec.toBase64(value);
    }

    @Override
    public byte[] toBytes(String value) {
        return Codec.fromBase64(value);
    }

}
