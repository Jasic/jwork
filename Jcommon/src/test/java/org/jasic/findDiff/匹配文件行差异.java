package org.jasic.findDiff;

import org.apache.commons.io.FileUtils;
import org.jasic.util.CollectionUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author 菜鹰
 * @Date 15-5-26
 * 类解释：
 */
public class 匹配文件行差异 {

    public static void main(String[] args) throws IOException {
        File file1 = new File("D:\\github\\Jws\\Jcommon\\src\\test\\java\\org\\jasic\\findDiff\\源数据.txt");
        File file2 = new File("D:\\github\\Jws\\Jcommon\\src\\test\\java\\org\\jasic\\findDiff\\目标数据.txt");
        List<String> list1 = FileUtils.readLines(file1);
        List<String> list2 = FileUtils.readLines(file2);

        Map<String, List> map = CollectionUtil.findArrayDifferences(list1, list2);
        for (Map.Entry<String, List> entry : map.entrySet()) {

            System.out.println(entry.getValue().size() + "-->" + entry.toString());
        }
    }

    @Test

       public void test1(){

        boolean[] booleans = new boolean[4];

        boolean flag0 = booleans[0];
        boolean flag1 = booleans[1];
        boolean flag2 = booleans[2];
        boolean flag3 = booleans[3];
        System.out.println(flag0 && flag1 || flag2 && flag3);
        System.out.println(true && true || false && false);
        System.out.println(false || true & false || true);
        System.out.println();
        System.out.println(null instanceof Object);
        System.out.println(null instanceof 匹配文件行差异);
    }
}