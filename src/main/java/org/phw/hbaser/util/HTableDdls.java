package org.phw.hbaser.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.RowLock;
import org.apache.hadoop.hbase.util.Bytes;
import org.phw.core.lang.Pair;
import org.phw.core.lang.Strings;
import org.phw.hbaser.valuetype.ValueTypable;

import com.google.common.io.Closeables;

public class HTableDdls {
    private static JComboBox comboTable;

    public static void setComboTable(JComboBox combo) {
        comboTable = combo;
    }

    public static String[] getFamilies(Pair<HTable, RowLock> lockPair, String tableName) {
        HTable hTable = null;
        try {
            hTable = lockPair == null ? new HTable(HBaserConfig.config, tableName) : lockPair.getFirst();
            HTableDescriptor tableDescriptor = hTable.getTableDescriptor();
            HColumnDescriptor[] columnFamilies = tableDescriptor.getColumnFamilies();
            String[] ret = new String[columnFamilies.length];
            for (int i = 0; i < columnFamilies.length; ++i) {
                ret[i] = columnFamilies[i].getNameAsString();
            }

            return ret;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Closeables.closeQuietly(lockPair == null ? hTable : null);
        }

        return new String[0];
    }

    public static String getTableRowTemplate(String tableName, String tableMeta, JTextPane pane) {
        Watcher watcher = new Watcher(pane, "Create Table Row Tmplate" + tableName);
        HTableMetaConfig metaConfig = null;
        String[] tableFamilies = null;
        metaConfig = new HTableMetaConfig(tableName, tableMeta);
        tableFamilies = getFamilies(null, tableName);

        StringBuffer sb = new StringBuffer();
        sb.append("{rowkey:");
        ValueTypable rowkeyTypable = metaConfig.getRowkeyTypable();
        if (rowkeyTypable == null || rowkeyTypable.needQuoted()) {
            sb.append("\"\"");
        }

        for (String fam : tableFamilies) {
            sb.append(", ").append(JsonEscaper.quotString(fam)).append(": {}");
        }
        sb.append("}");

        watcher.stopWatch("Create Table Row Tmplate" + tableName + " 成功!");

        return sb.toString();
    }

    public static void createTable(String text, JTextPane pane) {
        Watcher watcher = new Watcher(pane, "Create Table " + text);
        try {
            String[] strs = Strings.split(text, ",", true);
            if (strs.length <= 1) {
                watcher.stopWatchWithError("建表至少有一个表名和一个列族名称。");
                JOptionPane.showMessageDialog(null, "建表至少有一个表名和一个列族名称。");
                return;
            }

            HBaseAdmin admin = new HBaseAdmin(HBaserConfig.config);
            String tableName = strs[0];
            if (admin.tableExists(tableName)) {
                watcher.watch("表" + tableName + "已经存在。");
                int confirm = JOptionPane.showConfirmDialog(null, "表" + tableName + "已经存在，是否重建？");
                if (confirm != JOptionPane.OK_OPTION) {
                    watcher.stopWatch("表" + tableName + "已经存在，选择不重建。");
                    return;
                }

                admin.disableTable(tableName);
                admin.deleteTable(tableName);
            }

            HTableDescriptor tableDesc = new HTableDescriptor(tableName);
            for (int i = 1; i < strs.length; ++i) {
                tableDesc.addFamily(new HColumnDescriptor(strs[i]));
            }
            admin.createTable(tableDesc);

            watcher.stopWatch("Create Table " + tableName + " 成功!");

            HTableDdls.listTables();
        }
        catch (Exception e) {
            watcher.error(e);
        }

    }

    public static List<String> listTableNames() {
        ArrayList<String> arrayList = new ArrayList<String>();
        try {
            HBaseAdmin admin = new HBaseAdmin(HBaserConfig.config);
            HTableDescriptor[] tables = admin.listTables();

            for (HTableDescriptor tableDescriptor : tables) {
                arrayList.add(tableDescriptor.getNameAsString());
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "出错" + e.toString());
        }

        return arrayList;
    }

    public static void listTables() {
        try {
            HBaseAdmin admin = new HBaseAdmin(HBaserConfig.config);
            HTableDescriptor[] tables = admin.listTables();
            comboTable.removeAllItems();

            for (HTableDescriptor tableDescriptor : tables) {
                comboTable.addItem(tableDescriptor.getNameAsString());
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "出错" + e.toString());
        }
    }

    public static void listTables2(JComboBox combo) throws IOException {
        HBaseAdmin admin = new HBaseAdmin(HBaserConfig.config);
        HTableDescriptor[] tables = admin.listTables();
        combo.removeAllItems();
        for (HTableDescriptor tableDescriptor : tables) {
            combo.addItem(tableDescriptor.getNameAsString());
        }
    }

