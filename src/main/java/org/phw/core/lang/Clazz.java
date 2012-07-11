package org.phw.core.lang;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.phw.core.exception.AppException;

/**
 * 类型相关的辅助小函数。
 * @author BingooHuang
 *
 */
public class Clazz {
    /**
     * Create a new instance given a class name(要求有默认构造函数).
     * 
     * @param <T> Type
     * @param clz  Class
     * @return A new instance
     */
    public static <T> T newInstance(Class<T> clz) {
        try {
            return clz.newInstance();
        }
        catch (InstantiationException e) {
            throw new AppException(e);
        }
        catch (IllegalAccessException e) {
            throw new AppException(e);
        }
    }

    /**
     * Return the context classloader. BL: if this is command line operation,
     * the classloading issues are more sane. During servlet execution, we
     * explicitly set the ClassLoader.
     * 
     * @return The context classloader.
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * For class without checked exception throws.
     * @param <T> Type
     * @param className className
     * @return Class
     */
    public static <T> Class<T> forClass(String className) {
        try {
            return (Class<T>) getClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e) {
            throw new AppException(e);
        }
    }

    /**
     * 使用类型的实例创建.(要求有默认构造函数).
     * @param <T> Type
     * @param className 类完整名称
     * @return 类实例
     */
    public static <T> T newInstance(String className) {
        try {
            Class<T> loadClass = forClass(className);
            return loadClass.newInstance();
        }
        catch (InstantiationException e) {
            throw new AppException(e);
        }
        catch (IllegalAccessException e) {
            throw new AppException(e);
        }
    }

    /**
     * Does this Class implement an interface with this name.
     *
     * @param clss Class instance
     * @param excls Class of potential interface
     *
     * @return boolean was it an implementor
     */
    public static boolean classImplements(Class<?> clss, Class<?> excls) {
        Class<?> sprcls = clss;

        while (sprcls != null) {
            Class<?>[] interfaces = sprcls.getInterfaces();

            for (Class<?> interface1 : interfaces) {
                if (interface1.equals(excls)) {
                    return true;
                }
            }

            sprcls = sprcls.getSuperclass();
        }

        return false;
    }

    /**
     * Get method.
     * @param class1 class
     * @param methodName method name
     * @return method
     */
    public static Method getMethod(Class<? extends Object> class1, String methodName) {
        try {
            return class1.getMethod(methodName);
        }
        catch (SecurityException e) {
            throw new AppException(e);
        }
        catch (NoSuchMethodException e) {
            throw new AppException(e);
        }
    }

    /**
     * 安静的调用对象的方法。
     * @param target 对象
     * @param m 方法
     * @return 方法返回
     */
    public static Object invokeQuietly(Object target, Method m) {
        try {
            return m.invoke(target);
        }
        catch (IllegalArgumentException e) {
        }
        catch (IllegalAccessException e) {
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
        }

        return null;
    }

    /**
     * Is this Class in the CLASSPATH。
     *
     * @param classname String of the class
     *
     * @return boolean exists or not.
     */
    public static boolean classExists(String classname) {
        if (Strings.isEmpty(classname)) {
            return false;
        }

        /* try and load class */
        try {
            Class.forName(classname);
        }
        catch (ClassNotFoundException cnfe) {
            return false;
        }
        catch (IllegalArgumentException iae) {
            return false;
        }

        return true;
    }
}
