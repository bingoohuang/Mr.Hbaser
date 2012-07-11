package org.phw.hbaser.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyledDocument;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.BinaryPrefixComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.filter.WritableByteArrayComparable;
import org.apache.hadoop.hbase.util.Bytes;
import org.phw.core.lang.Ios;
import org.phw.core.lang.Pair;
import org.phw.core.lang.Strings;
import org.phw.hbaser.ui.QueryOption;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public abstract class HTableBaseQuery {
    protected HTableMetaConfig metaConfig;

    public static interface CASByQueryCallback {
        boolean rowProcess(HTable hTable, Result rr, HbaseCell cell, JSONObject jsonObj) throws Exception;
    }

    public static interface OperateByQueryCallback {
        void rowProcess(HTable hTable, Result rr, JSONObject jsonObj) throws Exception;

        void endRowProcess() throws Exception;

        int getRownum();
    }

    protected void updateByQuery(String tableName, String tableMeta, String rowkey, String strWhere, String json,
            JTextPane textResult, String opName, OperateByQueryCallback operateByQueryCallback) {
        Watcher watcher = new Watcher(textResult, "Start " + opName);

        ResultScanner rs = null;
        String trimmedRowkey = rowkey.trim();
        HTable hTable = null;
        try {
            metaConfig = new HTableMetaConfig(tableName, tableMeta);
            hTable = new HTable(HBaserConfig.config, tableName);
            JSONObject jsonObj = json == null ? null : JSON.parseObject(json);
            rs = createResultScanner(null, strWhere, trimmedRowkey, hTable);
            for (Result rr = rs.next(); rr != null; rr = rs.next()) {
                operateByQueryCallback.rowProcess(hTable, rr, jsonObj);
            }
            operateByQueryCallback.endRowProcess();
            watcher.stopWatch("Finish " + opName + ". Total puts " + operateByQueryCallback.getRownum() + " records");
        }
        catch (Exception e) {
            watcher.error(e);
        }
        finally {
            Ios.closeQuietly(hTable, rs);
        }
    }

    protected void casByQuery(String tableName, String tableMeta, String rowkey, String strWhere,
            String check, String json, JTextPane textResult, String opName, CASByQueryCallback casCallback) {
        Watcher watcher = new Watcher(textResult, "Start " + opName);
        ResultScanner rs = null;
        HTable hTable = null;
        try {
            metaConfig = new HTableMetaConfig(tableName, tableMeta);
            String trimmedRowkey = rowkey.trim();

            JSONObject jsonObj = JSON.parseObject(json);
            HbaseCell cell = HTablePuts.parseCheck(metaConfig, tableName, watcher, check, jsonObj, true);
            if (cell == null) {
                return;
            }

            hTable = new HTable(HBaserConfig.config, tableName);

            rs = createResultScanner(null, strWhere, trimmedRowkey, hTable);
            int total = 0;
            int effected = 0;
            for (Result rr = rs.next(); rr != null; ++total, rr = rs.next()) {
                effected += casCallback.rowProcess(hTable, rr, cell, jsonObj) ? 1 : 0;
                if (effected == 1000) {
                    hTable.flushCommits();
                }
            }
            hTable.flushCommits();
            watcher.stopWatch("Finish " + opName + ". Total " + total + " effected " + effected + " records");
        }
        catch (Exception e) {
            watcher.error(e);
        }
        finally {
            Ios.closeQuietly(hTable, rs);
        }
    }

    protected ResultScanner createResultScanner(String strSelect, String strWhere,
            String trimmedRowkey, HTable hTable, Filter... extendFilters) throws IOException {
        ResultScanner rs;
        Scan s = createScan(trimmedRowkey);
        s.setMaxVersions(1);
        if (!Strings.isBlank(strSelect)) {
            selectQualifiers(strSelect, s);
        }
        whereScan(strWhere, s, extendFilters);

        rs = hTable.getScanner(s);
        return rs;
    }

    private void selectQualifiers(String selectQualifiers, Scan s) {
        String[] retQualifierArr = Strings.split(selectQualifiers, ",", true);
        for (String retQualifier : retQualifierArr) {
            if ("rowkey".equalsIgnoreCase(retQualifier)) {
                continue;
            }

            Pair<String, String> pair = metaConfig.spitFamilyAndQualifer(retQualifier);
            String famName = pair.getFirst();
            String qualifierName = pair.getSecond();

            if (!Strings.isEmpty(famName)) {
                if (!metaConfig.existsFamiliy(famName)) {
                    HBaserUtils.message("列族[" + famName + "]不存在，忽略。");
                    continue;
                }

                if ("*".equals(qualifierName) || Strings.isEmpty(qualifierName)) {
                    s.addFamily(Bytes.toBytes(famName));
                }
                else {
                    s.addColumn(Bytes.toBytes(famName), Bytes.toBytes(qualifierName));
                }
            }
        }
    }

    private void whereScan(String strWhere, Scan s, Filter... extendFilters) {
        Filter filter = s.getFilter();
        FilterList filterList = new FilterList(Operator.MUST_PASS_ALL);
        if (filter != null) {
            filterList.addFilter(filter);
        }

        for (Filter extendFilter : extendFilters) {
            filterList.addFilter(extendFilter);
        }

        String[] ands = strWhere.split("\\b[aA][nN][dD]\\b");
        for (String and : ands) {
            Filter f = createAndFilter(and);
            if (f != null) {
                filterList.addFilter(f);
            }
        }
        s.setFilter(filterList);
    }

    private Scan createScan(String trimmedRowkey) {
        Get get = null;
        String startRowKey = null;
        String endRowKey = null;
        String[] rowkeys = null;

        if (trimmedRowkey.length() > 0) {
            int tildePos = trimmedRowkey.indexOf('~');
            if (tildePos < 0) {
                rowkeys = Strings.split(trimmedRowkey, ",", true);
                if (rowkeys.length <= 1) {
                    get = new Get(metaConfig.getRowkey(trimmedRowkey));
                }
            }
            else {
                startRowKey = trimmedRowkey.substring(0, tildePos).trim();
                endRowKey = trimmedRowkey.substring(tildePos + 1).trim();
            }
        }

        Scan s = null;
        if (get != null) {
            s = new Scan(get);
        }
        else if (!Strings.isEmpty(startRowKey) && !Strings.isEmpty(endRowKey)) {
            s = new Scan(metaConfig.getRowkey(startRowKey), metaConfig.getRowkey(endRowKey));
        }
        else if (!Strings.isEmpty(startRowKey) && Strings.isEmpty(endRowKey)) {
            s = new Scan(metaConfig.getRowkey(startRowKey));
        }
        else if (Strings.isEmpty(startRowKey) && !Strings.isEmpty(endRowKey)) {
            RowFilter filter = new RowFilter(CompareOp.LESS_OR_EQUAL,
                    new BinaryComparator(Bytes.toBytes(endRowKey)));
            s = new Scan();
            s.setFilter(filter);
        }
        else if (rowkeys != null && rowkeys.length > 1) {
            FilterList filterList = new FilterList(Operator.MUST_PASS_ONE);
            for (String rk : rowkeys) {
                RowFilter filter = new RowFilter(CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(rk)));
                filterList.addFilter(filter);
            }
            s = new Scan();
            s.setFilter(filterList);
        }
        else {
            s = new Scan();
        }
        return s;
    }

    // ([^><=~\?]+)(\??)(==|=^|=~|>=|<=|!=|<>|>|<|=)([^><=~\?]+)
    // ([^><=~\?]+)\s+(\??)(contains|in|between)\s+([^><=~\?]+)
    private static Pattern cond1 = Pattern.compile("([^><=~\\?]+)(\\??)(==|=\\^|=~|>=|<=|!=|<>|>|<|=)([^\\^><=~\\?]+)");
    private static Pattern cond2 = Pattern.compile("([^><=~\\?]+)\\s+(\\??)(contains|in|between)\\s+([^><=~\\?]+)");

    private Filter createAndFilter(String and) {
        Matcher matcher = cond1.matcher(and);
        if (!matcher.matches()) {
            matcher = cond2.matcher(and);
        }
        if (!matcher.matches()) {
            return null;
        }

        String famQualifier = matcher.group(1).trim();
        boolean filterIfMissing = matcher.group(2).length() == 0;
        String op = matcher.group(3);
        String value = matcher.group(4).trim();
        if (Strings.isBlank(value)) {
            return null;
        }

        Pair<String, String> pair = metaConfig.spitFamilyAndQualifer(famQualifier);
        String famName = pair.getFirst();
        String qualifierName = pair.getSecond();

        if (Strings.isEmpty(famName) || Strings.isEmpty(qualifierName)) {
            HBaserUtils.message("列族[" + famName + "]或者列名[" + qualifierName + "]为空，忽略。");
            return null;
        }

        if (!metaConfig.existsFamiliy(famName)) {
            HBaserUtils.message("列族[" + famName + "]不存在，忽略。");
            return null;
        }

        byte[] qualifierValue = null;
        if (!"null".equals(value)) {
            value = JsonEscaper.removeQuotation(value);
            value = JsonEscaper.unescape(value);
            qualifierValue = isQualifierNameOperating(qualifierName)
                    ? metaConfig.getQualifier(famName, value)
                    : metaConfig.getQualifierValue(famName, qualifierName, value);
        }

        CompareOp compareOp = null;
        WritableByteArrayComparable comparator = null;
        Filter filter2 = null;
        if ("=".equals(op) || "==".equals(op)) {
            compareOp = CompareOp.EQUAL;
            comparator = new BinaryComparator(qualifierValue);
        }
        else if ("!=".equals(op) || "<>".equals(op)) {
            compareOp = CompareOp.NOT_EQUAL;
            comparator = new BinaryComparator(qualifierValue);
        }
        else if (">".equals(op)) {
            compareOp = CompareOp.GREATER;
            comparator = new BinaryComparator(qualifierValue);
        }
        else if (">=".equals(op)) {
            compareOp = CompareOp.GREATER_OR_EQUAL;
            comparator = new BinaryComparator(qualifierValue);
        }
        else if ("<".equals(op)) {
            compareOp = CompareOp.LESS;
            comparator = new BinaryComparator(qualifierValue);
        }
        else if ("<=".equals(op)) {
            compareOp = CompareOp.LESS_OR_EQUAL;
            comparator = new BinaryComparator(qualifierValue);
        }
        else if ("=^".equals(op)) {
            compareOp = CompareOp.EQUAL;
            comparator = new BinaryPrefixComparator(qualifierValue);
        }
        else if ("=~".equals(op)) {
            compareOp = CompareOp.EQUAL;
            comparator = new RegexStringComparator(value);
        }
        else if ("contains".equals(op)) {
            compareOp = CompareOp.EQUAL;
            comparator = new SubstringComparator(value);
        }
        else if ("in".equals(op)) {
            compareOp = CompareOp.EQUAL;
            filter2 = parseInExpr(famName, qualifierName, value, filterIfMissing);
        }
        else if ("between".equals(op)) {
            compareOp = CompareOp.EQUAL;
            filter2 = parseBetweenExpr(famName, qualifierName, value, filterIfMissing, filter2);
        }

        if (compareOp == null) {
            return null;
        }

        if (filter2 == null) {
            if (isQualifierNameOperating(qualifierName)) {
                QualifierFilter filter = new QualifierFilter(compareOp, comparator);
                filter2 = filter;
            }
            else {
                SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(famName),
                        metaConfig.getQualifier(famName, qualifierName), compareOp, comparator);
                filter.setFilterIfMissing(filterIfMissing);
                filter2 = filter;
            }
        }

        return filter2;
    }

    private boolean isQualifierNameOperating(String famQualifier) {
        return "qualifier".equalsIgnoreCase(famQualifier);
    }

    private Filter parseBetweenExpr(String famName, String qualifierName, String value,
            boolean filterIfMissing, Filter filter2) {
        String[] arr = Strings.split(value, ",", true);
        if (arr.length != 2) {
            return filter2;
        }

        FilterList filter3 = new FilterList(Operator.MUST_PASS_ALL);
        byte[] qValue = metaConfig.getQualifierValue(famName, qualifierName, arr[0]);
        SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(famName),
                Bytes.toBytes(qualifierName), CompareOp.GREATER_OR_EQUAL, new BinaryComparator(qValue));
        filter.setFilterIfMissing(filterIfMissing);
        filter3.addFilter(filter);

        qValue = metaConfig.getQualifierValue(famName, qualifierName, arr[1]);
        filter = new SingleColumnValueFilter(Bytes.toBytes(famName),
                Bytes.toBytes(qualifierName), CompareOp.LESS_OR_EQUAL, new BinaryComparator(qValue));
        filter.setFilterIfMissing(filterIfMissing);
        filter3.addFilter(filter);

        return filter3;
    }

    private Filter parseInExpr(String famName, String qualifierName, String value,
            boolean filterIfMissing) {
        Filter filter2;
        FilterList filter3 = new FilterList(Operator.MUST_PASS_ONE);
        String[] arr = Strings.split(value, ",", true);

        for (String str : arr) {
            byte[] qValue = metaConfig.getQualifierValue(famName, qualifierName, str);
            SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(famName),
                    Bytes.toBytes(qualifierName), CompareOp.EQUAL, new BinaryComparator(qValue));
            filter.setFilterIfMissing(filterIfMissing);
            filter3.addFilter(filter);
        }
        filter2 = filter3;
        return filter2;
    }

    protected void showRow(int lines, StyledDocument doc1, Result rr,
            QueryOption queryOption, OutputStreamWriter out) throws Exception {

        StyledDocument doc = doc1;
        if (out != null) {
            insertString(null, "{", JTextPaneStyle.LINEATTR, out);
            doc = null;
        }
        else {
            if (queryOption.isShowLineNo()) {
                insertString(doc, lines + ": ", JTextPaneStyle.REDATTR, null);
            }

            insertString(doc, "{", JTextPaneStyle.LINEATTR, null);
        }

        insertString(doc, "rowkey: ", JTextPaneStyle.FAMATTR, out);
        insertString(doc, metaConfig.getRowkey(rr.getRow()) + ", ", null, out);

        HashMap<String, List<Entry<Long, List<Pair<byte[], byte[]>>>>> remap = null;
        remap = HBaserUtils.remapByFamilyAndTimestamp(rr.getMap());
        String[] tableFamilies = metaConfig.getTableFamilies();
        for (int k = 0; k < tableFamilies.length; ++k) {
            String originalFam = tableFamilies[k];
            List<Entry<Long, List<Pair<byte[], byte[]>>>> values = remap.get(originalFam);
            if (values == null) {
                continue;
            }

            showFamily(doc, queryOption, out, k, originalFam, values);
        }

        insertString(doc, "}\n", null, out);
    }

    private void showFamily(StyledDocument doc, QueryOption queryOption, OutputStreamWriter out, int k,
            String originalFam, List<Entry<Long, List<Pair<byte[], byte[]>>>> values) throws Exception {
        insertString(doc, (k > 0 ? ", " : "") + JsonEscaper.quotString(originalFam) + ": {",
                JTextPaneStyle.REDATTR, out);

        boolean firstKey = true;
        for (int i = values.size() - 1; i > -1; i--) {
            Entry<Long, List<Pair<byte[], byte[]>>> tsEntry = values.get(i);
            Long ts = tsEntry.getKey();
            List<Pair<byte[], byte[]>> qualifierValues = tsEntry.getValue();
            for (Pair<byte[], byte[]> pair : qualifierValues) {
                if (!firstKey) {
                    insertString(doc, ", ", null, out);
                }
                else {
                    firstKey = false;
                }

                String originalQualifier = metaConfig.getQualifier(originalFam, pair.getFirst());

                insertString(doc, JsonEscaper.quotKey(originalQualifier), JTextPaneStyle.KEYWORDATTR, out);

                String qualifierValue = metaConfig.getQualifierValue(originalFam, originalQualifier, pair.getSecond(),
                        ts, out == null, queryOption.isShowTimeStamp());
                insertString(doc, ": " + qualifierValue, null, out);
            }
        }

        insertString(doc, "}", JTextPaneStyle.REDATTR, out);
    }

    protected void insertString(StyledDocument doc, String str, AttributeSet a, OutputStreamWriter out)
            throws Exception {
        if (out != null) {
            out.write(str);
        }
        if (doc != null) {
            doc.insertString(doc.getLength(), str, a);
        }
    }

    protected void writeHead(String tableName, String tableMeta, StyledDocument doc, OutputStreamWriter out)
            throws Exception {
        if (out != null) {
            out.write("[TABLE]\r\n");
            out.write(tableName);
            for (String fam : metaConfig.getTableFamilies()) {
                out.write(",");
                out.write(fam);
            }
            out.write("\r\n");

            out.write("[META]\r\n");
            out.write(tableMeta + "\r\n");
            out.write("[DATA]\r\n");

        }
    }

}
