package org.jasic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: Jasic
 * Date: 13-10-18
 */
public class TestThreadPool {
    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(10);
        final AtomicInteger i = new AtomicInteger(0);

        for (int index = 0; index != 16; index++)
            es.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + " " + i.getAndAdd(1));
                    Thread.currentThread().stop();
                }
            });


        es.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("继续产生线程");
            }
        });
    }
}
