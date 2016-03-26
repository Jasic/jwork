package org.jasic;

import org.junit.Test;

/**
 * @Author 菜鹰.
 * @Date 2014/10/26
 */
public class TestString {

    public static void main(String[] args) {

        Byte a = 1;
        int b = 1;
        System.out.println((b != a) + "");

        int t1 = 1;
        Integer t2 = null;

        System.out.println(t1 == t2);
    }

    @Test
    public void test1() {
        int nThreads = 9;
        int size = 99;
        for (int i = 0; i < nThreads; i++) {
            double start = (double) size / nThreads * i;
            double end = (double) size / nThreads * (i + 1);

            System.out.println("start:" + (int)start + ", end:" + (int)end);
        }
    }

    @Test
    public void test2(){
        int size = 250;
        int num = size / 100 + 1;
        // 获取品类ID，并且组装放进map
        for (int i = 0; i < num; i++) {
            double start = (double) size / num * i;
            double end = (double) size / num * (i + 1);

            System.out.printf("开始%s，结束%S\n",(int)start,(int)end);
        }
    }
}
