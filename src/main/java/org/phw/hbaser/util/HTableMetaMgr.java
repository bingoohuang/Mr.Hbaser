package org.phw.hbaser.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JCheckBox;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.phw.core.lang.Ios;
import org.phw.core.lang.Strings;

import com.google.common.io.Closeables;

public class HTableMetaMgr {
    private static final byte[] METACONFIG = Bytes.toBytes("metaconfig");
    private static final byte[] FAM = Bytes.toBytes("fam");
    private static final String META_TABLE_NAME = "HBASER_META";
    private static Map<String, StringBuilder> sections = new HashMap<String, StringBuilder>();
    private String metaFileName;
    private JCheckBox chkUseMetaTable;

    public HTableMetaMgr(JCheckBox chkUseMetaTable) {
        this.chkUseMetaTable = chkUseMetaTable;
    }

    private static void tryCreateMetaTable() {
        if (HTableDdls.existsTable(META_TABLE_NAME)) {
            return;
        }

        HTableDdls.createTable(META_TABLE_NAME + ", fam", null);
    }

    public void save() throws Exception {
        if (chkUseMetaTable.isSelected()) {
            return;
        }

        if (Strings.isEmpty(metaFileName) || sections == null || sections.size() == 0) {
            return;
        }

        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(metaFileName), "UTF-8");
            for (Entry<String, StringBuilder> entry : sections.entrySet()) {
                out.write("[" + entry.getKey() + "]\r\n");
                out.write(entry.getValue().toString().trim() + "\r\n");
            }
        }
        finally {
            Ios.closeQuietly(out);
        }

    }

    public void loadMetaFromMetaTable() {
        ResultScanner rs = null;
        HTable hTable = null;
        try {
            tryCreateMetaTable();
            hTable = new HTable(HBaserConfig.config, META_TABLE_NAME);
            Scan scan = new Scan();
            scan.addFamily(FAM);
            rs = hTable.getScanner(scan);
            for (Result rr = rs.next(); rr != null; rr = rs.next()) {
                String tableName = Bytes.toString(rr.getRow());
                String tableMeta = Bytes.toString(rr.getValue(FAM, METACONFIG));
                sections.put(tableName, new StringBuilder(tableMeta));
            }
        }
        catch (IOException e) {
            HBaserUtils.error(e);
        }
        finally {
            HBaserUtils.closeQuietly(hTable, rs);
        }
    }

    public static void saveMetaToMetaTable(String tableName, String meta) {
        sections.put(tableName, new StringBuilder(meta));

        HTable hTable = null;
        try {
            tryCreateMetaTable();
            hTable = new HTable(HBaserConfig.config, META_TABLE_NAME);
            Put put = new Put(Bytes.toBytes(tableName));
            put.add(FAM, METACONFIG, Bytes.toBytes(meta));

            hTable.put(put);
            hTable.flushCommits();
        }
        catch (IOException e) {
            HBaserUtils.error(e);
        }
        finally {
            Closeables.closeQuietly(hTable);
        }
    }

    public static String loadMetaFromMetaTable(String tableName) {
        HTable hTable = null;
        try {
            tryCreateMetaTable();
            hTable = new HTable(HBaserConfig.config, META_TABLE_NAME);
            Get get = new Get(Bytes.toBytes(tableName));
            Result result = hTable.get(get);
            String ret = Bytes.toString(result.getValue(FAM, METACONFIG));
            return ret == null ? "" : ret;
        }
        catch (IOException e) {
            HBaserUtils.error(e);
        }
        finally {
            Closeables.closeQuietly(hTable);
        }
        return "";
    }

    public void load(String env) throws IOException {
        if (chkUseMetaTable.isSelected()) {
            loadMetaFromMetaTable();
            return;
        }

        metaFileName = env + "_meta.ini";
        File metaFile = new File(metaFileName);
        if (!metaFile.exists()) {
            return;
        }

        FileInputStream is = new FileInputStream(metaFile);
        List<String> lines = Ios.readLines(is);
        StringBuilder sb = null;
        for (String line : lines) {
            String sectionLine = Strings.trim(Strings.substringBetween(line, "[", "]"));
            if (sectionLine == null) {
                if (sb != null) {
                    sb.append(line).append("\r\n");
                }
            }
            else {
                sb = new StringBuilder();
                sections.put(sectionLine, sb);
            }
        }
        Ios.closeQuietly(is);
    }

    public String getSection(String section) {
        StringBuilder sb = sections.get(section);
        return sb == null ? null : sb.toString().trim();
    }

    public void setSection(String tableName, String text) {
        if (chkUseMetaTable.isSelected()) {
            saveMetaToMetaTable(tableName, text);
        }

        StringBuilder sb = sections.get(tableName);
        if (sb == null) {
            sb = new StringBuilder();
            sections.put(tableName, sb);
        }

        sb.delete(0, sb.length());
        sb.append(text.trim());
    }

}
