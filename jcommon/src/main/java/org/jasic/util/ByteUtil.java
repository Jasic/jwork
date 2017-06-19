package org.jasic.util;

import java.util.Arrays;

/**
 * byte操作类
 *
 * @author chris
 */
public final class ByteUtil {
    /**
     * Bytes array to raw binary string
     */
    public static String byteArr2bitStr(byte[] bytes) {

        Asserter.notNull(bytes);

        StringBuilder sb = new StringBuilder();

        String bin;
        char temp[] = new char[32];
        Arrays.fill(temp, '0');
        for (int index = 0; index < bytes.length; index++) {

            bin = Integer.toBinaryString(bytes[index]);
            System.arraycopy(bin.toCharArray(), 0, temp, temp.length - bin.length(), bin.length());
            sb.append(new String(temp));

            sb.append(",");
        }

        return sb.toString();
    }

    /**
     * 获取byte数组的子数据
     *
     * @param bytes
     * @param begin
     * @param len
     * @return
     */
    public static byte[] subBytes(byte[] bytes, int begin, int len) {
        if (bytes == null || bytes.length < begin + len) {
            return null;
        }

        byte[] result = new byte[len];

        for (int i = 0; i < len; i++) {
            result[i] = bytes[begin + i];
        }

        return result;
    }

    /**
     * 获取byte数组的子数据
     *
     * @param bytes
     * @param begin
     * @return
     */
    public static byte[] getSubBytes(byte[] bytes, int begin) {
        if (bytes == null || bytes.length < begin) {
            return null;
        }

        return subBytes(bytes, begin, bytes.length - begin);
    }

