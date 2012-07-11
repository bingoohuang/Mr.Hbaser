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
import java.util.Map;

import com.alibaba.fastjson.serializer.SerializeWriter;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class JSONPrettyFormatOutputVisitor extends DefaultJSONOutputVisitor {

    private String indent = "  ";
    private int indentCount = 0;

    public JSONPrettyFormatOutputVisitor(SerializeWriter out) {
        super(out);
    }

    @Override
    public void incrementIndent() {
        indentCount++;
    }

    @Override
    public void decementIndent() {
        indentCount--;
    }

    @Override
    public boolean visitArray(Collection<?> array) {
        if (array.size() == 0) {
            print0("[]");
            return false;
        }

        print('[');

        incrementIndent();
        println();

        boolean first = true;
        for (Object item : array) {
            if (!first) {
                print(',');
                println();
            }

            accept(item);

            first = false;
        }

        decementIndent();
        println();

        print(']');
        return false;
    }

    @Override
    public boolean visitArray(Object[] array) {
        if (array.length == 0) {
            print0("[]");
            return false;
        }

        print('[');

        incrementIndent();
        println();

        boolean first = true;
        for (Object item : array) {
            if (!first) {
                print(',');
                println();
            }

            accept(item);

            first = false;
        }

        decementIndent();
        println();

        print(']');
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean visitEntry(Map.Entry<?, ?> entry) {
        acceptKey(entry.getKey().toString());
        print(':');
        print(' ');

        Object value = entry.getValue();
        if (value instanceof Collection) {
            Collection array = (Collection) value;
            if (array.size() != 0) {
                incrementIndent();
                println();
                accept(entry.getValue());
                decementIndent();
            }
            else {
                accept(entry.getValue());
            }
        }
        else {
            accept(entry.getValue());
        }

        return false;
    }

    @Override
    public boolean visitObject(Map<?, ?> map) {
        if (map.size() == 0) {
            print0("{}");
            return false;
        }

        print('{');

        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (ignoreNull && entry.getValue() == null) {
                continue;
            }

            if (!first) {
                print(',');
                println();
            }
            else {
                incrementIndent();
                println();
            }

            accept(entry);

            first = false;
        }

        if (!first) {
            decementIndent();
            println();
        }

        print('}');

        return false;
    }

    @Override
    public void println() {
        print('\n');
        for (int i = 0; i < indentCount; ++i) {
            print0(indent);
        }
    }
}
