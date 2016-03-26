package org.jasic.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: Jasic
 * Date: 13-1-15
 */
public class BlockingQueuePool<E> {

    private int poolCount;

    private List<BlockingQueue<E>> tList;

    private Random random;

    private Object lock;

    /**
     * @param totalSize
     * @param poolCount
     * @param clazz
     */
    @SuppressWarnings("unchecked")
    public BlockingQueuePool(int totalSize, int poolCount, Class<?> clazz) {

        if (totalSize % poolCount != 0) {
            throw new IllegalArgumentException("You must be sure that: totalSize=poolCount*n, n should be a int type! ");
        }
        int n = totalSize / poolCount;
        this.poolCount = poolCount;

        try {

            this.tList = new CopyOnWriteArrayList<BlockingQueue<E>>();
            for (int i = 0; i < n; i++) {
                Constructor<?> constructor = clazz.getConstructor(int.class);
                tList.add((BlockingQueue<E>) constructor.newInstance(n));
            }

            this.random = new Random();
            this.lock = new Object();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 向池中添加一个元素
     *
     * @param e
     * @throws InterruptedException
     */
    public void put(E e) {
        int index = this.random.nextInt(this.poolCount);

        synchronized (this.lock) {
            this.lock.notifyAll();
        }
        try {
            this.tList.get(index).put(e);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 在池中获取一个元素
     *
     * @return
     */
    public E take() {

        List<BlockingQueue<E>> temp = new ArrayList<BlockingQueue<E>>();

        E e = null;
        try {

            synchronized (this.lock) {
                do {
                    for (BlockingQueue<E> queue : this.tList) {
                        if (queue.size() > 0) {
                            temp.add(queue);
                        }
                    }

                    if (temp.size() == 0) {
                        this.lock.wait();
                    }
                } while (temp.size() == 0);
                this.lock.notifyAll();
            }
            e = temp.get(this.random.nextInt(temp.size())).take();
            temp.clear();
            temp = null;
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        return e;
    }


    /**
     * 获取总尺寸大小
     *
     * @return
     */
    public int size() {

        int total = 0;
        for (BlockingQueue<E> queue : this.tList) {
            total += queue.size();
        }
        return total;
    }


    public static void main(String[] args) {
        final BlockingQueuePool<String> spoo = new BlockingQueuePool<String>(100, 10, LinkedBlockingQueue.class);
        spoo.put("a");
        spoo.put("b");
        spoo.put("c");
        spoo.put("d");
        spoo.put("e");
        spoo.put("f");
        spoo.put("g");
        spoo.put("h");
        spoo.put("i");

        System.out.println(spoo.size());
        System.out.println(spoo.take());
        System.out.println(spoo.take());
        System.out.println(spoo.take());
        System.out.println(spoo.take());
        System.out.println(spoo.take());
        System.out.println(spoo.take());

        new Thread() {

            public void run() {
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                spoo.put("j");
                System.out.println(spoo.size());
            }
        }.start();

        System.out.println(spoo.take());
        System.out.println(spoo.take());
        System.out.println(spoo.take());
        System.out.println(spoo.take());
    }
}
