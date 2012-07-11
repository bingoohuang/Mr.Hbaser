package org.phw.core.lang;

import java.io.Serializable;

/**
 * 二元值类型。
 * @author BingooHuang
 *
 * @param <A> 第一值类型
 * @param <B> 第二值类型
 */
public class Pair<A, B> implements Serializable {
    private static final long serialVersionUID = -3439517338171618334L;
    private A first;
    private B second;

    /**
     * default ctor。
     */
    public Pair() {
        // Default Ctor
    }

    /**
     * ctor。
     * @param first 第一值对象
     * @param second 第二值对象
     */
    public Pair(A first, B second) {
        super();
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.first == null ? 0 : this.first.hashCode());
        result = prime * result + (this.second == null ? 0 : this.second.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Pair other = (Pair) obj;
        if (this.first == null) {
            if (other.first != null) {
                return false;
            }
        }
        else if (!this.first.equals(other.first)) {
            return false;
        }
        if (this.second == null) {
            if (other.second != null) {
                return false;
            }
        }
        else if (!this.second.equals(other.second)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    /**
     * 取得第一值。
     * @return 第一值
     */
    public A getFirst() {
        return first;
    }

    /**
     * 设置第一值。
     * @param first 第一值
     */
    public void setFirst(A first) {
        this.first = first;
    }

    /**
     * 取得第二值。
     * @return 第二值
     */
    public B getSecond() {
        return second;
    }

    /**
     * 设置第二值。
     * @param second 第二值
     */
    public void setSecond(B second) {
        this.second = second;
    }

    /**
     * 构造对偶类型对象。
     * @param <A> 第一值类型
     * @param <B> 第二值类型
     * @param a 第一值
     * @param b 第二值
     * @return 二元值对象
     */
    public static <A, B> Pair<A, B> makePair(A a, B b) {
        return new Pair<A, B>(a, b);
    }
}
