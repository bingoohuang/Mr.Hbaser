package org.phw.hbaser.util;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTable;

public class HTableBatchDeletes {
    private ArrayList<Delete> putArr = new ArrayList<Delete>(1000);
    private HTable hTable;
    private int rownum = 0;

    public HTableBatchDeletes(HTable hTable) {
        this.hTable = hTable;
    }

    public void deleteRow(Delete delete) throws IOException {
        ++rownum;

        putArr.add(delete);
        if (putArr.size() == 1000 && hTable != null) {
            commitPuts();
        }
    }

    public void deleteEnd() throws IOException {
        if (putArr.size() > 0 && hTable != null) {
            commitPuts();
        }
    }

    private void commitPuts() throws IOException {
        hTable.delete(putArr);
        hTable.flushCommits();

        putArr.clear();
    }

    public int getRownum() {
        return rownum;
    }
}
