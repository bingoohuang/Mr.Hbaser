package org.phw.core.lang;

/**
 * 对象的一些常用方法。
 * @author BingooHuang
 *
 */
public class Objects {
    /**
     * 判断两个对象是否相等。
     * @param a1 Object
     * @param a2 Object
     * @return true 两个对象相等。
     */
    public static boolean equals(Object a1, Object a2) {
        return a1 != null ? a1.equals(a2) : a2 == null;
    }

}
