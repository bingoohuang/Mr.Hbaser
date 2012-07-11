package org.phw.hbaser.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.phw.core.lang.Ios;
import org.phw.hbaser.ui.CheckableItem;
import org.phw.hbaser.util.HBaserUtils.FileOp;

public class HTablesMultiExport {
    public static void createData(DefaultListModel listModel) {
        List<String> listTableNames = HTableDdls.listTableNames();
        int n = listTableNames.size();
        for (int i = 0; i < n; i++) {
            listModel.addElement(new CheckableItem(listTableNames.get(i)));
        }
    }

    public boolean exportHTables(String env, List<String> listTables) {
        JFileChooser fc = HBaserUtils.getJFileChooserWithLastPath();
        fc.setSelectedFile(new File(env + ".zip"));
        File fFile = HBaserUtils.getSelectedFile(fc, FileOp.Save);
        if (fFile == null) {
            return false;
        }

        ZipOutputStream out = null;

        try {
            out = new ZipOutputStream(new FileOutputStream(fFile));
            for (String table : listTables) {
                out.putNextEntry(new ZipEntry(table + ".hbaser"));
                exportHTable(table, new PrintWriter(new OutputStreamWriter(out, "UTF8")));
                out.closeEntry();
            }
        }
        catch (Exception e) {
            HBaserUtils.error(e);
            return false;
        }
        finally {
            Ios.closeQuietly(out);
        }

        return true;
    }

    private void exportHTable(String table, PrintWriter out) {
        String meta = HTableMetaMgr.loadMetaFromMetaTable(table);
        HTableMetaConfig metaConfig = new HTableMetaConfig(table, meta);

        writeHead(metaConfig, table, meta, out);
        HTable hTable = null;
        ResultScanner rs = null;
        try {
            hTable = new HTable(HBaserConfig.config, table);
            Scan scan = new Scan();
            rs = hTable.getScanner(scan);
            for (Result rr = rs.next(); rr != null; rr = rs.next()) {
                writeRow(metaConfig, rr, out);
            }
        }
        catch (Exception e) {
            HBaserUtils.error(e);
        }
        finally {
            Ios.closeQuietly(hTable, rs);
        }
    }

    private void writeRow(HTableMetaConfig metaConfig, Result rr, PrintWriter ps) {
        ps.print('{');
        ps.print("rowkey:");
        ps.print(metaConfig.getRowkey(rr.getRow()));
        ps.print(',');

        KeyValue[] kvs = rr.raw();
        String fam = null;
        for (KeyValue kv : kvs) {
            String originalFam = Bytes.toString(kv.getFamily());
            String fam2 = JsonEscaper.quotString(originalFam);
            if (!fam2.equals(fam)) {
                if (fam != null) {
                    ps.print("},");
                }

                ps.print(fam2);
                ps.print(":{");
                fam = fam2;
            }
            else {
                ps.print(',');
            }

            String originalQualifier = metaConfig.getQualifier(originalFam, kv.getQualifier());
            ps.print(JsonEscaper.quotKey(originalQualifier));

            String qualifierValue = metaConfig.getQualifierValue(originalFam, originalQualifier, kv.getValue(),
                    kv.getTimestamp(), false, false);
            ps.print(':');
            ps.print(qualifierValue);
        }
        if (fam != null) {
            ps.print('}');
        }

        ps.println('}');
        ps.flush();
    }

    protected void writeHead(HTableMetaConfig metaConfig, String tableName, String tableMeta,
            PrintWriter ps) {
        ps.println("[TABLE]");
        ps.print(tableName);
        for (String fam : metaConfig.getTableFamilies()) {
            ps.print(",");
            ps.print(fam);
        }
        ps.println();

        ps.println("[META]");
        ps.println(tableMeta);
        ps.println("[DATA]");

        ps.flush();
    }
}
