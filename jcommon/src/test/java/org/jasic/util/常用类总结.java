package org.jasic.util;

import java.util.Comparator;
import java.util.HashMap;

/**
 * @Author 菜鹰.
 * @Date 14-9-26
 */
public class 常用类总结 {

    /**
     * 比较Comparable、Comparator
     * 1、Comparable：支持内比较
     * 2、Comparator：如果内测满足不了可以使用外比较
     */ {
        Comparable comparable = null;
        Comparator comparator = null;

        comparable.compareTo(null);
        comparator.compare(null, null);
    }

    /**
     *HashMap 是一个二维表，可看作二维数组：
     * 横向：横向座标是hash值为点（x）
     * 纵向：数据对象entry组成的一条链路（y）
     *
     * 1、loadFactor:因数
     * 2、table：ha
     *
     * 初始容量和加载因子
     *
     * ConcurrentModificationException
     */ {
        HashMap hashMap = new HashMap();

        // 1、先计算key的hash，再结合table的长度进行再次hash得出横向x座标，定位值x1；
        // 2、找到x1为点的整条纵向链路y，轮询y所有的值，对比key是否相等，key相等则直接覆盖value，将旧value返回。
        // 3、如果找不到相等的key，则根据步骤1找x1，把新增的entry设置为第一个元素，旧的被接在新元素的next属性下。
        // 4、如果size大于threshold，则重新resize为原来的2倍。
        // 5、resize的时候将所有元素一个个的计算步骤1、2以重新搬到新的table元素链上。
        hashMap.put("A", "");

        // 1、entrySet()会返回一个内部set集合、使用nextEntry()方法。
        // 2、keySet、valueSet都是通过nextEntry()返回key、value的集合
        // 3、重点关注nextEntry()方法：if (modCount != expectedModCount) throw new ConcurrentModificationException();
        //    expectedModCount是获取iterator时就与modCount等值，然后如果modCount被其它线程修改，将致使它们不相等从而抛异常
        hashMap.entrySet();
        hashMap.values();
        hashMap.keySet();
    }

    /**
     *  不同点：
     *  1、StringBuffer 线程安全而 StringBuilder线程不安全
     *
     *  共同点：
     *  1、它们都继承于AbstractStringBuilder.
     *
     *  特点：自动扩展
     *  它的实现是当长度需要扩展则新开辟一个byte数组，使用System.arrayCopy旧数据。
     *
     */

    {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("");

        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("");

    }

}
