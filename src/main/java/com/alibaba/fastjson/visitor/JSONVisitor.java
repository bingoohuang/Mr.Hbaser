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

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONStreamAware;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public interface JSONVisitor {

    JavaBeanHandler getJavaBeanHandler();

    void setJavaBeanHandler(JavaBeanHandler javaBeanHandler);

    void accept(Object object);

    void acceptBean(JSONAware javaBean);

    void acceptBean(JSONStreamAware javaBean);

    void acceptBean(Object javaBean);

    void acceptNull();

    void acceptValue(Date value);

    void acceptValue(Number value);

    void acceptValue(String value);

    void acceptValue(Boolean value);

    void acceptKey(String key);

    void acceptArray(Collection<?> array);

    void acceptObject(Map<?, ?> object);

    void acceptEntry(Map.Entry<?, ?> entry);

    void visitBean(JSONStreamAware object);

    void endVisitBean(JSONStreamAware object);

    void visitBean(JSONAware object);

    void endVisitBean(JSONAware object);

    void visitBean(Object object);

    void endVisitBean(Object object);

    boolean visitObject(Map<?, ?> map);

    void endVisitObject(Map<?, ?> map);

    boolean visitEntry(Map.Entry<?, ?> entry);

    void endVisitEntry(Map.Entry<?, ?> entry);

    boolean visitArray(Collection<?> array);

    void endVisitArray(Collection<?> array);

    void visitValue(Number value);

    void endVisitValue(Number value);

    void visitValue(Date value);

    void endVisitValue(Date value);

    void visitValue(boolean value);

    void endVisitValue(boolean value);

    void visitValue(String value);

    void endVisitValue(String value);

    void visitNull();

    void endVisitNull();

    void visitKey(String key);

    void endVisitKey(String key);

    void preVisit(Object value);

    void postVisit(Object value);
}
