package org.jasic;

import org.jasic.util.DateTimeUtil;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: Jasic
 * Date: 13-9-25
 */
public class TestThread {

    @Test
    public void testScheduledThreadPool() {
        ExecutorService ses = Executors.newCachedThreadPool();
        final AtomicInteger ai = new AtomicInteger(0);

        ses.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(ai.getAndAdd(1));
                    }
                }
        );
    }

     int i;

    @Test
    public void testVolatile() {

        Executor executor = Executors.newFixedThreadPool(1000);
        for (int index = 0; index < 100000; index++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    i++;
//                    System.out.println(i);
                }
            });
        }

        System.out.println(">> 最后结果 :" + i);
    }

    public static void main(String[] args) {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(100);
        final AtomicInteger ai = new AtomicInteger(0);

        while (true)
            ses.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(ai.getAndAdd(1));

                            DateTimeUtil.sleep(1000);
                        }
                    }, 0, 1, TimeUnit.SECONDS);
    }
}
