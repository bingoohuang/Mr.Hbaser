package org.phw.core.lang;

/**
 * 数组辅助小函数类。
 * @author BingooHuang
 *
 */
public class Arrs {
    /**
     * An empty immutable <code>Object</code> array.
     */
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    /**
     * An empty immutable <code>Class</code> array.
     */
    public static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    /**
     * An empty immutable <code>String</code> array.
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * 判断一个数组内是否包括某一个对象。 它的比较将通过 equals(Object,Object) 方法。
     * 
     * @param <T> 对象类型。
     * @param array
     *            数组
     * @param ele
     *            对象
     * @return true 包含 false 不包含
     */
    public static <T> boolean contains(T ele, T... array) {
        if (null == array) {
            return false;
        }

        for (T e : array) {
            if (Objects.equals(e, ele)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 数组是否为空(null或长度为0).
     * @param <T> 元素类型
     * @param array 数据
     * @return true 为空
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }
}
