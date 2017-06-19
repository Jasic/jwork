package org.jasic.modue;

import org.jasic.util.DateTimeUtil;
import org.jasic.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public abstract class JvmMonitor extends Thread {

    protected static Logger logger = LoggerFactory.getLogger(JvmMonitor.class);


    private int monInterval;

    private boolean tDetail;

    public JvmMonitor(int monInterval, boolean tDetail) {
        this.monInterval = monInterval;
        this.tDetail = tDetail;
        super.setName("JvmMonitor");
    }

    protected abstract void doMon();

    public void run() {
        this.monitor();
    }


    private void monitor() {

        while (true) {
            try {
                logger.info("---------------------- " + JvmMonitor.class.getSimpleName() + " Start ----------------------");

                logger.info("JVM Memory Monitor:");
                memoryMonitor();
                logger.info("");

                doMon();

                logger.info("Threads Monitor:");
                threadMonitor();
                logger.info("---------------------- " + JvmMonitor.class.getSimpleName() + " End ----------------------\n");
                DateTimeUtil.sleep(this.monInterval);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * JVM memory monitor
     */
    private void memoryMonitor() {
        logger.info("last totalMemory : "
                + Runtime.getRuntime().totalMemory() / 1024 / 1024
                + "mb");
        logger.info("last maxMemory : "
                + Runtime.getRuntime().maxMemory() / 1024 / 1024
                + "mb");
        logger.info("last freeMemory : "
                + Runtime.getRuntime().freeMemory() / 1024 / 1024
                + "mb");
    }


    /**
     * All threads!
     */
    private void threadMonitor() {

        ThreadMXBean mb = ManagementFactory.getThreadMXBean();

        logger.info("All Threads {started:[" + mb.getTotalStartedThreadCount()
                + "], alive:[" + mb.getThreadCount() + "(" + findAllThreads().length + ")"
                + "], death:[" + (mb.getTotalStartedThreadCount() - mb.getThreadCount()
                + "]}"));
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        while (tg.getParent() != null) {
            tg = tg.getParent();
        }

        printList(tg, 0, this.tDetail);
    }


    /**
     * return all the states of threads belonged to bg as string
     *
     * @param tg         source ThreadGroup
     * @param indent
     * @param threadInfo
     * @return
     */
    private String printList(ThreadGroup tg, int indent, boolean threadInfo) {

        StringBuilder result = new StringBuilder();
        StringBuilder sb = new StringBuilder();

        for (int index = 0; index <= indent; index++) {
            sb.append("-");
        }
        indent += 2;

        /** Get the actual threads array */
        Thread[] tTemp = new Thread[tg.activeCount()];
        int tSize = tg.enumerate(tTemp, false);
        Thread threads[] = Arrays.copyOf(tTemp, tSize);

        /** Get the actual threadGroups array */
        ThreadGroup tgTemp[] = new ThreadGroup[tg.activeGroupCount()];
        int tgSize = tg.enumerate(tgTemp, false);
        ThreadGroup[] tgGroups = Arrays.copyOf(tgTemp, tgSize);

        /** Info of threadGroup & its childThread state */
        String groupInfo = sb.toString() + "|" + tg.getName()
                + StringUtil.mapToString(getState(threads));

        result.append(groupInfo.toUpperCase() + "\n");
        logger.info(groupInfo);

        for (Thread t : threads) {
            String temp = sb.toString() + "|__" + tg.getName() + " {id:[" + t.getId() + "], name:[" + t.getName() + "], state:[" + t.getState() + "], priority:[" + t.getPriority() + "]}";
            result.append(temp + "\n");

            if (threadInfo)
                logger.info(temp);
            else
                logger.debug(temp);
        }

        /** recurse childGroup */
        for (ThreadGroup tGroup : tgGroups) {
            result.append(printList(tGroup, indent, threadInfo));
        }

        return result.toString();
    }

    /**
     * find all alive threads
     *
     * @return
     */
    public Thread[] findAllThreads() {

        ThreadGroup group = Thread.currentThread().getThreadGroup();
        ThreadGroup topGroup = group;

        // traverse the ThreadGroup tree to the top
        while (group != null) {
            topGroup = group;
            group = group.getParent();
        }
        /*
           * Create a destination array that is about twice as big as needed to be very confident that none are clipped.
           */
        int estimatedSize = topGroup.activeCount() * 2;
        Thread[] slackList = new Thread[estimatedSize];

        /*
           * Load the thread references into the oversized array. The actual number of threads loaded is returned.
           */
        int actualSize = topGroup.enumerate(slackList);

        /*
           *  copy into a list that is the exact size
           */
        Thread[] list = new Thread[actualSize];
        System.arraycopy(slackList, 0, list, 0, actualSize);
        return list;
    }

    /**
     * Save count the state of threads desperated !
     *
     * @param threads
     * @return
     */
    private Map<State, Integer> getState(Thread[] threads) {

        Map<State, Integer> stateMap = new HashMap<State, Integer>();
        if (threads != null & threads.length != 0) {
            for (Thread t : threads) {
                State state = t.getState();
                if (stateMap.containsKey(state)) {
                    Integer value = stateMap.get(state);
                    stateMap.put(state, value + 1);
                } else
                    stateMap.put(state, 1);

            }
        }
        return stateMap;
    }
}
