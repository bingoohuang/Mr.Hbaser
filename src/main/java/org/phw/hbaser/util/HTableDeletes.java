package org.phw.hbaser.util;

import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JTextPane;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.RowLock;
import org.apache.hadoop.hbase.util.Bytes;
import org.phw.core.lang.Pair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Closeables;

public class HTableDeletes {
    private HTableMetaConfig metaConfig;

    public void checkAndDeleteValuesByRowkey(String tableName, String meta, String check, String json, JTextPane pane) {
        Watcher watcher = new Watcher(pane, "Start CheckAndDelete values By Rowkey for " + tableName + " with " + json);
        HTable hTable = null;
        try {
            metaConfig = new HTableMetaConfig(tableName, meta);
            JSONObject jsonObj = JSON.parseObject(json);
            HbaseCell cell = HTablePuts.parseCheck(metaConfig, tableName, watcher, check, jsonObj, false);
            if (cell == null) {
                return;
            }

            Delete delete = createValueDelete(metaConfig, jsonObj, cell.getRowkey());
            hTable = new HTable(HBaserConfig.config, tableName);
            boolean checkAndPut = hTable.checkAndDelete(cell.getRowkey(), Bytes.toBytes(cell.getFamily()),
                    cell.getQualifier(), cell.getValue(), delete);

            hTable.flushCommits();
            watcher.stopWatch("CheckAndDelete values By Rowkey for " + tableName + " " + (checkAndPut ? "成功!" : "失败！"));
        }
        catch (Exception e) {
            watcher.error(e);
        }
        finally {
            Closeables.closeQuietly(hTable);
        }
    }

    public void checkAndDeleteRowByRowkey(String tableName, String meta, String check, String json, JTextPane pane) {
        Watcher watcher = new Watcher(pane, "Start CheckAndDelete row By Rowkey for " + tableName + " with " + json);
        HTable hTable = null;
        try {
            metaConfig = new HTableMetaConfig(tableName, meta);
            JSONObject jsonObj = JSON.parseObject(json);
            HbaseCell cell = HTablePuts.parseCheck(metaConfig, tableName, watcher, check, jsonObj, false);
            if (cell == null) {
                return;
            }
            Delete delete = new Delete(cell.getRowkey());
            hTable = new HTable(HBaserConfig.config, tableName);
            boolean checkAndPut = hTable.checkAndDelete(cell.getRowkey(), Bytes.toBytes(cell.getFamily()),
                    cell.getQualifier(), cell.getValue(), delete);

            hTable.flushCommits();
            watcher.stopWatch("CheckAndDelete row By Rowkey for " + tableName + " " + (checkAndPut ? "成功!" : "失败！"));
        }
        catch (Exception e) {
            watcher.error(e);
        }
        finally {
            Closeables.closeQuietly(hTable);
        }
    }

    public void deleteRow(String tableName, String meta, String json, JTextPane pane) {
        Watcher watcher = new Watcher(pane, "Start delete row from" + tableName + " with " + json);
        HTable hTable = null;
        try {
            metaConfig = new HTableMetaConfig(null, tableName, meta);

            JSONObject jsonObj = JSON.parseObject(json);
            String rowkey = jsonObj.getString("rowkey");

            hTable = new HTable(HBaserConfig.config, tableName);
            Delete delete = new Delete(metaConfig.getRowkey(rowkey));

            hTable.delete(delete);
            hTable.flushCommits();

            watcher.stopWatch("Delete row from " + tableName + " 成功!");
        }
        catch (Exception e) {
            watcher.error(e);
        }
        finally {
            Closeables.closeQuietly(hTable);
        }
    }

    public void deleteValuesByRowkey(Pair<HTable, RowLock> lockPair, String tableName, String meta, String json,
            JTextPane pane) {
        Watcher watcher = new Watcher(pane, "Start delete values from" + tableName + " with " + json);
        HTable hTable = null;
        try {
            metaConfig = new HTableMetaConfig(lockPair, tableName, meta);

            JSONObject jsonObj = JSON.parseObject(json);
            String rowkey = jsonObj.getString("rowkey");

            hTable = lockPair == null ? new HTable(HBaserConfig.config, tableName) : lockPair.getFirst();
            Delete delete = createValueDelete(metaConfig, jsonObj, rowkey);
            hTable.delete(delete);
            hTable.flushCommits();

            watcher.stopWatch("Delete values from " + tableName + " 成功!");
        }
        catch (Exception e) {
            watcher.error(e);
        }
        finally {
            Closeables.closeQuietly(lockPair == null ? hTable : null);
        }
    }

    public static Delete createValueDelete(HTableMetaConfig metaConfig, JSONObject jsonObj, String rowkey) {
        return createValueDelete(metaConfig, jsonObj, metaConfig.getRowkey(rowkey));
    }

    public static Delete createValueDelete(HTableMetaConfig metaConfig, JSONObject jsonObj, byte[] rowkey) {
        Delete delete = new Delete(rowkey);
        Set<String> keySet = jsonObj.keySet();
        for (String fam : keySet) {
            if ("rowkey".equals(fam)) {
                continue;
            }

            JSONObject kvs = jsonObj.getJSONObject(fam);
            byte[] bFam = Bytes.toBytes(fam);
            Set<Entry<String, Object>> entrySet = kvs.entrySet();
            for (Entry<String, Object> entry : entrySet) {
                String qualifier = entry.getKey();
                delete.deleteColumn(bFam, metaConfig.getQualifier(fam, qualifier));
            }
        }
        return delete;
    }

    public static Delete createRowDelete(byte[] rowkey) {
        return new Delete(rowkey);
    }

}
