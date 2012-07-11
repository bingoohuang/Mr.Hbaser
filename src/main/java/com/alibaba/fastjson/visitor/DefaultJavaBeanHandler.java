/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.fastjson.visitor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class DefaultJavaBeanHandler implements JavaBeanHandler {

    private ConcurrentMap<Class<?>, Map<String, Method>> gettersMap = new ConcurrentHashMap<Class<?>, Map<String, Method>>();

    private final static DefaultJavaBeanHandler          instance   = new DefaultJavaBeanHandler();

    public final static DefaultJavaBeanHandler getInstance() {
        return instance;
    }

    public void handle(Object javaBean, JSONVisitor visitor) {
        Class<?> clazz = javaBean.getClass();
        init(clazz);

        JSONOutputVisitor outputVisitor = (JSONOutputVisitor) visitor;
        outputVisitor.print('{');
        outputVisitor.incrementIndent();

        boolean first = true;

        Map<String, Method> getters = getGetters(clazz);
        for (Map.Entry<String, Method> getter : getters.entrySet()) {
            try {
                String name = getter.getKey();
                Object value = getter.getValue().invoke(javaBean, new Object[0]);
                if (value instanceof Date) {
                    value = ((Date) value).getTime();
                }

                if (value == null && outputVisitor.isIgnoreNull()) {
                    continue;
                }

                if (!first) {
                    outputVisitor.print(',');
                    outputVisitor.println();
                } else {
                    outputVisitor.println();
                }

                visitor.acceptEntry(new SimpleEntry(name, value));
            } catch (Throwable e) {
                throw new JSONException("output error, class : " + clazz.getName());
            }

            first = false;
        }

        outputVisitor.decementIndent();
        outputVisitor.println();
        outputVisitor.print('}');

    }

    public Map<String, Method> getGetters(Class<?> clazz) {
        return gettersMap.get(clazz);
    }

    public void init(Class<?> clazz) {
        Map<String, Method> getters = gettersMap.get(clazz);
        if (getters == null) {
            getters = new HashMap<String, Method>();
            for (Method method : clazz.getMethods()) {
                String methodName = method.getName();

                if (Modifier.isStatic(method.getModifiers())) {
                    continue;
                }

                if (method.getReturnType().equals(Void.TYPE)) {
                    continue;
                }

                if (method.getParameterTypes().length != 0) {
                    continue;
                }

                JSONField annotation = method.getAnnotation(JSONField.class);

                if (annotation != null) {
                    if (!annotation.serialize()) {
                        continue;
                    }

                    if (annotation.name().length() != 0) {
                        String propertyName = annotation.name();
                        getters.put(propertyName, method);
                        continue;
                    }
                }

                if (methodName.startsWith("get")) {
                    if (methodName.length() < 4) {
                        continue;
                    }

                    if (methodName.equals("getClass")) {
                        continue;
                    }

                    if (!Character.isUpperCase(methodName.charAt(3))) {
                        continue;
                    }

                    String propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                    getters.put(propertyName, method);
                }

                if (methodName.startsWith("is")) {
                    if (methodName.length() < 3) {
                        continue;
                    }

                    if (!Character.isUpperCase(methodName.charAt(2))) {
                        continue;
                    }

                    String propertyName = Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
                    getters.put(propertyName, method);
                }
            }
            gettersMap.putIfAbsent(clazz, getters);
        }
    }

    @SuppressWarnings("rawtypes")
    public static class SimpleEntry implements Map.Entry {

        private Object key;
        private Object value;

        public SimpleEntry(Object key, Object value){
            super();
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public Object setValue(Object value) {
            throw new UnsupportedOperationException();
        }
    }
}
