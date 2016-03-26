package org.jasic.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;

import static org.jasic.util.ByteUtil.bitStrToByteArray;
import static org.jasic.util.ByteUtil.byteArrToBitString;
import static org.jasic.util.StringUtil.*;

/**
 * User: Jasic
 * Date: 13-9-10
 */
public abstract class NetWorkUtil {

    public static final String ipRegex = "^[1-2]?[0-9]{1,2}[.][1-2]?[0-9]{1,2}[.][1-2]?[0-9]{1,2}[.][1-2]?[0-9]{1,2}$";
    public static final String macRegex = "^[0-9A-Za-z]{2}-[0-9A-Za-z]{2}-[0-9A-Za-z]{2}-[0-9A-Za-z]{2}-[0-9A-Za-z]{2}-[0-9A-Za-z]{2}$";

    private static final String osName = System.getProperty("os.name");

    public static final boolean IS_WIN = osName.startsWith("Windows");

    public static final boolean IS_AIX = osName.equals("AIX");

    public static final boolean IS_SOLARIS = osName.equals("SunOS");

    public static final boolean IS_LINUX = osName.equals("Linux");

    public static Map<String, String> getGateWayIps() {

        if (IS_WIN) return getWindowsGateWayIps();

        if (IS_LINUX) return getLinuxGateWayIps();

        return new HashMap<String, String>(0);
    }

