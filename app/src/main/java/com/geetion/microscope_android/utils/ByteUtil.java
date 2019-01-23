package com.geetion.microscope_android.utils;

import java.util.Arrays;

/**
 * Created by virgilyan on 15/7/3.
 */
public class ByteUtil {
    /**
     * 转换short为byte
     *
     * @param b
     * @param s     需要转换的short
     * @param index
     */
    public static void putShort(byte b[], short s, int index) {
        index = index * 2;
        b[index + 1] = (byte) (s >> 8);
        b[index + 0] = (byte) (s >> 0);
    }

    /**
     * 通过byte数组取到short
     *
     * @param b
     * @return
     */
    public static short getShort(byte[] b) {
        return getShort(b, 0);
    }

    /**
     * 通过byte数组取到short
     *
     * @param b
     * @param index 第几位开始取
     * @return
     */
    public static short getShort(byte[] b, int index) {
        index = index * 2;
        return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff));
    }

    public static short getUint8(byte[] b) {
        return getUint8(b, 0);
    }

    public static short getUint8(byte[] b, int index) {
        index = index * 2;
        return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff) & 0x00ff);
    }

    /**
     * 将short转成byte[2]
     *
     * @param a
     * @return
     */
    public static byte[] short2Byte(short a) {
        byte[] b = new byte[2];

        b[0] = (byte) (a >> 8);
        b[1] = (byte) (a);

        return b;
    }

    /**
     * 转换int为byte数组
     *
     * @param bb
     * @param x
     * @param index
     */
    public static void putInt(byte[] bb, int x, int index) {
        index = index * 4;
        bb[index + 3] = (byte) (x >> 24);
        bb[index + 2] = (byte) (x >> 16);
        bb[index + 1] = (byte) (x >> 8);
        bb[index + 0] = (byte) (x >> 0);
    }

    /**
     * 通过byte数组取到int
     *
     * @param bb
     * @return
     */
    public static int getInt(byte[] bb) {
        return getInt(bb, 0);
    }

    /**
     * 通过byte数组取到int
     *
     * @param bb
     * @param index 第几位开始
     * @return
     */
    public static int getInt(byte[] bb, int index) {
        try {
            index = index * 4;
            return ((bb[index + 3] & 0xff) << 24)
                    | ((bb[index + 2] & 0xff) << 16)
                    | ((bb[index + 1] & 0xff) << 8)
                    | ((bb[index + 0] & 0xff) << 0);
        } catch (Exception e) {
            return -1;
        }
    }

    public int getUint16(byte[] bb, int index) {
        try {
            index = index * 4;
            return ((bb[index + 3] & 0xff) << 24)
                    | ((bb[index + 2] & 0xff) << 16)
                    | ((bb[index + 1] & 0xff) << 8)
                    | ((bb[index + 0] & 0xff) << 0) & 0x0000ffff;
        } catch (Exception e) {
            return -1 & 0x0000ffff;
        }
    }

    /**
     * 转换long型为byte数组
     *
     * @param bb
     * @param x
     * @param index
     */
    public static void putLong(byte[] bb, long x, int index) {
        index = index * 8;
        bb[index + 7] = (byte) (x >> 56);
        bb[index + 6] = (byte) (x >> 48);
        bb[index + 5] = (byte) (x >> 40);
        bb[index + 4] = (byte) (x >> 32);
        bb[index + 3] = (byte) (x >> 24);
        bb[index + 2] = (byte) (x >> 16);
        bb[index + 1] = (byte) (x >> 8);
        bb[index + 0] = (byte) (x >> 0);
    }

    /**
     * 通过byte数组取到long
     *
     * @param bb
     * @return
     */
    public static long getLong(byte[] bb) {
        return getLong(bb, 0);
    }

    /**
     * 通过byte数组取到long
     *
     * @param bb
     * @param index
     * @return
     */
    public static long getLong(byte[] bb, int index) {
        index = index * 8;
        return ((((long) bb[index + 7] & 0xff) << 56)
                | (((long) bb[index + 6] & 0xff) << 48)
                | (((long) bb[index + 5] & 0xff) << 40)
                | (((long) bb[index + 4] & 0xff) << 32)
                | (((long) bb[index + 3] & 0xff) << 24)
                | (((long) bb[index + 2] & 0xff) << 16)
                | (((long) bb[index + 1] & 0xff) << 8)
                | (((long) bb[index + 0] & 0xff) << 0));
    }

    public long getUint32(long l) {
        return l & 0x00000000ffffffff;
    }

    /**
     * 字符到字节转换
     *
     * @param ch
     * @return
     */
    public static void putChar(byte[] bb, char ch, int index) {
        index = index * 2;
        int temp = (int) ch;
        // byte[] b = new byte[2];
        for (int i = 0; i < 2; i++) {
            bb[index + i] = new Integer(temp & 0xff).byteValue(); // 将最高位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
    }

    /**
     * 字节到字符转换
     *
     * @param b
     * @return
     */
    public static char getChar(byte[] b) {
        return getChar(b, 0);
    }

    /**
     * 字节到字符转换
     *
     * @param b
     * @return
     * @Param index
     */
    public static char getChar(byte[] b, int index) {
        index = index * 2;
        int s = 0;
        if (b[index + 1] > 0)
            s += b[index + 1];
        else
            s += 256 + b[index + 0];
        s *= 256;
        if (b[index + 0] > 0)
            s += b[index + 1];
        else
            s += 256 + b[index + 0];
        char ch = (char) s;
        return ch;
    }

    /**
     * float转换byte
     *
     * @param bb
     * @param x
     * @param index
     */
    public static void putFloat(byte[] bb, float x, int index) {
        // byte[] b = new byte[4];
        int l = Float.floatToIntBits(x);
        for (int i = 0; i < 4; i++) {
            bb[index + i] = new Integer(l).byteValue();
            l = l >> 8;
        }
    }

    /**
     * 通过byte数组取得float
     *
     * @param b
     * @return
     */
    public static float getFloat(byte[] b) {
        return getFloat(b, 0);
    }

    /**
     * 通过byte数组取得float
     *
     * @param b
     * @param index
     * @return
     */
    public static float getFloat(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

    /**
     * double转换byte
     *
     * @param bb
     * @param x
     * @param index
     */
    public static void putDouble(byte[] bb, double x, int index) {
        // byte[] b = new byte[8];
        index = index * 8;
        long l = Double.doubleToLongBits(x);
        for (int i = 0; i < 8; i++) {
            bb[index + i] = new Long(l).byteValue();
            l = l >> 8;
        }
    }

    /**
     * 通过byte数组取得float
     *
     * @param b
     * @return
     */
    public static double getDouble(byte[] b) {
        return getDouble(b, 0);
    }

    /**
     * 通过byte数组取得float
     *
     * @param b
     * @param index
     * @return
     */
    public static double getDouble(byte[] b, int index) {
        index = index * 8;
        long l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        l &= 0xffffffffl;
        l |= ((long) b[index + 4] << 32);
        l &= 0xffffffffffl;
        l |= ((long) b[index + 5] << 40);
        l &= 0xffffffffffffl;
        l |= ((long) b[index + 6] << 48);
        l &= 0xffffffffffffffl;
        l |= ((long) b[index + 7] << 56);
        return Double.longBitsToDouble(l);
    }

    /**
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
     *
     * @param b
     * @return
     */
    public static byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    /**
     * 把byte转为字符串的bit
     *
     * @param b
     * @return
     */
    public static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    /**
     * 二进制字符串转byte
     *
     * @param byteStr
     * @return
     */
    public static byte decodeBinaryString(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {// 4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }

    public static byte reversedByte(byte b) {
        return decodeBinaryString(""
                + (byte) ((b >> 0) & 0x1) + (byte) ((b >> 1) & 0x1)
                + (byte) ((b >> 2) & 0x1) + (byte) ((b >> 3) & 0x1)
                + (byte) ((b >> 4) & 0x1) + (byte) ((b >> 5) & 0x1)
                + (byte) ((b >> 6) & 0x1) + (byte) ((b >> 7) & 0x1));
    }

    /**
     * 把字节数组转换成16进制字符串
     *
     * @param bArray
     * @return
     */
    public static String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 把字节数组转换成2进制字符串
     *
     * @param byteArray
     * @return
     */
    public static String bytesToBinaryString(byte[] byteArray) {
        int capacity = byteArray.length * 8;
        StringBuilder sb = new StringBuilder(capacity);
        for (int i = 0; i < byteArray.length; i++) {
            sb.append(Integer.toBinaryString(byteArray[i]));
        }
        return sb.toString();
    }

    /**
     * byte转Unicode字符串
     *
     * @param aByte
     * @return
     */
    public static String Byte2Unicode(byte[] aByte) {
        return Byte2Unicode(aByte, aByte.length);
    }

    public static String Byte2Unicode(byte[] aByte, int len) {
        return Byte2Unicode(aByte, 0, len);
    }

    public static String Byte2Unicode(byte aByte[], int st, int bEnd) { // 不包含bEnd
        if (aByte != null && aByte.length >= (bEnd - st)) {
            StringBuffer sb = new StringBuffer("");
            for (int j = st; j < bEnd; ) {
                int lw = aByte[j++];
                if (lw < 0) lw += 256;
                int hi = aByte[j++];
                if (hi < 0) hi += 256;
                char c = (char) (lw + (hi << 8));
                sb.append(c);
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * Unicode字符串转byte
     *
     * @param s
     * @return
     */
    public static byte[] Unicode2Byte(String s) {
        int len = s.length();
        byte aByte[] = new byte[len << 1];
        int j = 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            aByte[j++] = (byte) (c & 0xff);
            aByte[j++] = (byte) (c >> 8);
        }
        return aByte;
    }

    /**
     * byte转UTF-8字符串
     *
     * @param aByte
     * @return
     */
    public static String Byte2String(byte[] aByte) {
        return Byte2String(aByte, aByte.length);
    }

    public static String Byte2String(byte[] aByte, int length) {
        return Byte2String(aByte, 0, length);
    }

    public static String Byte2String(byte[] bytes, int start, int end) {
        if (bytes != null && bytes.length - 1 >= end && (end - start) > 0) {
            return new String(Arrays.copyOfRange(bytes, start, end));
        }
        return null;
    }

    public static byte[] byteMerger(byte[] aBytes, byte[] bBytes) {
        byte[] cBytes = new byte[aBytes.length + bBytes.length];
        System.arraycopy(aBytes, 0, cBytes, 0, aBytes.length);
        System.arraycopy(bBytes, 0, cBytes, aBytes.length, bBytes.length);
        return cBytes;
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2, int recvMsgSize) {
        byte[] byte_3 = new byte[byte_1.length + recvMsgSize];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, recvMsgSize);
        return byte_3;
    }

    private static final int MASKBITS = 0x3F;
    private static final int MASKBYTE = 0x80;
    private static final int MASK2BYTES = 0xC0;
    private static final int MASK3BYTES = 0xE0;

    /**
     * @功能: 将UNICODE（UTF-16LE）编码转成UTF-8编码
     * @参数: byte[] b 源字节数组
     * @返回值: byte[] b 转为UTF-8编码后的数组
     * @作者: imuse
     */
    public static byte[] UNICODE_TO_UTF8(byte[] b) {
        int i = 0;
        int j = 0;
        byte[] utf8Byte = new byte[b.length * 2];
        while (i < b.length) {
            byte[] bUTF = new byte[1];
            int nCode = (b[i] & 0xFF) | ((b[i + 1] & 0xFF) << 8);
            if (nCode < 0x80) {
                bUTF = new byte[1];
                bUTF[0] = (byte) nCode;
            }
            // 110xxxxx 10xxxxxx
            else if (nCode < 0x800) {
                bUTF = new byte[2];
                bUTF[0] = (byte) (MASK2BYTES | nCode >> 6);
                bUTF[1] = (byte) (MASKBYTE | nCode & MASKBITS);
            }
            // 1110xxxx 10xxxxxx 10xxxxxx
            else if (nCode < 0x10000) {
                bUTF = new byte[3];
                bUTF[0] = (byte) (MASK3BYTES | nCode >> 12);
                bUTF[1] = (byte) (MASKBYTE | nCode >> 6 & MASKBITS);
                bUTF[2] = (byte) (MASKBYTE | nCode & MASKBITS);
            }
            for (int k = 0; k < bUTF.length; k++) {
                utf8Byte[j++] = bUTF[k];
            }
            i += 2;
        }
        b = new byte[j];
        System.arraycopy(utf8Byte, 0, b, 0, j);
        return b;
    }

    public static byte binaryToByte(String binary) {
        return Byte.parseByte(binary, 2);
    }

    /**
     * 该方法等同于Integer.toBinaryString(b)
     *
     * @param b
     * @return
     */
    public static String byte2bits(byte b) {
        int z = b;
        z |= 256;
        String str = Integer.toBinaryString(z);
        int len = str.length();
        return str.substring(len - 8, len);
    }

    /**
     * 将二进制字符串转换回字节
     *
     * @param bString
     * @return
     */
    public static byte bit2byte(String bString) {
        byte result = 0;
        for (int i = bString.length() - 1, j = 0; i >= 0; i--, j++) {
            result += (Byte.parseByte(bString.charAt(i) + "") * Math.pow(2, j));
        }
        return result;
    }
}
