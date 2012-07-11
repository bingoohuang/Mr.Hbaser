package org.phw.hbaser.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.RowLock;
import org.apache.hadoop.hbase.util.Bytes;
import org.phw.core.lang.Ios;
import org.phw.core.lang.Pair;
import org.phw.core.lang.Strings;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Closeables;

public class HTablePuts {
    private HTableMetaConfig metaConfig;

    public boolean importData(File dataFile, JTextPane pane, JComboBox comboTables, JTextPane textTableMeta) {
        Watcher watcher = new Watcher(pane, "Start Import " + dataFile);
        BufferedReader reader = null;
        HTable hTable = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "UTF8"));
            String tableInfo = null;
            StringBuilder tableMeta = new StringBuilder();
            SectionState sectionState = SectionState.NONE;
            HTableBatchPuts htableBatchPuts = null;
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if ("[TABLE]".equals(line)) {
                    sectionState = SectionState.TABLESTART;
                    continue;
                }
                if ("[META]".equals(line)) {
                    sectionState = SectionState.METASTART;
                    continue;
                }
                if ("[DATA]".equals(line)) {
                    sectionState = SectionState.DATASTART;

                    String tableName = Strings.substringBefore(tableInfo, ",");
                    this.prepareTable(comboTables, tableInfo, tableName);

                    String meta = HTableMetaMgr.loadMetaFromMetaTable(tableName);
                    if (Strings.isEmpty(meta)) {
                        meta = tableMeta.toString();
                    }

                    textTableMeta.setText(meta);

                    this.metaConfig = new HTableMetaConfig(null, tableName, meta);

                    hTable = new HTable(HBaserConfig.config, tableName);
                    htableBatchPuts = new HTableBatchPuts(hTable);
                    continue;
                }

                if (sectionState == SectionState.TABLESTART) {
                    tableInfo = line;
                }
                else if (sectionState == SectionState.METASTART) {
                    tableMeta.append(line).append("\r\n");
                }
                else if (sectionState == SectionState.DATASTART && htableBatchPuts != null) {
                    Put put = putRow(null, this.metaConfig, line, watcher);
                    htableBatchPuts.putRow(put);
                }
            }

            if (htableBatchPuts != null) {
                htableBatchPuts.putEnd();
                watcher.stopWatch("导入" + tableInfo + "结束，共导入" + htableBatchPuts.getRownum() + "记录。");
                return true;
            }

            throw new RuntimeException("[DATA] section not found!");
        }
        catch (Exception e) {
            watcher.error(e);
            return false;
        }
        finally {
            Ios.closeQuietly(reader);
            Closeables.closeQuietly(hTable);
        }
    }

    private void prepareTable(JComboBox comboTables, String tableInfo, String tableName) {
        if (!HTableDdls.existsTable(tableName)) {
            HTableDdls.createTable(tableInfo, null);
            comboTables.addItem(tableName);
        }
        else {
            int ret = JOptionPane.showConfirmDialog(null, "表" + tableName + "已经存在，是否在导入前删除数据？");
            if (ret == JOptionPane.OK_OPTION) {
                HTableDdls.truncateTable(tableName, null);
            }
        }
        comboTables.setSelectedItem(tableName);
    }

    public static void addIncrementColumns(HTableMetaConfig metaConfig, Increment increment, JSONObject jsonObj) {
        Set<String> keySet = jsonObj.keySet();
        for (String key : keySet) {
            if ("rowkey".equals(key)) {
                continue;
            }

            JSONObject jsonQualifier = jsonObj.getJSONObject(key);
            for (String qualifier : jsonQualifier.keySet()) {
                Long increseValue = jsonQualifier.getLong(qualifier);
                if (increseValue == null) {
                    continue;
                }

                increment.addColumn(Bytes.toBytes(key),
                        metaConfig.getQualifier(key, qualifier), increseValue);
            }
        }
    }

    public static HbaseCell parseCheck(HTableMetaConfig metaConfig, String tableName, Watcher watcher, String check,
            JSONObject jsonObj, boolean operationByQuery) {
        byte[] bRowkey = null;
        if (!operationByQuery) {
            String rowkey = jsonObj.getString("rowkey");
            if (Strings.isBlank(rowkey)) {
                watcher.stopWatchWithError("rowkey不能为空。");
                return null;
            }
            bRowkey = metaConfig.getRowkey(rowkey);
        }

        if (Strings.isEmpty(check)) {
            watcher.stopWatchWithError("check不能为空。");
            return null;
        }

        int equalPos = check.indexOf('=');
        if (equalPos <= 0) {
            watcher.stopWatchWithError("ERR1: check 格式必须是: [family.]qualifier=value/null。");
            return null;
        }
        String familyQualifier = check.substring(0, equalPos).trim();
        Pair<String, String> pair = metaConfig.spitFamilyAndQualifer(familyQualifier);
        String famName = pair.getFirst();
        String qualifierName = pair.getSecond();
        if (Strings.isEmpty(famName) || Strings.isEmpty(qualifierName)) {
            watcher.stopWatchWithError("ERR2: check 格式必须是: [family.]qualifier=value/null。");
            return null;
        }

        String strValue = check.substring(equalPos + 1).trim();
        if (Strings.isEmpty(strValue)) {
            watcher.stopWatchWithError("ERR3: check 格式必须是: [family.]qualifier=value/null。");
            return null;
        }
        byte[] bValue = null;
        if (!"null".equals(strValue)) {
            strValue = JsonEscaper.unescape(JsonEscaper.removeQuotation(strValue));
            bValue = metaConfig.getQualifierValue(famName, qualifierName, strValue);
        }

        HbaseCell hbaseCell = new HbaseCell();
        hbaseCell.setFamily(famName);
        hbaseCell.setQualifier(metaConfig.getQualifier(famName, qualifierName));
        hbaseCell.setRowkey(bRowkey);
        hbaseCell.setValue(bValue);

        return hbaseCell;
    }

    public void checkAndPutByRow(String tableName, String meta, String check, String json, JTextPane pane) {
        Watcher watcher = new Watcher(pane, "Start CheckAndPut By Rowkey for " + tableName + " with " + json);
        HTable hTable = null;
        try {
            this.metaConfig = new HTableMetaConfig(tableName, meta);
            JSONObject jsonObj = JSON.parseObject(json);
            HbaseCell cell = parseCheck(this.metaConfig, tableName, watcher, check, jsonObj, false);
            if (cell == null) {
                return;
            }
            Put put = createRowPut(null, this.metaConfig, jsonObj, cell.getRowkey());
            hTable = new HTable(HBaserConfig.config, tableName);
            boolean checkAndPut = hTable.checkAndPut(cell.getRowkey(), Bytes.toBytes(cell.getFamily()),
                    cell.getQualifier(), cell.getValue(), put);

            hTable.flushCommits();
            watcher.stopWatch("CheckAndPut By Rowkey for " + tableName + " " + (checkAndPut ? "成功!" : "失败！"));
        }
        catch (Exception e) {
            watcher.error(e);
        }
        finally {
            Closeables.closeQuietly(hTable);
        }
    }

    public void increment(String tableName, String meta, String json, JTextPane pane) {
        Watcher watcher = new Watcher(pane, "Start Increment " + tableName + " with " + json);
        this.metaConfig = new HTableMetaConfig(tableName, meta);
        HTable hTable = null;
        try {
            JSONObject jsonObj = JSON.parseObject(json);
            String rowkey = jsonObj.getString("rowkey");
            if (Strings.isBlank(rowkey)) {
                watcher.stopWatchWithError("rowkey不能为空。");
                return;
            }

            hTable = new HTable(HBaserConfig.config, tableName);
            Increment increment = new Increment(this.metaConfig.getRowkey(rowkey));
            addIncrementColumns(this.metaConfig, increment, jsonObj);
            hTable.increment(increment);
            hTable.flushCommits();
            watcher.stopWatch("Increment " + tableName + " 成功!");
        }
        catch (Exception e) {
            watcher.error(e);
        }
        finally {
            Closeables.closeQuietly(hTable);
        }
    }

    public boolean unlockRow(String tableName, Pair<HTable, RowLock> lockPair, JTextPane pane) {
        Watcher watcher = new Watcher(pane, "Start UnLock Row of " + tableName);
        try {
            lockPair.getFirst().unlockRow(lockPair.getSecond());
            watcher.stopWatch("Start UnLock Row of " + tableName + " 成功!");
            return true;
        }
        catch (IOException e) {
            watcher.error(e);
        }
        return false;
    }

    public Pair<HTable, RowLock> lockRow(String tableName, String meta, String json, JTextPane pane) {
        Watcher watcher = new Watcher(pane, "Start Lock Row of " + tableName + " with " + json);
        this.metaConfig = new HTableMetaConfig(null, tableName, meta);
        HTable hTable = null;
        try {
            hTable = new HTable(HBaserConfig.config, tableName);
            JSONObject jsonObj = JSON.parseObject(json);
            String rowkey = jsonObj.getString("rowkey");
            if (Strings.isBlank(rowkey)) {
                watcher.stopWatchWithError("rowkey不能为空。");
                return null;
            }
            RowLock lockRow = hTable.lockRow(this.metaConfig.getRowkey(rowkey));

            watcher.stopWatch("Start Lock Row of " + tableName + " 成功!");
            return Pair.makePair(hTable, lockRow);
        }
        catch (Exception e) {
            watcher.error(e);
            return null;
        }
        finally {
            // HBaserUtils.closeQuietly(hTable);
        }
    }

    public void mergeValuesByRowkey(Pair<HTable, RowLock> lockPair, String tableName, String meta, String json,
            JTextPane pane, boolean isInsert) {
        Watcher watcher = new Watcher(pane, "Start "
                + (isInsert ? "Insert " : "Update ") + tableName + " with " + json);
        this.metaConfig = new HTableMetaConfig(lockPair, tableName, meta);
        HTable hTable = null;
        try {
            JSONObject jsonObj = JSON.parseObject(json);
            String rowkey = jsonObj.getString("rowkey");
            if (Strings.isBlank(rowkey)) {
                watcher.stopWatchWithError("rowkey不能为空。");
                return;
            }
            byte[] bRowkey = this.metaConfig.getRowkey(rowkey);

            hTable = lockPair == null ? new HTable(HBaserConfig.config, tableName) : lockPair.getFirst();

            // Take a lock, no one else can do something with this row while we have the lock
            RowLock rowLock = lockPair != null ? lockPair.getSecond() : hTable.lockRow(bRowkey);
            try {
                // Check if the record exists
                Get get = new Get(bRowkey, rowLock);
                Result result = hTable.get(get);
                final boolean empty = result.isEmpty();
                if (empty ? !isInsert : isInsert) {
                    final String info = "Row with this key " + (empty ? "does not" : "already") + " exists.";
                    watcher.stopWatchWithError(info);
                    HBaserUtils.message(info);
                    return;
                }

                Put put = putRow(rowLock, this.metaConfig, jsonObj, watcher);
                if (put == null) {
                    return;
                }

                hTable.put(put);
                hTable.flushCommits();
                watcher.stopWatch((isInsert ? "Insert " : "Update ") + tableName + " 成功!");
            }
            finally {
                if (lockPair == null) {
                    hTable.unlockRow(rowLock);
                }
            }

        }
        catch (Exception e) {
            watcher.error(e);
        }
        finally {
            Closeables.closeQuietly(lockPair == null ? hTable : null);
        }
    }

    public void putValuesByRowkey(Pair<HTable, RowLock> lockPair, String tableName, String meta, String json,
            JTextPane pane) {
        Watcher watcher = new Watcher(pane, "Start Puts " + tableName + " with " + json);
        this.metaConfig = new HTableMetaConfig(lockPair, tableName, meta);
        HTable hTable = null;
        try {
            hTable = lockPair == null ? new HTable(HBaserConfig.config, tableName) : lockPair.getFirst();
            Put put = putRow(lockPair != null ? lockPair.getSecond() : null, this.metaConfig, json, watcher);
            if (put == null) {
                return;
            }

            hTable.put(put);
            hTable.flushCommits();
            watcher.stopWatch("Puts " + tableName + " 成功!");
        }
        catch (Exception e) {
            watcher.error(e);
        }
        finally {
            Closeables.closeQuietly(lockPair == null ? hTable : null);
        }
    }

    public static Put putRow(RowLock rowLock, HTableMetaConfig metaConfig, String json, Watcher watcher) {
        JSONObject jsonObj = JSON.parseObject(json);
        return putRow(rowLock, metaConfig, jsonObj, watcher);
    }

    public static Put putRow(RowLock rowLock, HTableMetaConfig metaConfig, JSONObject jsonObj, Watcher watcher) {

        String rowkey = jsonObj.getString("rowkey");
        if (Strings.isBlank(rowkey)) {
            if (watcher != null) {
                watcher.stopWatchWithError("rowkey不能为空。");
            }
            return null;
        }

        return createRowPut(rowLock, metaConfig, jsonObj, rowkey);
    }

    public static Put createRowPut(RowLock rowLock, HTableMetaConfig metaConfig, JSONObject jsonObj, String rowkey) {
        return createRowPut(rowLock, metaConfig, jsonObj, metaConfig.getRowkey(rowkey));
    }

    public static Put createRowPut(RowLock rowLock, HTableMetaConfig metaConfig, JSONObject jsonObj, byte[] rowkey) {
        Put put = new Put(rowkey, rowLock);
        Set<String> keySet = jsonObj.keySet();
        for (String fam : keySet) {
            if ("rowkey".equals(fam)) {
                continue;
            }

            JSONObject kvs = jsonObj.getJSONObject(fam);
            byte[] bFam = Bytes.toBytes(fam);
            Set<Entry<String, Object>> entrySet = kvs.entrySet();
            for (Entry<String, Object> entry : entrySet) {
                String key = entry.getKey();
                byte[] qualifierBytes = metaConfig.getQualifier(fam, key);
                Object value = entry.getValue();
                byte[] bValue = value == null ? null : metaConfig.getQualifierValue(fam, key, "" + value);
                put.add(bFam, qualifierBytes, bValue);
            }
        }
        return put;
    }

}
