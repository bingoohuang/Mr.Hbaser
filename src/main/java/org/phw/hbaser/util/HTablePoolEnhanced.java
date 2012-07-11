package org.phw.hbaser.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.client.RowLock;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.coprocessor.Batch.Call;
import org.apache.hadoop.hbase.client.coprocessor.Batch.Callback;
import org.apache.hadoop.hbase.ipc.CoprocessorProtocol;

public class HTablePoolEnhanced extends HTablePool {
    private static volatile HashMap<String, HTablePool> poolCache = new HashMap<String, HTablePool>();

    public static HTablePool getTablePool() {
        return getHTablePool(null, "default");
    }

    private static HTablePool getHTablePool(Properties props, String hbaseInstanceName) {
        HTablePool hTablePool = poolCache.get(hbaseInstanceName);
        if (hTablePool != null) {
            return hTablePool;
        }

        synchronized (poolCache) {
            hTablePool = poolCache.get(hbaseInstanceName);
            if (hTablePool != null) {
                return hTablePool;
            }

            Configuration configuration = HBaseConfiguration.create();
            String quorumKey = "hbase.zookeeper.quorum";
            configuration.set(quorumKey, props.getProperty(hbaseInstanceName + '.' + quorumKey, "127.0.0.1"));
            String clientPortKey = "hbase.zookeeper.property.clientPort";
            configuration.set(clientPortKey, props.getProperty(hbaseInstanceName + '.' + clientPortKey, "2181"));
            // String maxSize = "hbase.tablepool.maxsize";

            hTablePool = new HTablePoolEnhanced(configuration, Integer.MAX_VALUE);
            poolCache.put(hbaseInstanceName, hTablePool);

        }
        return hTablePool;
    }

    /**
     * Default Constructor.  Default HBaseConfiguration and no limit on pool size.
     */
    public HTablePoolEnhanced() {
        super();
    }

    /**
     * Constructor to set maximum versions and use the specified configuration.
     * @param config configuration
     * @param maxSize maximum number of references to keep for each table
     */
    public HTablePoolEnhanced(final Configuration config, final int maxSize) {
        super(config, maxSize);
    }

    @Override
    public HTableInterface getTable(String tableName) {
        return new PooledHTable(super.getTable(tableName));
    }

    public class PooledHTable implements HTableInterface {
        private HTableInterface table;

        public PooledHTable(HTableInterface table) {
            this.table = table;
        }

        @Override
        public void close() throws IOException {
            table.close();
        }

        @Override
        public byte[] getTableName() {
            return table.getTableName();
        }

        @Override
        public Configuration getConfiguration() {
            return table.getConfiguration();
        }

        @Override
        public HTableDescriptor getTableDescriptor() throws IOException {
            return table.getTableDescriptor();
        }

        @Override
        public boolean exists(Get get) throws IOException {
            return table.exists(get);
        }

        @Override
        public void batch(List<Row> actions, Object[] results) throws IOException, InterruptedException {
            table.batch(actions, results);
        }

        @Override
        public Object[] batch(List<Row> actions) throws IOException, InterruptedException {
            return table.batch(actions);
        }

        @Override
        public Result get(Get get) throws IOException {
            return table.get(get);
        }

        @Override
        public Result[] get(List<Get> gets) throws IOException {
            return table.get(gets);
        }

        @SuppressWarnings("deprecation")
        @Override
        public Result getRowOrBefore(byte[] row, byte[] family) throws IOException {
            return table.getRowOrBefore(row, family);
        }

        @Override
        public ResultScanner getScanner(Scan scan) throws IOException {
            return table.getScanner(scan);
        }

        @Override
        public ResultScanner getScanner(byte[] family) throws IOException {
            return table.getScanner(family);
        }

        @Override
        public ResultScanner getScanner(byte[] family, byte[] qualifier) throws IOException {
            return table.getScanner(family, qualifier);
        }

        @Override
        public void put(Put put) throws IOException {
            table.put(put);
        }

        @Override
        public void put(List<Put> puts) throws IOException {
            table.put(puts);
        }

        @Override
        public boolean checkAndPut(byte[] row, byte[] family, byte[] qualifier, byte[] value, Put put)
                throws IOException {
            return table.checkAndPut(row, family, qualifier, value, put);
        }

        @Override
        public void delete(Delete delete) throws IOException {
            table.delete(delete);
        }

        @Override
        public void delete(List<Delete> deletes) throws IOException {
            table.delete(deletes);
        }

        @Override
        public boolean checkAndDelete(byte[] row, byte[] family, byte[] qualifier, byte[] value, Delete delete)
                throws IOException {
            return table.checkAndDelete(row, family, qualifier, value, delete);
        }

        @Override
        public Result increment(Increment increment) throws IOException {
            return table.increment(increment);
        }

        @Override
        public long incrementColumnValue(byte[] row, byte[] family, byte[] qualifier, long amount) throws IOException {
            return table.incrementColumnValue(row, family, qualifier, amount);
        }

        @Override
        public long incrementColumnValue(byte[] row, byte[] family, byte[] qualifier, long amount, boolean writeToWAL)
                throws IOException {
            return table.incrementColumnValue(row, family, qualifier, amount, writeToWAL);
        }

        @Override
        public boolean isAutoFlush() {
            return table.isAutoFlush();
        }

        @Override
        public void flushCommits() throws IOException {
            table.flushCommits();

        }

        @Override
        public RowLock lockRow(byte[] row) throws IOException {
            return table.lockRow(row);
        }

        @Override
        public void unlockRow(RowLock rl) throws IOException {
            table.unlockRow(rl);
        }

        @Override
        public <T extends CoprocessorProtocol, R> Map<byte[], R> coprocessorExec(Class<T> arg0, byte[] arg1,
                byte[] arg2, Call<T, R> arg3) throws IOException, Throwable {
            return table.coprocessorExec(arg0, arg1, arg2, arg3);
        }

        @Override
        public <T extends CoprocessorProtocol, R> void coprocessorExec(Class<T> arg0, byte[] arg1, byte[] arg2,
                Call<T, R> arg3, Callback<R> arg4) throws IOException, Throwable {
           table.coprocessorExec(arg0, arg1, arg2, arg3, arg4);
        }

        @Override
        public <T extends CoprocessorProtocol> T coprocessorProxy(Class<T> arg0, byte[] arg1) {
            return table.coprocessorProxy(arg0, arg1);
        }
    }

}