    /**
     * 比较byte数组
     *
     * @param dstBytes
     * @param srcBytes
     * @return
     */
    public static boolean compareBytes(byte[] dstBytes, byte[] srcBytes) {
        if (dstBytes == null && srcBytes == null) {
            return true;
        }

        if (dstBytes == null || srcBytes == null || dstBytes.length != srcBytes.length) {
            return false;
        }

        for (int i = 0; i < dstBytes.length; i++) {
            if (dstBytes[i] != srcBytes[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 比较byte数组
     *
     * @param bytes
     * @param begin
     * @param len
     * @param subBytes
     * @return
     */
    public static boolean compareSubBytes(byte[] bytes, int begin, int len, byte[] subBytes) {
        if (bytes == null || subBytes == null || bytes.length < subBytes.length + len) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            if (subBytes[i] != bytes[begin + i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断byte[]是否都是数字
     *
     * @param bytes
     * @return
     */
    public static boolean isNumeric(byte[] bytes) {
        if (bytes == null || bytes.length <= 0)
            return false;

        for (int i = 0; i < bytes.length; i++) {
            int n = bytes[i] & 0xff;
            if (n < 48 || n > 57) {
                return false;
            }
        }

        return true;
    }


    /**
     * uint转换为byte[]
     *
     * @param i
     * @return
     */
    public static byte[] ushortToBytes(int n) {
        byte[] bytes = new byte[2];

        ushortToBytes(bytes, 0, n);

        return bytes;
    }

    /**
     * uint转换为byte[]
     *
     * @param bytes
     * @param begin
     * @param value
     */
    public static void ushortToBytes(byte[] bytes, int begin, int value) {
        if (bytes == null || bytes.length < begin + 2) {
            return;
        }

        bytes[begin] = (byte) (value >> 8 & 0xFF);
        bytes[(begin + 1)] = (byte) (value >> 0 & 0xFF);

    }

    /**
     * uint转换为byte[]
     *
     * @param i
     * @return
     */
    public static byte[] uintToBytes(int n) {
        byte[] bytes = new byte[4];

        uintToBytes(bytes, 0, n);

        return bytes;
    }

    /**
     * uint转换为byte[]
     *
     * @param bytes
     * @param begin
     * @param value
     */
    public static void uintToBytes(byte[] bytes, int begin, int value) {
        if (bytes == null || bytes.length < begin + 4) {
            return;
        }

        bytes[begin] = (byte) (value >> 24 & 0xFF);
        bytes[(begin + 1)] = (byte) (value >> 16 & 0xFF);
        bytes[(begin + 2)] = (byte) (value >> 8 & 0xFF);
        bytes[(begin + 3)] = (byte) (value & 0xFF);
    }

    /**
     * byte[]转换为int
     *
     * @param bytes
     * @return
     */
    public static int bytesToUINT(byte[] bytes) {
        return bytesToUINT(bytes, 0);
    }

    /**
     * byte[]转换为uint
     *
     * @param bytes
     * @param begin
     * @return
     */
    public static int bytesToUSHORT(byte[] bytes, int begin) {
        if (bytes == null || bytes.length <= begin + 1) {
            return 0;
        }

        int r = 0;

        r |= 0xFF & bytes[begin];
        r <<= 8;
        r |= 0xFF & bytes[(begin + 1)];

        return r;
    }

    /**
     * byte[]转换为uint
     *
     * @param bytes
     * @param begin
     * @return
     */
    public static int bytesToUINT(byte[] bytes, int begin) {
        if (bytes == null || bytes.length <= begin + 3) {
            return 0;
        }

        int r = 0;

        r |= 0xFF & bytes[begin];
        r <<= 8;
        r |= 0xFF & bytes[(begin + 1)];
        r <<= 8;
        r |= 0xFF & bytes[(begin + 2)];
        r <<= 8;
        r |= 0xFF & bytes[(begin + 3)];

        return r;
    }

    /**
     * byte[]转换为unit(long)
     *
     * @param bytes
     * @return
     */
    public static long bytesToUINTEx(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            return 0;
        }

        int firstByte = (0x000000FF & ((int) bytes[0]));
        int secondByte = (0x000000FF & ((int) bytes[1]));
        int thirdByte = (0x000000FF & ((int) bytes[2]));
        int fourthByte = (0x000000FF & ((int) bytes[3]));

        return ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
    }

    /**
     * uint转换为byte[]
     *
     * @param bytes
     * @param begin
     * @param value
     */
    public static void uintToBytesEx(byte[] bytes, int begin, long value) {
        if (bytes == null || bytes.length < begin + 4) {
            return;
        }

        bytes[begin] = (byte) (value >> 24 & 0xFF);
        bytes[(begin + 1)] = (byte) (value >> 16 & 0xFF);
        bytes[(begin + 2)] = (byte) (value >> 8 & 0xFF);
        bytes[(begin + 3)] = (byte) (value & 0xFF);
    }

    /**
     * uint(long)转换为byte[]
     *
     * @param l
     * @return
     */
    public static byte[] uintToBytesEx(long l) {
        byte[] bytes = new byte[4];

        bytes[0] = (byte) ((l & 0xff000000) >> 24);
        bytes[1] = (byte) ((l & 0x00ff0000) >> 16);
        bytes[2] = (byte) ((l & 0x0000ff00) >> 8);
        bytes[3] = (byte) ((l & 0x000000ff));

        return bytes;
    }

    /**
     * 字节转换为字符串
     *
     * @param bytes
     * @param begin
     * @param len
     * @return
     */
    public static String bytesToString(byte[] bytes, int begin, int len) {

        if (bytes == null || bytes.length <= begin + len - 1)
            return null;

        String r = new String();

        for (int i = 0; i < len; i++) {
            if (bytes[(begin + i)] == 0)
                break;

            r += (char) bytes[(begin + i)];
        }

        return r;
    }

    /**
     * byte数组转换为CString(以NULL结束的ASCII字符串)
     *
     * @param bytes
     * @param begin
     * @return
     */
    public static String bytesToCString(byte[] bytes, int begin) {
        if (bytes == null)
            return null;

        String result = new String();

        for (int i = 0; begin < bytes.length; i++) {
            if (bytes[(begin + i)] == 0x00)
                break;

            result += (char) bytes[(begin + i)];
        }

        return result;
    }

    /**
     * byte数组转换为CString(以NULL结束的ASCII字符串)
     *
     * @param bytes
     * @param begin
     * @return
     */
    public static String bytesToCString(byte[] bytes, int begin, int len) {
        if (bytes == null)
            return null;

        String result = new String();

        for (int i = 0; i < len; i++) {
            if (bytes[(begin + i)] == 0x00)
                break;

            result += (char) bytes[(begin + i)];
        }

        return result;
    }

    /**
     * String转换为byte[]
     *
     * @param str
     * @param bytes
     * @param begin
     * @param len
     */
    public static void stringToBytes(byte[] bytes, String str, int begin, int len) {
        copyBytes(bytes, str.getBytes(), begin, 0, len);
    }

    /**
     * octetString转换为byte[]
     *
     * @param str
     * @param bytes
     * @param begin
     * @param len
     */
    public static void octetStringToBytes(byte[] bytes, String str, int begin, int len) {
        if (bytes == null) {
            return;
        }

        int strLen = 0;
        byte[] strBytes = null;

        if (str != null) {
            strBytes = str.getBytes();
            strLen = strBytes.length;
        }

        for (int i = 0; i < len; i++) {
            bytes[(i + begin)] = i < strLen ? strBytes[i] : 0;
        }
    }

    /**
     * 复制数组
     *
     * @param dstBytes
     * @param srcBytes
     * @param dstOffset
     * @param srcOffset
     * @param len
     */
    public static void copyBytes(byte[] dstBytes, byte[] srcBytes, int dstOffset, int srcOffset, int len) {
        if (dstBytes == null || srcBytes == null || len == 0 || dstBytes.length < dstOffset + len || srcBytes.length < srcOffset + len) {
            return;
        }

        for (int i = 0; i < len; i++) {
            dstBytes[(i + dstOffset)] = srcBytes[(i + srcOffset)];
        }
    }

    /**
     * 复制数组
     *
     * @param srcBytes
     * @param srcOffset
     * @param len
     * @return
     */
    public static byte[] copyBytes(byte[] srcBytes, int srcOffset, int len) {
        if (srcBytes == null || len == 0 || srcBytes.length < srcOffset + len) {
            return null;
        }

        byte[] result = new byte[len];

        copyBytes(result, srcBytes, 0, srcOffset, len);

        return result;
    }

    /**
     * 复制数组
     *
     * @param srcBytes
     * @param srcOffset
     * @return
     */
    public static byte[] copyBytes(byte[] srcBytes, int srcOffset) {
        if (srcBytes == null || srcBytes.length < srcOffset) {
            return null;
        }

        return copyBytes(srcBytes, srcOffset, srcBytes.length - srcOffset);
    }

    /**
     * 获取byte[]的十六进制字符串
     *
     * @param bytes
     * @return
     */
    public static String toHexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        String hexString = "";

        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);

            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            if (i > 0)
                hexString += " ";

            hexString += hex.toUpperCase();

        }

        return hexString;

    }

    /**
     * bcd字节数组转换为数字字符串
     *
     * @param bytes
     * @return
     */
    public static String bcdToString(byte[] bytes) {
        return bcdToString(bytes, 0, bytes.length);
    }

    /**
     * bcd字节数组转换为数字字符串
     *
     * @param bytes
     * @param begin
     * @param len
     * @return
     */
    public static String bcdToString(byte[] bytes, int begin, int len) {
        if (bytes == null || bytes.length <= begin + len - 1)
            return null;

        String result = "";

        for (int i = 0; i < len; i++) {
            result += (byte) ((bytes[begin + i] & 0xf0) >>> 4);
            result += (byte) (bytes[begin + i] & 0x0f);
        }

        return result;
    }

    /**
     * 数字字符串转换为bcd字节数组
     *
     * @param asc
     * @return
     */
    public static byte[] stringToBCD(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }

        return bbt;
    }

    /**
     * 将二进制字符串转成byte数组
     *
     * @param bitStr
     * @return
     */
    public static byte[] bitStrToByteArray(String bitStr) {
        Asserter.isTrue(bitStr != null && StringUtil.deleteWhitespace(bitStr).length() != 0);
        byte[] result = new byte[0];

        /*
         * 补全位数
         */
        int left = bitStr.length() % 8;
        StringBuilder letfStr = new StringBuilder();
        for (int i = 0; left != 0 && i < 8 - left; i++) {
            letfStr.append("0");
        }
        bitStr = letfStr.append(bitStr).toString();

        int max = bitStr.length() / 8;
        result = new byte[max];
        for (int index = 0; index < max; index++) {
            String str = bitStr.substring(index * 8, index * 8 + 8);
            result[index] = (byte) Integer.parseInt(str, 2);
        }

        return result;
    }

    /**
     * 获得字节数组的比特流字符串
     *
     * @param byteArr
     * @return
     */
    public static String byteArrToBitString(byte[] byteArr) {
        Asserter.isTrue(byteArr != null, "bit len must be larger than zero!");
        return byteArrToBitString(byteArr, byteArr.length * 8, true);
    }

    /**
     * 字节转二进制比特流，当需要的比特字符串长度小于字节数组乘4的时候，则会截断，最后一个参数是向左或向右截断，true截断高位，false截断低位。
     * 如果所需要的比特字符串长度大于字节数组乘4的时候，则会填充‘0’，最后一个参数是向左填充或向右填充，true向高位填充，false向代位填充。
     *
     * @param byteArr   字节数组
     * @param bitLen    所需要的二进制长度
     * @param lookRight 窗口对齐 true:相当于margin-right, 否则相当于margin-left.
     * @return
     */
    public static String byteArrToBitString(byte[] byteArr, int bitLen, boolean lookRight) {
        Asserter.isTrue(byteArr != null && byteArr.length > 0 && bitLen > 0, "bit len must be larger than zero!");

        char[] chars = new char[byteArr.length * 8];
        int pos = 0;
        for (byte b : byteArr) {
            char[] bBitCharArr = new char[8];
            Arrays.fill(bBitCharArr, '0');
            char[] temp = Integer.toBinaryString(b & 0xFF).toCharArray();
            int tempLen = temp.length;
            System.arraycopy(temp, 0, bBitCharArr, 8 - tempLen, tempLen);
            int len = bBitCharArr.length;
            System.arraycopy(bBitCharArr, 0, chars, pos, len);
            pos += len;
        }

        char[] result = new char[bitLen];
        Arrays.fill(result, '0');

        int len = bitLen > byteArr.length * 8 ? byteArr.length * 8 : bitLen;
        int pos1 = 0;
        int pos2 = 0;
        //margin-letf is default
        // pos1=0;pos2=0;

        // margin-right
        if (lookRight) {
            if (bitLen <= byteArr.length * 8) {
                pos1 = byteArr.length * 8 - bitLen;
                pos2 = 0;
            } else {
                pos1 = 0;
                pos2 = bitLen - byteArr.length * 8;
            }
        }
        System.arraycopy(chars, pos1, result, pos2, len);
        return new String(result);
    }

    /**
     * 将byte数组的数组的数据集合成一个byte数组
     *
     * @param bytes
     * @return
     */
    public static byte[] combine(byte[]... bytes) {

        if (bytes == null || bytes.length == 0) return new byte[0];

        byte[] result = new byte[0];
        byte[] temp;
        for (byte[] bArr : bytes) {

            temp = result;
            result = new byte[temp.length + bArr.length];
            System.arraycopy(temp, 0, result, 0, temp.length);
            System.arraycopy(bArr, 0, result, temp.length, bArr.length);
        }

        return result;
    }

    public static void main(String[] args) {
        byte[] a = new byte[4];
        int n = 11223344;

        ByteUtil.uintToBytes(a, 0, n);

        System.out.println(ByteUtil.toHexString(ByteUtil.uintToBytes(9)));
        System.out.println(ByteUtil.bytesToUINTEx(a));
        System.out.println(ByteUtil.bytesToUINT(a, 0));

        System.out.println(ByteUtil.bcdToString(new byte[]{0xa}));

        byte[] aaaa = ByteUtil.combine(new byte[]{1}, new byte[]{2}, new byte[]{3, 3, 3, 3});
        System.out.println(aaaa);

    }

}
