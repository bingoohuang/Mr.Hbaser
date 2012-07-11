package org.phw.hbaser.valuetype;

import org.phw.core.lang.Strings;

public abstract class ArgumentsApply implements ValueTypable, ArgumentsAppliable {
    private int valueLength = 0;

    @Override
    public void apply(String[] args) {
        if (args != null && args.length > 0 && Strings.isInteger(args[0])) {
            valueLength = Integer.valueOf(args[0]);
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
