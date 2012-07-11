package org.phw.core.lang;

/**
 * 对象包装器。
 *
 * @param <T> 包装的类型
 */
public class Wrapper<T> {
    private T object;
    private boolean initialized;

    /**
     * ctor.
     * @param obj 被包装的对象
     */
    public Wrapper(T obj) {
        this.set(obj);
        setInitialized(true);
    }

    /**
     * default ctor.
     */
    public Wrapper() {
        setInitialized(false);
    }

    /**
     * 设置对象。
     * @param object 被包装的对象
     */
    public void set(T object) {
        this.object = object;
        setInitialized(true);
    }

    /**
     * 获取对象。
     * @return 被包装的对象
     */
    public T get() {
        return object;
    }

    /**
     * 设置是否初始化。
     * @param initialized 是否初始化
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * 是否初始化。
     * @return true 被包装对象已经初始化; false 未初始化
     */
    public boolean isInitialized() {
        return initialized;
    }
}
