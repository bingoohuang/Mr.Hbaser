package org.phw.core.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 集合类的一些操作。
 * @author BingooHuang
 *
 */
public class Collections {
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
    public static <T> boolean contains(Collection<T> array, T ele) {
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
     * 用该方法来代替 {@code new HashMap<K, V>()} 方式获得新的 {@code java.util.Map} 的实例对象。
     * 
     * @param <K> {@code Map} 中的键对象。
     * @param <V> {@code Map} 中的值对象。
     * @return 返回 {@code java.util.Map<K, V>} 关于 {@code java.util.HashMap<K, V>}
     *         实现的新实例。
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

    /**
     * 获得新的 {@code java.util.Map} 的实例对象, 并初始化容量.
     * @param <K> 键类型
     * @param <V> 值类型
     * @param initialCapacity 初试容量
     * @return HashMap实例
     */
    public static <K, V> HashMap<K, V> newHashMap(int initialCapacity) {
        return new HashMap<K, V>(initialCapacity);
    }

    /**
     * 获得新的 {@code java.util.Map} 的实例对象。
     * @param <K> 键类型
     * @param <V> 值类型
     * @return ConcurrentHashMap实例
     */
    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap<K, V>();
    }

    /**
     * 获得新的 {@code java.util.Map} 的实例对象, 并初始化容量.
     * @param <K> 键类型
     * @param <V> 值类型
     * @param initialCapacity 初试容量
     * @return ConcurrentHashMap实例
     */
    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(int initialCapacity) {
        return new ConcurrentHashMap<K, V>(initialCapacity);
    }

    /**
     * 用该方法来代替 {@code new ArrayList<T>()} 方式获得新的 {@code java.util.List} 的实例对象。
     * 
     * @param <T> {@code List<T>} 中保存的对象。
     * @return 返回 {@code java.util.List<T>} 关于 {@code java.util.ArrayList<T>}
     *         实现的新实例。
     */
    public static <T> ArrayList<T> newArrayList() {
        return new ArrayList<T>();
    }

    /**
     * 获得新的 {@code java.util.List} 的实例对象, 并初始化容量.
     * @param <T> 容器元素类型
     * @param initialCapacity 初试容量
     * @return ArrayList实例
     */
    public static <T> ArrayList<T> newArrayList(int initialCapacity) {
        return new ArrayList<T>(initialCapacity);
    }

    /**
     * 生成map.
     * @param <T> Type
     * @param objects 对象
     * @return map object
     */
    public static <T> Map<T, T> mapOf(T... objects) {
        Map m = new HashMap(objects.length / 2 + 1);
        int i = 0;
        for (; i < objects.length - 1; i += 2) {
            m.put(objects[i], objects[i + 1]);
        }

        if (i < objects.length) {
            m.put(objects[i], null);
        }

        return m;
    }

    /**
     * 组合一组对象为Map, 偶数位对象为键, 奇数位对象为值.
     * @param objects 对象列表。
     * @return Map实例
     */
    public static Map asMap(Object... objects) {
        Map m = new HashMap(objects.length / 2 + 1);
        int i = 0;
        for (; i < objects.length - 1; i += 2) {
            m.put(objects[i], objects[i + 1]);
        }

        if (i < objects.length) {
            m.put(objects[i], null);
        }

        return m;
    }

    /**
     * Returns {@code true} if the specified {@code Map} is {@code null} or {@link Map#isEmpty empty},
     * {@code false} otherwise.
     *
     * @param m the {@code Map} to check
     * @return {@code true} if the specified {@code Map} is {@code null} or {@link Map#isEmpty empty},
     *         {@code false} otherwise.
     * @since 1.0
     */
    public static boolean isEmpty(Map m) {
        return m == null || m.isEmpty();
    }

    /**
     * Returns {@code true} if the specified {@code Collection} is {@code null} or {@link Collection#isEmpty empty},
     * {@code false} otherwise.
     *
     * @param c the collection to check
     * @return {@code true} if the specified {@code Collection} is {@code null} or {@link Collection#isEmpty empty},
     *         {@code false} otherwise.
     * @since 1.0
     */
    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }
}
