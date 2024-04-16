package com.face.utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class IPValidator {

    // 正则表达式来匹配一个单独的IPv4地址
    private static final String IPV4_REGEX = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
            + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
            + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
            + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    // 编译正则表达式为 Pattern 对象
    private static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

    public static boolean isValidIPList(String ipList) {
        // 检查传入字符串是否为空
        if (ipList == null || ipList.isEmpty()) {
            return false;
        }

        // 分割字符串以检查每个 IP 地址
        String[] ips = ipList.split(",");
        for (String ip : ips) {
            // 去除字符串两端的空白字符
            ip = ip.trim();
            // 检查每个 IP 地址是否符合 IPv4 格式
            if (!IPV4_PATTERN.matcher(ip).matches()) {
                return false; // 如果任何一个 IP 地址不匹配，则返回 false
            }
        }
        return true; // 所有 IP 地址都符合格式
    }

    // Method to check if a given IP is in the provided comma-separated list of IPs
    public static boolean isIPInList(String ip, String ipList) {
        if (ip == null || ipList == null || ipList.isEmpty()) {
            return false;
        }
        List<String> ipArray = Arrays.asList(ipList.split(","));
        return ipArray.contains(ip);
    }

    public static void main(String[] args) {
        // 测试示例
        String ipList = "192.168.1.1, 10.10.10.10";
        System.out.println("IP List is valid: " + isValidIPList(ipList));
    }
}
