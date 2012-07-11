package org.phw.core.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IP and HostName retriver.
 * @author BingooHuang
 *
 */
public class Inets {
    private static Logger logger = LoggerFactory.getLogger(Inets.class);
    private static String ip;
    private static String hostname;

    static {
        NetworkInterface ni = null;

        try {
            ni = NetworkInterface.getByName("bond0");
        }
        catch (SocketException e) {
            logger.warn("Get NetworkInterface bond0 fail", e);
        }

        if (null != ni) {
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress ia = addresses.nextElement();

                if (ia instanceof Inet6Address) {
                    continue;
                }
                ip = ia.getHostAddress();

                if (null != ip && ip.length() > 20) {
                    ip = ip.substring(0, 20);
                }
                break;
            }
        }
        else {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            }
            catch (UnknownHostException e) {
                logger.warn("getHostAddress fail", e);
            }
        }

        try {
            hostname = InetAddress.getLocalHost().getHostName();

            if (null != hostname && hostname.length() > 50) {
                hostname = hostname.substring(0, 50);
            }
        }
        catch (UnknownHostException e) {
            logger.warn("getHostName fail", e);
        }

    }

    public static String getIp() {
        return ip;
    }

    public static String getHostName() {
        return hostname;
    }

    /**
     * Convert ip address to long.
     * @param str IP ADDRESS
     * @return long
     */
    public static long convert(String str) {
        return bytesToLong(getInetAddress(str).getAddress());
    }

    public static void main(String[] args) throws UnknownHostException {
        InetAddress address = InetAddress.getByName("2001:da8:9000:b255:200:e8ff:feb0:5c5e");
        System.out.println("IP: " + address.getHostAddress());
        switch (address.getAddress().length) {
        case 4:
            System.out.println("根据byte数组长度判断这个IP地址是IPv4地址!");
            break;
        case 16:
            System.out.println("根据byte数组长度判断这个IP地址是IPv6地址!");
            break;
        }

        System.out.println(Inets.getStringIp(address.getAddress()));

        if (address instanceof Inet4Address) {
            System.out.println("使用instanceof判断这个IP地址是IPv4地址!");
        }
        else if (address instanceof Inet6Address) {
            System.out.println("使用instanceof判断这个IP地址是IPv6地址!");
        }
    }

    /**
     * Convert ip address to InetAddress.
     * @param str IP ADDRESS
     * @return InetAddress
     */
    public static InetAddress getInetAddress(String str) {
        try {
            return InetAddress.getByName(str);
        }
        catch (UnknownHostException e) {
            return null;
        }

    }

    public static String getStringIp(byte[] ipBytes) {
        try {
            return InetAddress.getByAddress(ipBytes).getHostAddress();
        }
        catch (UnknownHostException e) {
            return null;
        }
    }

    /**
     * Returns the long version of an IP address given an InetAddress object.
     *
     * @param address the InetAddress.
     * @return the long form of the IP address.
     */
    public static long bytesToLong(byte[] address) {
        long ipnum = 0;
        for (int i = 0; i < 4; ++i) {
            long y = address[i];
            if (y < 0) {
                y += 256;
            }
            ipnum += y << (3 - i) * 8;
        }
        return ipnum;
    }
}
