package org.phw.hbaser.util;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;

public class HTableBatchPuts {
    private ArrayList<Put> putArr = new ArrayList<Put>(1000);
    private HTable hTable;
    private int rownum = 0;

    public HTableBatchPuts(HTable hTable) {
        this.hTable = hTable;
    }

    public void putRow(Put put) throws IOException {
        ++rownum;

        putArr.add(put);
        if (putArr.size() == 1000 && hTable != null) {
            commitPuts();
        }
    }

    public void putEnd() throws IOException {
        if (putArr.size() > 0 && hTable != null) {
            commitPuts();
        }
    }

    private void commitPuts() throws IOException {
        hTable.put(putArr);
        hTable.flushCommits();

        putArr.clear();
    }

    public int getRownum() {
        return rownum;
    }
}
