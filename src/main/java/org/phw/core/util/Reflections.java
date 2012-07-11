package org.phw.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.phw.core.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 反射工具类.
 *
 */
public abstract class Reflections {
    private static final Logger logger = LoggerFactory.getLogger(Reflections.class);

    /**
     * 设置对象的属性值。
     * @param target 对象实例
     * @param fname 属性名称
     * @param fvalue 属性值
     */
    public static void setFieldValue(Object target, String fname, Object fvalue) {
        setFieldValue(target, target.getClass(), fname, fvalue.getClass(), fvalue);
    }

    /**
     * 设置对象的属性值。
     * @param target 对象实例
     * @param fname 属性名称
     * @param ftype 属性类型
     * @param fvalue 属性值
     */
    public static void setFieldValue(Object target, String fname, Class<?> ftype, Object fvalue) {
        setFieldValue(target, target.getClass(), fname, ftype, fvalue);
    }

    /**
     * 设置对象的属性值。
     * @param target 对象实例
     * @param clazz 对象类型
     * @param fname 属性名称
     * @param ftype 属性类型
     * @param fvalue 属性值
     */
    public static void setFieldValue(Object target, Class<?> clazz, String fname, Class<?> ftype, Object fvalue) {
        if (target == null || fname == null || "".equals(fname)) {
            return;
        }
        if (fvalue != null && !ftype.isAssignableFrom(fvalue.getClass())) {
            return;
        }

        try {
            Method method = clazz.getDeclaredMethod(
                    "set" + Character.toUpperCase(fname.charAt(0)) + fname.substring(1), ftype);
            //if (!Modifier.isPublic(method.getModifiers())) {
            method.setAccessible(true);
            //}
            method.invoke(target, fvalue);

        }
        catch (Exception me) {
            logger.debug("exception", me);

            try {
                Field field = clazz.getDeclaredField(fname);
                //if (!Modifier.isPublic(field.getModifiers())) {
                field.setAccessible(true);
                //}
                field.set(target, fvalue);
            }
            catch (Exception fe) {
                logger.debug("exception", me);
            }
        }
    }

    /**
     * 获取对象的属性值.
     * @param target 对象实例
     * @param fname 属性名称
     * @return 属性值
     */
    public static Object getFieldValue(Object target, String fname) {
        return getFieldValue(target, target.getClass(), fname);
    }

    /**
     * 获取对象的属性值。
     * @param target 对象实例
     * @param clazz 对象类型
     * @param fname 属性名称
     * @return 属性值
     */
    public static Object getFieldValue(Object target, Class<?> clazz, String fname) {
        if (target == null || fname == null || "".equals(fname)) {
            return null;
        }

        boolean exCatched = false;
        try {
            String methodname = "get" + Strings.capitalize(fname);
            Method method = clazz.getDeclaredMethod(methodname);
            //if (!Modifier.isPublic(method.getModifiers())) {
            method.setAccessible(true);
            //}
            return method.invoke(target);
        }
        catch (NoSuchMethodException e) {
            exCatched = true;
        }
        catch (InvocationTargetException e) {
            exCatched = true;
        }
        catch (IllegalAccessException e) {
            exCatched = true;
        }

        if (exCatched) {
            try {
                Field field = clazz.getDeclaredField(fname);
                //if (!Modifier.isPublic(field.getModifiers())) {
                field.setAccessible(true);
                //}
                return field.get(target);
            }
            catch (Exception fe) {
                logger.debug("getDeclaredField exception", fe);
            }
        }
        return null;
    }

}
