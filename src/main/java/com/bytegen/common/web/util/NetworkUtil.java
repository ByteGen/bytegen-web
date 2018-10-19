package com.bytegen.common.web.util;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
public class NetworkUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkUtil.class);

    /**
     * may be more than one ip is found
     */
    public static Set<InetAddress> resolveLocalAddresses() {
        Set<InetAddress> addrs = new HashSet<InetAddress>();
        Enumeration<NetworkInterface> ns = null;
        try {
            ns = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ignored) {
        }
        while (ns != null && ns.hasMoreElements()) {
            NetworkInterface n = ns.nextElement();
            Enumeration<InetAddress> is = n.getInetAddresses();
            while (is.hasMoreElements()) {
                InetAddress i = is.nextElement();
                if (!i.isLoopbackAddress() && !i.isLinkLocalAddress() && !i.isMulticastAddress()
                        && !isSpecialIp(i.getHostAddress())) addrs.add(i);
            }
        }
        return addrs;
    }

    public static String resolveLocalIp() {
        Set<InetAddress> addrs = resolveLocalAddresses();
        for (InetAddress addr : addrs) {
            return addr.getHostAddress();
        }
        return "";
    }

    public static Set<String> resolveLocalIps() {
        Set<InetAddress> addrs = resolveLocalAddresses();
        Set<String> ret = new HashSet<String>();
        for (InetAddress addr : addrs)
            ret.add(addr.getHostAddress());
        return ret;
    }

    private static boolean isSpecialIp(String ip) {
        if (ip.contains(":")) return true;
        if (ip.startsWith("127.")) return true;
        if (ip.startsWith("169.254.")) return true;
        if (ip.equals("255.255.255.255")) return true;
        return false;
    }

    public static String getLocalHostName() {
        String hostname = System.getenv("HOSTNAME");
        if (hostname == null || hostname.length() == 0) {
            InputStream in = null;
            BufferedReader reader = null;
            try {
                Process pro = Runtime.getRuntime().exec("hostname");
                pro.waitFor();
                in = pro.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in, Charset.forName("utf-8")));
                hostname = reader.readLine();
            } catch (IOException e) {
                LOGGER.error("getLocalHostName IOException");
            } catch (InterruptedException e) {
                LOGGER.error("getLocalHostName InterruptedException");
            } finally {
                closeIgnoreException(reader);
                closeIgnoreException(in);
            }
        }
        return hostname;
    }

    public static boolean isIp(String src) {
        return InetAddressValidator.getInstance().isValid(src);
    }

    public static boolean isSubnet(String subnet) {
        try {
            SubnetUtils subnetUtils = new SubnetUtils(subnet);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Close the closeable, and ignore IOException.
     */
    public static boolean closeIgnoreException(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
            return true;
        } catch (IOException e) {
            LOGGER.error("Close failed.", e);
            return false;
        }
    }
}
