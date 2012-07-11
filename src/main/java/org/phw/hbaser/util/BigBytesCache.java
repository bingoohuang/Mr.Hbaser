package org.phw.hbaser.util;

import java.util.ArrayList;

import org.apache.hadoop.hbase.util.Bytes;
import org.phw.hbaser.valuetype.ValueTypable;

public class BigBytesCache {
    private static final String CACHEFLAG = "#Cache#";
    private static ArrayList<byte[]> cache = new ArrayList<byte[]>(10);

    public static void clearCache() {
        cache.clear();
    }

    public static String putCache(byte[] value) {
        cache.add(value);

        return CACHEFLAG + (cache.size() - 1);
    }

    public static byte[] getCache(String value) {
        if (value == null) {
            return null;
        }
        int posPos = value.indexOf(CACHEFLAG);
        if (posPos != 0) {
            return null;
        }

        String posStr = value.substring(posPos + CACHEFLAG.length());
        return getCacheBytes(Integer.valueOf(posStr));
    }

    public static byte[] getCacheBytes(String value, ValueTypable vt) {
        if (value == null) {
            return null;
        }

        int posPos = value.indexOf(CACHEFLAG);
        if (posPos != 0) {
            return vt == null ? Bytes.toBytes(value) : vt.toBytes(value);
        }

        String posStr = value.substring(posPos + CACHEFLAG.length());
        byte[] cachedBytes = getCacheBytes(Integer.valueOf(posStr));
        if (cachedBytes == null) {
            return vt == null ? Bytes.toBytes(value) : vt.toBytes(value);
        }

        return cachedBytes;
    }

    public static byte[] getCacheBytes(int pos) {
        if (pos < 0 || pos >= cache.size()) {
            return null;
        }

        return cache.get(pos);
    }

}
