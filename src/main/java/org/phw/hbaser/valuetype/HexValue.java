package org.phw.hbaser.valuetype;

import org.phw.core.lang.Codec;

public class HexValue extends ArgumentsApply {

    @Override
    public String toStr(byte[] value) {
        return Codec.toHex(value);
    }

    @Override
    public byte[] toBytes(String value) {
        return Codec.fromHex(value);
    }

}
