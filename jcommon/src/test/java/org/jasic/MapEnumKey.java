package org.jasic;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author 菜鹰.
 * @Date 14-10-2
 */
public class MapEnumKey {

    enum Enum {
        A("CODE:b", "VALUE:a"), B("CODE:b", "VALUE:b");

        String code;
        String value;

        Enum(String code, String value) {
            this.code = code;
            this.value = value;
        }
    }

    public static void main(String[] args) {

        Map map = new HashMap();
        map.put(Enum.A.toString(), "A");
        map.put(Enum.B.toString(), "B");

        System.out.println(map);
    }
}
