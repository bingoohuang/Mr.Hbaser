package org.phw.hbaser.valuetype;

import org.phw.core.lang.Strings;
import org.phw.core.net.Inets;

public class IpValue implements ValueTypable, ArgumentsAppliable {
    private int valueLength = 4;

    @Override
    public String toStr(byte[] value) {
        return Inets.getStringIp(value);
    }

    @Override
    public byte[] toBytes(String value) {
        return Inets.getInetAddress(value).getAddress();
    }

    @Override
    public void apply(String[] args) {
        if (args != null && args.length > 0) {
            if (Strings.equalsIgnoreCase("v4", args[0])) {
                valueLength = 4;
            }
            else if (Strings.equalsIgnoreCase("v6", args[0])) {
                valueLength = 16;
            }
        }
    }

    @Override
    public int getValueLength() {
        return valueLength;
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
