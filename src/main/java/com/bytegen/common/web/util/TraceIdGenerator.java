package com.bytegen.common.web.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
public class TraceIdGenerator {

    private static AtomicLong lastId = new AtomicLong(); // 自增id，用于traceId的生成过程
    private static final String ip = NetworkUtil.resolveLocalIp(); // 本机ip地址，用于traceId的生成过程
    private static final String SEPARATOR = "_";
    private static final int ID_CYCLE = 1000000;

    public static String generateTraceId() {
        // 规则： hexIp(ip)base36(timestamp)-seq
        final long startTimeStamp = System.currentTimeMillis();
        return hexIp(ip) + Long.toString(startTimeStamp, Character.MAX_RADIX) + SEPARATOR
                + lastId.incrementAndGet() % ID_CYCLE;
    }

    // 将ip转换为定长8个字符的16进制表示形式：255.255.255.255 -> FFFFFFFF
    private static String hexIp(String ip) {
        final StringBuilder sb = new StringBuilder();
        for (String seg : ip.split("\\.")) {
            String h = Integer.toHexString(Integer.parseInt(seg));
            if (h.length() == 1) sb.append("0");
            sb.append(h);
        }
        return sb.toString();
    }

}
