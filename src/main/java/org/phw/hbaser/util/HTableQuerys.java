package org.phw.hbaser.util;

import java.io.OutputStreamWriter;

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RowLock;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.phw.core.lang.Dates;
import org.phw.core.lang.Ios;
import org.phw.core.lang.Pair;
import org.phw.core.lang.Strings;
import org.phw.hbaser.ui.QueryOption;

import com.alibaba.fastjson.JSONObject;

public class HTableQuerys extends HTableBaseQuery {
    public void checkAndDeleteValuesByQuery(String tableName, String tableMeta, String rowkey, String strWhere,
            String check, String json, JTextPane textResult) {
        casByQuery(tableName, tableMeta, rowkey, strWhere, check, json, textResult, "CheckAndDelete values By Query",
                new CASByQueryCallback() {
                    @Override
                    public boolean rowProcess(HTable hTable, Result rr, HbaseCell cell, JSONObject jsonObj)
                            throws Exception {
                        Delete delete = HTableDeletes.createValueDelete(metaConfig, jsonObj, rr.getRow());
                        return hTable.checkAndDelete(rr.getRow(), Bytes.toBytes(cell.getFamily()),
                                cell.getQualifier(), cell.getValue(), delete);
                    }
                });
    }

    public void checkAndDeleteRowsByQuery(String tableName, String tableMeta, String rowkey, String strWhere,
            String check, String json, JTextPane textResult) {
        casByQuery(tableName, tableMeta, rowkey, strWhere, check, json, textResult, "CheckAndDelete rows By Query",
                new CASByQueryCallback() {
                    @Override
                    public boolean rowProcess(HTable hTable, Result rr, HbaseCell cell, JSONObject jsonObj)
                            throws Exception {
                        Delete delete = HTableDeletes.createRowDelete(rr.getRow());
                        return hTable.checkAndDelete(rr.getRow(), Bytes.toBytes(cell.getFamily()),
                                cell.getQualifier(), cell.getValue(), delete);
                    }
                });
    }

    public void checkAndPutsByQuery(String tableName, String tableMeta, String rowkey, String strWhere, String check,
            String json, JTextPane textResult) {
        casByQuery(tableName, tableMeta, rowkey, strWhere, check, json, textResult, "CheckAndPuts By Query",
                new CASByQueryCallback() {
                    @Override
                    public boolean rowProcess(HTable hTable, Result rr, HbaseCell cell, JSONObject jsonObj)
                            throws Exception {
                        Put put = HTablePuts.createRowPut(null, metaConfig, jsonObj, rr.getRow());
                        return hTable.checkAndPut(rr.getRow(), Bytes.toBytes(cell.getFamily()),
                                cell.getQualifier(), cell.getValue(), put);
                    }
                });
    }

    public void deleteValuesByQuery(String tableName, String tableMeta, String rowkey, String strWhere,
            String json, JTextPane textResult) {
        updateByQuery(tableName, tableMeta, rowkey, strWhere, json, textResult, "Delete values By Query",
                new OperateByQueryCallback() {
                    HTableBatchDeletes hTableBatchDeletes;

                    @Override
                    public void rowProcess(HTable hTable, Result rr, JSONObject jsonObj) throws Exception {
                        if (hTableBatchDeletes == null) {
                            hTableBatchDeletes = new HTableBatchDeletes(hTable);
                        }
                        Delete delete = HTableDeletes.createValueDelete(metaConfig, jsonObj, rr.getRow());
                        hTableBatchDeletes.deleteRow(delete);

                    }

                    @Override
                    public int getRownum() {
                        return hTableBatchDeletes.getRownum();
                    }

                    @Override
                    public void endRowProcess() throws Exception {
                        hTableBatchDeletes.deleteEnd();
                    }
                });

    }

    public void deleteRowsByQuery(String tableName, String tableMeta, String rowkey, String strWhere,
            JTextPane textResult) {
        updateByQuery(tableName, tableMeta, rowkey, strWhere, null, textResult, "Delete rows By Query",
                new OperateByQueryCallback() {
                    HTableBatchDeletes hTableBatchDeletes;

                    @Override
                    public void rowProcess(HTable hTable, Result rr, JSONObject jsonObj) throws Exception {
                        if (hTableBatchDeletes == null) {
                            hTableBatchDeletes = new HTableBatchDeletes(hTable);
                        }
                        Delete delete = HTableDeletes.createRowDelete(rr.getRow());
                        hTableBatchDeletes.deleteRow(delete);
                    }

                    @Override
                    public int getRownum() {
                        return hTableBatchDeletes.getRownum();
                    }

                    @Override
                    public void endRowProcess() throws Exception {
                        hTableBatchDeletes.deleteEnd();
                    }
                });

    }

