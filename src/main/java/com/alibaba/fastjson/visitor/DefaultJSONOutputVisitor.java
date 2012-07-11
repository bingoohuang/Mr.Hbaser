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

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONStreamAware;
import com.alibaba.fastjson.serializer.SerializeWriter;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class DefaultJSONOutputVisitor extends JSONVisitorAdapter implements JSONOutputVisitor {

    protected final SerializeWriter out;
    protected boolean               ignoreNull = true;

    public DefaultJSONOutputVisitor(SerializeWriter out){
        super();
        this.out = out;
    }

    public boolean isIgnoreNull() {
        return ignoreNull;
    }

    public void setIgnoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
    }

    public Appendable getOut() {
        return out;
    }

    @Override
    public boolean visitArray(Collection<?> array) {
        print('[');

        boolean first = true;
        for (Object item : array) {
            if (!first) {
                print(',');
            }

            accept(item);

            first = false;
        }

        print(']');
        return false;
    }
    
    @Override
    public boolean visitArray(Object[] array) {
        print('[');

        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                print(',');
            }

            accept(array[i]);
        }

        print(']');
        return false;
    }

    @Override
    public boolean visitObject(Map<?, ?> map) {
        print('{');

        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (ignoreNull && entry.getValue() == null) {
                continue;
            }

            if (!first) {
                print(',');
            }

            acceptEntry(entry);

            first = false;
        }
        print('}');

        return false;
    }

    @Override
    public void visitKey(String key) {
        print(key);
    }

    @Override
    public boolean visitEntry(Map.Entry<?, ?> entry) {
        acceptKey(entry.getKey().toString());
        print(':');
        accept(entry.getValue());

        return false;
    }

    @Override
    public void visitValue(String value) {
        print(value);
    }

    @Override
    public void visitValue(Date value) {
        print0("new Date(");
        print0(Long.toString(value.getTime()));
        print(')');
    }

    @Override
    public void visitValue(Number value) {
        if (value.equals(Double.NaN) || value.equals(Float.NaN)) {
            print0("null");
            return;
        }

        print0(value.toString());
    }

    @Override
    public void visitValue(boolean value) {
        print0(value ? "true" : "false");
    }

    @Override
    public void visitNull() {
        print0("null");
    }

    protected void print0(String text) {
        out.append(text);
    }

    public void print(String text) {
        out.writeString(text);
    }

    public void print(char ch) {
        out.append(ch);
    }
    
    public void visitValue(char value) {
        print(new String(Character.toString(value)));
    }

    @Override
    public void visitBean(JSONStreamAware object) {
        if (object == null) {
            print0("null");
            return;
        }

        try {
            object.writeJSONString(out);
        } catch (IOException e) {
            throw new JSONException("visitBean error", e);
        }
    }

    @Override
    public void visitBean(JSONAware object) {
        if (object == null) {
            print0("null");
            return;
        }

        print(object.toJSONString());
    }

    @Override
    public void visitBean(Object javaBean) {
        if (javaBeanHandler != null) {
            javaBeanHandler.handle(javaBean, this);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void println() {
        // skip
    }

    public void incrementIndent() {
        // skip
    }

    public void decementIndent() {
        // skip
    }
}
