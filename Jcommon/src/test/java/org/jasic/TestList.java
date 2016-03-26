package org.jasic;

import java.io.IOException;
import java.util.*;

/**
 * User: Jasic
 * Date: 13-10-21
 */
public class TestList {
    public static void main(String[] args) {
//test1();
        test2();
    }

    public static void test2() {

        List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("2");
        list.add("2");
        list.add("3");


        List<String> repeatList = new ArrayList<String>();
        HashSet<String> set = new HashSet<String>();
        for (String tradeId : list) {
            if (Collections.frequency(list, tradeId) > 1) {
                repeatList.add(tradeId);
                set.add(tradeId);
            }
        }

        System.out.println("repeatList:" + repeatList);
        System.out.println("repeatSet:" + set);

        list.removeAll(repeatList);
        list.addAll(set);
        System.out.println("unique:" + list);
    }

    public static void test1() {

        while (!Thread.interrupted()) {

            int i = 0;
            try {
                Random random = new Random();
                if (random.nextBoolean()) {
                    throw new RuntimeException("1111");
                } else throw new IOException("222");
            } catch (RuntimeException e) {

                continue;
            } catch (IOException e) {
                break;
            } finally {
                System.out.println("常用类总结[" + i++ + "]");
            }
        }


        List<String> strlist = new ArrayList<String>();
        strlist.add("11");
        strlist.add("12");
        strlist.add("13");
        strlist.add("14");
        strlist.add("15");
        strlist.add("16");
        strlist.add("17");
        strlist.add("18");
        strlist.add("19");
        strlist.add("10");

        int size = strlist.size();
        int avgSpeed = 2;

        int count = 0;
        List<List<String>> a = new ArrayList<List<String>>();
        while (size > avgSpeed) {
            List<String> temp = new ArrayList<String>(strlist.subList(0, avgSpeed));
            a.add(temp);
            strlist.removeAll(temp);
            size = strlist.size();
            System.out.println("List's:" + size);
            System.out.println("count:" + count++);
        }
        System.out.println("List's:" + size);
        System.out.println("count:" + count++);
        a.add(strlist);
        System.out.println(a);
    }
}