    public void incrementByQuery(String tableName, String tableMeta, String rowkey, String strWhere,
            String json, JTextPane textResult) {
        updateByQuery(tableName, tableMeta, rowkey, strWhere, json, textResult, "Increment By Query",
                new OperateByQueryCallback() {
                    int rownum = 0;
                    HTable hTable = null;

                    @Override
                    public void rowProcess(HTable hTable, Result rr, JSONObject jsonObj) throws Exception {
                        if (this.hTable == null) {
                            this.hTable = hTable;
                        }

                        Increment increment = new Increment(rr.getRow());
                        HTablePuts.addIncrementColumns(metaConfig, increment, jsonObj);
                        this.hTable.increment(increment);
                        ++rownum;
                        if (rownum == 1000) {
                            this.hTable.flushCommits();
                        }
                    }

                    @Override
                    public int getRownum() {
                        return rownum;
                    }

                    @Override
                    public void endRowProcess() throws Exception {
                        hTable.flushCommits();
                    }
                });
    }

    public void putsByQuery(String tableName, String tableMeta, String rowkey, String strWhere,
            String json, JTextPane textResult) {
        updateByQuery(tableName, tableMeta, rowkey, strWhere, json, textResult, "Puts By Query",
                new OperateByQueryCallback() {
                    HTableBatchPuts hTableBatchPuts;

                    @Override
                    public void rowProcess(HTable hTable, Result rr, JSONObject jsonObj) throws Exception {
                        if (hTableBatchPuts == null) {
                            hTableBatchPuts = new HTableBatchPuts(hTable);
                        }
                        Put put = HTablePuts.createRowPut(null, metaConfig, jsonObj, rr.getRow());
                        hTableBatchPuts.putRow(put);
                    }

                    @Override
                    public int getRownum() {
                        return hTableBatchPuts.getRownum();
                    }

                    @Override
                    public void endRowProcess() throws Exception {
                        hTableBatchPuts.putEnd();
                    }
                });
    }

    public void queryTable(Pair<HTable, RowLock> lockPair, String tableName, String tableMeta,
            String rowkey, String strSelect,
            String strWhere, JTextPane textResult, QueryOption queryOption,
            OutputStreamWriter out) {
        BigBytesCache.clearCache();

        StyledDocument doc = textResult == null ? null : textResult.getStyledDocument();
        HTable hTable = null;
        ResultScanner rs = null;
        String trimmedRowkey = rowkey.trim();

        try {
            metaConfig = new HTableMetaConfig(tableName, tableMeta);

            writeHead(tableName, tableMeta, doc, out);

            long start = System.currentTimeMillis();
            writeQueryExpr(tableName, strSelect, strWhere, out, doc, trimmedRowkey, start);
            hTable = lockPair == null ? new HTable(HBaserConfig.config, tableName) : lockPair.getFirst();

            FilterList filterList = new FilterList();
            if (queryOption.isKeyOnlyFilter()) {
                filterList.addFilter(new KeyOnlyFilter());
            }
            rs = createResultScanner(strSelect, strWhere, trimmedRowkey, hTable, filterList);
            int lines = 0;
            for (Result rr = rs.next(); rr != null && lines < queryOption.getRowLimit(); rr = rs.next()) {
                showRow(++lines, doc, rr, queryOption, out);
            }

            long end = System.currentTimeMillis();
            insertString(doc, Dates.format(end) + " 共" + lines + "行, 用时"
                    + (end - start) / 1000. + "秒\n\n", JTextPaneStyle.FAMATTR, null);

            if (doc != null && textResult != null) {
                textResult.setCaretPosition(doc.getLength());
            }
        }
        catch (Exception e) {
            HBaserUtils.error(e);
        }
        finally {
            Ios.closeQuietly(lockPair == null ? hTable : null, rs);
        }
    }

    private void writeQueryExpr(String tableName, String strSelect, String strWhere, OutputStreamWriter out,
            StyledDocument doc, String trimmedRowkey, long start) throws Exception {

        insertString(doc, Dates.format(start) + (out == null ? " Query" : " Export")
                + " Starting", JTextPaneStyle.FAMATTR, null);
        insertString(doc, "\nSELECT ", JTextPaneStyle.FAMATTR, null);
        insertString(doc, Strings.isBlank(strSelect) ? "*" : strSelect, null, null);
        insertString(doc, "\nFROM ", JTextPaneStyle.FAMATTR, null);
        insertString(doc, tableName, null, null);
        if (!Strings.isEmpty(trimmedRowkey)) {
            insertString(doc, "\nROWKEY IN ", JTextPaneStyle.FAMATTR, null);
            insertString(doc, trimmedRowkey, null, null);
        }
        if (!Strings.isEmpty(strWhere)) {
            insertString(doc, "\nWHERE ", JTextPaneStyle.FAMATTR, null);
            insertString(doc, strWhere, null, null);
        }

        insertString(doc, "\n", null, null);
    }

}
