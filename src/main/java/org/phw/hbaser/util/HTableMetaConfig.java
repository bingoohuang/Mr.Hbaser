package org.phw.hbaser.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.RowLock;
import org.apache.hadoop.hbase.util.Bytes;
import org.phw.core.lang.Arrs;
import org.phw.core.lang.Dates;
import org.phw.core.lang.Ios;
import org.phw.core.lang.Pair;
import org.phw.core.lang.Strings;
import org.phw.core.util.FnMatch;
import org.phw.hbaser.valuetype.MatchAware;
import org.phw.hbaser.valuetype.ValueTypable;
import org.phw.hbaser.valuetype.ValueTypeFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class HTableMetaConfig {
    private static final String ROWKEYTYPE = "rowkeytype";
    private static final String QUALIFIER = "qualifiertype.";
    private static final String FAMVALUETYPE = "famvaluetype.";
    private static final String VALUETYPE = "valuetype.";

    private static final String IMGLOCATION = "image.";

    private List<ValueTypable> rowkeyTypes = new ArrayList<ValueTypable>();
    private Map<String, String> imageFormats = new HashMap<String, String>();
    private Map<String, ValueTypable> tableValueTypes = new HashMap<String, ValueTypable>();
    private Multimap<String, ValueTypable> tableQualifierTypes = Multimaps.newListMultimap(
            Maps.<String, Collection<ValueTypable>> newHashMap(),
            new Supplier<List<ValueTypable>>() {
                @Override
                public List<ValueTypable> get() {
                    return Lists.newArrayList();
                }
            });

    private Map<String, ValueTypable> famValueTypes = new HashMap<String, ValueTypable>();
    private String[] tableFamilies;

    public HTableMetaConfig(String tableName, String meta) {
        this(null, tableName, meta);
    }

    public HTableMetaConfig(Pair<HTable, RowLock> lockPair, String tableName, String meta) {
        setTableFamilies(HTableDdls.getFamilies(lockPair, tableName));

        List<String> lines;
        try {
            lines = Ios.readLines(Ios.toInputStream(meta));
        }
        catch (IOException e) {
            HBaserUtils.error(e);
            return;
        }

        for (String line : lines) {
            line = line.trim();
            if (line.length() == 0 || line.startsWith("#")) { // 忽略空行和注释
                continue;
            }

            String keys = Strings.substringBefore(line, "=").trim();
            String values = Strings.substringAfter(line, "=").trim();

            // 一个key或多个 key（以^分割）可以对应多个以^分割的value
            String[] keyArr = Strings.split(keys, "^", true);
            String[] valueArr = Strings.split(values, "^", true);
            for (String key : keyArr) {
                for (String value : valueArr) {
                    processMetaKeyAndValue(key, value);
                }
            }
        }
    }

    private void processMetaKeyAndValue(String key, String value) {
        if (ROWKEYTYPE.equals(key)) {
            rowkeyTypes.add(ValueTypeFactory.getValueType(value));
        }
        else if (key.startsWith(VALUETYPE)) {
            addTableQualifierValueType(tableValueTypes, key.substring(VALUETYPE.length()), value);
        }
        else if (key.startsWith(IMGLOCATION)) {
            imageFormats.put(key.substring(IMGLOCATION.length()), value);
        }
        else if (key.startsWith(QUALIFIER)) {
            addTableQualifierValueType(tableQualifierTypes, key.substring(QUALIFIER.length()), value);
        }
        else if (key.startsWith(FAMVALUETYPE)) {
            addTableQualifierValueType(famValueTypes, key.substring(FAMVALUETYPE.length()), value);
        }
    }

    public ImageMeta getImageColumnAndFormat(JSONObject jsonObj) {
        for (Entry<String, String> entry : imageFormats.entrySet()) {
            Pair<String, String> famQualifier = spitFamilyAndQualifer(entry.getKey());
            if (Strings.isEmpty(famQualifier.getFirst())) {
                continue;
            }

            JSONObject famObj = jsonObj.getJSONObject(famQualifier.getFirst());
            if (famObj == null) {
                continue;
            }

            String base64 = famObj.getString(famQualifier.getSecond());
            ImageMeta imageMeta = new ImageMeta();
            imageMeta.setFamily(famQualifier.getFirst());
            imageMeta.setQualifier(famQualifier.getSecond());
            imageMeta.setImage(BigBytesCache.getCache(base64));
            imageMeta.setFormat(entry.getValue());

            return imageMeta;
        }

        return null;
    }

    public Pair<String, String> spitFamilyAndQualifer(String famQualifier) {
        String famName = null;
        String qualifierName = null;
        int indexOf = famQualifier.indexOf('.');
        if (indexOf < 0) {
            if (tableFamilies.length == 1) {
                famName = tableFamilies[0].trim();
                qualifierName = famQualifier;
            }
            else {
                qualifierName = famQualifier;
                HBaserUtils.message("列名[" + qualifierName + "]找不到合适的列族，将被忽略。");
            }
        }
        else {
            famName = famQualifier.substring(0, indexOf).trim();
            qualifierName = famQualifier.substring(indexOf + 1).trim();
        }

        return Pair.makePair(famName, qualifierName);
    }

    private static void addTableQualifierValueType(Multimap<String, ValueTypable> valueTypes,
            String tableQualifier, String value) {
        ValueTypable valueType = ValueTypeFactory.getValueType(value);
        if (valueType == null) {
            // 配置错误, 应该警告
            return;
        }

        valueTypes.put(tableQualifier, valueType);
    }

    private static void addTableQualifierValueType(Map<String, ValueTypable> valueTypes,
            String tableQualifier, String value) {
        ValueTypable valueType = ValueTypeFactory.getValueType(value);
        if (valueType == null) {
            // 配置错误, 应该警告
            return;
        }

        valueTypes.put(tableQualifier, valueType);
    }

    public String getRowkey(byte[] rowkey) {
        ValueTypable rowkeyTypable = findValueTypeable(rowkey, rowkeyTypes);
        if (rowkeyTypable == null) {
            return "\"" + JsonEscaper.escape(Bytes.toString(rowkey)) + "\"";
        }

        String str = rowkeyTypable.toStr(rowkey);
        if (rowkeyTypable.needQuoted()) {
            return "\"" + (rowkeyTypable.needEsacaping() ? JsonEscaper.escape(str) : str) + "\"";
        }

        return rowkeyTypable.needEsacaping() ? JsonEscaper.escape(str) : str;
    }

    public byte[] getRowkey(String rowkey) {
        ValueTypable rowkeyTypable = findValueTypeable(rowkey, rowkeyTypes);
        return rowkeyTypable == null ? Bytes.toBytes(rowkey) : rowkeyTypable.toBytes(rowkey);
    }

    public String getQualifierValue(String family, String qualifier, byte[] value, long ts, boolean speedTooBigBytes,
            boolean showTimestamp) {
        ValueTypable valueTypable = getValueTypable(family, qualifier);
        if (value == null) {
            return null;
        }

        if (speedTooBigBytes && value.length > 1000) { // TOO BIG
            return '\"' + BigBytesCache.putCache(value) + '\"';
        }

        if (valueTypable == null) {
            return "\"" + JsonEscaper.escape(Bytes.toString(value))
                    + (showTimestamp ? "@" + Dates.format(ts) : "") + "\"";
        }

        if (!valueTypable.needQuoted() && value.length == 0) {
            return null;
        }

        String str = valueTypable.toStr(value);
        if (valueTypable.needQuoted() || showTimestamp) {
            return "\"" + (valueTypable.needEsacaping() ? JsonEscaper.escape(str) : str)
                    + (showTimestamp ? "@" + Dates.format(ts) : "") + "\"";
        }

        return valueTypable.needEsacaping() ? JsonEscaper.escape(str) : str;
    }

    private ValueTypable getValueTypable(String family, String qualifier) {
        String familyDotQualifier = family + '.' + qualifier;
        ValueTypable valueTypable = tableValueTypes.get(familyDotQualifier);
        if (valueTypable != null) {
            return valueTypable;
        }

        for (Entry<String, ValueTypable> entry : tableValueTypes.entrySet()) {
            if (FnMatch.fnmatch(entry.getKey(), familyDotQualifier)) {
                return entry.getValue();
            }
        }

        return famValueTypes.get(family);
    }

    public byte[] getQualifierValue(String family, String qualifier, String value) {
        ValueTypable valueTypable = getValueTypable(family, qualifier);

        return BigBytesCache.getCacheBytes(value, valueTypable);
    }

    public String getQualifier(String family, byte[] qualifier) {
        Collection<ValueTypable> valueTypables = tableQualifierTypes.get(family);
        ValueTypable valueTypable = findValueTypeable(qualifier, valueTypables);

        if (valueTypable == null) {
            return JsonEscaper.escape(Bytes.toString(qualifier));
        }

        String str = valueTypable.toStr(qualifier);

        return valueTypable.needEsacaping() ? JsonEscaper.escape(str) : str;
    }

    private ValueTypable findValueTypeable(byte[] bytes, Collection<ValueTypable> valueTypables) {
        ValueTypable valueTypable = null;
        for (ValueTypable vt : valueTypables) {
            if (vt instanceof MatchAware) {
                if (((MatchAware) vt).match(bytes, 0)) {
                    valueTypable = vt;
                    break;
                }
            }
            else {
                valueTypable = vt;
            }
        }
        return valueTypable;
    }

    private ValueTypable findValueTypeable(String str, Collection<ValueTypable> valueTypables) {
        ValueTypable valueTypable = null;
        for (ValueTypable vt : valueTypables) {
            if (vt instanceof MatchAware) {
                if (((MatchAware) vt).match(str)) {
                    valueTypable = vt;
                    break;
                }
            }
            else {
                valueTypable = vt;
            }
        }
        return valueTypable;
    }

    public byte[] getQualifier(String family, String value) {
        Collection<ValueTypable> valueTypables = tableQualifierTypes.get(family);
        ValueTypable valueTypable = null;
        for (ValueTypable vt : valueTypables) {
            if (vt instanceof MatchAware) {
                if (((MatchAware) vt).match(value)) {
                    valueTypable = vt;
                    break;
                }
            }
            else if (valueTypable != null) {
                valueTypable = vt;
            }
        }

        return valueTypable == null ? Bytes.toBytes(value) : valueTypable.toBytes(value);
    }

    public String[] getTableFamilies() {
        return tableFamilies;
    }

    public void setTableFamilies(String[] tableFamilies) {
        this.tableFamilies = tableFamilies;
    }

    public boolean existsFamiliy(String famName) {
        return Arrs.contains(famName, tableFamilies);
    }

    public ValueTypable getRowkeyTypable() {
        return rowkeyTypes.size() == 0 ? null : rowkeyTypes.get(0);
    }

}
