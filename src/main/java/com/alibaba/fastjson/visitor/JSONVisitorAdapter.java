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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONStreamAware;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class JSONVisitorAdapter implements JSONVisitor {

    protected JavaBeanHandler javaBeanHandler;
    protected int             depth    = 0;
    private int               maxDepth = 100;

    public JSONVisitorAdapter(){
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public JavaBeanHandler getJavaBeanHandler() {
        return javaBeanHandler;
    }

    public void setJavaBeanHandler(JavaBeanHandler javaBeanHandler) {
        this.javaBeanHandler = javaBeanHandler;
    }

    public void accept(Object object) {
        if (object == null) {
            acceptNull();
            return;
        }

        Class<?> clazz = object.getClass();

        depth++;
        try {
            if (depth > maxDepth) {
                throw new JSONException("depth " + depth + ", maybe circular references");
            }
            if (clazz == Integer.class) {
                acceptValue((Number) object);
            } else if (clazz == Long.class) {
                acceptValue((Number) object);
            } else if (object instanceof Map<?, ?>) {
                acceptObject((Map<?, ?>) object);
            } else if (object instanceof Collection<?>) {
                acceptArray((Collection<?>) object);
            } else if (object instanceof Map.Entry) {
                acceptEntry((Map.Entry<?, ?>) object);
            } else if (object instanceof Boolean) {
                acceptValue((Boolean) object);
            } else if (object instanceof Number) {
                acceptValue((Number) object);
            } else if (object instanceof String) {
                acceptValue((String) object);
            } else if (object instanceof Date) {
                acceptValue((Date) object);
            } else if (object instanceof Character) {
                acceptValue((Character) object);
            } else if (object instanceof JSONAware) {
                acceptBean((JSONAware) object);
            } else if (object instanceof JSONStreamAware) {
                acceptBean((JSONStreamAware) object);
            } else if (object.getClass().isArray()) {
                acceptArray(object);
            } else {
                acceptBean(object);
            }
        } finally {
            depth--;
        }
    }

    public void acceptBean(JSONAware javaBean) {
        this.preVisit(javaBean);
        this.visitBean(javaBean);
        this.endVisitBean(javaBean);
        this.postVisit(javaBean);
    }

    public void acceptBean(JSONStreamAware javaBean) {
        this.preVisit(javaBean);
        this.visitBean(javaBean);
        this.endVisitBean(javaBean);
        this.postVisit(javaBean);
    }

    public void acceptBean(Object javaBean) {
        this.preVisit(javaBean);
        this.visitBean(javaBean);
        this.endVisitBean(javaBean);
        this.postVisit(javaBean);
    }

    public void acceptNull() {
        this.preVisit(null);
        this.visitNull();
        this.endVisitNull();
        this.postVisit(null);
    }

    public void acceptValue(Date value) {
        this.preVisit(value);
        this.visitValue(value);
        this.endVisitValue(value);
        this.postVisit(value);
    }
    
    public void acceptValue(Character value) {
        this.preVisit(value);
        this.visitValue(value);
        this.endVisitValue(value);
        this.postVisit(value);
    }

    public void acceptValue(Number value) {
        this.preVisit(value);
        this.visitValue(value);
        this.endVisitValue(value);
        this.postVisit(value);
    }

    public void acceptValue(String value) {
        this.preVisit(value);
        this.visitValue(value);
        this.endVisitValue(value);
        this.postVisit(value);
    }

    public void acceptValue(Boolean value) {
        this.preVisit(value);
        this.visitValue(value);
        this.endVisitValue(value);
        this.postVisit(value);
    }

    public void acceptKey(String key) {
        this.preVisit(key);
        this.visitKey(key);
        this.endVisitKey(key);
        this.postVisit(key);
    }

    public void acceptArray(Collection<?> array) {
        this.preVisit(array);
        if (this.visitArray(array)) {
            for (Object item : array) {
                accept(item);
            }
        }
        this.endVisitArray(array);
        this.postVisit(array);
    }
    
    public void acceptArray(Object array) {
        int length = Array.getLength(array);
        Object[] objArray = new Object[length];
        for (int i = 0; i < length; ++i) {
            objArray[i] = Array.get(array, i);
        }
        acceptArray(objArray);
    }
    
  
    
    public void acceptArray(Object[] array) {
        this.preVisit(array);
        if (this.visitArray(array)) {
            for (Object item : array) {
                accept(item);
            }
        }
        this.endVisitArray(array);
        this.postVisit(array);
    }

    public void acceptObject(Map<?, ?> object) {
        this.preVisit(object);
        if (this.visitObject(object)) {
            for (Map.Entry<?, ?> entry : object.entrySet()) {
                acceptEntry(entry);
            }
        }
        this.endVisitObject(object);
        this.postVisit(object);
    }

    public void acceptEntry(Map.Entry<?, ?> entry) {
        this.preVisit(entry);
        if (this.visitEntry(entry)) {
            acceptKey(entry.getKey().toString());
            accept(entry.getValue());
        }
        this.endVisitEntry(entry);
        this.postVisit(entry);
    }

    public boolean visitObject(Map<?, ?> map) {
        return true;
    }

    public void endVisitObject(Map<?, ?> map) {

    }

    public boolean visitArray(Collection<?> array) {
        return true;
    }
    
    public boolean visitArray(Object[] array) {
        return true;
    }

    public void endVisitArray(Collection<?> array) {

    }
    
    public void endVisitArray(Object[] array) {
        
    }

    public void preVisit(Object value) {

    }

    public void postVisit(Object value) {

    }

    public boolean visitEntry(Map.Entry<?, ?> entry) {
        return true;
    }

    public void endVisitEntry(Map.Entry<?, ?> entry) {

    }

    public void visitValue(Number value) {
    }

    public void endVisitValue(Number value) {

    }
    
    public void visitValue(char value) {
    }
    
    public void endVisitValue(char value) {
        
    }

    public void visitValue(boolean value) {
    }

    public void endVisitValue(boolean value) {

    }

    public void visitValue(String value) {
    }

    public void endVisitValue(String value) {

    }

    public void visitKey(String key) {
    }

    public void endVisitKey(String key) {

    }

    public void visitNull() {
    }

    public void endVisitNull() {

    }

    public void visitBean(JSONStreamAware object) {
    }

    public void endVisitBean(JSONStreamAware object) {

    }

    public void visitBean(JSONAware object) {
    }

    public void endVisitBean(JSONAware object) {

    }

    public void visitValue(Date value) {

    }

    public void endVisitValue(Date value) {

    }

    public void visitBean(Object object) {

    }

    public void endVisitBean(Object object) {
    }

}