    /**
     * ------------------------------------  Windows --------------------------------------
     *
     * @return 描述. . . . . . . . . . . . . . . : Realtek PCIe GBE Family Controller
     * 物理地址. . . . . . . . . . . . . : 78-45-C4-05-80-1D
     * DHCP 已启用 . . . . . . . . . . . : 否
     * 自动配置已启用. . . . . . . . . . : 是
     * 本地链接 IPv6 地址. . . . . . . . : fe80::d83d:f662:8dce:9c18%11(首选)
     * IPv4 地址 . . . . . . . . . . . . : 172.16.27.39(首选)
     * 子网掩码  . . . . . . . . . . . . : 255.255.0.0
     * 默认网关. . . . . . . . . . . . . : 172.16.27.1
     * DHCPv6 IAID . . . . . . . . . . . : 242763204
     * DHCPv6 客户端 DUID  . . . . . . . : 00-01-00-01-19-87-E2-0A-78-45-C4-05-80-1
     * <p/>
     * DNS 服务器  . . . . . . . . . . . : 202.96.128.86
     * TCPIP 上的 NetBIOS  . . . . . . . : 已启用
     * ------------------------------------  Windows --------------------------------------
     */
    private static Map<String, String> getWindowsGateWayIps() {
        Process pro = null;
        Map<String, String> ips = new HashMap<String, String>();
        try {
            pro = Runtime.getRuntime().exec("cmd /c ipconfig /all");
            InputStreamReader isr = new InputStreamReader(pro.getInputStream(), Charset.forName("GBK"));
            BufferedReader br = new BufferedReader(isr);
            String str = br.readLine();
            String ip = null;
            String gateway = null;
            while (str != null) {
                if (str.toLowerCase().contains("gateway") || str.toLowerCase().contains("网关")) {
                    gateway = StringUtil.deleteWhitespace(str.toLowerCase().split(":")[1]);
                } else if (str.toLowerCase().contains("ipv4")) {
                    ip = getMatch(deleteWhitespace(str.toLowerCase().split(":")[1]), "[1-2]?[0-9]{1,2}[.][1-2]?[0-9]{1,2}[.][1-2]?[0-9]{1,2}[.][1-2]?[0-9]{1,2}");
                }

                if (ip != null && gateway != null && ip.trim().length() != 0 && gateway.trim().length() != 0) {
                    ips.put(ip, gateway);
                    ip = null;
                    gateway = null;
                }
                str = br.readLine();
            }

            br.close();
            isr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ips;
    }

    private static Map<String, String> getLinuxGateWayIps() {
        throw new RuntimeException("Not support  now~");
    }

    /**
     * 根据指定ip获取网关ip
     *
     * @param ip
     * @return
     */
    public static String getGateWayIp(String ip) {

        return getGateWayIps().get(ip);
    }

    /**
     * 根据ip地址去获取当前网卡信息
     *
     * @param ip
     * @return
     */
    public static List<InterfaceAddress> getInterfaceAddress(String ip) throws UnknownHostException, SocketException {

        byte[] ip_addr = ipStrToByteArr(ip);
        InetAddress address = InetAddress.getByAddress(ip_addr);
        NetworkInterface ni = NetworkInterface.getByInetAddress(address);
        return ni.getInterfaceAddresses();
    }

    /**
     * 动态修改java.library.path,因为在System.setProperty("java.library.path","new path");是不会生效的
     * java.library.path只在jvm启动时读取一次，其他情况下的修改不会起作用的
     * 原因和ClassLoader的实现有关系，ClassLoader.loadLibrary()
     */
    public static void addLibraryPath(String s) throws IOException {
        try {
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[]) field.get(null);
            for (int i = 0; i < paths.length; i++) {
                if (s.equals(paths[i])) {
                    return;
                }
                String[] tmp = new String[paths.length + 1];
                System.arraycopy(paths, 0, tmp, 0, paths.length);
                tmp[paths.length] = s;
                field.set(null, tmp);
            }
        } catch (IllegalAccessException e) {
            throw new IOException("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new IOException("Failed to get field handle to set library path");
        }
    }


    /**
     * 把ip地址转成byte数组
     *
     * @param ip
     * @return
     */
    public static byte[] ipStrToByteArr(String ip) {

        byte[] ip_addr = new byte[]{0x00, 0x00, 0x00, 0x00};
        if (isMatch(ipRegex, ip)) {
            String[] ipArr = ip.split("\\.");
            for (int index = 0; index != ipArr.length; index++) {
                ip_addr[index] = (byte) (Integer.parseInt(ipArr[index]) % 256);
            }
        }
        return ip_addr;
    }

    /**
     * 把ip数组转成ip字符串
     *
     * @param arr
     * @return
     */
    public static String ipByteArrToStr(byte[] arr) {
        Asserter.isTrue(!(arr == null || arr.length != 4));

        StringBuilder sbIp = new StringBuilder();
        for (byte b : arr) {
            sbIp.append(b & 0xFF).append(".");
        }
//        System.out.println(sbIp.length());
        sbIp.delete(sbIp.length() - 1, sbIp.length());

        return sbIp.toString();
    }

    /**
     * @param gateWayIp
     * @return
     */
    public static String getLanMacByIp(String gateWayIp) {
        return getLanMacByIp(new String[]{gateWayIp}).get(gateWayIp);
    }

    /**
     * ---------------------------------------------  Windows arp -a ------------------------------------------------------
     * 接口: 172.16.27.39 --- 0xb
     * Internet 地址         物理地址              类型
     * 172.16.24.80          00-b0-d0-9c-99-45     动态
     * 172.16.26.21          a4-1f-72-89-ed-e5     动态
     * 172.16.27.1           00-23-eb-72-a0-3f     动态
     * 224.0.0.251           01-00-5e-00-00-fb     静态
     * 224.0.0.252           01-00-5e-00-00-fc     静态
     * 230.0.0.3             01-00-5e-00-00-03     静态
     * 239.203.13.64         01-00-5e-4b-0d-40     静态
     * 239.255.255.251       01-00-5e-7f-ff-fb     静态
     * <p/>
     * 接口: 192.168.2.1 --- 0x11
     * Internet 地址         物理地址              类型
     * 239.255.255.251       01-00-5e-7f-ff-fb     静态
     * ---------------------------------------------  Windows arp -a ------------------------------------------------------
     *
     * @param gateWayIps
     * @return
     */
    public static Map<String, String> getLanMacByIp(String... gateWayIps) {
        Process pro = null;
        Map<String, String> ips = new HashMap<String, String>();
        try {
//            Runtime.getRuntime().exec("cmd /c arp -d");//先清除
            pro = Runtime.getRuntime().exec("cmd /c arp /a");
            InputStreamReader isr = new InputStreamReader(pro.getInputStream(), Charset.forName("GBK"));
            BufferedReader br = new BufferedReader(isr);
            String str = br.readLine();
            while (str != null) {
                for (String gateWayIp : gateWayIps) {
                    // 已获取的不再查
                    if (ips.containsKey(gateWayIp)) {
                        continue;
                    }
                    // 不包括的也不查
                    if (!StringUtil.strContainsStr(str, gateWayIp)) {
                        continue;
                    }
                    String[] arrays = str.split(" ");

                    String ip = null;
                    String mac = null;
                    for (String secStr : arrays) {
                        secStr = StringUtil.deleteWhitespace(secStr);
                        if (secStr.trim().length() < 8) continue; // ip与mac的长度肯定大于8

                        //匹配ip
                        if (StringUtil.strContainsStr(secStr, gateWayIp)) {
                            ip = gateWayIp;
                            continue;
                        }

                        //匹配mac
                        if (StringUtil.isMatch(macRegex, secStr)) {
                            mac = secStr;
                        }
                        if (ip != null && mac != null) {
                            ips.put(ip, mac);
                            break;
                        }
                    }
                }
                str = br.readLine();
            }
            br.close();
            isr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ips;
    }

    /**
     * 根据IP，子网掩码获取当前局域网内所有可能的ip地址
     *
     * @param ipStr
     * @param netMask
     * @return
     */
    public static List<String> getLanIps(String ipStr, String netMask) {
        return getLanIps(ipStr, netMask, true, true);
    }

    /**
     * 根据IP，子网掩码获取当前局域网内所有可能的ip地址
     *
     * @param ipStr    ip地址
     * @param netMask  子网掩码
     * @param inBroNet 网段地址
     * @param inSubNet 网段广播地址
     * @return
     */
    public static List<String> getLanIps(String ipStr, String netMask, boolean inBroNet, boolean inSubNet) {
        List<String> ips = new ArrayList<String>();
        if (isMatch(ipRegex, ipStr) && isMatch(ipRegex, netMask)) {
            /**
             * 1、计算局域网内主机数
             */
//          byte[] ipBroArr = NetWorkUtil.ipStrToByteArr("255.255.255.255");
            byte[] ipBroArr = new byte[4];
            Arrays.fill(ipBroArr, (byte) (0 | 0xFF));
            byte[] ipMaskArr = NetWorkUtil.ipStrToByteArr(netMask);
            byte[] ipArr = NetWorkUtil.ipStrToByteArr(ipStr);

            StringBuilder ipBroBitStr = new StringBuilder();
            StringBuilder ipMaskBitStr = new StringBuilder();

            ipBroBitStr.append(byteArrToBitString(ipBroArr));
            ipMaskBitStr.append(byteArrToBitString(ipMaskArr));

            int lanSize = (int) Math.abs(Long.parseLong(ipBroBitStr.toString(), 2) - Long.parseLong(ipMaskBitStr.toString(), 2));

            int hostlen = Integer.toBinaryString(lanSize).length();
            //要包括网络地址与广播地址

            System.out.println("此网段最大主机数为：" + (lanSize + 1));

            /**
             * 2、获取网络段地址
             */
            char[] ipCharArr = new char[32];
            int ipNetLen = 32 - hostlen;
            String ipNetHeader = byteArrToBitString(ipArr, ipNetLen, false);

            /**
             * 3、获取主机段地址
             */
            for (int i = 0; i <= lanSize; i++) {
                if (i == 0 && !inSubNet) {
                    continue;
                } else if (i == lanSize && !inBroNet) {
                    continue;
                } else {
                    String ipHostTail = byteArrToBitString(ByteUtil.uintToBytes(i), hostlen, true);
                    String ipBitStr = ipNetHeader + ipHostTail;
                    byte[] ipA = bitStrToByteArray(ipBitStr);
                    String ip = ipByteArrToStr(ipA);
//                    System.out.println(i + " : " + ip);
                    ips.add(ip);
                }
            }
        }
        return ips;
    }

    /**
     * 将mac地址由字符转换byte数组
     *
     * @param s
     * @return
     */
    public static byte[] macStrToByte(String s) {
        byte[] mac = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        String[] s1 = s.split("-");
        for (int x = 0; x < s1.length; x++) {
            mac[x] = (byte) ((Integer.parseInt(s1[x], 16)) & 0xff);
        }
        return mac;
    }

    /**
     * 将mac地址由byte数组转换字符串
     *
     * @param
     * @return
     */
    public static String macByteToStr(byte[] arr) {
        Asserter.isTrue(arr != null && arr.length == 6, "byte array of mac:{" + StringUtil.fieldval2Map(arr) + "} is illegal~");
        StringBuilder mac = new StringBuilder();

        for (int x = 0; x < arr.length; x++) {
            mac.append(ByteUtil.toHexString(new byte[]{arr[x]})).append("-");
        }
        mac.delete(mac.length() - 1, mac.length());
        return mac.toString();
    }

    public static void main(String[] args) throws Throwable {

//        String gateWayIp = getGateWayIp("172.16.27.39");
//        System.out.println(gateWayIp);
//        String gateWayMac = getLanMacByIp(gateWayIp);
//        System.out.println(gateWayMac);
//        System.out.println(mapToString(getLanMacByIp(new String[]{"1.1.1.1",
//                "172.16.24.80", "172.16.26.21", "172.16.27.1", "224.0.0.251", "224.0.0.252", "230.0.0.3", "239.203.13.64", "239.255.255.251"
//        })));


        System.out.println(getLanIps("192.168.1.100", "255.255.254.0"));

//        byte[] ipArr = ipStrToByteArr("115.245.254.119");
//        System.out.println(ipByteArrToStr(ipArr));
    }
}

