package org.jasic.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefualtThreadFactory
        implements ThreadFactory {

    private final ThreadGroup group;
    private final AtomicInteger threadNumber;
    private final String namePrefix;

    public DefualtThreadFactory() {
        this(Thread.currentThread().getThreadGroup().getName());
    }

    public DefualtThreadFactory(String poolName) {
        threadNumber = new AtomicInteger(1);
        SecurityManager s = System.getSecurityManager();
        group = s == null ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
        namePrefix = (new StringBuilder()).append("pool-[").append(poolName.trim()).append("]-->[%s]").toString();
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, String.format(namePrefix, Integer.valueOf(threadNumber.getAndIncrement())), 0L);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != 5)
            t.setPriority(5);
        return t;
    }
}
