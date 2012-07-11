package org.phw.hbaser.util;

import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.phw.hbaser.ui.HBaserLoginDialog;

public class HBaserConfig {
    public static Configuration config;
    public static final HBaserLoginDialog loginDialog = new HBaserLoginDialog(null);

    // private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();
    public static void setConfig(Properties props) {
        config = new Configuration();
        String quorumKey = "hbase.zookeeper.quorum";
        config.set(quorumKey, props.getProperty(quorumKey, "127.0.0.1"));
        String clientPortKey = "hbase.zookeeper.property.clientPort";
        config.set(clientPortKey, props.getProperty(clientPortKey, "2181"));
        config.set("hbase.client.retries.number", "3");
    }
}
