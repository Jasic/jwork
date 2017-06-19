package org.jasic;

import java.util.UUID;

/**
 * User: Jasic
 * Date: 13-10-28
 */
public class TestSequence {

    public static void main(String a[]) {
        System.out.println(new String(inverse("123456789".toCharArray())));
    }

    public static char[] inverse(char[] array) {
        if (array == null) return null;

        int len = array.length;
        char[] oArray = new char[len];

        for (int index = 0; index < len; index++) {
            oArray[index] = array[len - (index + 1)];
        }

        return oArray;
    }

    public void testUUIDSeq() {
        String time = System.currentTimeMillis() + "";

        System.out.println(time);

        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-", "");

        byte[] uuidByte = uuid.getBytes();
        System.out.println(uuid);

        StringBuilder sb = new StringBuilder();

        int len = uuid.length();

        for (int index = 0; index < len; index += 2) {
            System.out.println();
            String s = new String(uuidByte, index, 2);
            System.out.println("index:" + index + ", s:" + s + ",v:" + Integer.parseInt(s, 16));
        }
    }
}
