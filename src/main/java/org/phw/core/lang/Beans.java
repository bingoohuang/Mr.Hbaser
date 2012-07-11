package org.phw.core.lang;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * JAVA BEAN的一些处理。
 * @author BingooHuang
 *
 */
public class Beans {
    /**
     * 设置属性。
     * @param obj Object.
     * @param proName Property Name.
     * @param value property value.
     */
    public static void setProperty(Object obj, String proName, String value) {
        if (obj == null) {
            return;
        }

        PropertyDescriptor proDescriptor;
        try {
            proDescriptor = new PropertyDescriptor(proName, obj.getClass());
        }
        catch (IntrospectionException e) {
            return;
        }
        Method m = proDescriptor.getWriteMethod();
        try {
            m.invoke(obj, Converts.convert(value, proDescriptor.getPropertyType()));
        }
        catch (IllegalArgumentException e) {
        }
        catch (IllegalAccessException e) {
        }
        catch (InvocationTargetException e) {
        }
    }

}
