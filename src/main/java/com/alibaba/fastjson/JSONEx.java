package com.alibaba.fastjson;

import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.visitor.DefaultJSONOutputVisitor;
import com.alibaba.fastjson.visitor.DefaultJavaBeanHandler;
import com.alibaba.fastjson.visitor.JSONOutputVisitor;
import com.alibaba.fastjson.visitor.JSONPrettyFormatOutputVisitor;
import com.alibaba.fastjson.visitor.JavaBeanHandler;

public class JSONEx {
    public static final String toJSONString(Object object, boolean prettyFormat) {
        if (!prettyFormat) {
            return JSON.toJSONString(object);
        }

        return toJSONString(object, DefaultJavaBeanHandler.getInstance(), prettyFormat);
    }

    public static final String toJSONString(Object object, JavaBeanHandler javaBeanHandler) {
        return toJSONString(object, javaBeanHandler, false);
    }

    public static final String toJSONString(Object object, JavaBeanHandler javaBeanHandler, boolean prettyFormat) {
        SerializeWriter out = new SerializeWriter();

        try {
            JSONOutputVisitor visitor;

            if (prettyFormat) {
                visitor = new JSONPrettyFormatOutputVisitor(out);
            } else {
                visitor = new DefaultJSONOutputVisitor(out);
            }

            visitor.setJavaBeanHandler(javaBeanHandler);

            visitor.accept(object);

            return out.toString();
        } finally {
            out.close();
        }
    }
}