    public static void listTables(JComboBox combo) {
        try {
            HBaseAdmin admin = new HBaseAdmin(HBaserConfig.config);
            HTableDescriptor[] tables = admin.listTables();
            combo.removeAllItems();
            for (HTableDescriptor tableDescriptor : tables) {
                combo.addItem(tableDescriptor.getNameAsString());
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "出错" + e.toString());
        }
    }

    public static boolean existsTable(String tableName) {
        try {
            HBaseAdmin admin = new HBaseAdmin(HBaserConfig.config);
            return admin.tableExists(tableName);
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Fail:" + ex.getMessage());
        }
        return false;
    }

    public static void disableTable(String tableName, JTextPane pane) {
        Watcher watcher = new Watcher(pane, "Disable Table " + tableName);
        try {
            HBaseAdmin admin = new HBaseAdmin(HBaserConfig.config);
            if (!admin.tableExists(tableName)) {
                tableNotExists(tableName, watcher);
                return;
            }

            if (admin.isTableDisabled(tableName)) {
                String info = "表" + tableName + "已经处于disabled状态。";
                watcher.stopWatchWithError(info);
                JOptionPane.showMessageDialog(null, info);
                return;
            }

            admin.disableTable(tableName);
            watcher.stopWatch("Disable Table " + tableName + " 成功!");
        }
        catch (Exception e) {
            watcher.error(e);
        }
    }

    public static void enableTable(String tableName, JTextPane pane) {
        Watcher watcher = new Watcher(pane, "Enable Table " + tableName);
        try {
            HBaseAdmin admin = new HBaseAdmin(HBaserConfig.config);
            if (!admin.tableExists(tableName)) {
                tableNotExists(tableName, watcher);
                return;
            }

            if (!admin.isTableDisabled(tableName)) {
                String info = "表" + tableName + "已经处于enabled状态。";
                watcher.stopWatchWithError(info);
                JOptionPane.showMessageDialog(null, info);
                return;
            }

            admin.enableTable(tableName);
            watcher.stopWatch("Enable Table " + tableName + " 成功!");
        }
        catch (Exception e) {
            watcher.error(e);
        }

    }

    public static void truncateTable(String tableName, JTextPane pane) {
        Watcher watcher = new Watcher(pane, "Truncate Table " + tableName);
        HTable hTable = null;
        try {
            HBaseAdmin admin = new HBaseAdmin(HBaserConfig.config);
            if (!admin.tableExists(tableName)) {
                tableNotExists(tableName, watcher);
                return;
            }

            hTable = new HTable(HBaserConfig.config, tableName);
            HTableDescriptor tableDescriptor = hTable.getTableDescriptor();

            if (!admin.isTableDisabled(tableName)) {
                admin.disableTable(tableName);
            }
            admin.deleteTable(tableName);
            admin.createTable(tableDescriptor);
            watcher.stopWatch("Truncate Table " + tableName + " 成功!");
        }
        catch (Exception e) {
            watcher.error(e);
        }
        finally {
            Closeables.closeQuietly(hTable);
        }

    }

    public static void dropTable(String tableName, JTextPane pane) {
        Watcher watcher = new Watcher(pane, "Drop Table " + tableName);
        HBaseAdmin admin = null;
        try {
            admin = new HBaseAdmin(HBaserConfig.config);
            if (!admin.tableExists(tableName)) {
                tableNotExists(tableName, watcher);
                return;
            }

            if (!admin.isTableDisabled(tableName)) {
                admin.disableTable(tableName);
            }
            admin.deleteTable(tableName);
            HTableDdls.listTables();
            watcher.stopWatch("Drop Table " + tableName + " 成功!");
        }
        catch (Exception e) {
            watcher.error(e);
        }
        finally {
            Closeables.closeQuietly(admin);
        }
    }

    public static void describeTable(String tableName, JTextPane pane) {
        Watcher watcher = new Watcher(pane, "Describe Table " + tableName);
        try {
            HBaseAdmin admin = new HBaseAdmin(HBaserConfig.config);
            if (!admin.tableExists(tableName)) {
                tableNotExists(tableName, watcher);
                return;
            }

            HTableDescriptor tableDescriptor = admin.getTableDescriptor(Bytes.toBytes(tableName));
            if (admin.isTableEnabled(tableName)) {
                watcher.watch("状态：Enabled");
            }
            else {
                watcher.watch("状态：Disabled");
            }
            watcher.stopWatch(tableDescriptor.toString());
        }
        catch (Exception e) {
            watcher.error(e);
        }
    }

    private static void tableNotExists(String tableName, Watcher watcher) {
        String info = "表" + tableName + "不存在，请重新选择表格。";
        watcher.stopWatchWithError(info);
        JOptionPane.showMessageDialog(null, info);
    }
}
