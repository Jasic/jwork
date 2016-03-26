package org.jasic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User: Jasic
 * Date: 13-10-31
 */
public class TestDelayQueue {

    private static Logger logger = LoggerFactory.getLogger(TestDelayQueue.class);

    public static void main(String[] args) throws InterruptedException {

        BlockingQueue<DelayedItem> queue = new DelayQueue<DelayedItem>();
        System.out.println("start time:" + new Date());
        DelayedItem item1 = new DelayedItem(1 * 1000 * 1000 * 1000l);
        DelayedItem item2 = new DelayedItem(10 * 1000 * 1000 * 1000l);

        queue.put(item1);
        queue.put(item2);

        System.out.println("Item1:" + item1.getDelay(TimeUnit.NANOSECONDS));
        System.out.println("Item2:" + item2.getDelay(TimeUnit.NANOSECONDS));

        DelayedItem temp1 = queue.take();
        System.out.println("start time:" + new Date() + ", temp1:" + temp1.getId());
        DelayedItem temp2 = queue.take();
        System.out.println("start time:" + new Date() + ", temp2:" + temp2.getId());

        item1.setTime(20000);
        queue.put(item1);
        DelayedItem temp3 = queue.take();
        System.out.println("start time:" + new Date() + ", temp3:" + temp3.getId());
    }


    /**
     * User: Jasic
     * Date: 13-10-30
     * Most of it copied from DelayedTimer
     */
    static class DelayedItem implements Delayed {

        private static final long NANO_ORIGIN = System.nanoTime();

        /**
         * Sequence number to break scheduling ties, and in turn to
         * guarantee FIFO order among tied entries.
         */
        private static final AtomicLong sequencer = new AtomicLong(0);

        /**
         * Sequence number to break ties FIFO
         */
        private final long sequenceNumber;

        /**
         * The time the task is enabled to execute in nanoTime units
         */
        private volatile long time;

        public DelayedItem(long nanos) {
            time = nanos;
            sequenceNumber = sequencer.getAndIncrement();
        }

        final public long getDelay(TimeUnit unit) {
            long now = now();
            long left = time - now;
            return unit.convert(left, TimeUnit.NANOSECONDS);
        }

        final void setTime(long nanos) {
            time = nanos * 1000000l;
        }

        final long getId() {
            return this.sequenceNumber;
        }

        public int compareTo(Delayed other) {
            return (getDelay(TimeUnit.NANOSECONDS) > other.getDelay(TimeUnit.NANOSECONDS)) ? 1 : -1;
        }

        /**
         * Returns nanosecond time offset by origin
         */
        private static long now() {
            return System.nanoTime() - NANO_ORIGIN;
        }
    }

}
