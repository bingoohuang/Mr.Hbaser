package org.phw.hbaser.util;

public class HbaseCell {
    private String family;
    private byte[] qualifier;
    private byte[] value;
    private byte[] rowkey;

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public byte[] getRowkey() {
        return rowkey;
    }

    public void setRowkey(byte[] rowkey) {
        this.rowkey = rowkey;
    }

    public byte[] getQualifier() {
        return qualifier;
    }

    public void setQualifier(byte[] qualifier) {
        this.qualifier = qualifier;
    }
}
