package org.phw.hbaser.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.phw.core.lang.Collections;
import org.phw.core.lang.Ios;
import org.phw.core.lang.Strings;
import org.phw.hbaser.ui.CheckableItem;

import com.google.common.io.Closeables;

public class HTablesMultiImport {

    public boolean exportHTables(File file, List<String> selectedTables) {
        String extension = getExtension(file.getName());
        ZipInputStream zis = null;
        FileInputStream fis = null;
        try {
            if ("hbaser".equals(extension)) {
                fis = new FileInputStream(file);
                this.importData(fis);
            }
            else if ("zip".equals(extension)) {
                zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
                for (ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()) {
                    String tableName = getBasename(entry.getName());
                    if (Collections.contains(selectedTables, tableName)) {
                        this.importData(zis);
                    }
                }
            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            HBaserUtils.error(e);
            return false;
        }
        finally {
            Ios.closeQuietly(fis);
            Ios.closeQuietly(zis);
        }
    }

    public boolean importData(InputStream is) {
        HTable hTable = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF8"));
            String tableInfo = null;
            StringBuilder tableMeta = new StringBuilder();
            SectionState sectionState = SectionState.NONE;
            HTableBatchPuts htableBatchPuts = null;
            HTableMetaConfig metaConfig = null;
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
                    prepareTable(tableInfo, tableName);

                    String meta = HTableMetaMgr.loadMetaFromMetaTable(tableName);
                    if (Strings.isEmpty(meta)) {
                        meta = tableMeta.toString();
                    }
                    metaConfig = new HTableMetaConfig(tableName, meta);

                    HTableMetaMgr.saveMetaToMetaTable(tableName, meta);
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
                    Put put = HTablePuts.putRow(null, metaConfig, line, null);
                    htableBatchPuts.putRow(put);
                }
            }

            if (htableBatchPuts != null) {
                htableBatchPuts.putEnd();
                return true;
            }
            throw new RuntimeException("[DATA] not found!");
        }
        catch (Exception e) {
            HBaserUtils.error(e);
            return false;
        }
        finally {
            Closeables.closeQuietly(hTable);
        }
    }

    private static void prepareTable(String tableInfo, String tableName) {
        if (!HTableDdls.existsTable(tableName)) {
            HTableDdls.createTable(tableInfo, null);
        }
        else {
            int ret = JOptionPane.showConfirmDialog(null, "表" + tableName + "已经存在，是否在导入前删除数据？");
            if (ret == JOptionPane.OK_OPTION) {
                HTableDdls.truncateTable(tableName, null);
            }
        }
    }

    public static CheckableItem[] createData(File file) {
        try {
            List<String> listTableNames = new ArrayList<String>();
            String extension = getExtension(file.getName());
            if ("hbaser".equals(extension)) {
                listTableNames.add(getHBaserTableName(file));
            }
            else if ("zip".equals(extension)) {
                ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
                for (ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()) {
                    listTableNames.add(getBasename(entry.getName()));
                }
                zis.close();
            }

            int n = listTableNames.size();
            CheckableItem[] items = new CheckableItem[n];
            for (int i = 0; i < n; i++) {
                items[i] = new CheckableItem(listTableNames.get(i));
            }

            return items;
        }
        catch (IOException ex) {
            HBaserUtils.error(ex);
        }

        return null;
    }

    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = filename.lastIndexOf('.');
        if (index == -1) {
            return "";
        }

        return filename.substring(index + 1);
    }

    public static String getBasename(String filename) {
        if (filename == null) {
            return null;
        }
        int index = filename.lastIndexOf('.');
        if (index == -1) {
            return "";
        }

        return filename.substring(0, index);
    }

    public static String getHBaserTableName(File dataFile) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "UTF8"));
            SectionState sectionState = SectionState.NONE;
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if ("[TABLE]".equals(line)) {
                    sectionState = SectionState.TABLESTART;
                    continue;
                }
                if (sectionState == SectionState.TABLESTART) {
                    return Strings.substringBefore(line, ",");
                }
            }
        }
        catch (Exception e) {
            HBaserUtils.error(e);
        }
        finally {
            Closeables.closeQuietly(reader);
        }
        return null;
    }
}
