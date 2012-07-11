package org.phw.hbaser.valuetype;

public interface MatchAware {
    boolean match(byte[] value, int offset);

    boolean match(String value);
}
