package org.jasic;

import org.jasic.util.DateTimeUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: Jasic
 * Date: 13-10-28
 * !
 */
public class SeqCreator {

    private static final Random _RANDOM;
    private static final AtomicInteger _OSEQ;

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        long test = 0;
        for
                (; test < 1000000000; test++) {

        }
        long t2 = System.currentTimeMillis();
        System.out.println("!" + (t2 - t1));
        Map<String, String> map = new HashMap<String, String>();


        for (int i = 0; i < 100000; i++) {
            String result = genSequence() + "";
            String replica = map.put(result, result);
            if (replica != null) {
                System.out.println("!:" + replica + "!" + result + ",e:" + (replica.equals(result)));
            }
        }
        System.out.println(Long.MAX_VALUE);
    }

    static {
        _RANDOM = new Random();
        _OSEQ = new AtomicInteger(0);
    }

    private static int getMidfix() {
        int i = _OSEQ.getAndAdd(1);
        if (i == 300) {
            _OSEQ.set(0);
            DateTimeUtil.sleep(1);
        }
        return i;
    }

    public static Long genSequence() {
        String prefix = (System.currentTimeMillis() + "").substring(0, 13);
        prefix = new String(inverse(prefix.toCharArray()));

        String rawMidfux = "000" + getMidfix() + "";
        int rawMixLen = rawMidfux.length();
        String midfix = rawMidfux.substring(rawMixLen - 3, rawMixLen);

        String rawSufix = "00" + Math.abs(_RANDOM.nextInt(99));
        int rawLen = rawSufix.length();
        String sufix = rawSufix.substring(rawLen - 2, rawLen);

        String id = prefix + midfix + sufix;
        return Long.parseLong(id);
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
}
